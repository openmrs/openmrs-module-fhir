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
package org.openmrs.module.fhir.util;

import org.apache.commons.lang.StringUtils;
import org.hl7.fhir.dstu3.model.Annotation;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Condition;
import org.openmrs.CodedOrFreeText;
import org.openmrs.ConditionClinicalStatus;
import org.openmrs.ConditionVerificationStatus;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.FHIRConstants;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @since 1.20.0
 */
public class FHIRConditionUtil2_2 {

	/**
	 * Generate concept from FHIR Condition
	 *
	 * @param condition FHIR condition
	 * @return codedOrFreeText
	 */
	public static CodedOrFreeText generateConceptFromFHIRCondition(Condition condition) {
		List<Coding> codings = condition.getCode().getCoding();
		CodedOrFreeText codedOrFreeText = new CodedOrFreeText();
		for (Coding coding : codings) {
			Boolean isOpenmrs = coding.getSystem().equals(FHIRConstants.OPENMRS_URI);
			Boolean isCoded = Context.getConceptService().getConceptByName(coding.getDisplay()) != null;
			if (isOpenmrs && isCoded) {
				codedOrFreeText.setCoded(Context.getConceptService().getConcept(coding.getCode()));
			} else {
				codedOrFreeText.setNonCoded(coding.getDisplay());
			}
		}
		return codedOrFreeText;
	}

	/**
	 * Generate condition clinical status from FHIR condition
	 * Mapping for clinical status
	 * FHIR            OPENMRS
	 * active          active
	 * recurrence      active
	 * inactive        inactive
	 * remission       inactive
	 * resolved        history_of
	 *
	 * @param condition FHIR condition
	 * @return Condition Clinical Status
	 */
	public static ConditionClinicalStatus generateConditionClinicalStatusFromFHIRCondition(Condition condition) {
		switch (condition.getClinicalStatus().getDisplay().toLowerCase()) {
			case "active":
			case "recurrence":
				return ConditionClinicalStatus.ACTIVE;
			case "inactive":
			case "remission":
				return ConditionClinicalStatus.INACTIVE;
			case "resolved":
				return ConditionClinicalStatus.HISTORY_OF;
			default:
				return ConditionClinicalStatus.INACTIVE;
		}
	}

	/**
	 * Maps openmrs condition clinical Status to FHIR ClinicalStatus
	 *
	 * @param status condition clinical status
	 * @return ConditionClinicalStatus
	 */
	public static Condition.ConditionClinicalStatus mapOpenmrsStatusToFHIRClinicalStatus(ConditionClinicalStatus status) {
		switch (status) {
			case ACTIVE:
				return Condition.ConditionClinicalStatus.ACTIVE;
			case INACTIVE:
				return Condition.ConditionClinicalStatus.INACTIVE;
			case HISTORY_OF:
				return Condition.ConditionClinicalStatus.RESOLVED;
			default:
				return Condition.ConditionClinicalStatus.NULL;
		}
	}

	/**
	 * Maps openmrs condition verification status to fhir condition verification status
	 *
	 * @param verificationStatus openmrs verification status
	 * @return FHIR ConditionVerificationStatus
	 */
	public static Condition.ConditionVerificationStatus mapOpenmrsConditionVerificationStatusToFhirConditionVerificationStatus(
			ConditionVerificationStatus verificationStatus) {
		switch (verificationStatus) {
			case CONFIRMED:
				return Condition.ConditionVerificationStatus.CONFIRMED;
			case PROVISIONAL:
				return Condition.ConditionVerificationStatus.PROVISIONAL;
			default:
				return Condition.ConditionVerificationStatus.UNKNOWN;
		}
	}

	/**
	 * Maps FHIR Condition verification status to openmrs condition verification status
	 *
	 * @param verificationStatus FHIR ConditionVerificationStatus
	 * @return Openmrs ConditionVerificationStatus
	 */
	public static ConditionVerificationStatus mapFhirConditionVerificationStatusToOpenmrsConditionVerificationStatus(
			Condition.ConditionVerificationStatus verificationStatus) {
		switch (verificationStatus) {
			case CONFIRMED:
				return ConditionVerificationStatus.CONFIRMED;
			default:
				return ConditionVerificationStatus.PROVISIONAL;
		}
	}

	/**
	 * Gets additional detail
	 *
	 * @param annotations List of annotation from FHIR condition
	 * @return Notes String
	 */
	public static String getAdditionalDetail(List<Annotation> annotations) {
		List<String> notes = new ArrayList<>();
		annotations.forEach(annotation -> {
			notes.add(annotation.getText());
		});

		return StringUtils.join(notes, ",");
	}

	/**
	 * Gets a list of annotations from openmrsConditionAdditionalDetail
	 *
	 * @param additionalDetail Openmrs AdditionalDetail
	 * @return Annotation list
	 */
	public static List<Annotation> getListOfAnnotations(String additionalDetail) {
		return Stream.of(additionalDetail)
				.map(FHIRConditionUtil2_2::setAnnotation)
				.collect(Collectors.toList());
	}

	/**
	 * Set annotation
	 *
	 * @param text string text from openmrs additionalDetail
	 * @return Annotation
	 */
	private static Annotation setAnnotation(String text) {
		return new Annotation().setText(text).setTime(new Date());
	}
}
