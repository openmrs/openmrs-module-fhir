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

import ca.uhn.fhir.model.dstu.resource.Observation;
import org.openmrs.api.OpenmrsService;
import org.springframework.transaction.annotation.Transactional;

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
	 * @param concepts    concepts to be search
	 * @return fhir obs resource list
	 */
	public List<Observation> searchObsByPatientandConcept(String patientUUid, String[] concepts);

	/**
	 * Search observations by patient and concepts
	 *
	 * @param id obs uuid
	 * @return fhir obs resource list
	 */
	public List<Observation> searchObsById(String id);

}
