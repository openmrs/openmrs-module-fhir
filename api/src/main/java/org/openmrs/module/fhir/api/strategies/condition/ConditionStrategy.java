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
import org.openmrs.module.fhir.api.util.ContextUtil;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("DefaultConditionStrategy")
public class ConditionStrategy implements GenericConditionStrategy {

	/**
	 * @see org.openmrs.module.fhir.api.strategies.condition.GenericConditionStrategy#getConditionByUuid(java.lang.String)
	 */
	@Override
	public Condition getConditionByUuid(String uuid) {
		return ContextUtil.getConditionHelper().getConditionByUuid(uuid);
	}

	/**
	 * @see org.openmrs.module.fhir.api.strategies.condition.GenericConditionStrategy#getConditionsByPatientUuid(java.lang.String)
	 */
	@Override
	public List<Condition> getConditionsByPatientUuid(String patientUuid) {
		return ContextUtil.getConditionHelper().getConditionsByPatientUuid(patientUuid);
	}

	/**
	 * @see org.openmrs.module.fhir.api.strategies.condition.GenericConditionStrategy#createFHIRCondition(org.hl7.fhir.dstu3.model.Condition)
	 */
	@Override
	public Condition createFHIRCondition(Condition condition) {
		return ContextUtil.getConditionHelper().createCondition(condition);
	}

	/**
	 * @see org.openmrs.module.fhir.api.strategies.condition.GenericConditionStrategy#updateFHIRCondition(org.hl7.fhir.dstu3.model.Condition)
	 */
	@Override
	public Condition updateFHIRCondition(Condition condition) {
		return ContextUtil.getConditionHelper().updateFHIRCondition(condition);
	}
}
