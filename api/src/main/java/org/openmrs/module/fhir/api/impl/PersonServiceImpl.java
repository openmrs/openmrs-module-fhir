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

import ca.uhn.fhir.model.dstu2.resource.Person;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.server.exceptions.MethodNotAllowedException;
import ca.uhn.fhir.rest.server.exceptions.NotModifiedException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.PersonService;
import org.openmrs.module.fhir.api.db.FHIRDAO;
import org.openmrs.module.fhir.api.util.FHIRPersonUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class PersonServiceImpl implements PersonService {

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

	@Override
	public Person getPerson(String id) {
		org.openmrs.Person omrsPerson = Context.getPersonService().getPersonByUuid(id);
		if (omrsPerson == null) {
			return null;
		}
		return FHIRPersonUtil.generatePerson(omrsPerson);
	}

	@Override
	public List<Person> searchPersonById(String id) {
		org.openmrs.Person omrsPerson = Context.getPersonService().getPersonByUuid(id);
		List<Person> personList = new ArrayList<Person>();
		if (omrsPerson != null) {
			personList.add(FHIRPersonUtil.generatePerson(omrsPerson));
		}
		return personList;
	}

	@Override
	public List<Person> searchPersons(String name, Integer birthYear, String gender) {
		Set<org.openmrs.Person> persons = Context.getPersonService().getSimilarPeople(name, birthYear, gender);
		List<Person> fhirPersonsList = new ArrayList<Person>();
		for (org.openmrs.Person person : persons) {
			fhirPersonsList.add(FHIRPersonUtil.generatePerson(person));
		}
		return fhirPersonsList;
	}

	@Override
	public List<Person> searchPersonsByName(String name) {
		List<org.openmrs.Person> persons = Context.getPersonService().getPeople(name, null);
		List<Person> fhirPersonsList = new ArrayList<Person>();
		for (org.openmrs.Person person : persons) {
			fhirPersonsList.add(FHIRPersonUtil.generatePerson(person));
		}
		return fhirPersonsList;
	}

	@Override
	public Person createFHIRPerson(Person person) {
		org.openmrs.Person omrsPerson = FHIRPersonUtil.generateOpenMRSPerson(person);
		org.openmrs.api.PersonService personService = Context.getPersonService();
		omrsPerson = personService.savePerson(omrsPerson);
		return FHIRPersonUtil.generatePerson(omrsPerson);
	}

	@Override
	public Person updateFHIRPerson(Person thePerson, String theId) {
		org.openmrs.api.PersonService personService = Context.getPersonService();
		org.openmrs.Person retrievedPerson = personService.getPersonByUuid(theId);
		if (retrievedPerson != null) { // update person
			org.openmrs.Person omrsPerson = FHIRPersonUtil.generateOpenMRSPerson(thePerson);
			retrievedPerson = FHIRPersonUtil.updatePersonAttributes(omrsPerson, retrievedPerson);
			Context.getPersonService().savePerson(retrievedPerson);
			return FHIRPersonUtil.generatePerson(retrievedPerson);
		} else { // no person is associated with the given uuid. so create a new person with the given uuid
			if (thePerson.getId() == null) { // since we need to PUT the Person to a specific URI, we need to set the uuid here, if it is not already set.
				IdDt uuid = new IdDt();
				uuid.setValue(theId);
				thePerson.setId(uuid);
			}
			return createFHIRPerson(thePerson);
		}
	}

	/**
	 * @see org.openmrs.module.fhir.api.PersonService#retirePerson(String)
	 */
	@Override
	public void retirePerson(String id) throws ResourceNotFoundException, NotModifiedException {
		org.openmrs.Person person = Context.getPersonService().getPersonByUuid(id);
		if (person == null) throw new ResourceNotFoundException(String.format("Person with id '%s' not found", id));
		if (person.isPersonVoided()) return;
		try {
			Context.getPersonService().voidPerson(person, "Voided by FHIR Request");
		} catch (APIException apie) {
			throw new MethodNotAllowedException(String.format("OpenMRS has failed to retire person '%s': %s", id, apie.getMessage()));
		}
	}
}
