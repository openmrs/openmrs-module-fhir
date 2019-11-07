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
package org.openmrs.module.fhir.helper;

import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import org.apache.commons.lang.StringUtils;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Condition;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Reference;
import org.openmrs.Patient;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.ConditionService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.helper.ConditionHelper;
import org.openmrs.module.fhir.api.util.BaseOpenMRSDataUtil;
import org.openmrs.module.fhir.api.util.FHIRUtils;
import org.openmrs.module.fhir.util.FHIRConditionUtil2_2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component(value = "fhir.ConditionHelper")
@OpenmrsProfile(openmrsPlatformVersion = "2.2.* - 2.4.*")
public class ConditionHelperImpl2_2 implements ConditionHelper {

	/**
	 * @see org.openmrs.module.fhir.api.helper.ConditionHelper#getConditionByUuid(java.lang.String)
	 */
	@Override
	public Condition getConditionByUuid(String uuid) {
		org.openmrs.Condition condition = Context.getService(ConditionService.class).getConditionByUuid(uuid);
		if (condition == null) {
			return null;
		}
		return this.generateFHIRCondition(condition);
	}

	/**
	 * @see org.openmrs.module.fhir.api.helper.ConditionHelper#createCondition(org.hl7.fhir.dstu3.model.Condition)
	 */
	@Override
	public Condition createCondition(Condition condition) {
		if (condition.isEmpty()) {
			throw new UnprocessableEntityException("condition resource cannot be null");
		}
		if (condition.getSubject().isEmpty()) {
			throw new UnprocessableEntityException("condition subject cannot be null");
		}
		org.openmrs.Condition openMrsCondition = Context.getService(org.openmrs.api.ConditionService.class)
				.saveCondition(generateOpenMrsCondition(condition));

		return generateFHIRCondition(openMrsCondition);
	}

	/**
	 * @see org.openmrs.module.fhir.api.helper.ConditionHelper#updateFHIRCondition(org.hl7.fhir.dstu3.model.Condition)
	 */
	@Override
	public Condition updateFHIRCondition(Condition fhirCondition) {
		if (fhirCondition.getId() != null) {
			ConditionService conditionService = Context.getService(ConditionService.class);
			org.openmrs.Condition openmrsCondition = conditionService.getConditionByUuid(fhirCondition.getId());
			return openmrsCondition != null ?
					generateFHIRCondition(updateOpenmrsCondition(fhirCondition, openmrsCondition)) :
					createCondition(fhirCondition);
		} else {
			throw new UnprocessableEntityException("condition Id cannot be null");
		}

	}

	/**
	 * Update openmrs condition
	 *
	 * @param fhirCondition    FHIR condition to be updated
	 * @param openmrsCondition Openmrs condition to be updated
	 * @return Updated Openmrs condition
	 */
	private org.openmrs.Condition updateOpenmrsCondition(Condition fhirCondition, org.openmrs.Condition openmrsCondition) {
		ConditionService conditionService = Context.getService(ConditionService.class);
		org.openmrs.Condition omrsCondition = this.generateOpenMrsCondition(fhirCondition);

		if (omrsCondition.getClinicalStatus() != null) {
			openmrsCondition.setClinicalStatus(omrsCondition.getClinicalStatus());
		}
		if (omrsCondition.getVerificationStatus() != null) {
			openmrsCondition.setVerificationStatus(omrsCondition.getVerificationStatus());
		}
		if (omrsCondition.getAdditionalDetail() != null) {
			openmrsCondition.setAdditionalDetail(omrsCondition.getAdditionalDetail());
		}
		if (omrsCondition.getOnsetDate() != null) {
			openmrsCondition.setOnsetDate(omrsCondition.getOnsetDate());
		}

		return conditionService.saveCondition(openmrsCondition);
	}

	/**
	 * @see org.openmrs.module.fhir.api.helper.ConditionHelper#getConditionsByPatientUuid(java.lang.String)
	 */
	@Override
	public List<Condition> getConditionsByPatientUuid(String patientUuid) {
		List<Condition> fhirConditions = new ArrayList<>();
		PatientService patientService = Context.getPatientService();
		ConditionService conditionService = Context.getService(org.openmrs.api.ConditionService.class);

		if (StringUtils.isNotBlank(patientUuid)) {
			conditionService.getActiveConditions(
					patientService.getPatientByUuid(patientUuid))
					.forEach(condition -> fhirConditions.add(generateFHIRCondition(condition)));
		}

		return fhirConditions;
	}

	/**
	 * @see org.openmrs.module.fhir.api.helper.ConditionHelper#generateOpenMrsCondition(org.hl7.fhir.dstu3.model.Condition)
	 */
	@Override
	public org.openmrs.Condition generateOpenMrsCondition(Condition condition) {
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
			String patientUuid = FHIRUtils.extractUuid(patientRef);
			Patient openMrsPatient = Context.getPatientService().getPatientByUuid(patientUuid);
			openMrsCondition.setPatient(openMrsPatient);
		} else {
			errors.add("condition subject cannot be null");
		}

		// set condition Uuid
		openMrsCondition.setUuid(condition.getContext().getReference());

		// set additional details
		if (!condition.getNote().isEmpty()) {
			openMrsCondition.setAdditionalDetail(FHIRConditionUtil2_2.getAdditionalDetail(condition.getNote()));
		}
		// set verification status
		if (condition.getVerificationStatus() != null) {
			openMrsCondition.setVerificationStatus(FHIRConditionUtil2_2
					.mapFhirConditionVerificationStatusToOpenmrsConditionVerificationStatus(
							condition.getVerificationStatus()));
		}

		openMrsCondition.setDateChanged(condition.getAssertedDate());
		openMrsCondition.setClinicalStatus(
				FHIRConditionUtil2_2.generateConditionClinicalStatusFromFHIRCondition(condition));
		openMrsCondition.setDateCreated(new Date());

		// set condition
		openMrsCondition.setCondition(FHIRConditionUtil2_2.generateConceptFromFHIRCondition(condition));
		openMrsCondition.setCreator(Context.getAuthenticatedUser());

		FHIRUtils.checkGeneratorErrorList(errors);

		return openMrsCondition;
	}

	/**
	 * @see org.openmrs.module.fhir.api.helper.ConditionHelper#generateFHIRCondition(org.openmrs.Condition)
	 */
	@Override
	public Condition generateFHIRCondition(org.openmrs.Condition condition) {
		org.hl7.fhir.dstu3.model.Condition fhirCondition = new org.hl7.fhir.dstu3.model.Condition();
		IdType id = new IdType();
		id.setValue(condition.getUuid());
		fhirCondition.setId(id);

		//Set patient reference
		Reference patient = FHIRUtils.buildPatientOrPersonResourceReference(condition.getPatient());
		fhirCondition.setSubject(patient);

		//Set on set date
		fhirCondition.setAssertedDate(condition.getDateChanged());

		// set Condition
		if (condition.getCondition().getCoded() == null) {
			fhirCondition.setCode(new CodeableConcept().setText(condition.getCondition().getNonCoded()));
		} else {
			fhirCondition.setCode(
					FHIRUtils.createCodeableConcept(condition.getCondition().getCoded()));
		}

		// set Note
		if (condition.getAdditionalDetail() != null) {
			fhirCondition.setNote(FHIRConditionUtil2_2.getListOfAnnotations(
					condition.getAdditionalDetail()));
		}

		// set verification status
		if (condition.getVerificationStatus() != null) {
			fhirCondition.setVerificationStatus(FHIRConditionUtil2_2
					.mapOpenmrsConditionVerificationStatusToFhirConditionVerificationStatus(
							condition.getVerificationStatus()));
		}

		// set clinical status
		fhirCondition.setClinicalStatus(
				FHIRConditionUtil2_2.mapOpenmrsStatusToFHIRClinicalStatus(
						condition.getClinicalStatus()));

		return fhirCondition;

	}
}
