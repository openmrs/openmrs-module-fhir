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
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Condition;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Reference;
import org.openmrs.Patient;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.ConditionService;
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
	 * @see org.openmrs.module.fhir.api.helper.ConditionHelper#getCondition(java.lang.String)
	 */
	@Override
	public Condition getCondition(String uuid) {
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

		openMrsCondition.setAdditionalDetail(condition.getNoteFirstRep().getText());
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

		// set clinical status
		fhirCondition.setClinicalStatus(
				FHIRConditionUtil2_2.mapOpenmrsStatusToFHIRClinicalStatus(condition.getClinicalStatus()));

		return fhirCondition;

	}
}
