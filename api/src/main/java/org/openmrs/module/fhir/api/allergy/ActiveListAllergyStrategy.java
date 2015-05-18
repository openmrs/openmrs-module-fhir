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
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.activelist.ActiveListItem;
import org.openmrs.activelist.ActiveListType;
import org.openmrs.activelist.Allergy;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.FHIRAllergyIntoleranceUtil;

import java.util.ArrayList;
import java.util.List;

public class ActiveListAllergyStrategy implements GenericAllergyStrategy {

	public AllergyIntolerance getAllergyById(String uuid) {
		ActiveListItem allergy = Context.getActiveListService().getActiveListItemByUuid(uuid);
		if (allergy == null || allergy.isVoided()) {
			return null;
		}
		return FHIRAllergyIntoleranceUtil.generateAllergyTolerance((Allergy) allergy);
	}

	public List<AllergyIntolerance> searchAllergyById(String uuid) {
		ActiveListItem allergy = Context.getActiveListService().getActiveListItemByUuid(uuid);
		List<AllergyIntolerance> allergies = new ArrayList<AllergyIntolerance>();
		if (allergy != null && !allergy.isVoided()) {
			allergies.add(FHIRAllergyIntoleranceUtil.generateAllergyTolerance((Allergy) allergy));
		}
		return allergies;
	}

	public List<AllergyIntolerance> searchAllergyByName(String name) {
		return null;
	}

	public List<AllergyIntolerance> searchAllergiesByPatientIdentifier(String identifier) {
		org.openmrs.api.PatientService patientService = Context.getPatientService();
		List<AllergyIntolerance> allergies = new ArrayList<AllergyIntolerance>();
		List<PatientIdentifierType> allPatientIdentifierTypes = patientService.getAllPatientIdentifierTypes();
		List<org.openmrs.Patient> patientList = patientService.getPatients(null, identifier, allPatientIdentifierTypes,
				true);
		if (patientList != null && !patientList.isEmpty()) {
			List<Allergy> omrsAllergies = Context.getActiveListService().getActiveListItems(Allergy.class, patientList.get(
					0), new ActiveListType(1));
			for (Allergy allergy : omrsAllergies) {
				allergies.add(FHIRAllergyIntoleranceUtil.generateAllergyTolerance(allergy));
			}
		}
		return allergies;
	}

	public List<AllergyIntolerance> searchAllergiesByPatientName(String name) {
		org.openmrs.api.PatientService patientService = Context.getPatientService();
		List<org.openmrs.Patient> patientList = patientService.getPatients(name, null, null, true);
		List<AllergyIntolerance> allergies = new ArrayList<AllergyIntolerance>();
		for (Patient patient : patientList) {
			List<Allergy> omrsAllergies = Context.getActiveListService().getActiveListItems(Allergy.class, patient, new
					ActiveListType(1));
			for (Allergy allergy : omrsAllergies) {
				allergies.add(FHIRAllergyIntoleranceUtil.generateAllergyTolerance(allergy));
			}
		}
		return allergies;
	}

	@Override
	public List<AllergyIntolerance> searchAllergiesByPersonId(String uuid) {
		org.openmrs.api.PatientService patientService = Context.getPatientService();
		Patient patient = patientService.getPatientByUuid(uuid);
		List<AllergyIntolerance> allergies = new ArrayList<AllergyIntolerance>();
		if(patient != null) {
			List<Allergy> omrsAllergies = Context.getActiveListService().getActiveListItems(Allergy.class, patient, new
					ActiveListType(1));
			for (Allergy allergy : omrsAllergies) {
				allergies.add(FHIRAllergyIntoleranceUtil.generateAllergyTolerance(allergy));
			}
		}
		return allergies;
	}
}
