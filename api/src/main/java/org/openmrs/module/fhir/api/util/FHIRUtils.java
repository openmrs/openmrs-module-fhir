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
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ValidationResult;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.mutable.MutableBoolean;
import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.AllergyIntolerance;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.dstu3.model.StringType;
import org.openmrs.Concept;
import org.openmrs.ConceptMap;
import org.openmrs.EncounterRole;
import org.openmrs.EncounterType;
import org.openmrs.PersonName;
import org.openmrs.RelationshipType;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.manager.FHIRContextFactory;

import java.util.ArrayList;
import java.util.List;

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

	public static void validate(Resource resource) {
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

	public static String getMedicationStrategy() {
		return Context.getAdministrationService().getGlobalProperty("fhir.medication.strategy");
	}

	public static String getMedicationRequestStrategy() {
		return Context.getAdministrationService().getGlobalProperty("fhir.medicationRequest.strategy");
	}

	public static String getProcedureRequestStrategy() {
		return Context.getAdministrationService().getGlobalProperty("fhir.procedureRequest.strategy");
	}

	public static String getAllergyStrategy() {
		return Context.getAdministrationService().getGlobalProperty("fhir.allergy.strategy");
	}

	public static String getPersonStrategy() {
		return Context.getAdministrationService().getGlobalProperty("fhir.person.strategy");
	}

	public static String getPatientStrategy() {
		return Context.getAdministrationService().getGlobalProperty("fhir.patient.strategy");
	}

	public static String getRelatedPersonStrategy() {
		return Context.getAdministrationService().getGlobalProperty("fhir.relatedPerson.strategy");
	}

	public static String getAppointmentStrategy() {
		return Context.getAdministrationService().getGlobalProperty("fhir.appointment.strategy");
	}

	public static String getLocationStrategy() {
		return Context.getAdministrationService().getGlobalProperty("fhir.location.strategy");
	}

	public static String getObservationStrategy() {
		return Context.getAdministrationService().getGlobalProperty("fhir.observation.strategy");
	}

	public static String getGroupStrategy() {
		return Context.getAdministrationService().getGlobalProperty("fhir.group.strategy");
	}

	public static String getEncounterStrategy() {
		return Context.getAdministrationService().getGlobalProperty("fhir.encounter.strategy");
	}

	public static String getPractitionerStrategy() {
		return Context.getAdministrationService().getGlobalProperty("fhir.practitioner.strategy");
	}

	public static String getPlanDefinitionStrategy() {
		return Context.getAdministrationService().getGlobalProperty("fhir.planDefinition.strategy");
	}

	public static int[] getConceptIdsOfConditions() {
		String conceptsAsConditions = Context.getAdministrationService().getGlobalProperty(FHIRConstants
				.CONCEPTS_CONVERTABLE_TO_CONDITIONS_STORED_AS_OBS);
		if (StringUtils.isNotBlank(conceptsAsConditions)) {
			String[] concepts = conceptsAsConditions.split(",");
			int[] conceptIds = new int[concepts.length];
			int counter = 0;
			for (String str : concepts) {
				conceptIds[counter] = Integer.parseInt(str);
			}
			return conceptIds;
		} else {
			return null;
		}
	}

	public static String getObsAllergyStrategyConceptUuid() {
		return Context.getAdministrationService().getGlobalProperty("fhir.allergy.strategy.concept.uuid");
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
	public static Reference buildPatientOrPersonResourceReference(org.openmrs.Person person) {
		Reference reference = new Reference();
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
		reference.setReference(uri);
		reference.setId(person.getUuid());
		Identifier identifier = new Identifier();
		identifier.setId(person.getUuid());
		reference.setIdentifier(identifier);
		return reference;
	}

	public static RelationshipType getRelationshipTypeByCoding(Coding coding, MutableBoolean isAToB) {
		if (coding.getCode() != null) {
			List<RelationshipType> relationshipList = Context.getPersonService().getAllRelationshipTypes();
			for (RelationshipType relationshipType : relationshipList) {
				if (coding.getCode().equals(relationshipType.getaIsToB())) {
					isAToB.setValue(true);
					return relationshipType;
				} else if (coding.getCode().equals(relationshipType.getbIsToA())) {
					isAToB.setValue(false);
					return relationshipType;
				}
			}
		}
		return null;
	}

	public static HumanName buildHumanName(org.openmrs.PersonName personName) {
		HumanName fhirName = new HumanName();
		fhirName.setFamily(personName.getFamilyName());
		StringType givenName = new StringType();
		givenName.setValue(personName.getGivenName());
		List<StringType> givenNames = new ArrayList<StringType>();
		givenNames.add(givenName);
		fhirName.setGiven(givenNames);

		if (personName.getFamilyNameSuffix() != null) {
			StringType suffix = new StringType();
			suffix.setValue(personName.getFamilyNameSuffix());
			List<StringType> suffixes = new ArrayList<StringType>();
			suffixes.add(suffix);
			fhirName.setSuffix(suffixes);
		}

		if (personName.getPrefix() != null) {
			StringType prefix = new StringType();
			prefix.setValue(personName.getPrefix());
			List<StringType> prefixes = new ArrayList<StringType>();
			prefixes.add(prefix);
			fhirName.setSuffix(prefixes);
		}

		//TODO needs to set catagory appropriately
		if (personName.isPreferred()) {
			fhirName.setUse(HumanName.NameUse.USUAL);
		} else {
			fhirName.setUse(HumanName.NameUse.OLD);
		}

		return fhirName;
	}

	public static Address buildAddress(org.openmrs.PersonAddress personAddress) {
		Address fhirAddress = new Address();
		fhirAddress.setCity(personAddress.getCityVillage());
		fhirAddress.setCountry(personAddress.getCountry());
		fhirAddress.setState(personAddress.getStateProvince());
		fhirAddress.setPostalCode(personAddress.getPostalCode());
		List<StringType> addressStrings = new ArrayList<StringType>();
		addressStrings.add(new StringType(personAddress.getAddress1()));
		addressStrings.add(new StringType(personAddress.getAddress2()));
		addressStrings.add(new StringType(personAddress.getAddress3()));
		addressStrings.add(new StringType(personAddress.getAddress4()));
		addressStrings.add(new StringType(personAddress.getAddress5()));
		fhirAddress.setLine(addressStrings);
		if (personAddress.isPreferred()) {
			fhirAddress.setUse(Address.AddressUse.HOME);
		} else {
			fhirAddress.setUse(Address.AddressUse.OLD);
		}
		return fhirAddress;
	}

	/**
	 * Generates practitioner referenceDt
	 *
	 * @param provider the provider ob
	 * @return the practitioner resource reference
	 */
	public static Reference buildPractitionerReference(org.openmrs.Provider provider) {
		Reference providerDt = new Reference();
		StringBuilder providerNameDisplay = new StringBuilder();
		providerNameDisplay.append(provider.getName());
		providerNameDisplay.append("(");
		providerNameDisplay.append(FHIRConstants.IDENTIFIER);
		providerNameDisplay.append(":");
		providerNameDisplay.append(provider.getIdentifier());
		providerNameDisplay.append(")");
		providerDt.setDisplay(providerNameDisplay.toString());
		String providerUri = FHIRConstants.PRACTITIONER + "/" + provider.getUuid();
		providerDt.setReference(providerUri);
		return providerDt;
	}

	public static Coding getCodingDtByConceptMappings(ConceptMap conceptMap) {
		//Set concept source concept name as the display value and set concept uuid if name is empty
		String display = conceptMap.getConceptReferenceTerm().getName();
		//Get concept source name and uri pair if it available
		ConceptSourceNameURIPair sourceNameURIPair = FHIRConstants.conceptSourceMap.get(conceptMap
				.getConceptReferenceTerm().getConceptSource().getName().toLowerCase());
		if (sourceNameURIPair != null) {
			return new Coding().setCode(conceptMap.getConceptReferenceTerm().getCode()).setDisplay(display).setSystem
					(sourceNameURIPair.getConceptSourceURI());
		}
		if (display != null && !display.isEmpty()) {
			return new Coding().setCode(conceptMap.getConceptReferenceTerm().getCode()).setDisplay(display).setSystem(
					conceptMap
							.getConceptReferenceTerm().getConceptSource().getName());
		} else {
			return new Coding().setCode(conceptMap.getConceptReferenceTerm().getCode()).setSystem(
					conceptMap.getConceptReferenceTerm().getConceptSource().getName());
		}
	}

	public static CodeableConcept getCodeableConceptConceptMappings(ConceptMap conceptMap) {
		//Set concept source concept name as the display value and set concept uuid if name is empty
		String display = conceptMap.getConceptReferenceTerm().getName();
		CodeableConcept codeableConcept = new CodeableConcept();
		//Get concept source name and uri pair if it available
		ConceptSourceNameURIPair sourceNameURIPair = FHIRConstants.conceptSourceMap.get(conceptMap
				.getConceptReferenceTerm().getConceptSource().getName().toLowerCase());
		if (sourceNameURIPair != null) {
			return codeableConcept.addCoding(
					new Coding().setCode(conceptMap.getConceptReferenceTerm().getCode()).setDisplay(display).setSystem
							(sourceNameURIPair.getConceptSourceURI()));
		}
		if (display != null && !display.isEmpty()) {
			return codeableConcept.addCoding(
					new Coding().setCode(conceptMap.getConceptReferenceTerm().getCode()).setDisplay(display).setSystem(
							conceptMap
									.getConceptReferenceTerm().getConceptSource().getName()));
		} else {
			return codeableConcept.addCoding(new Coding().setCode(conceptMap.getConceptReferenceTerm().getCode()).setSystem(
					conceptMap.getConceptReferenceTerm().getConceptSource().getName()));
		}
	}

	public static AllergyIntolerance.AllergyIntoleranceReactionComponent getAllergyReactionComponent(ConceptMap conceptMap
			, AllergyIntolerance.AllergyIntoleranceReactionComponent component) {
		//Set concept source concept name as the display value and set concept uuid if name is empty
		if (component == null) {
			component = new AllergyIntolerance.AllergyIntoleranceReactionComponent();
		}
		CodeableConcept substance = new CodeableConcept();
		String display = conceptMap.getConceptReferenceTerm().getName();
		//Get concept source name and uri pair if it available
		ConceptSourceNameURIPair sourceNameURIPair = FHIRConstants.conceptSourceMap.get(conceptMap
				.getConceptReferenceTerm().getConceptSource().getName().toLowerCase());
		if (sourceNameURIPair != null) {
			Coding code = new Coding();
			code.setSystem(sourceNameURIPair.getConceptSourceURI());
			code.setCode(conceptMap.getConceptReferenceTerm().getCode());
			code.setDisplay(display);
			substance.addCoding(code);
			component.setSubstance(substance);
			return component;
		}
		if (display != null && !display.isEmpty()) {
			Coding code = new Coding();
			code.setSystem(conceptMap.getConceptReferenceTerm().getConceptSource().getName());
			code.setCode(conceptMap.getConceptReferenceTerm().getCode());
			code.setDisplay(display);
			substance.addCoding(code);
			component.setSubstance(substance);
			return component;
		} else {
			Coding code = new Coding();
			code.setSystem(conceptMap.getConceptReferenceTerm().getConceptSource().getName());
			code.setCode(conceptMap.getConceptReferenceTerm().getCode());
			substance.addCoding(code);
			component.setSubstance(substance);
			return component;
		}
	}

	public static Coding getCodingDtByOpenMRSConcept(Concept concept) {
		String display = null;
		//Set concept name as the display value and set concept uuid if name is empty
		if (concept.getName() != null) {
			display = concept.getName().getName();
		}

		if (display != null && !display.isEmpty()) {
			return new Coding().setCode(concept.getUuid()).setDisplay(display).setSystem(FHIRConstants.OPENMRS_URI);
		} else {
			return new Coding().setCode(concept.getUuid()).setSystem(FHIRConstants.OPENMRS_URI);
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

	public static String getObjectUuidByReference(Reference objectRef) {
		Identifier identifier = objectRef.getIdentifier();
		String uuid = objectRef.getId();

		if (StringUtils.isEmpty(uuid) && identifier != null) {
			uuid = identifier.getId();
		}

		if (StringUtils.isEmpty(uuid)) {
			String objectRefStr = objectRef.getReference();
			String[] objectRefStrSplit = objectRefStr.split("/");

			if (objectRefStrSplit.length > 1) {
				uuid = objectRefStrSplit[1];
			}
		}

		return uuid;
	}

	public static String getObjectUuidByIdentifier(Identifier identifier) {
		return (identifier != null) ? identifier.getValue() : null;
	}

	public static Identifier createIdentifier(String uuid) {
		Identifier identifier = new Identifier();
		identifier.setValue(uuid);
		return identifier;
	}

	public static CodeableConcept createCodeableConcept(Concept concept) {
		if (concept == null) {
			return null;
		}

		CodeableConcept codeableConcept = new CodeableConcept();

		for (ConceptMap conceptMap : concept.getConceptMappings()) {
			Coding code = new Coding();
			String display = conceptMap.getConceptReferenceTerm().getName();
			ConceptSourceNameURIPair sourceNameURIPair = FHIRConstants.conceptSourceMap.get(conceptMap
					.getConceptReferenceTerm().getConceptSource().getName().toLowerCase());

			code.setSystem(sourceNameURIPair.getConceptSourceURI());
			code.setCode(conceptMap.getConceptReferenceTerm().getCode());
			code.setDisplay(display);
			codeableConcept.addCoding(code);
		}

		codeableConcept.setText(concept.getDisplayString());
		return codeableConcept;
	}

	public static Concept getConceptByCodeableConcept(CodeableConcept codeableConcept) {
		if (codeableConcept == null) {
			return null;
		}
		Concept result = null;
		for (Coding coding : codeableConcept.getCoding()) {
			String code = coding.getCode();
			String sourceName = FHIRConstants.conceptSourceURINameMap.get(coding.getSystem());
			List<Concept> concepts = Context.getConceptService().getConceptsByMapping(code, sourceName);
			if (!concepts.isEmpty()) {
				result = concepts.get(0);
				break;
			}
		}
		return result;
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

	public static Concept getImagingStudySeriesConcept() {
		return getConceptByConceptId("fhir.imagingstudy.series");
	}

	public static Concept getImagingStudySeriesInstanceConcept() {
		return getConceptByConceptId("fhir.imagingstudy.series.instance");
	}

	public static Concept getDiagnosticReportPresentedFormConcept() {
		return getConceptByConceptId("fhir.diagnosticreport.presentedform");
	}

	public static Concept getImagingStudySeriesInstanceContentConcept() {
		return getConceptByConceptId("fhir.imagingstudy.series.instance.content");
	}

	public static String getDiagnosticReportRadiologyBaseServerURL() {
		return Context.getAdministrationService().getGlobalProperty("fhir.diagnosticreport.radiology.server");
	}

	public static void checkGeneratorErrorList(List<String> errors) {
		if (!errors.isEmpty()) {
			StringBuilder errorMessage = new StringBuilder("The request cannot be processed due to the following issues \n");
			for (int i = 0; i < errors.size(); i++) {
				errorMessage.append(i + 1)
						.append(" : ")
						.append(errors.get(i))
						.append("\n");
			}
			throw new UnprocessableEntityException(errorMessage.toString());
		}
	}

	/**
	 * Get concept from code
	 *
	 * @param codeableConcept codeable concept
	 * @param errors          error list
	 * @return OpenMRS concept
	 */
	public static Concept getConceptFromCode(CodeableConcept codeableConcept, List<String> errors) {
		String conceptCode;
		String system;
		Concept concept = null;
		List<Coding> dts = codeableConcept.getCoding();

		for (Coding coding : dts) {
			conceptCode = coding.getCode();
			system = coding.getSystem();
			if (FHIRConstants.OPENMRS_URI.equals(system)) {
				concept = Context.getConceptService().getConceptByUuid(conceptCode);
			} else {
				String systemName = FHIRConstants.conceptSourceURINameMap.get(system);
				if (systemName != null && !systemName.isEmpty()) {
					concept = Context.getConceptService().getConceptByMapping(conceptCode, systemName);
				}
			}
			if (concept != null) {
				break;
			}
		}
		if (concept == null) {
			errors.add("No matching concept found for the given codings");
			return null;
		} else {
			return concept;
		}
	}

	private static Concept getConceptByConceptId(String globalPropertyName) {
		String globalProperty = Context.getAdministrationService().getGlobalProperty(globalPropertyName);
		Concept concept = Context.getConceptService().getConcept(Integer.parseInt(globalProperty));
		if (concept == null) {
			throw new IllegalStateException("Configuration required: " + globalPropertyName);
		}
		return concept;
	}

	public static String extractUuid(String uuid) {
		return uuid.contains("/") ? uuid.substring(uuid.indexOf("/") + 1) : uuid;
	}
}
