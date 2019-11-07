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

import ca.uhn.fhir.rest.server.exceptions.NotModifiedException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.dstu3.model.Condition;

import java.util.List;

public interface ConditionService {

	/**
	 * Get condition by uuid
	 *
	 * @param uuid condition uuid
	 * @return FHIR condition resource and will return null if condition not found for the given uuid
	 */
	Condition getConditionByUuid(String uuid);

	/**
	 * Gets conditions by patient uuid
	 *
	 * @param patientUuid patient uuid
	 * @return FHIR condition resource list and will return an empty list if patient with the given UUD has no active conditions
	 */
	List<Condition> getConditionsByPatientUuid(String patientUuid);

	/**
	 * creates openmrs condition from FHIR condition
	 *
	 * @param condition FHIR condition object
	 * @return FHIR Condition which was created if successful or null if not successful
	 */
	Condition createFHIRCondition(Condition condition);

	/**
	 * update a OpenMRS Condition from FHIR Condition
	 *
	 * @param condition the FHIR representation of the {@link}Condition to be updated
	 * @return the updated FHIR {@link}Condition Resource
	 */
	Condition updateFHIRCondition(Condition condition);

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
