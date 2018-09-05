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

import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.hl7.fhir.dstu3.model.StringType;
import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.fhir.api.PractitionerService;
import org.openmrs.module.fhir.api.db.FHIRDAO;
import org.openmrs.module.fhir.api.strategies.practitioner.PractitionerStrategy;
import org.openmrs.module.fhir.api.strategies.practitioner.PractitionerStrategyUtil;
import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.openmrs.module.fhir.api.util.FHIRPractitionerUtil;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.valueOf;

/**
 * It is a default implementation of {@link org.openmrs.module.fhir.api.PatientService}.
 */
public class PractitionerServiceImpl extends BaseOpenmrsService implements PractitionerService {

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
	 * @see org.openmrs.module.fhir.api.PractitionerService#getPractitioner(String)
	 */
	public Practitioner getPractitioner(String id) {
		return PractitionerStrategyUtil.getPractitionerStrategy().getPractitioner(id);
	}

	/**
	 * @see org.openmrs.module.fhir.api.PractitionerService#searchPractitionersById(String)
	 */
	public List<Practitioner> searchPractitionersById(String id) {
		return PractitionerStrategyUtil.getPractitionerStrategy().searchPractitionersByUuid(id);
	}

	/**
	 * @see org.openmrs.module.fhir.api.PractitionerService#searchPractitionersByName(String)
	 */
	public List<Practitioner> searchPractitionersByName(String name) {
		return PractitionerStrategyUtil.getPractitionerStrategy().searchPractitionersByName(name);
	}

	/**
	 * @see org.openmrs.module.fhir.api.PractitionerService#searchPractitionersByGivenName(String)
	 */
	public List<Practitioner> searchPractitionersByGivenName(String givenName) {
		return PractitionerStrategyUtil.getPractitionerStrategy().searchPractitionersByGivenName(givenName);
	}

	/**
	 * @see org.openmrs.module.fhir.api.PractitionerService#searchPractitionersByFamilyName(String)
	 */
	public List<Practitioner> searchPractitionersByFamilyName(String familyName) {
		return PractitionerStrategyUtil.getPractitionerStrategy().searchPractitionersByFamilyName(familyName);
	}

	/**
	 * @see org.openmrs.module.fhir.api.PractitionerService#searchPractitionersByIdentifier(String)
	 */
	public List<Practitioner> searchPractitionersByIdentifier(String identifier) {
		return PractitionerStrategyUtil.getPractitionerStrategy().searchPractitionersByIdentifier(identifier);
	}
	
	/**
	 * @see org.openmrs.module.fhir.api.PractitionerService#createFHIRPractitioner(Practitioner)
	 */
	public Practitioner createFHIRPractitioner(Practitioner practitioner) {
		return PractitionerStrategyUtil.getPractitionerStrategy().createFHIRPractitioner(practitioner);
	}
	
	/**
	 * @see org.openmrs.module.fhir.api.PractitionerService#updatePractitioner(Practitioner
	 *      practitioner, String theId)
	 */
	public Practitioner updatePractitioner(Practitioner practitioner, String theId) {
		return PractitionerStrategyUtil.getPractitionerStrategy().updatePractitioner(practitioner, theId);
	}
}
