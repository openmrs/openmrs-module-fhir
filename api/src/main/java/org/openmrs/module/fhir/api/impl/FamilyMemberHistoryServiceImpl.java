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
import org.openmrs.Person;
import org.openmrs.Relationship;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.fhir.api.FamilyMemberHistoryService;
import org.openmrs.module.fhir.api.db.FHIRDAO;
import org.openmrs.module.fhir.api.util.FHIRFamilyMemberHistoryUtil;

import java.util.ArrayList;
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
	 * @see org.openmrs.module.fhir.api.FamilyHistoryService#searchFamilyHistoryByPersonId(String)
	 */
	public List<FamilyMemberHistory> searchFamilyHistoryByPersonId(String personId) {
		Person person = Context.getPersonService().getPersonByUuid(personId);
		List<FamilyMemberHistory> fhirFamilyHistory = new ArrayList<FamilyMemberHistory>();
		List<Relationship> relationships = null;
		if (person != null && !person.isVoided()) {
			relationships = Context.getPersonService().getRelationshipsByPerson(person);
		}
		if (relationships != null && relationships.size() > 0) {
			for (Relationship relationship : relationships) {
				fhirFamilyHistory.add(FHIRFamilyMemberHistoryUtil.generateFamilyHistory(relationship, person));
			}
		}
		return fhirFamilyHistory;
	}

	/**
	 * @see org.openmrs.module.fhir.api.FamilyHistoryService#getRelationshipById(String)
	 */
	public FamilyMemberHistory getRelationshipById(String id) {
		Person person = Context.getPersonService().getPersonByUuid(id);
		List<Relationship> relationships = Context.getPersonService().getRelationshipsByPerson(person);
		return FHIRFamilyMemberHistoryUtil.generateFamilyHistory(relationships.get(0), person);
	}

	/**
	 * @see org.openmrs.module.fhir.api.FamilyHistoryService#searchRelationshipsById(String)
	 */
	public List<FamilyMemberHistory> searchRelationshipsById(String id) {
		Person person = Context.getPersonService().getPersonByUuid(id);
		List<FamilyMemberHistory> familyHistories = new ArrayList<FamilyMemberHistory>();
		List<Relationship> relationships = Context.getPersonService().getRelationshipsByPerson(person);
		if (relationships != null) {
			for (Relationship relationship : relationships) {
				familyHistories.add(FHIRFamilyMemberHistoryUtil.generateFamilyHistory(relationship, person));
			}
		}
		return familyHistories;
	}
}
