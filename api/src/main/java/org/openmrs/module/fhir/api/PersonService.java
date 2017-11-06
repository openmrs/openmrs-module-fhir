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
package org.openmrs.module.fhir.api;

import ca.uhn.fhir.rest.server.exceptions.NotModifiedException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.dstu3.model.Person;

import java.util.List;

public interface PersonService {

	/**
	 * Get fhir person resource by uuid
	 *
	 * @param uuid 	The uuid of the patient.
	 * @return fhir patient resource and will return null if patient not found for the given uuid
	 */
	Person getPerson(String uuid);

	/**
	 * Search persons by uuid
	 *
	 * @param uuid the uuid to be search
	 * @return fhir patient resource list
	 */
	List<Person> searchPersonByUuid(String uuid);

	/**
	 * Search all persons for given attributes
	 *
	 * @param name      Name of person to search
	 * @param birthYear The year of birth to restrict
	 * @param gender    The gender field to search on (Typically just "M" or "F")
	 * @return persons list
	 */
	List<Person> searchPersons(String name, Integer birthYear, String gender);

	/**
	 * Search persons by name
	 *
	 * @param name the name to be search
	 * @return fhir persons resource list
	 */
	List<Person> searchPersonsByName(String name);

	/**
	 * creates a oms Person from FHIR personn
	 *
	 * @param person
	 * @return
	 */
	Person createFHIRPerson(Person person);

	/**
	 * update a OpenMRS Person from FHIR Person
	 *
	 * @param person the FHIR representation of the {@link}Person to be updated
	 * @param uuid  the uuid of the Person to be updated
	 * @return the updated FHIR {@link}Person Resource
	 */
	Person updateFHIRPerson(Person person, String uuid);

	/**
	 * makes a Person retired
	 *
	 * @param uuid the uuid of the Person to retire
	 * @should make person void
	 * @should throw ResourceNotFoundException if person with given id not found
	 * @should do nothing if person already void
	 * @should throw MethodNotAllowedException if API has refused the operation
	 */
	void retirePerson(String uuid) throws ResourceNotFoundException, NotModifiedException;
}
