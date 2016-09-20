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
package org.openmrs.module.fhir.api.condition;

import ca.uhn.fhir.model.dstu2.resource.Condition;

import java.util.List;

public interface GenericConditionStrategy {

	/**
	 * Get condition by id
	 * @param uuid the uuid of the condition that need to retrieve
	 * @return Condition Return fhir condition resource and will return null if condition is not found for the given id
	 */
	Condition getConditionById(String uuid);

	List<Condition> searchConditionById(String uuid);

	List<Condition> searchConditionByName(String name);
}
