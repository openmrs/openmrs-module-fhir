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
import org.hl7.fhir.dstu3.model.AllergyIntolerance;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.fhir.api.AllergyIntoleranceService;
import org.openmrs.module.fhir.api.db.FHIRDAO;
import org.openmrs.module.fhir.api.strategies.allergy.AllergyStrategyUtil;

import java.util.List;

/**
 * It is a default implementation of {@link org.openmrs.module.fhir.api.AllergyIntoleranceService}.
 */
public class AllergyIntoleranceServiceImpl extends BaseOpenmrsService implements AllergyIntoleranceService {

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
	public AllergyIntolerance getAllergyById(String uuid) {
		return AllergyStrategyUtil.getAllergyStrategy().getAllergyById(uuid);
	}

	@Override
	public List<AllergyIntolerance> searchAllergiesById(String uuid) {
		return AllergyStrategyUtil.getAllergyStrategy().searchAllergyById(uuid);
	}

	@Override
	public List<AllergyIntolerance> searchAllergiesByPatientIdentifier(String identifier) {
		return AllergyStrategyUtil.getAllergyStrategy().searchAllergiesByPatientIdentifier(identifier);
	}

	@Override
	public List<AllergyIntolerance> searchAllergiesByPatientName(String name) {
		return AllergyStrategyUtil.getAllergyStrategy().searchAllergiesByPatientName(name);
	}

	@Override
	public List<AllergyIntolerance> searchAllergiesByPatientUuid(String personId) {
		return AllergyStrategyUtil.getAllergyStrategy().searchAllergiesByPersonId(personId);
	}

	@Override
	public AllergyIntolerance createAllergy(AllergyIntolerance allergyIntolerance) {
		return AllergyStrategyUtil.getAllergyStrategy().createAllergy(allergyIntolerance);
	}

	@Override
	public AllergyIntolerance updateAllergy(AllergyIntolerance allergyIntolerance, String uuid) {
		return AllergyStrategyUtil.getAllergyStrategy().updateAllergy(allergyIntolerance, uuid);
	}

	@Override
	public void deleteAllergy(String uuid) {
		AllergyStrategyUtil.getAllergyStrategy().deleteAllergy(uuid);
	}
}
