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
import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ValidationResult;
import org.openmrs.Concept;
import org.openmrs.ConceptMap;
import org.openmrs.EncounterRole;
import org.openmrs.EncounterType;
import org.openmrs.PersonName;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.manager.FHIRContextFactory;

public class FHIRUtils {

	public static final String CONTENT_TYPE_APPLICATION_XML_FHIR = "application/xml+fhir";

	public static final String CONTENT_TYPE_APPLICATION_JSON_FHIR = "application/json+fhir";

	public static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";

	public static final String CONTENT_TYPE_APPLICATION_XML = "application/xml";

	public static final String PATIENT_IDENTIFIER_TYPE_REST_RESOURCE_URI = "/ws/rest/v1/patientidentifiertype/";

	public static final String PATIENT_PHONE_NUMBER_ATTRIBUTE = "Telephone Number";

	private static FhirContext ctx = FHIRContextFactory.getFHIRContext();

	private static FhirValidator val = ctx.newValidator();

	public static String getFHIRBaseUrl() {
		return Context.getAdministrationService().getGlobalProperty("fhir.baseUrl");
	}

	public static boolean isCustomNarrativesEnabled() {
		String enabled = Context.getAdministrationService().getGlobalProperty("fhir.isCustomerNarrativesEnabled");
		return Boolean.parseBoolean(enabled);
	}

	public static String getCustomNarrativesPropertyPath() {
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

	public static String getObsAllergyStrategyConceptUuid() {
		return Context.getAdministrationService().getGlobalProperty("fhir.allergy.ObsAllergyStrategy.concept.uuid");
	}

	public static String getConceptCodingSystem() {
		return Context.getAdministrationService().getGlobalProperty("fhir.concept.codingSystem");
	}

	/**
	 * This method accept person object and check whether there is a patient exist, if so it will build reference with
	 * patient else it will contain person reference
	 *
	 * @param person person ob
	 * @return resource reference
	 */
	public static ResourceReferenceDt buildPatientOrPersonResourceReference(org.openmrs.Person person) {
		ResourceReferenceDt reference = new ResourceReferenceDt();
		PersonName name = person.getPersonName();
		StringBuilder nameDisplay = new StringBuilder();
		nameDisplay.append(name.getGivenName());
		nameDisplay.append(" ");
		nameDisplay.append(name.getFamilyName());
		String uri;
		if (Context.getPatientService().getPatientByUuid(person.getUuid()) != null) {
			nameDisplay.append("(");
			nameDisplay.append(FHIRConstants.IDENTIFIER);
			nameDisplay.append(":");
			nameDisplay.append(Context.getPatientService().getPatientByUuid(person.getUuid())
					.getPatientIdentifier()
					.getIdentifier());
			nameDisplay.append(")");
			uri = FHIRConstants.PATIENT + "/" + person.getUuid();
		} else {
			uri = FHIRConstants.PERSON + "/" + person.getUuid();
		}
		reference.setDisplay(nameDisplay.toString());
		IdDt patientRef = new IdDt();
		patientRef.setValue(uri);
		reference.setReference(patientRef);
		return reference;
	}

	public static CodingDt getCodingDtByConceptMappings(ConceptMap conceptMap) {
		//Set concept source concept name as the display value and set concept uuid if name is empty
		String display = conceptMap.getConceptReferenceTerm().getName();
		//Commented to omit setting concept uuid if concept name is null or empty
			/*if (display == null || display.isEmpty()) {
				display = conceptMap.getConceptReferenceTerm().getUuid();
			}*/
		//Get concept source name and uri pair if it available
		ConceptSourceNameURIPair sourceNameURIPair = FHIRConstants.conceptSourceMap.get(conceptMap
				.getConceptReferenceTerm().getConceptSource().getName().toLowerCase());
		if (sourceNameURIPair != null) {
			return new CodingDt().setCode(conceptMap.getConceptReferenceTerm().getCode()).setDisplay(display).setSystem
					(sourceNameURIPair.getConceptSourceURI());
		}
		if (display != null && !display.isEmpty()) {
			return new CodingDt().setCode(conceptMap.getConceptReferenceTerm().getCode()).setDisplay(display).setSystem(
					conceptMap
							.getConceptReferenceTerm().getConceptSource().getName());
		} else {
			return new CodingDt().setCode(conceptMap.getConceptReferenceTerm().getCode()).setSystem(
					conceptMap.getConceptReferenceTerm().getConceptSource().getName());
		}
	}

	public static CodingDt getCodingDtByOpenMRSConcept(Concept concept) {
		String display = null;
		//Set concept name as the display value and set concept uuid if name is empty
		if (concept.getName() != null) {
			display = concept.getName().getName();
		}
		//Commented out for omiiting setting concept uuid in display term
		/*if (display == null || display.isEmpty()) {
			display = concept.getUuid();
		}*/

		if (display != null && !display.isEmpty()) {
			return new CodingDt().setCode(concept.getUuid()).setDisplay(display).setSystem(FHIRConstants.OPENMRS_URI);
		} else {
			return new CodingDt().setCode(concept.getUuid()).setSystem(FHIRConstants.OPENMRS_URI);
		}
	}

	private static Concept getConceptByGlobalProperty(String globalPropertyName) {
		String globalProperty = Context.getAdministrationService().getGlobalProperty(globalPropertyName);
		Concept concept = Context.getConceptService().getConceptByUuid(globalProperty);
		if (concept == null) {
			throw new IllegalStateException("Configuration required: " + globalPropertyName);
		}
		return concept;
	}

	public static Concept getMildSeverityConcept() {
		return getConceptByGlobalProperty("allergy.concept.severity.mild");
	}

	public static Concept getModerateSeverityConcept() {
		return getConceptByGlobalProperty("allergy.concept.severity.moderate");
	}

	public static Concept getSevereSeverityConcept() {
		return getConceptByGlobalProperty("allergy.concept.severity.severe");
	}

	public static EncounterRole getEncounterRole() {
		String globalProperty = Context.getAdministrationService().getGlobalProperty("fhir.encounter.encounterRoleUuid");
		EncounterRole encounterRole = Context.getEncounterService().getEncounterRoleByUuid(globalProperty);
		if (encounterRole == null) {
			throw new IllegalStateException("Configuration required for " + globalProperty);
		}
		return encounterRole;
	}

	public static EncounterType getEncounterType(String code) {
		String globalProperty = Context.getAdministrationService().getGlobalProperty("fhir.encounter.encounterType." +
				code);
		EncounterType encounterType = Context.getEncounterService().getEncounterTypeByUuid(globalProperty);
		if (encounterType == null) {
			throw new IllegalStateException("Configuration required for " + globalProperty);
		}
		return encounterType;
	}

	public static Concept getDiagnosticReportNameConcept() {
		return getConceptByConceptId("fhir.diagnosticreport.name");
	}

	public static Concept getDiagnosticReportStatusConcept() {
		return getConceptByConceptId("fhir.diagnosticreport.status");
	}

	public static Concept getDiagnosticReportResultConcept() {
		return getConceptByConceptId("fhir.diagnosticreport.result");
	}

	public static Concept getDiagnosticReportImagingStudyConcept() {
		return getConceptByConceptId("fhir.diagnosticreport.imagingstudy");
	}

	public static Concept getDiagnosticReportPresentedFormConcept() {
		return getConceptByConceptId("fhir.diagnosticreport.presentedform");
	}

	public static String getDiagnosticReportRadiologyBaseServerURL() {
		return Context.getAdministrationService().getGlobalProperty("fhir.diagnosticreport.radiology.server");
	}

	private static Concept getConceptByConceptId(String globalPropertyName) {
		String globalProperty = Context.getAdministrationService().getGlobalProperty(globalPropertyName);
		Concept concept = Context.getConceptService().getConcept(Integer.parseInt(globalProperty));
		if (concept == null) {
			throw new IllegalStateException("Configuration required: " + globalPropertyName);
		}
		return concept;
	}
}
