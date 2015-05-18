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
package org.openmrs.module.fhir.api.allergy;

import ca.uhn.fhir.model.dstu2.resource.AllergyIntolerance;
import ca.uhn.fhir.rest.server.exceptions.NotImplementedOperationException;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.allergyapi.Allergy;
import org.openmrs.module.allergyapi.api.PatientService;
import org.openmrs.module.fhir.api.util.FHIRAllergyIntoleranceAllergyAPIUtil;

import java.util.ArrayList;
import java.util.List;

public class AllergyApiModuleAllergyStrategy implements GenericAllergyStrategy {
	
	@Override
	public AllergyIntolerance getAllergyById(String uuid) {
		throw new NotImplementedOperationException("Allergy API module not supported for get allergies by Id ");
	}

	@Override
	public List<AllergyIntolerance> searchAllergyById(String uuid) {
		throw new NotImplementedOperationException("Allergy API module not supported for search allergies by Id ");
	}

	@Override
	public List<AllergyIntolerance> searchAllergyByName(String name) {
		return null;
	}

	@Override
	public List<AllergyIntolerance> searchAllergiesByPatientIdentifier(String identifier) {
		org.openmrs.api.PatientService patientService = Context.getPatientService();
		PatientService allergyService = Context.getService(PatientService.class);
		List<AllergyIntolerance> allergies = new ArrayList<AllergyIntolerance>();
		List<PatientIdentifierType> allPatientIdentifierTypes = patientService.getAllPatientIdentifierTypes();
		List<org.openmrs.Patient> patientList = patientService.getPatients(null, identifier, allPatientIdentifierTypes,
				true);
		if (patientList != null && !patientList.isEmpty()) {
			List<org.openmrs.module.allergyapi.Allergy> omrsAllergies;
			for (Patient patient : patientList) {
				omrsAllergies = allergyService.getAllergies(patient);
				for (org.openmrs.module.allergyapi.Allergy allergy : omrsAllergies) {
					allergies.add(FHIRAllergyIntoleranceAllergyAPIUtil.generateAllergyTolerance(allergy));
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
		List<AllergyIntolerance> allergies = new ArrayList<AllergyIntolerance>();
		for (Patient patient : patientList) {
			List<org.openmrs.module.allergyapi.Allergy> omrsAllergies = allergyService.getAllergies(patient);
			for (Allergy allergy : omrsAllergies) {
				allergies.add(FHIRAllergyIntoleranceAllergyAPIUtil.generateAllergyTolerance(allergy));
			}
		}
		return allergies;
	}

	@Override
	public List<AllergyIntolerance> searchAllergiesByPersonId(String uuid) {
		org.openmrs.api.PatientService patientService = Context.getPatientService();
		PatientService allergyService = Context.getService(PatientService.class);
		Patient patient = patientService.getPatientByUuid(uuid);
		List<AllergyIntolerance> allergies = new ArrayList<AllergyIntolerance>();
		if (patient != null) {
			List<org.openmrs.module.allergyapi.Allergy> omrsAllergies = allergyService.getAllergies(patient);
			for (Allergy allergy : omrsAllergies) {
				allergies.add(FHIRAllergyIntoleranceAllergyAPIUtil.generateAllergyTolerance(allergy));
			}
		}
		return allergies;
	}
}
