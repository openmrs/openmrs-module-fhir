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
package org.openmrs.module.fhir.providers;

import ca.uhn.fhir.rest.annotation.ConditionalUrlParam;
import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.Delete;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.annotation.Update;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.PreconditionFailedException;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Person;
import org.hl7.fhir.dstu3.model.Resource;
import org.openmrs.module.fhir.resources.FHIRPersonResource;
import org.openmrs.module.fhir.util.MethodOutcomeBuilder;

import java.util.List;

public class RestfulPersonResourceProvider implements IResourceProvider {

	private FHIRPersonResource personResource;

	private static final String ERROR_MESSAGE = "No Person is associated with the given UUID to update."
			+ " Please make sure you have set at lease one non-delete name, Gender and Birthdate to create a new "
			+ "Person with the given UUID";

	public RestfulPersonResourceProvider() {
		this.personResource = new FHIRPersonResource();
	}

	@Override
	public Class<? extends Resource> getResourceType() {
		return Person.class;
	}

	/**
	 * The "@Read" annotation indicates that this method supports the read operation. Read
	 * operations should return a single resource instance.
	 *
	 * @param theId The read operation takes one parameter, which must be of type IdDt and must be
	 *              annotated with the "@Read.IdParam" annotation.
	 * @return Returns a resource matching this identifier, or null if none exists.
	 */
	@Read
	public Person getResourceById(@IdParam IdType theId) {
		return personResource.getByUniqueId(theId);
	}

	/**
	 * Search person by unique id
	 *
	 * @param id object containing the requested person
	 */
	@Search
	public List<Person> findPractitionerByUniqueId(@RequiredParam(name = Person.SP_RES_ID) TokenParam id) {
		return personResource.searchByUniqueId(id);
	}

	/**
	 * Search persons by name, birthYear and gender
	 *
	 * @param name      Name of person to search
	 * @param birthYear The year of birth to restrict
	 * @param gender    The gender field to search on (Typically just "M" or "F")
	 * @return This method returns a list of Persons. This list may contain multiple matching
	 * resources, or it may also be empty.
	 */
	@Search
	public List<Person> findPersons(@RequiredParam(name = Person.SP_NAME) StringParam name,
	                                 @RequiredParam(name = Person.SP_BIRTHDATE) DateParam birthDate,
	                                 @RequiredParam(name = Person.SP_GENDER) StringParam gender) {
		Integer birthYear = 1900 + birthDate.getValue().getYear(); // e.g. 2011-01-02
		return personResource.searchPersons(name.getValue(), birthYear, gender);
	}

	/**
	 * Search persons by name
	 *
	 * @param name name of persons
	 * @return This method returns a list of Persons. This list may contain multiple matching
	 * resources, or it may also be empty.
	 */
	@Search
	public List<Person> findPersonsByName(@RequiredParam(name = Person.SP_NAME) StringParam name) {
		return personResource.searchByName(name);
	}

	/**
	 * Create person
	 *
	 * @param person fhir person object
	 * @return This method returns a list of Persons. This list may contain multiple matching
	 * resources, or it may also be empty.
	 */
	@Create
	public MethodOutcome createFHIRPerson(@ResourceParam Person person) {
		return MethodOutcomeBuilder.buildCreate(personResource.createFHIRPerson(person));
	}

	@Update
	public MethodOutcome updatePersonConditional(@ResourceParam Person thePerson, @IdParam IdType theId) {
		try {
			return MethodOutcomeBuilder.buildUpdate(personResource.updateFHIRPerson(thePerson, theId.getIdPart()));
		} catch (Exception e) {
			return MethodOutcomeBuilder.buildCustom(ERROR_MESSAGE);
		}
	}

	/**
	 * Delete person by unique id
	 *
	 * @param theId object containing the id
	 */
	@Delete
	public void deletePerson(@IdParam IdType theId) {
		personResource.deletePerson(theId);
	}
	
	/**
	 * Update Person by name.
	 *
	 * @param person {@link ca.uhn.fhir.model.dstu2.resource.Person} object provided by the
	 *            {@link ca.uhn.fhir .rest.server.RestfulServer}
	 * @param theId Only one of theId or theConditional will have a value and the other will be
	 *            null, depending on the URL passed into the server
	 * @param theConditional This will have a value like "Person?name=John
	 * @return MethodOutcome which contains the status of the operation
	 */
	@Update
	public MethodOutcome updatePersonByName(@ResourceParam Person person, @IdParam IdType theId,
	                                        @ConditionalUrlParam String theConditional) {
		if (theConditional != null) {
			int startIndex = theConditional.lastIndexOf('=');
			List<Person> personList = personResource.searchPersons(theConditional.substring(startIndex + 1), null, null);
			if (personList.isEmpty()) {
				return updatePersonConditional(person, null);
			} else if (personList.size() == 1) {
				IdType id = new IdType();
				id.setValue(personList.get(0).getId());
				return updatePersonConditional(person, id);
			} else {
				throw new PreconditionFailedException("There are more than one person for the given name");
			}
		} else {
			return updatePersonConditional(person, theId);
		}
	}
}
