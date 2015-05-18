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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.ConditionService;
import org.openmrs.module.fhir.api.db.FHIRDAO;
import org.openmrs.module.fhir.api.util.FHIRConditionUtil;

import java.util.ArrayList;
import java.util.List;

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

	@Override
	public Condition getCondition(String id) {
		org.openmrs.Condition condition = Context.getService(org.openmrs.api.ConditionService.class).getConditionByUuid(id);
		if (condition == null || condition.isVoided()) {
			return null;
		}
		return FHIRConditionUtil.generateFHIRCondition(condition);
	}

	@Override
	public List<Condition> searchConditionById(String id) {
		org.openmrs.Condition condition = Context.getService(org.openmrs.api.ConditionService.class).getConditionByUuid(id);
		List<Condition> conditionList = new ArrayList<Condition>();
		if (condition != null) {
			conditionList.add(FHIRConditionUtil.generateFHIRCondition(condition));
		}
		return conditionList;
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
