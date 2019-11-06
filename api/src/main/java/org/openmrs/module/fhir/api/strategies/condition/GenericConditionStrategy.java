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
package org.openmrs.module.fhir.api.strategies.condition;

import org.hl7.fhir.dstu3.model.Condition;

import java.util.List;

public interface GenericConditionStrategy {

	/**
	 * Get condition by uuid
	 *
	 * @param uuid condition uuid
	 * @return Condition Return fhir condition resource and will return null if condition is not found for the given uuid
	 */
	Condition getConditionByUuid(String uuid);

	/**
	 * Gets conditions by patient uuid
	 *
	 * @param patientUuid patient UUID
	 * @return Condition FHIR condition resource list and will return Empty list if the patient with given uuid has not active conditions
	 */
	List<Condition> getConditionsByPatientUuid(String patientUuid);

	/**
	 * Create FHIRCondition
	 *
	 * @param condition FHIR condition
	 * @return Created FHIRCondition
	 */
	Condition createFHIRCondition(Condition condition);
}
