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

import ca.uhn.fhir.model.dstu2.resource.Observation;
import org.openmrs.api.OpenmrsService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Transactional
public interface ObsService extends OpenmrsService {

	/**
	 * Get observation by id
	 *
	 * @param id uuid of the observation
	 * @return observation fhir resource
	 */
	public Observation getObs(String id);

	/**
	 * Search observations by patient and concepts
	 *
	 * @param patientUUid patient uuid
	 * @param conceptNames names of the concepts
	 * @return fhir obs resource list
	 */
	public List<Observation> searchObsByPatientAndConcept(String patientUUid, List<String> conceptNames);

	/**
	 * Search observations by patient and concepts
	 *
	 * @param id obs uuid
	 * @return fhir obs resource list
	 */
	public List<Observation> searchObsById(String id);

	/**
	 * Search observations by observation name
	 *
	 * @param name obs name
	 * @return fhir obs resource list
	 */
	public List<Observation> searchObsByName(String name);

	/**
	 * Search observations by observation date
	 *
	 * @param date obs date
	 * @return fhir obs resource list
	 */
	public List<Observation> searchObsByDate(Date date);

	/**
	 * Search observations by person
	 *
	 * @param personUuid person uuid of the person which observations needs to search for
	 * @return fhir obs resource list
	 */
	public List<Observation> searchObsByPerson(String personUuid);

	/**
	 * Search observations by value concept
	 *
	 * @param conceptName value concept name
	 * @return fhir obs resource list
	 */
	public List<Observation> searchObsByValueConcept(String conceptName);
}
