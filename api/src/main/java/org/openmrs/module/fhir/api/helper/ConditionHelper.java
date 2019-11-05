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
package org.openmrs.module.fhir.api.helper;

import org.hl7.fhir.dstu3.model.Condition;

/**
 * <h1>Condition Helper</h1>
 * <p>Adapts specific Condition to be used by business logic in different Openmrs versions.</p>
 *
 * @since 1.20.0
 */

public interface ConditionHelper {

	/**
	 * Get condition by uuid
	 *
	 * @param uuid condition uuid
	 * @return Condition Return fhir condition resource and will return null if condition is not found for the given uuid
	 */
	Condition getCondition(String uuid);

	/**
	 * Create Condition
	 *
	 * @param condition FHIR condition
	 * @return  FHIR Condition
	 */
	Condition createCondition(Condition condition);

	/**
	 * Generates openmrs condition from FHIR condition
	 *
	 * @param condition FHIR condition
	 * @return Openmrs condition
	 */
	org.openmrs.Condition generateOpenMrsCondition(Condition condition);

	/**
	 * Generate FHIR condition from openmrs condition
	 *
	 * @param condition Openmrs condition
	 * @return FHIR condition
	 */
	Condition generateFHIRCondition(org.openmrs.Condition condition);

}
