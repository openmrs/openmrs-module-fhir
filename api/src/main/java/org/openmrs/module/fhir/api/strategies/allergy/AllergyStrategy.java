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
import org.openmrs.Allergy;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.FHIRAllergyIntoleranceUtil;

import java.util.ArrayList;
import java.util.List;

public class AllergyStrategy implements GenericAllergyStrategy {

	@Override
	public AllergyIntolerance getAllergyById(String uuid) {
		PatientService patientService = Context.getPatientService();

		org.openmrs.Allergy openMRSAllergy = patientService.getAllergyByUuid(uuid);

		return openMRSAllergy != null ?
				FHIRAllergyIntoleranceUtil.generateAllergyIntolerance(openMRSAllergy)
				: null;
	}

	@Override
	public List<AllergyIntolerance> searchAllergyById(String uuid) {
		List<AllergyIntolerance> list = new ArrayList<>();
		PatientService patientService = Context.getPatientService();

		org.openmrs.Allergy openMRSAllergy = patientService.getAllergyByUuid(uuid);

		AllergyIntolerance allergyIntolerance = FHIRAllergyIntoleranceUtil.generateAllergyIntolerance(openMRSAllergy);
		if (allergyIntolerance != null) {
			list.add(allergyIntolerance);
		}
		return list;
	}

	@Override
	public List<AllergyIntolerance> searchAllergiesByPatientIdentifier(String identifier) {
		org.openmrs.api.PatientService patientService = Context.getPatientService();
		PatientService allergyService = Context.getService(PatientService.class);
		List<AllergyIntolerance> allergies = new ArrayList<>();
		List<PatientIdentifierType> allPatientIdentifierTypes = patientService.getAllPatientIdentifierTypes();
		List<org.openmrs.Patient> patientList = patientService.getPatients(identifier, null, allPatientIdentifierTypes,
				true);
		if (patientList != null && !patientList.isEmpty()) {
			for (Patient patient : patientList) {
				for (org.openmrs.Allergy allergy : allergyService.getAllergies(patient)) {
					allergies.add(FHIRAllergyIntoleranceUtil.generateAllergyIntolerance(allergy));
				}
			}
		}
		return allergies;
	}

	@Override
	public List<AllergyIntolerance> searchAllergiesByPatientName(String name) {
		org.openmrs.api.PatientService patientService = Context.getPatientService();
		PatientService allergyService = Context.getService(PatientService.class);
		List<org.openmrs.Patient> patientList = patientService.getPatients(name, null, null, true);
		List<AllergyIntolerance> allergies = new ArrayList<>();
		for (Patient patient : patientList) {
			for (Allergy allergy : allergyService.getAllergies(patient)) {
				allergies.add(FHIRAllergyIntoleranceUtil.generateAllergyIntolerance(allergy));
			}
		}
		return allergies;
	}

	@Override
	public List<AllergyIntolerance> searchAllergiesByPersonId(String uuid) {
		PatientService patientService = Context.getPatientService();
		Patient patient = patientService.getPatientByUuid(uuid);
		List<AllergyIntolerance> allergies = new ArrayList<>();
		if (patient != null) {
			for (Allergy allergy : patientService.getAllergies(patient)) {
				allergies.add(FHIRAllergyIntoleranceUtil.generateAllergyIntolerance(allergy));
			}
		}
		return allergies;
	}
}
