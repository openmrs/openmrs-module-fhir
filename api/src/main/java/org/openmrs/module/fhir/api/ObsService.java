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

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openmrs.api.OpenmrsService;
import org.springframework.transaction.annotation.Transactional;

import ca.uhn.fhir.model.dstu2.resource.Observation;

@Transactional
public interface ObsService extends OpenmrsService {

	/**
	 * Get observation by id
	 *
	 * @param id uuid of the observation
	 * @return fhir observation resource
	 */
	public Observation getObs(String id);

	/**
	 * Search observations by patient and concepts
	 *
	 * @param patientUUid patient uuid
	 * @param conceptNamesAndURIs names of the concepts with system uris in a map
	 * @return fhir Observation resource list
	 */
	public List<Observation> searchObsByPatientAndConcept(String patientUUid, Map<String, String> conceptNamesAndURIs);

	/**
	 * Search observations by patient and concepts
	 *
	 * @param id obs uuid
	 * @return fhir Observation resource list
	 */
	public List<Observation> searchObsById(String id);

	/**
	 * Search observations by observation name
	 *
	 * @param conceptNamesAndURIs obs codes and system uris
	 * @return fhir Observation resource list
	 */
	public List<Observation> searchObsByCode(Map<String, String> conceptNamesAndURIs);

	/**
	 * Search observations by observation date
	 *
	 * @param date obs date
	 * @return fhir Observation resource list
	 */
	public List<Observation> searchObsByDate(Date date);

	/**
	 * Search observations by person
	 *
	 * @param personUuid person uuid of the person which observations needs to search for
	 * @return fhir Observation resource list
	 */
	public List<Observation> searchObsByPerson(String personUuid);

	/**
	 * Search observations by value concept
	 *
	 * @param conceptName value concept name
	 * @return fhir Observation resource list
	 */
	public List<Observation> searchObsByValueConcept(String conceptName);

	/**
	 * Search observations by patient identifier *
	 * 
	 * @param identifier to be search
	 * @return fhir observation resource list
	 */
	public List<Observation> searchObsByPatientIdentifier(String identifier);

	/**
	 * Delete observation by id
	 *
	 * @param id uuid of the observation
	 */
	public void deleteObs(String id);
	
	/**
	 * create observation
	 * 
	 * @param observation the observation representation
	 * @return created fhir observation resource
	 */
	public Observation createFHIRObservation(Observation observation);

}
