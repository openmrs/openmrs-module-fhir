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

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Composition;
import org.hl7.fhir.dstu3.model.Encounter;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.fhir.api.EncounterService;
import org.openmrs.module.fhir.api.db.FHIRDAO;
import org.openmrs.module.fhir.api.strategies.encounter.EncounterStrategyUtil;

import java.util.List;

/**
 * It is a default implementation of {@link org.openmrs.module.fhir.api.EncounterService}.
 */
public class EncounterServiceImpl extends BaseOpenmrsService implements EncounterService {

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
	 * @see org.openmrs.module.fhir.api.EncounterService#getEncounter(String)
	 */
	@Override
	public Encounter getEncounter(String uuid) {
		return EncounterStrategyUtil.getEncounterStrategy().getEncounter(uuid);
	}

	/**
	 * @see org.openmrs.module.fhir.api.EncounterService#searchEncounterById(String)
	 */
	public List<Encounter> searchEncounterById(String uuid) {
		return EncounterStrategyUtil.getEncounterStrategy().searchEncounterById(uuid);
	}

	/**
	 * @see org.openmrs.module.fhir.api.EncounterService#searchEncountersByPatientIdentifier(String)
	 */
	public List<Encounter> searchEncountersByPatientIdentifier(String identifier) {
		return EncounterStrategyUtil.getEncounterStrategy().searchEncountersByPatientIdentifier(identifier);
	}

	/**
	 * @see org.openmrs.module.fhir.api.EncounterService#searchEncounterComposition(String)
	 */
	public List<Composition> searchEncounterComposition(String uuid) {
		return EncounterStrategyUtil.getEncounterStrategy().searchEncounterComposition(uuid);
	}

	/**
	 * @see org.openmrs.module.fhir.api.EncounterService#searchEncounterCompositionByPatientId(String)
	 */
	public List<Composition> searchEncounterCompositionByPatientId(String patientId) {
		return EncounterStrategyUtil.getEncounterStrategy().searchEncounterCompositionByPatientId(patientId);
	}

	/**
	 * @see org.openmrs.module.fhir.api.EncounterService#searchEncounterCompositionByEncounterId(String)
	 */
	public List<Composition> searchEncounterCompositionByEncounterId(String encounterId) {
		return EncounterStrategyUtil.getEncounterStrategy().searchEncounterCompositionByEncounterId(encounterId);
	}

	/**
	 * @see org.openmrs.module.fhir.api.EncounterService#getEncounterOperationsById(String)
	 */
	public Bundle getEncounterOperationsById(String encounterId) {
		return EncounterStrategyUtil.getEncounterStrategy().getEncounterOperationsById(encounterId);
	}

	/**
	 * @see org.openmrs.module.fhir.api.EncounterService#getEncounterOperationsById(String,
	 *      org.hl7.fhir.dstu3.model.Bundle, boolean)
	 */
	public Bundle getEncounterOperationsById(String encounterId, Bundle bundle, boolean includePatient) {
		return EncounterStrategyUtil.getEncounterStrategy().getEncounterOperationsById(encounterId, bundle, includePatient);
	}

	/**
	 * @see org.openmrs.module.fhir.api.EncounterService#deleteEncounter(String)
	 */
	@Override
	public void deleteEncounter(String uuid) {
		EncounterStrategyUtil.getEncounterStrategy().deleteEncounter(uuid);
	}

	/**
	 * @see org.openmrs.module.fhir.api.EncounterService#searchEncountersByPatientIdentifierAndPartOf(String,
	 *      String)
	 */
	@Override
	public List<Encounter> searchEncountersByPatientIdentifierAndPartOf(String patientIdentifier, String partOf) {
		return EncounterStrategyUtil.getEncounterStrategy().searchEncountersByPatientIdentifierAndPartOf(patientIdentifier, partOf);
	}

	/**
	 * @see org.openmrs.module.fhir.api.EncounterService#searchEncountersByEncounterIdAndPartOf(String,
	 *      String)
	 */
	@Override
	public List<Encounter> searchEncountersByEncounterIdAndPartOf(String encounterId, String partOf) {
		return EncounterStrategyUtil.getEncounterStrategy().searchEncountersByEncounterIdAndPartOf(encounterId, partOf);
	}
	
	/**
	 * @see org.openmrs.module.fhir.api.EncounterService#createFHIREncounter(Encounter)
	 */
	@Override
	public Encounter createFHIREncounter(Encounter encounter) {
		return EncounterStrategyUtil.getEncounterStrategy().createFHIREncounter(encounter);
	}

	/**
	 * @see org.openmrs.module.fhir.api.EncounterService#updateEncounter(Encounter, String)
	 */
	@Override
	public Encounter updateEncounter(Encounter encounter, String uuid) {
		return EncounterStrategyUtil.getEncounterStrategy().updateEncounter(encounter, uuid);
	}
}
