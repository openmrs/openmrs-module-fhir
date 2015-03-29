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

import ca.uhn.fhir.model.dstu2.resource.Observation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.fhir.api.ObsService;
import org.openmrs.module.fhir.api.db.FHIRDAO;
import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.openmrs.module.fhir.api.util.FHIRObsUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * It is a default implementation of {@link org.openmrs.module.fhir.api.PatientService}.
 */
public class ObsServiceImpl extends BaseOpenmrsService implements ObsService {

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
	 * @see org.openmrs.module.fhir.api.ObsService#getObs(String)
	 */
	public Observation getObs(String id) {
		Obs omrsObs = Context.getObsService().getObsByUuid(id);
		if (omrsObs == null) {
			return null;
		}
		return FHIRObsUtil.generateObs(omrsObs);
	}

	/**
	 * @see org.openmrs.module.fhir.api.ObsService#searchObsByPatientAndConcept(String, java.util.List)
	 */
	public List<Observation> searchObsByPatientAndConcept(String patientUUid, List<String> conceptNames) {

		Patient patient = Context.getPatientService().getPatientByUuid(patientUUid);
		Concept concept;
		List<Observation> obsList = new ArrayList<Observation>();
		for (String conceptName : conceptNames) {
			concept = Context.getConceptService().getConceptByMapping(conceptName, FHIRConstants.LOINC);
			List<Obs> obs = Context.getObsService().getObservationsByPersonAndConcept(patient, concept);
			for (Obs ob : obs) {
				obsList.add(FHIRObsUtil.generateObs(ob));
			}
		}
		return obsList;
	}

	/**
	 * @see org.openmrs.module.fhir.api.ObsService#searchObsById(String)
	 */
	public List<Observation> searchObsById(String id) {
		Obs omrsObs = Context.getObsService().getObsByUuid(id);
		List<Observation> obsList = new ArrayList<Observation>();
		if (omrsObs != null) {
			obsList.add(FHIRObsUtil.generateObs(omrsObs));
		}
		return obsList;
	}

	/**
	 * @see org.openmrs.module.fhir.api.ObsService#searchObsByName(String)
	 */
	public List<Observation> searchObsByName(String name) {
		Concept concept = Context.getConceptService().getConcept(name);
		List<Concept> concepts = new ArrayList<Concept>();
		concepts.add(concept);
		List<Obs> omrsObs = Context.getObsService().getObservations(null, null, concepts, null, null, null, null, null,
				null, null, null, false);
		List<Observation> obsList = new ArrayList<Observation>();
		for (Obs obs : omrsObs) {
			obsList.add(FHIRObsUtil.generateObs(obs));
		}
		return obsList;
	}

	/**
	 * @see org.openmrs.module.fhir.api.ObsService#searchObsByDate(java.util.Date)
	 */
	public List<Observation> searchObsByDate(Date date) {
		List<Obs> omrsObs = Context.getObsService().getObservations(null, null, null, null, null, null, null, null,
				null, date, date, false);
		List<Observation> obsList = new ArrayList<Observation>();
		for (Obs obs : omrsObs) {
			obsList.add(FHIRObsUtil.generateObs(obs));
		}
		return obsList;
	}

	/**
	 * @see org.openmrs.module.fhir.api.ObsService#searchObsByPerson(String)
	 */
	public List<Observation> searchObsByPerson(String personUuid) {
		Person person = Context.getPersonService().getPersonByUuid(personUuid);
		List<Obs> omrsObs = Context.getObsService().getObservationsByPerson(person);
		List<Observation> obsList = new ArrayList<Observation>();
		for (Obs obs : omrsObs) {
			obsList.add(FHIRObsUtil.generateObs(obs));
		}
		return obsList;
	}

	/**
	 * @see org.openmrs.module.fhir.api.ObsService#searchObsByValueConcept(String)
	 */
	public List<Observation> searchObsByValueConcept(String conceptName) {
		Concept concept = Context.getConceptService().getConcept(conceptName);
		List<Concept> conceptsAnswers = new ArrayList<Concept>();
		conceptsAnswers.add(concept);
		List<Obs> omrsObs = Context.getObsService().getObservations(null, null, null, conceptsAnswers, null, null, null,
				null,
				null, null, null, false);
		List<Observation> obsList = new ArrayList<Observation>();
		for (Obs obs : omrsObs) {
			obsList.add(FHIRObsUtil.generateObs(obs));
		}
		return obsList;
	}

	/**
	 * @see org.openmrs.module.fhir.api.ObsService#searchObsByPatientIdentifier(String)
	 */
	public List<Observation> searchObsByPatientIdentifier(String identifier) {
		List<Observation> fhirObsList = new ArrayList<Observation>();

		List<Obs> ormsObs = Context.getObsService().getObservations(identifier);
		for (Obs obs: ormsObs) {
			fhirObsList.add(FHIRObsUtil.generateObs(obs));
		}
		return fhirObsList;
	}

}
