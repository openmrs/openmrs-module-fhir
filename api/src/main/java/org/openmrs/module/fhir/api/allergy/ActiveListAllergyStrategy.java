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
import ca.uhn.fhir.rest.param.ReferenceParam;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
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
		if(allergy == null) {
			return null;
		}
		return FHIRAllergyIntoleranceUtil.generateAllergyTolerance((Allergy)allergy);
	}

	public List<AllergyIntolerance> searchAllergyById(String uuid) {
		ActiveListItem allergy = Context.getActiveListService().getActiveListItemByUuid(uuid);
		List<AllergyIntolerance> allergies = new ArrayList<AllergyIntolerance>();
		if(allergy != null) {
			allergies.add(FHIRAllergyIntoleranceUtil.generateAllergyTolerance((Allergy)allergy));
		}
		return allergies;
	}

	public List<AllergyIntolerance> searchAllergyByName(String name) {
		return null;
	}

    public List<AllergyIntolerance> searchAllergiesByPatientIdentifier(String identifier) {
        org.openmrs.api.PatientService patientService = Context.getPatientService();
        List<PatientIdentifierType> allPatientIdentifierTypes = patientService.getAllPatientIdentifierTypes();
        List<org.openmrs.Patient> patientList = patientService.getPatients(null, identifier, allPatientIdentifierTypes,
                true);
        List<Allergy> omrsAllergies = Context.getActiveListService().getActiveListItems(Allergy.class, patientList.get(0), new ActiveListType(1));
        List<AllergyIntolerance> allergies = new ArrayList<AllergyIntolerance>();
        for(Allergy allergy : omrsAllergies) {
            allergies.add(FHIRAllergyIntoleranceUtil.generateAllergyTolerance(allergy));
        }
        return allergies;
    }
}
