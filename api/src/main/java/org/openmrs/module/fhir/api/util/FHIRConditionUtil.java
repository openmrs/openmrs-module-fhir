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

import org.apache.commons.lang.StringUtils;
import org.hl7.fhir.dstu3.model.Annotation;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Reference;
import org.openmrs.Concept;
import org.openmrs.ConceptMap;
import org.openmrs.Condition;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.api.ConditionService;
import org.openmrs.api.context.Context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class FHIRConditionUtil {

	/**
	 * Generates FHIR Condition from openmrs condition
	 *
	 * @param condition Openmrs condition
	 * @return FHIR Condition
	 */
	public static org.hl7.fhir.dstu3.model.Condition generateFHIRCondition(Condition condition) {
		org.hl7.fhir.dstu3.model.Condition fhirCondition = new org.hl7.fhir.dstu3.model.Condition();
		IdType id = new IdType();
		id.setValue(condition.getUuid());
		fhirCondition.setId(id);

		//Set patient reference
		Reference patient = FHIRUtils.buildPatientOrPersonResourceReference(condition.getPatient());
		fhirCondition.setSubject(patient);

		//Set on set date
		fhirCondition.setAssertedDate(condition.getDateChanged());

		//Set condition concept
		if (condition.getConcept() != null) {
			CodeableConcept conceptDt = fhirCondition.getCode();
			Collection<ConceptMap> mappings = condition.getConcept().getConceptMappings();
			List<Coding> dts = conceptDt.getCoding();

			//Set concept codings
			if (mappings != null && !mappings.isEmpty()) {
				for (ConceptMap map : mappings) {
					if (map.getConceptReferenceTerm() != null) {
						dts.add(FHIRUtils.createCoding(map));
					}
				}
			}

			//Setting default openmrs concept
			if (condition.getConcept().getName() != null) {
				dts.add(new Coding().setCode(condition.getConcept().getUuid()).setDisplay(
						condition.getConcept().getName().getName()).setSystem(FHIRConstants.OPENMRS_URI));
			} else {
				dts.add(new Coding().setCode(condition.getConcept().getUuid()).setSystem(
						FHIRConstants.OPENMRS_URI));
			}
			conceptDt.setCoding(dts);
			fhirCondition.setCode(conceptDt);
		}
		fhirCondition.setClinicalStatus(
				MapOpenmrsStatusToFHIRClinicalStatus(condition));

		return fhirCondition;
	}

	/**
	 * Generates Openmrs Condition from FHIR Condition
	 *
	 * @param condition FHIR condition
	 * @return Openmrs Condition
	 * @since 1.20.0
	 */
	public static org.openmrs.Condition generateOpenMRSCondition(org.hl7.fhir.dstu3.model.Condition condition) {
		org.openmrs.Condition openMrsCondition;
		List<String> errors = new ArrayList<>();

		openMrsCondition = Context.getService(ConditionService.class).getConditionByUuid(condition.getId());
		if (openMrsCondition == null) {
			// No condition found to be updated
			openMrsCondition = new org.openmrs.Condition();
		}
		BaseOpenMRSDataUtil.readBaseExtensionFields(openMrsCondition, condition);

		// set patient
		if (condition.getSubject() != null) {
			String patientRef = condition.getSubject().getReference();
			Patient openMrsPatient = Context.getPatientService().getPatientByUuid(patientRef);
			openMrsCondition.setPatient(openMrsPatient);
		}

		// set condition Uuid
		openMrsCondition.setUuid(condition.getContext().getReference());

		openMrsCondition.setAdditionalDetail(condition.getNoteFirstRep().getText());
		openMrsCondition.setDateChanged(condition.getAssertedDate());

		// set Condition concept
		openMrsCondition.setConcept(FHIRUtils.getConceptFromCode(condition.getCode(), errors));

		// set condition status
		openMrsCondition.setStatus(
				FHIRConditionUtil.generateConditionClinicalStatusFromFHIRCondition(condition));

		openMrsCondition.setCreator(Context.getAuthenticatedUser());
		FHIRUtils.checkGeneratorErrorList(errors);

		return openMrsCondition;
	}

	/**
	 * Generates condition clinical status from FHIR condition
	 * Mapping for clinical status
	 * FHIR            OPENMRS
	 * active          active
	 * recurrence      active
	 * inactive        inactive
	 * remission       inactive
	 * resolved        history_of
	 *
	 * @param condition FHIR condition
	 * @return Openmrs Condition Status
	 */
	private static org.openmrs.Condition.Status generateConditionClinicalStatusFromFHIRCondition(
			org.hl7.fhir.dstu3.model.Condition condition) {
		switch (condition.getClinicalStatus().getDisplay().toLowerCase()) {
			case "active":
			case "recurrence":
				return org.openmrs.Condition.Status.CONFIRMED;
			case "inactive":
			case "remission":
				return org.openmrs.Condition.Status.PRESUMED;
			case "resolved":
				return org.openmrs.Condition.Status.HISTORY_OF;
			default:
				return org.openmrs.Condition.Status.PRESUMED;
		}
	}

	/**
	 * Maps openmrs Condition status to FHIR Clinical status
	 *
	 * @param status openmrs condition status
	 * @return FHIR Condition Clinical Status
	 */
	private static org.hl7.fhir.dstu3.model.Condition.ConditionClinicalStatus MapOpenmrsStatusToFHIRClinicalStatus(
			org.openmrs.Condition status) {
		switch (status.getStatus()) {
			case CONFIRMED:
				return org.hl7.fhir.dstu3.model.Condition.ConditionClinicalStatus.ACTIVE;
			case PRESUMED:
				return org.hl7.fhir.dstu3.model.Condition.ConditionClinicalStatus.INACTIVE;
			case HISTORY_OF:
				return org.hl7.fhir.dstu3.model.Condition.ConditionClinicalStatus.RESOLVED;
			default:
				return org.hl7.fhir.dstu3.model.Condition.ConditionClinicalStatus.NULL;
		}
	}

	/**
	 * Generate FHIR condition from openmrs observation
	 *
	 * @param openMrsObs openmrs Observation
	 * @return Condition
	 * @since 1.20.0
	 */
	public static org.hl7.fhir.dstu3.model.Condition generateFHIRConditionFromOpenMRSObs(Obs openMrsObs) {

		org.hl7.fhir.dstu3.model.Condition fhirCondition = new org.hl7.fhir.dstu3.model.Condition();
		IdType id = new IdType();
		id.setValue(openMrsObs.getUuid());
		fhirCondition.setId(id);

		if (openMrsObs.getPerson().isPatient()) {
			Reference patient = FHIRUtils.buildPatientOrPersonResourceReference(openMrsObs.getPerson());
			fhirCondition.setSubject(patient);
			//Set Encounter
			if (openMrsObs.getEncounter() != null) {
				fhirCondition.setContext(buildPatientReference(openMrsObs.getEncounter()));
			}
		}

		//Set Asserter
		fhirCondition.setAssertedDate(openMrsObs.getDateCreated());

		if (openMrsObs.getConcept() != null) {
			CodeableConcept conceptDt = fhirCondition.getCode();
			Collection<ConceptMap> mappings = openMrsObs.getConcept().getConceptMappings();
			List<Coding> dts = conceptDt.getCoding();
			if (mappings != null && !mappings.isEmpty()) {
				for (ConceptMap map : mappings) {
					if (map.getConceptReferenceTerm() != null) {
						dts.add(FHIRUtils.createCoding(map));
					}
				}
			}
			if (openMrsObs.getConcept().getName() != null) {
				dts.add(new Coding().setCode(openMrsObs.getConcept().getUuid()).setDisplay(
						openMrsObs.getConcept().getName().getName()).setSystem(FHIRConstants.OPENMRS_URI));
			} else {
				dts.add(new Coding().setCode(openMrsObs.getConcept().getUuid()).setSystem(
						FHIRConstants.OPENMRS_URI));
			}
			conceptDt.setCoding(dts);
			fhirCondition.setCode(conceptDt);
		}

		if (!StringUtils.isEmpty(openMrsObs.getComment())) {
			List<Annotation> annotations = new ArrayList<Annotation>();
			Annotation annotation = new Annotation();
			annotation.setText(openMrsObs.getComment());
			fhirCondition.setNote(annotations);
		}

		return fhirCondition;
	}

	/**
	 * Generate Openmrs Obs from FHIR Condition
	 *
	 * @param condition FHIR condition
	 * @return Obs
	 * @since 1.20.0
	 */
	public static Obs generateOpenMrsObsFromFHIRCondition(org.hl7.fhir.dstu3.model.Condition condition) {
		Obs obs = new Obs();
		List<String> errors = new ArrayList<>();
		BaseOpenMRSDataUtil.readBaseExtensionFields(obs, condition);

		if (StringUtils.isNotBlank(condition.getId())) {
			obs.setUuid(FHIRUtils.extractUuid(condition.getId()));
		}

		// set concept
		if (condition.getCode().getText() != null) {
			Concept concept = Context.getConceptService().getConceptByName(condition.getCode().getText());
			obs.setConcept(concept);
		}

		// set person
		if (condition.getSubject() != null) {
			Reference subjectRef = condition.getSubject();
			String patientUuid = subjectRef.getId();
			Person person = Context.getPersonService().getPersonByUuid(patientUuid);
			if (person == null) {
				errors.add("There is no person for the given uuid");
			} else {
				obs.setPerson(person);
			}
		} else {
			errors.add("Subject cannot be empty");
		}

		Date date = condition.getAssertedDate() == null ? new Date() : condition.getAssertedDate();
		obs.setDateCreated(date);
		obs.setObsDatetime(date);
		obs.setValueText("yes");

		// set comment
		List<String> notes = new ArrayList<>();
		for (Annotation note : condition.getNote()) {
			notes.add(note.getText());
		}
		obs.setComment(StringUtils.join(notes, ","));
		FHIRUtils.checkGeneratorErrorList(errors);

		return obs;
	}

	/**
	 * Build and set patient reference
	 *
	 * @param omrsEncounter openmrs Encounter
	 * @return Reference for the patient
	 */
	private static Reference buildPatientReference(org.openmrs.Encounter omrsEncounter) {
		Reference patientReference = new Reference();
		PersonName name = omrsEncounter.getPatient().getPersonName();

		String patientUri = FHIRConstants.PATIENT + "/" + omrsEncounter.getPatient().getUuid();
		patientReference.setReference(patientUri);
		String nameDisplay = name.getGivenName()
				+ " "
				+ name.getFamilyName()
				+ "("
				+ FHIRConstants.IDENTIFIER
				+ ":"
				+ omrsEncounter.getPatient().getPatientIdentifier().getIdentifier()
				+ ")";
		patientReference.setDisplay(nameDisplay);
		return patientReference;
	}

}
