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

import ca.uhn.fhir.model.dstu2.resource.Condition;
import ca.uhn.fhir.rest.server.exceptions.NotModifiedException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;

import java.util.List;

public interface ConditionService {

	/**
	 * Get fhir condtion resource by uuid
	 *
	 * @param id uuid of the patient
	 * @return fhir condition resource and will return null if condition not found for the given id
	 */
	Condition getCondition(String id);

	/**
	 * Search condition by uuid
	 *
	 * @param id the uuid to be search
	 * @return fhir condition resource list
	 */
	List<Condition> searchConditionById(String id);

	/**
	 * Search conditions by name
	 *
	 * @param patientUuid the name to be search
	 * @return fhir contion resource list
	 */
	List<Condition> searchConditionsByPatient(String patientUuid);

	/**
	 * creates a oms Condition from FHIR personn
	 *
	 * @param condition
	 * @return
	 */
	Condition createFHIRCondition(Condition condition);

	/**
	 * update a OpenMRS Condition from FHIR Condition
	 *
	 * @param condition the FHIR representation of the {@link}Condition to be updated
	 * @param theId     the uuid of the Condition to be updated
	 * @return the updated FHIR {@link}Condition Resource
	 */
	Condition updateFHIRCondition(Condition condition, String theId);

	/**
	 * makes a Condition retired
	 *
	 * @param theId the uuid of the Condition to retire
	 * @should make condition void
	 * @should throw ResourceNotFoundException if condition with given id not found
	 * @should do nothing if person already void
	 * @should throw MethodNotAllowedException if API has refused the operation
	 */
	void retireCondition(String theId) throws ResourceNotFoundException, NotModifiedException;
}
