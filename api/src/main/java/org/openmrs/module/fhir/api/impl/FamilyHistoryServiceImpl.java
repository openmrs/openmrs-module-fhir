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
import ca.uhn.fhir.model.dstu.resource.Observation;
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

	public List<FamilyHistory> searchFamilyHistoryByPerson(String personId) {
		Person person = Context.getPersonService().getPersonByUuid(personId);
		List<Relationship> relationships = Context.getPersonService().getRelationshipsByPerson(person);
		FamilyHistory history = FHIRFamilyHistoryUtil.generateFamilyHistory(relationships, person);
		List<FamilyHistory> fhirFamilyHistory = new ArrayList<FamilyHistory>();
		if(history != null) {
			fhirFamilyHistory.add(history);
		}
		return fhirFamilyHistory;
	}

	public FamilyHistory getRelationshipById(String id) {
		Relationship relationship = Context.getPersonService().getRelationshipByUuid(id);
		if(relationship == null) {
			return null;
		}
		List<Relationship> omrsRelationships = new ArrayList<Relationship>();
		omrsRelationships.add(relationship);
		return FHIRFamilyHistoryUtil.generateFamilyHistory(omrsRelationships, relationship.getPersonA());
	}

	public List<FamilyHistory> searchRelationshipsById(String id) {
		Relationship relationship = Context.getPersonService().getRelationshipByUuid(id);
		List<FamilyHistory> familyHistories = new ArrayList<FamilyHistory>();
		if(relationship != null) {
			List<Relationship> omrsRelationships = new ArrayList<Relationship>();
			omrsRelationships.add(relationship);
			familyHistories.add(FHIRFamilyHistoryUtil.generateFamilyHistory(omrsRelationships, relationship.getPersonA()));
		}
		return familyHistories;
	}
}
