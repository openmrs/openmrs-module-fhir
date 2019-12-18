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
package org.openmrs.module.fhir.server;

import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.servlet.ServletRequestDetails;
import org.hl7.fhir.dstu3.hapi.rest.server.ServerCapabilityStatementProvider;
import org.hl7.fhir.dstu3.model.CapabilityStatement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ConformanceProvider {

	private static CapabilityStatement conformance = null;

	private static RestfulServer restfulServer;

	public static void setConformance(CapabilityStatement conformanceStatement) {
		conformance = conformanceStatement;
	}

	public static CapabilityStatement getConformance(HttpServletRequest request, HttpServletResponse response) {
		if (conformance == null) {
			//Get server conformance provider
			ServerCapabilityStatementProvider confProvider = (ServerCapabilityStatementProvider) restfulServer
					.getServerConformanceProvider();
			ServletRequestDetails theRequestDetails = new ServletRequestDetails(restfulServer.getInterceptorService());
			theRequestDetails.setServer(restfulServer);
			theRequestDetails.setServletRequest(request);
			theRequestDetails.setServletResponse(response);
			conformance = confProvider.getServerConformance(request, theRequestDetails);
		}

		return conformance;
	}

	public static RestfulServer getRestfulServer() {
		return restfulServer;
	}

	public void setRestfulServer(RestfulServer restfulServer) {
		this.restfulServer = restfulServer;
	}
}
