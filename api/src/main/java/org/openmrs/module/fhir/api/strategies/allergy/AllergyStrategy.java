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

import ca.uhn.fhir.rest.server.exceptions.MethodNotAllowedException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.dstu3.model.AllergyIntolerance;
import org.openmrs.Allergy;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.APIException;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.FHIRAllergyIntoleranceUtil;
import org.openmrs.module.fhir.api.util.FHIRConstants;
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
		org.openmrs.Allergy openMRSAllergy = patientService.getAllergyByUuid(uuid);

		return openMRSAllergy != null ?
				FHIRAllergyIntoleranceUtil.generateAllergyIntolerance(openMRSAllergy)
				: null;
	}

	@Override
	public List<AllergyIntolerance> searchAllergyById(String uuid) {
		List<AllergyIntolerance> list = new ArrayList<>();
		org.openmrs.Allergy openMRSAllergy = patientService.getAllergyByUuid(uuid);

		AllergyIntolerance allergyIntolerance = FHIRAllergyIntoleranceUtil.generateAllergyIntolerance(openMRSAllergy);
		if (allergyIntolerance != null) {
			list.add(allergyIntolerance);
		}
		return list;
	}

	@Override
	public List<AllergyIntolerance> searchAllergiesByPatientIdentifier(String identifier) {
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
		Patient patient = patientService.getPatientByUuid(uuid);
		List<AllergyIntolerance> allergies = new ArrayList<>();
		if (patient != null) {
			for (Allergy allergy : patientService.getAllergies(patient)) {
				allergies.add(FHIRAllergyIntoleranceUtil.generateAllergyIntolerance(allergy));
			}
		}
		return allergies;
	}

	@Override
	public AllergyIntolerance createAllergy(AllergyIntolerance allergyIntolerance) {
		Allergy allergy = FHIRAllergyIntoleranceUtil.generateAllergy(allergyIntolerance);
		return FHIRAllergyIntoleranceUtil.generateAllergyIntolerance(saveAllergy(allergy));
	}

	@Override
	public AllergyIntolerance updateAllergy(AllergyIntolerance allergyIntolerance, String uuid) {
		Allergy newAllergy = FHIRAllergyIntoleranceUtil.generateAllergy(allergyIntolerance);

		Allergy allergy = patientService.getAllergyByUuid(uuid);
		if (allergy != null) {
			allergy = FHIRAllergyIntoleranceUtil.updateAllergyAttributes(allergy, newAllergy);
			allergy = saveAllergy(allergy);
		} else {
			newAllergy.setUuid(uuid);
			allergy = saveAllergy(newAllergy);
		}

		return FHIRAllergyIntoleranceUtil.generateAllergyIntolerance(allergy);
	}

	@Override
	public void deleteAllergy(String uuid) {
		Allergy allergy = patientService.getAllergyByUuid(uuid);

		if (allergy == null) {
			throw new ResourceNotFoundException(String.format("Allergy with id '%s' not found", uuid));
		} else {
			try {
				patientService.removeAllergy(allergy, FHIRConstants.FHIR_RETIRED_MESSAGE);
			} catch (APIException apie) {
				throw new MethodNotAllowedException(String.format("OpenMRS has failed to retire allergy'%s': %s", uuid,
						apie.getMessage()));
			}
		}
	}

	private Allergy saveAllergy(Allergy allergy) {
		patientService.saveAllergy(allergy);
		//retrieve is necessary as saveAllergy(...) returns no value
		return patientService.getAllergyByUuid(allergy.getUuid());
	}
}
