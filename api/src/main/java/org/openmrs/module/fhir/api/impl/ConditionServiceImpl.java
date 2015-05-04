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
package org.openmrs.module.fhir.api.impl;

import ca.uhn.fhir.model.dstu2.resource.Condition;
import ca.uhn.fhir.rest.server.exceptions.NotModifiedException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.openmrs.module.fhir.api.ConditionService;

import java.util.List;

public class ConditionServiceImpl implements ConditionService {
	
	@Override
	public Condition getCondition(String id) {
		return null;
	}

	@Override
	public List<Condition> searchConditionById(String id) {
		return null;
	}

	@Override
	public List<Condition> searchConditionsByName(String name) {
		return null;
	}

	@Override
	public Condition createFHIRCondition(Condition condition) {
		return null;
	}

	@Override
	public Condition updateFHIRCondition(Condition condition, String theId) {
		return null;
	}

	@Override
	public void retireCondition(String theId) throws ResourceNotFoundException, NotModifiedException {

	}
}
