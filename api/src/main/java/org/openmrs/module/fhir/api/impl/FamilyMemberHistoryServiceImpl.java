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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hl7.fhir.dstu3.model.FamilyMemberHistory;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.fhir.api.FamilyMemberHistoryService;
import org.openmrs.module.fhir.api.db.FHIRDAO;
import org.openmrs.module.fhir.api.strategies.familymemberhistory.FamilyMemberHistoryStrategyUtil;

import java.util.List;


public class FamilyMemberHistoryServiceImpl extends BaseOpenmrsService implements FamilyMemberHistoryService {

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
	 * @see org.openmrs.module.fhir.api.FamilyMemberHistoryService#searchFamilyHistoryByPersonId(String)
	 */
	public List<FamilyMemberHistory> searchFamilyHistoryByPersonId(String personId) {
		return FamilyMemberHistoryStrategyUtil.getFamilyMemberStrategy().searchFamilyHistoryByPersonId(personId);
	}

	/**
	 * @see org.openmrs.module.fhir.api.FamilyMemberHistoryService#getRelationshipById(String)
	 */
	public FamilyMemberHistory getRelationshipById(String id) {
		return FamilyMemberHistoryStrategyUtil.getFamilyMemberStrategy().getRelationshipById(id);
	}

	/**
	 * @see org.openmrs.module.fhir.api.FamilyMemberHistoryService#searchRelationshipsById(String)
	 */
	public List<FamilyMemberHistory> searchRelationshipsById(String id) {
		return FamilyMemberHistoryStrategyUtil.getFamilyMemberStrategy().searchRelationshipsById(id);
	}
}
