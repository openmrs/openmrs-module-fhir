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
package org.openmrs.module.fhir.omod;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.server.EncodingEnum;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.interceptor.LoggingInterceptor;
import ca.uhn.fhir.rest.server.interceptor.ResponseHighlighterInterceptor;
import ca.uhn.fhir.rest.server.provider.dstu2.ServerConformanceProvider;
import org.junit.Test;
import org.openmrs.module.fhir.addressstrategy.OpenMRSFHIRRequestAddressStrategy;
import org.openmrs.module.fhir.providers.*;
import org.openmrs.module.fhir.server.ConformanceProvider;
import org.openmrs.module.fhir.swagger.SwaggerSpecificationCreator;
import org.openmrs.module.fhir.util.FHIROmodConstants;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SwaggerDocumentGenerationTestCase extends RestfulServer {
	private static final String MODULE_SERVELET_PREFIX = "/fhir/fhirServelet";

	public SwaggerDocumentGenerationTestCase() {
		initialize();
	}
	/**
	 * The initialize method is automatically called when the servlet is starting up, so it can
	 * be used to configure the servlet to define resource providers, or set up
	 * configuration, interceptors, etc.
	 */
	@Override
	protected void initialize() {
		this.setServerAddressStrategy(new OpenMRSFHIRRequestAddressStrategy());
		List<IResourceProvider> resourceProviders = new ArrayList<IResourceProvider>();
		resourceProviders.add(new RestfulPatientResourceProvider());
		resourceProviders.add(new RestfulAllergyIntoleranceResourceProvider());
		resourceProviders.add(new RestfulEncounterResourceProvider());
		resourceProviders.add(new RestfulFamilyMemberHistoryResourceProvider());
		resourceProviders.add(new RestfulLocationResourceProvider());
		resourceProviders.add(new RestfulObservationResourceProvider());
		resourceProviders.add(new RestfulPractitionerResourceProvider());
		resourceProviders.add(new RestfulConditionResourceProvider());
		resourceProviders.add(new RestfulDiagnosticReportResourceProvider());
		//Removing composition since we now not support for it
		//resourceProviders.add(new RestfulCompositionResourceProvider());
		resourceProviders.add(new RestfulPersonResourceProvider());
		this.setFhirContext(FhirContext.forDstu2());
		setResourceProviders(resourceProviders);
		setServerName(FHIROmodConstants.OPENMRS_FHIR_SERVER_NAME);
		setServerVersion(FHIROmodConstants.OPENMRS_FHIR_SERVER_VERSION);
		setImplementationDescription(FHIROmodConstants.OPENMRS_FHIR_SERVER_DES);
		setDefaultPrettyPrint(true);
		setDefaultResponseEncoding(EncodingEnum.JSON);
		ResponseHighlighterInterceptor responseHighlighter = new ResponseHighlighterInterceptor();
		registerInterceptor(responseHighlighter);
		LoggingInterceptor loggingInterceptor = new LoggingInterceptor();
		registerInterceptor(loggingInterceptor);
		loggingInterceptor.setLoggerName("test.accesslog");
		loggingInterceptor
				.setMessageFormat("Source[${remoteAddr}] Operation[${operationType} ${idOrResourceName}] UA[${requestHeader.user-agent}] Params[${requestParameters}]");
		ServerConformanceProvider sc = new ServerConformanceProvider(this);
		this.setServerConformanceProvider(sc);
		ConformanceProvider provider = new ConformanceProvider();
		provider.setRestfulServer(this);
	}

	protected String getRequestPath(String requestFullPath, String servletContextPath, String servletPath) {
		return requestFullPath.substring(escapedLength(servletContextPath) + escapedLength(servletPath) + escapedLength(
				MODULE_SERVELET_PREFIX));
	}

	@Test
	public void generateSwaggerDocumentation_shouldGenerateSwaggerDocumentation() {
		String urlWithoutScheme = "http";
		String basePath = "/ws/fhir";
		SwaggerSpecificationCreator creator = new SwaggerSpecificationCreator(urlWithoutScheme, basePath);
		String swaggerSpecificationJSON = creator.buildJSON();
		assertNotNull(swaggerSpecificationJSON);
		assertTrue(swaggerSpecificationJSON.contains("Auto-generated documentation for OpenMRS FHIR Rest services"));
	}
}
