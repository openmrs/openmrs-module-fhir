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
package org.openmrs.module.fhir.api;

import org.hl7.fhir.dstu3.model.AllergyIntolerance;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface AllergyIntoleranceService {

	/**
	 * Get allergy intolerance by uuid
	 *
	 * @param uuid of the requesting allergy
	 * @return allergy intolerance obj
	 */
	AllergyIntolerance getAllergyById(String uuid);

	/**
	 * Get allergy list by uuid
	 *
	 * @param uuid of the requesting allergies
	 * @return allergy intolerance obj
	 */
	List<AllergyIntolerance> searchAllergiesById(String uuid);

	/**
	 * Search allergies by patient identifier
	 *
	 * @param identifier to be search
	 * @return fhir allergy resource list
	 */
	List<AllergyIntolerance> searchAllergiesByPatientIdentifier(String identifier);

	/**
	 * Search allergies by patient name
	 *
	 * @param name to be search
	 * @return fhir allergy resource list
	 */
	List<AllergyIntolerance> searchAllergiesByPatientName(String name);

	/**
	 * Ger allergies by peron uuid
	 *
	 * @param personId uuid
	 * @return fhir allergy resource list
	 */
	List<AllergyIntolerance> searchAllergiesByPatientUuid(String personId);

	/**
	 * Create allergy intolerance
	 *
	 * @param allergyIntolerance to be created
	 * @return created allergy intolerance obj
	 */
	AllergyIntolerance createAllergy(AllergyIntolerance allergyIntolerance);

	/**
	 * Update allergy intolerance with uuid
	 *
	 * @param allergyIntolerance to be updated
	 * @param uuid of the requesting allergy
	 * @return updated allergy intolerance obj
	 */
	AllergyIntolerance updateAllergy(AllergyIntolerance allergyIntolerance, String uuid);

	/**
	 * Delete allergy intolerance with uuid
	 *
	 * @param uuid of allergy intolerance to be deleted
	 */
	void deleteAllergy(String uuid);
}
