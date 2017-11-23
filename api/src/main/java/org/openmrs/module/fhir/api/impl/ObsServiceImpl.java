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
import org.hl7.fhir.dstu3.model.Observation;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.fhir.api.ObsService;
import org.openmrs.module.fhir.api.db.FHIRDAO;
import org.openmrs.module.fhir.api.strategies.observation.ObservationStrategyUtil;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * It is a default implementation of {@link org.openmrs.module.fhir.api.PatientService}.
 */
public class ObsServiceImpl extends BaseOpenmrsService implements ObsService {

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
	 * @see org.openmrs.module.fhir.api.ObsService#getObs(String)
	 */
	public Observation getObs(String id) {
		return ObservationStrategyUtil.getObservationStrategy().getObservation(id);
	}

	/**
	 * @see org.openmrs.module.fhir.api.ObsService#searchObsByPatientAndConcept(String, java.util.Map)
	 */
	public List<Observation> searchObsByPatientAndConcept(String patientUUid, Map<String, String> conceptNamesAndURIs) {
		return ObservationStrategyUtil.getObservationStrategy().searchObservationByPatientAndConcept(patientUUid, conceptNamesAndURIs);
	}

	/**
	 * @see org.openmrs.module.fhir.api.ObsService#searchObsById(String)
	 */
	public List<Observation> searchObsById(String id) {
		return ObservationStrategyUtil.getObservationStrategy().searchObservationByUuid(id);
	}

	/**
	 * @see org.openmrs.module.fhir.api.ObsService#searchObsByCode(java.util.Map)
	 */
	public List<Observation> searchObsByCode(Map<String, String> conceptNamesAndURIs) {
		return ObservationStrategyUtil.getObservationStrategy().searchObservationsByCode(conceptNamesAndURIs);
	}

	/**
	 * @see org.openmrs.module.fhir.api.ObsService#searchObsByDate(java.util.Date)
	 */
	public List<Observation> searchObsByDate(Date date) {
		return ObservationStrategyUtil.getObservationStrategy().searchObservationByDate(date);
	}

	/**
	 * @see org.openmrs.module.fhir.api.ObsService#searchObsByPerson(String)
	 */
	public List<Observation> searchObsByPerson(String personUuid) {
		return ObservationStrategyUtil.getObservationStrategy().searchObservationByPerson(personUuid);
	}

	/**
	 * @see org.openmrs.module.fhir.api.ObsService#searchObsByValueConcept(String)
	 */
	public List<Observation> searchObsByValueConcept(String conceptName) {
		return ObservationStrategyUtil.getObservationStrategy().searchObservationByValueConcept(conceptName);
	}

	/**
	 * @see org.openmrs.module.fhir.api.ObsService#searchObsByPatientIdentifier(String)
	 */
	public List<Observation> searchObsByPatientIdentifier(String identifier) {
		return ObservationStrategyUtil.getObservationStrategy().searchObservationByPatientIdentifier(identifier);
	}

	/**
	 * @see org.openmrs.module.fhir.api.ObsService#deleteObs(String)
	 */
	@Override
	public void deleteObs(String id) {
		ObservationStrategyUtil.getObservationStrategy().deleteObservation(id);
	}
	
	/**
	 * @see org.openmrs.module.fhir.api.ObsService#createFHIRObservation(Observation)
	 */
	@Override
	public Observation createFHIRObservation(Observation observation) {
		return ObservationStrategyUtil.getObservationStrategy().createFHIRObservation(observation);
	}

	/**
	 * @see org.openmrs.module.fhir.api.ObsService#updateFHIRObservation(Observation, String)
	 */
	@Override
	public Observation updateFHIRObservation(Observation observation, String theId) {
		return ObservationStrategyUtil.getObservationStrategy().updateFHITObservation(observation, theId);
	}
}
