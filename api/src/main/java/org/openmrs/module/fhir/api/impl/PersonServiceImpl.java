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

import ca.uhn.fhir.rest.server.exceptions.NotModifiedException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hl7.fhir.dstu3.model.Person;
import org.openmrs.module.fhir.api.PersonService;
import org.openmrs.module.fhir.api.db.FHIRDAO;
import org.openmrs.module.fhir.api.strategies.person.PersonStrategyUtil;

import java.util.List;

public class PersonServiceImpl implements PersonService {

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
	 * @see org.openmrs.module.fhir.api.PersonService#getPerson(String)
	 */
	@Override
	public Person getPerson(String uuid) {
		return PersonStrategyUtil.getPersonStrategy().getPerson(uuid);
	}

	/**
	 * @see org.openmrs.module.fhir.api.PersonService#searchPersonByUuid(String)
	 */
	@Override
	public List<Person> searchPersonByUuid(String uuid) {
		return PersonStrategyUtil.getPersonStrategy().searchPersonByUuid(uuid);
	}

	/**
	 * @see org.openmrs.module.fhir.api.PersonService#searchPersons(String, Integer, String)
	 */
	@Override
	public List<Person> searchPersons(String name, Integer birthYear, String gender) {
		return PersonStrategyUtil.getPersonStrategy().searchPersons(name, birthYear, gender);
	}

	/**
	 * @see org.openmrs.module.fhir.api.PersonService#searchPersonsByName(String)
	 */
	@Override
	public List<Person> searchPersonsByName(String name) {
		return PersonStrategyUtil.getPersonStrategy().searchPersonsByName(name);
	}

	/**
	 * @see org.openmrs.module.fhir.api.PersonService#createFHIRPerson(Person)
	 */
	@Override
	public Person createFHIRPerson(Person person) {
		return PersonStrategyUtil.getPersonStrategy().createFHIRPerson(person);
	}

	/**
	 * @see org.openmrs.module.fhir.api.PersonService#updateFHIRPerson(Person, String)
	 */
	@Override
	public Person updateFHIRPerson(Person person, String uuid) {
		return PersonStrategyUtil.getPersonStrategy().updateFHIRPerson(person, uuid);
	}

	/**
	 * @see org.openmrs.module.fhir.api.PersonService#retirePerson(String)
	 */
	@Override
	public void retirePerson(String uuid) throws ResourceNotFoundException, NotModifiedException {
		PersonStrategyUtil.getPersonStrategy().retirePerson(uuid);
	}
}
