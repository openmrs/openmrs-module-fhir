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

import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.FHIRConstants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SwaggerSpecificationController extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        String swaggerSpecificationJSON = "";
        try {
            StringBuilder baseUrl = new StringBuilder();
            String scheme = request.getScheme();
            int port = request.getServerPort();

            baseUrl.append(scheme); // http, https
            baseUrl.append("://");
            baseUrl.append(request.getServerName());
            if ((scheme.equals("http") && port != 80) || (scheme.equals("https") && port != 443)) {
                baseUrl.append(':');
                baseUrl.append(request.getServerPort());
            }

            baseUrl.append(request.getContextPath());
            String resourcesUrl = Context.getAdministrationService().getGlobalProperty(FHIRConstants.URI_PREFIX_GLOBAL_PROPERTY_NAME, baseUrl.toString());
            resourcesUrl += "ws/fhir";
            String urlWithoutScheme = "";

            if (scheme.equals("http")) {
                urlWithoutScheme = resourcesUrl.replace("http://", "");
            } else if (scheme.equals("https")) {
                urlWithoutScheme = resourcesUrl.replace("https://", "");
            }

            SwaggerSpecificationCreator creator = new SwaggerSpecificationCreator(urlWithoutScheme);
            swaggerSpecificationJSON = creator.buildJSON();

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(swaggerSpecificationJSON);
        } catch (Exception exception) {

        }
    }

}

