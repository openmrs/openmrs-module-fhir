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
import ca.uhn.fhir.rest.api.EncodingEnum;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.interceptor.LoggingInterceptor;
import ca.uhn.fhir.rest.server.interceptor.ResponseHighlighterInterceptor;
import org.hl7.fhir.dstu3.hapi.rest.server.ServerCapabilityStatementProvider;
import org.junit.Test;
import org.openmrs.module.fhir.addressstrategy.OpenMRSFHIRRequestAddressStrategy;
import org.openmrs.module.fhir.exception.FHIRModuleOmodException;
import org.openmrs.module.fhir.providers.RestfulAllergyIntoleranceResourceProvider;
import org.openmrs.module.fhir.providers.RestfulConditionResourceProvider;
import org.openmrs.module.fhir.providers.RestfulDiagnosticReportResourceProvider;
import org.openmrs.module.fhir.providers.RestfulEncounterResourceProvider;
import org.openmrs.module.fhir.providers.RestfulFamilyMemberHistoryResourceProvider;
import org.openmrs.module.fhir.providers.RestfulLocationResourceProvider;
import org.openmrs.module.fhir.providers.RestfulMedicationRequestResourceProvider;
import org.openmrs.module.fhir.providers.RestfulObservationResourceProvider;
import org.openmrs.module.fhir.providers.RestfulPatientResourceProvider;
import org.openmrs.module.fhir.providers.RestfulPersonResourceProvider;
import org.openmrs.module.fhir.providers.RestfulPractitionerResourceProvider;
import org.openmrs.module.fhir.providers.RestfulProcedureRequestResourceProvider;
import org.openmrs.module.fhir.server.ConformanceProvider;
import org.openmrs.module.fhir.swagger.SwaggerSpecificationCreator;
import org.openmrs.module.fhir.swagger.codegen.SwaggerCodeGenerator;
import org.openmrs.module.fhir.util.FHIROmodConstants;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class SwaggerSDKGenerationTestCase extends RestfulServer {

	private static final String MODULE_SERVELET_PREFIX = "/fhir/fhirServelet";

	public SwaggerSDKGenerationTestCase() {
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
		resourceProviders.add(new RestfulMedicationRequestResourceProvider());
		resourceProviders.add(new RestfulProcedureRequestResourceProvider());
		//Removing composition since we now not support for it
		//resourceProviders.add(new RestfulCompositionResourceProvider());
		resourceProviders.add(new RestfulPersonResourceProvider());
		this.setFhirContext(FhirContext.forDstu3());
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
				.setMessageFormat("Source[${remoteAddr}] Operation[${operationType} ${idOrResourceName}] " +
						"UA[${requestHeader.user-agent}] Params[${requestParameters}]");
		ServerCapabilityStatementProvider sc = new ServerCapabilityStatementProvider(this);
		this.setServerConformanceProvider(sc);
		ConformanceProvider provider = new ConformanceProvider();
		provider.setRestfulServer(this);
	}

	protected String getRequestPath(String requestFullPath, String servletContextPath, String servletPath) {
		return requestFullPath.substring(escapedLength(servletContextPath) + escapedLength(servletPath) + escapedLength(
				MODULE_SERVELET_PREFIX));
	}

	@Test
	public void generateSwaggerSDK_shouldGenerateSwaggerSDK() throws FHIRModuleOmodException {
		String urlWithoutScheme = "http";
		String language = "java";
		String basePath = "/ws/fhir";
		HttpServletRequest request = new MockHttpServletRequest();
		SwaggerSpecificationCreator creator = new SwaggerSpecificationCreator(urlWithoutScheme, basePath, request);
		String swaggerSpecificationJSON = creator.buildJSON();
		assertNotNull(swaggerSpecificationJSON);
		SwaggerCodeGenerator swaggerCodeGenerator = new SwaggerCodeGenerator();
		String path = swaggerCodeGenerator.generateSDK(language, swaggerSpecificationJSON);
		assertNotNull(path);
	}

	@Test(expected = FHIRModuleOmodException.class)
	public void generateSwaggerSDKForInvalidLanguage_shouldNotGenerateSwaggerSDK() throws FHIRModuleOmodException {
		String urlWithoutScheme = "http";
		String language = "non-exist";
		String basePath = "/ws/fhir";
		HttpServletRequest request = new MockHttpServletRequest();
		SwaggerSpecificationCreator creator = new SwaggerSpecificationCreator(urlWithoutScheme, basePath, request);
		String swaggerSpecificationJSON = creator.buildJSON();
		assertNotNull(swaggerSpecificationJSON);
		SwaggerCodeGenerator swaggerCodeGenerator = new SwaggerCodeGenerator();
		String path = swaggerCodeGenerator.generateSDK(language, swaggerSpecificationJSON);
		fail("Attempt generate SDK for non exist language which should throw an exception");
	}
}
