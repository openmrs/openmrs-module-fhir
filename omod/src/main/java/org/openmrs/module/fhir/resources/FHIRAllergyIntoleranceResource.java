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
package org.openmrs.module.fhir.resources;

import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.dstu3.model.AllergyIntolerance;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.AllergyIntoleranceService;

import java.util.ArrayList;
import java.util.List;

public class FHIRAllergyIntoleranceResource extends Resource {

	public AllergyIntolerance getByUniqueId(IdType id) {
		AllergyIntoleranceService allergyIntoleranceService = Context.getService(AllergyIntoleranceService.class);
		AllergyIntolerance allergyIntolerance = allergyIntoleranceService.getAllergyById(id.getIdPart());
		if (allergyIntolerance == null) {
			throw new ResourceNotFoundException("AllergyIntolerance is not found for the given Id " + id.getIdPart());
		}
		return allergyIntolerance;
	}

	public List<AllergyIntolerance> searchAllergiesById(TokenParam id) {
		return Context.getService(AllergyIntoleranceService.class).searchAllergiesById(id.getValue());
	}

	public List<AllergyIntolerance> searchAllergiesByPatientIdentifier(ReferenceParam identifier) {
		List<AllergyIntolerance> fhirAllergies = new ArrayList<>();
		if (Patient.SP_IDENTIFIER.equals(identifier.getChain())) {
			fhirAllergies = Context.getService(AllergyIntoleranceService.class).searchAllergiesByPatientIdentifier(
					identifier.getValue());
		}
		return fhirAllergies;
	}

	public List<AllergyIntolerance> searchAllergiesByPatientName(ReferenceParam name) {
		List<AllergyIntolerance> fhirAllergies = new ArrayList<>();
		if (Patient.SP_NAME.equals(name.getChain())) {
			fhirAllergies = Context.getService(AllergyIntoleranceService.class).searchAllergiesByPatientName(
					name.getValue());
		}
		return fhirAllergies;
	}
}
