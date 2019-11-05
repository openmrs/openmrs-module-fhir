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

import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Condition;
import org.openmrs.CodedOrFreeText;
import org.openmrs.ConditionClinicalStatus;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.FHIRConstants;

import java.util.List;

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
}
