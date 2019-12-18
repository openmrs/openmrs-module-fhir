/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 * <p/>
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * <p/>
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.fhir.swagger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.openmrs.module.fhir.filter.ForwardingFilter;
import org.openmrs.module.fhir.swagger.codegen.SwaggerCodeGenerator;
import org.openmrs.module.fhir.util.FHIROmodConstants;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

public class SwaggerCodeGenController extends HttpServlet {

	protected Log log = LogFactory.getLog(getClass());

	protected void doGet(HttpServletRequest request, HttpServletResponse response) {

		String swaggerSpecificationJSON;
		try {
			StringBuilder baseUrl = new StringBuilder();
			String scheme = request.getScheme();
			int port = request.getServerPort();

			baseUrl.append(scheme); // http, https
			baseUrl.append(SwaggerDocConstants.SLASHES);
			baseUrl.append(request.getServerName());
			if ((SwaggerDocConstants.HTTP.equals(scheme) && port != 80) ||
					(SwaggerDocConstants.HTTPS.equals(scheme) && port != 443)) {
				baseUrl.append(SwaggerDocConstants.COLON);
				baseUrl.append(request.getServerPort());
			}

			baseUrl.append(request.getContextPath());
			String resourcesUrl = Context.getAdministrationService().
					getGlobalProperty(FHIRConstants.URI_PREFIX_GLOBAL_PROPERTY_NAME, baseUrl.toString());
			String urlWithoutScheme = "";
			String basePath = ForwardingFilter.getContextPath() + "/ws/fhir";
			if (SwaggerDocConstants.HTTP.equals(scheme)) {
				urlWithoutScheme = resourcesUrl.replace(SwaggerDocConstants.HTTP_WITH_SLASHES,
						SwaggerDocConstants.STR_EMPTY);
			} else if (SwaggerDocConstants.HTTPS.equals(scheme)) {
				urlWithoutScheme = resourcesUrl.replace(SwaggerDocConstants.HTTPS_WITH_SLASHES,
						SwaggerDocConstants.STR_EMPTY);
			}
			urlWithoutScheme = urlWithoutScheme.replace(ForwardingFilter.getContextPath(), SwaggerDocConstants.STR_EMPTY);
			SwaggerSpecificationCreator creator = new SwaggerSpecificationCreator(urlWithoutScheme, basePath, request, response);
			swaggerSpecificationJSON = creator.buildJSON();
			SwaggerCodeGenerator swaggerCodeGenerator = new SwaggerCodeGenerator();
			String language = request.getParameter(FHIROmodConstants.LANGUAGE);
			String path = swaggerCodeGenerator.generateSDK(language, swaggerSpecificationJSON);
			File sdkZipFile = new File(path);
			response.setHeader(FHIROmodConstants.CONTENT_TYPE, FHIROmodConstants.APPLICATION_ZIP_CHARSET_UTF_8);
			response.setHeader(FHIROmodConstants.CONTENT_DISPOSITION, FHIROmodConstants.ATTACHMENT_FILENAME
					+ "\"" + sdkZipFile.getName() + "\"");
			FileInputStream fileInputStream = new FileInputStream(sdkZipFile);
			OutputStream responseOutputStream = response.getOutputStream();
			int bytes;
			while ((bytes = fileInputStream.read()) != -1) {
				responseOutputStream.write(bytes);
			}
			response.flushBuffer();
		}
		catch (Exception e) {
			log.error("Error while processing request", e);
		}
	}

}

