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

import java.util.List;

public class ObsAllergyStrategy implements GenericAllergyStrategy {

	@Override
	public AllergyIntolerance getAllergyById(String uuid) {
		return null;
	}

	@Override
	public List<AllergyIntolerance> searchAllergyById(String uuid) {
		return null;
	}

	@Override
	public List<AllergyIntolerance> searchAllergiesByPatientIdentifier(String identifier) {
		return null;
	}

	@Override
	public List<AllergyIntolerance> searchAllergiesByPatientName(String name) {
		return null;
	}

	@Override
	public List<AllergyIntolerance> searchAllergiesByPersonId(String uuid) {
		return null;
	}

	@Override
	public AllergyIntolerance createAllergy(AllergyIntolerance allergy) {
		return null;
	}

	@Override
	public AllergyIntolerance updateAllergy(AllergyIntolerance allergy, String uuid) {
		return null;
	}

	@Override
	public void deleteAllergy(String uuid) {
	}
}
