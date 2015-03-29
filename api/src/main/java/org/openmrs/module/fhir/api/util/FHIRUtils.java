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
package org.openmrs.module.fhir.api.util;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ValidationResult;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.manager.FHIRContextFactory;

public class FHIRUtils {

	private static FhirContext ctx = FHIRContextFactory.getFHIRContext();
	private static FhirValidator val = ctx.newValidator();

	public static final String CONTENT_TYPE_APPLICATION_XML_FHIR = "application/xml+fhir";
	public static final String CONTENT_TYPE_APPLICATION_JSON_FHIR = "application/json+fhir";
	public static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";
	public static final String CONTENT_TYPE_APPLICATION_XML = "application/xml";
	public static final String PATIENT_IDENTIFIER_TYPE_REST_RESOURCE_URI = "/ws/rest/v1/patientidentifiertype/";
	public static final String PATIENT_PHONE_NUMBER_ATTRIBUTE = "Telephone Number";

	public static String getFHIRBaseUrl() {
		return Context.getAdministrationService().getGlobalProperty("fhir.baseUrl");
	}

	public static boolean isCustomNarrativesEnabled() {
		String enabled = Context.getAdministrationService().getGlobalProperty("fhir.isCustomerNarrativesEnabled");
		return Boolean.parseBoolean(enabled);
	}

	public static String gettCustomNarrativesPropertyPath() {
		return Context.getAdministrationService().getGlobalProperty("fhir.customNarrativePropertiesPath");
	}

	public static void validate(IResource resource) {
		ValidationResult result = val.validateWithResult(resource);
		if (!result.isSuccessful()) {
			throw new UnprocessableEntityException(ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(result
					.getOperationOutcome()));
		}
	}

	public static String buildURN(String type, String value) {
		StringBuilder urnBuilder = new StringBuilder();
		urnBuilder.append(FHIRConstants.URN).append(":").append(type).append(":").append(value);
		return urnBuilder.toString();
	}

	public static String getAllergyStrategy() {
		return Context.getAdministrationService().getGlobalProperty("fhir.allergy.allergyStrategy");
	}

	public static String getObsAllergyStrategyConcept() {
		return Context.getAdministrationService().getGlobalProperty("fhir.allergy.ObsAllergyStrategy.concept");
	}
}
