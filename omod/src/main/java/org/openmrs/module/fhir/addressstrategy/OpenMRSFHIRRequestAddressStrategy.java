/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.fhir.addressstrategy;

import ca.uhn.fhir.rest.server.IServerAddressStrategy;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

public class OpenMRSFHIRRequestAddressStrategy implements IServerAddressStrategy {

	private static final String MODULE_SERVELET_PREFIX = "fhir/fhirservice";

	@Override
	public String determineServerBase(ServletContext theServletContext, HttpServletRequest theRequest) {
		String requestFullPath = StringUtils.defaultString(theRequest.getRequestURI());
		String servletPath = StringUtils.defaultString(theRequest.getServletPath());
		StringBuffer requestUrl = theRequest.getRequestURL();
		String servletContextPath = "";
		if (theServletContext != null) {
			servletContextPath = StringUtils.defaultString(theServletContext.getContextPath());
			// } else {
			// servletContextPath = servletPath;
		}

		String requestPath = requestFullPath.substring(servletContextPath.length() + servletPath.length());
		if (requestPath.length() > 0 && requestPath.charAt(0) == '/') {
			requestPath = requestPath.substring(1);
		}

		int startOfPath = requestUrl.indexOf("//");
		if (startOfPath != -1 && (startOfPath + 2) < requestUrl.length()) {
			startOfPath = requestUrl.indexOf("/", startOfPath + 2);
		}
		if (startOfPath == -1) {
			startOfPath = 0;
		}

		int contextIndex;
		if (servletPath.length() == 0) {
			if (requestPath.length() == 0) {
				contextIndex = requestUrl.length();
			} else {
				contextIndex = requestUrl.indexOf(requestPath, startOfPath);
			}
		} else {
			contextIndex = requestUrl.indexOf(servletPath, startOfPath);
		}

		String fhirServerBase;
		int length = contextIndex + servletPath.length() + MODULE_SERVELET_PREFIX.length();
		fhirServerBase = requestUrl.substring(0, length + 1);
		return fhirServerBase;
	}
}
