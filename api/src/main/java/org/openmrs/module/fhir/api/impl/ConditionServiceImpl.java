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

import ca.uhn.fhir.rest.server.exceptions.NotModifiedException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hl7.fhir.dstu3.model.Condition;
import org.openmrs.module.fhir.api.ConditionService;
import org.openmrs.module.fhir.api.db.FHIRDAO;
import org.openmrs.module.fhir.api.strategies.condition.ConditionStrategyUtil;
import org.openmrs.module.fhir.api.util.ContextUtil;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public class ConditionServiceImpl implements ConditionService {

	protected final Log log = LogFactory.getLog(this.getClass());

	private FHIRDAO dao;

	/**
	 * @return the dao
	 */
	public FHIRDAO getDao() {
		return dao;
	}

	/**
	 * @param dao the dao to set
	 */
	public void setDao(FHIRDAO dao) {
		this.dao = dao;
	}

	/**
	 * @see org.openmrs.module.fhir.api.ConditionService#getConditionByUuid(java.lang.String)
	 */
	@Override
	public Condition getConditionByUuid(String uuid) {
		return ContextUtil.getConditionHelper().getConditionByUuid(uuid);
	}

	/**
	 * @see org.openmrs.module.fhir.api.ConditionService#getConditionsByPatientUuid(java.lang.String)
	 */
	@Override
	public List<Condition> getConditionsByPatientUuid(String name) {
		return ConditionStrategyUtil.getConditionStrategy().getConditionsByPatientUuid(name);
	}

	/**
	 * @see org.openmrs.module.fhir.api.ConditionService#createFHIRCondition(org.hl7.fhir.dstu3.model.Condition)
	 */
	@Override
	public Condition createFHIRCondition(Condition condition) {
		return ConditionStrategyUtil.getConditionStrategy().createFHIRCondition(condition);
	}

	/**
	 * @see org.openmrs.module.fhir.api.ConditionService#updateFHIRCondition(org.hl7.fhir.dstu3.model.Condition)
	 */
	@Override
	public Condition updateFHIRCondition(Condition condition) {
		return ConditionStrategyUtil.getConditionStrategy().updateFHIRCondition(condition);
	}

	@Override
	public void retireCondition(String theId) throws ResourceNotFoundException, NotModifiedException {

	}

}
