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

import ca.uhn.fhir.model.dstu2.resource.FamilyHistory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Person;
import org.openmrs.Relationship;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.fhir.api.FamilyHistoryService;
import org.openmrs.module.fhir.api.db.FHIRDAO;
import org.openmrs.module.fhir.api.util.FHIRFamilyHistoryUtil;

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
	 * @see org.openmrs.module.fhir.api.FamilyHistoryService#searchFamilyHistoryByPerson(String)
	 */
	public List<FamilyHistory> searchFamilyHistoryByPerson(String personId) {
		Person person = Context.getPersonService().getPersonByUuid(personId);
		List<FamilyHistory> fhirFamilyHistory = new ArrayList<FamilyHistory>();
		List<Relationship> relationships = null;
		if (person != null) {
			relationships = Context.getPersonService().getRelationshipsByPerson(person);
		}
		if (relationships != null && relationships.size() > 0) {
			FamilyHistory history = FHIRFamilyHistoryUtil.generateFamilyHistory(relationships, person);
			fhirFamilyHistory.add(history);
		}
		return fhirFamilyHistory;
	}

	/**
	 * @see org.openmrs.module.fhir.api.FamilyHistoryService#getRelationshipById(String)
	 */
	public FamilyHistory getRelationshipById(String id) {
		Person person = Context.getPersonService().getPersonByUuid(id);
		List<Relationship> relationships = Context.getPersonService().getRelationshipsByPerson(person);
		return FHIRFamilyHistoryUtil.generateFamilyHistory(relationships, person);
	}

	/**
	 * @see org.openmrs.module.fhir.api.FamilyHistoryService#searchRelationshipsById(String)
	 */
	public List<FamilyHistory> searchRelationshipsById(String id) {
		Person person = Context.getPersonService().getPersonByUuid(id);
		List<FamilyHistory> familyHistories = new ArrayList<FamilyHistory>();
		List<Relationship> relationships = Context.getPersonService().getRelationshipsByPerson(person);
		if (relationships != null) {
			familyHistories.add(FHIRFamilyHistoryUtil.generateFamilyHistory(relationships, person));
		}
		return familyHistories;
	}
}
