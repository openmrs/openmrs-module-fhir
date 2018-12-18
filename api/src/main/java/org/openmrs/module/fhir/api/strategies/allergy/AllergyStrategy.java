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
package org.openmrs.module.fhir.api.strategies.allergy;

import org.hl7.fhir.dstu3.model.AllergyIntolerance;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.helper.AllergyHelper;
import org.openmrs.module.fhir.api.util.ContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("DefaultAllergyStrategy")
public class AllergyStrategy implements GenericAllergyStrategy {

	@Autowired
	private PatientService patientService;

	@Override
	public AllergyIntolerance getAllergyById(String uuid) {
		return ContextUtil.getAllergyHelper().getAllergyIntolerance(uuid);
	}

	@Override
	public List<AllergyIntolerance> searchAllergyById(String uuid) {
		List<AllergyIntolerance> list = new ArrayList<>();

		AllergyIntolerance allergyIntolerance = getAllergyById(uuid);
		if (allergyIntolerance != null) {
			list.add(allergyIntolerance);
		}
		return list;
	}

	@Override
	public List<AllergyIntolerance> searchAllergiesByPatientIdentifier(String identifier) {
		List<AllergyIntolerance> allergies = new ArrayList<>();
		List<PatientIdentifierType> allPatientIdentifierTypes = patientService.getAllPatientIdentifierTypes();
		List<org.openmrs.Patient> patientList = patientService.getPatients(identifier, null, allPatientIdentifierTypes,
				true);
		if (patientList != null && !patientList.isEmpty()) {
			for (Patient patient : patientList) {
				allergies.addAll(ContextUtil.getAllergyHelper().getAllergyIntoleranceByPatient(patient));
			}
		}
		return allergies;
	}

	@Override
	public List<AllergyIntolerance> searchAllergiesByPatientName(String name) {
		PatientService allergyService = Context.getService(PatientService.class);
		List<org.openmrs.Patient> patientList = patientService.getPatients(name, null, null, true);
		List<AllergyIntolerance> allergies = new ArrayList<>();
		for (Patient patient : patientList) {
			allergies.addAll(ContextUtil.getAllergyHelper().getAllergyIntoleranceByPatient(patient));
		}
		return allergies;
	}

	@Override
	public List<AllergyIntolerance> searchAllergiesByPersonId(String uuid) {
		Patient patient = patientService.getPatientByUuid(uuid);
		List<AllergyIntolerance> allergies = new ArrayList<>();
		if (patient != null) {
			allergies.addAll(ContextUtil.getAllergyHelper().getAllergyIntoleranceByPatient(patient));
		}
		return allergies;
	}

	@Override
	public AllergyIntolerance createAllergy(AllergyIntolerance allergyIntolerance) {
		return ContextUtil.getAllergyHelper().createAllergy(allergyIntolerance);
	}

	@Override
	public AllergyIntolerance updateAllergy(AllergyIntolerance allergyIntolerance, String uuid) {
		return ContextUtil.getAllergyHelper().updateAllergy(allergyIntolerance, uuid);
	}

	@Override
	public void deleteAllergy(String uuid) {
		ContextUtil.getAllergyHelper().deleteAllergy(uuid);
	}
}
