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

import java.util.List;

import ca.uhn.fhir.model.dstu2.resource.Person;

public interface PersonService {
	
	/**
	 * Get fhir perso resource by uuid
	 *
	 * @param id uuid of the patient
	 * @return fhir patient resource and will return null if patient not found for the given id
	 */
	Person getPerson(String id);
	
	/**
	 * Search persons by uuid
	 *
	 * @param id the uuid to be search
	 * @return fhir patient resource list
	 */
	List<Person> searchPersonById(String id);
	
	/**
	 * Search all persons for given attributes
	 *
	 * @param name Name of person to search
	 * @param birthYear The year of birth to restrict
	 * @param gender The gender field to search on (Typically just "M" or "F")
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
	 * @param theId the uuid of the Person to be updated
	 * @return the updated FHIR {@link}Person Resource
	 */
	Person updateFHIRPerson(Person person, String theId);
	
}
