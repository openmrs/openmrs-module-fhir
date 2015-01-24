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

import ca.uhn.fhir.model.dstu.resource.FamilyHistory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.fhir.api.FamilyHistoryService;
import org.openmrs.module.fhir.api.db.FHIRDAO;
import org.openmrs.module.fhir.api.util.FamilyHistoryUtil;

import java.util.ArrayList;
import java.util.List;

public class FamilyHistoryServiceImpl extends BaseOpenmrsService implements FamilyHistoryService {

	protected final Log log = LogFactory.getLog(this.getClass());

	private FHIRDAO dao;

	/**
	 * @param dao the dao to set
	 */
	public void setDao(FHIRDAO dao) {
		this.dao = dao;
	}

	/**
	 * @return the dao
	 */
	public FHIRDAO getDao() {
		return dao;
	}

	/**
	 * @see org.openmrs.module.fhir.api.FamilyHistoryService#getFamilyHistory(String)
	 */
	public FamilyHistory getFamilyHistory(String id) {
		return FamilyHistoryUtil.generateFamilyHistory();
	}

	/**
	 * @see org.openmrs.module.fhir.api.FamilyHistoryService#searchFamilyHistoryById(String)
	 */
	public List<FamilyHistory> searchFamilyHistoryById(String id) {
		//TODO need to implement
		return new ArrayList<FamilyHistory>();
	}
}
