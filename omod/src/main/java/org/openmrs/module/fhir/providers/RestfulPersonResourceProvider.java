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

import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu2.resource.OperationOutcome;
import ca.uhn.fhir.model.dstu2.resource.Person;
import ca.uhn.fhir.model.primitive.IdDt;
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
import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.openmrs.module.fhir.resources.FHIRPersonResource;

import java.util.List;

public class RestfulPersonResourceProvider implements IResourceProvider {

	private FHIRPersonResource personResource;

	public RestfulPersonResourceProvider() {
		this.personResource = new FHIRPersonResource();
	}

	@Override
	public Class<? extends IResource> getResourceType() {
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
	@Read()
	public Person getResourceById(@IdParam IdDt theId) {
		Person result = null;
		result = personResource.getByUniqueId(theId);
		return result;
	}

	/**
	 * Search person by unique id
	 *
	 * @param id object contaning the requested person
	 */
	@Search()
	public List<Person> searchPractitionerByUniqueId(@RequiredParam(name = Person.SP_RES_ID) TokenParam id) {
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
	@Search()
	public List<Person> findPersonts(@RequiredParam(name = Person.SP_NAME) StringParam name,
	                                 @RequiredParam(name = Person.SP_BIRTHDATE) DateParam birthDate,
	                                 @RequiredParam(name = Person.SP_GENDER) StringParam gender) {
		Integer birthYear = 1900 + birthDate.getValue().getYear(); // e.g. 2011-01-02
		return personResource.searchPersons(name, birthYear, gender);
	}

	/**
	 * Search persons by name
	 *
	 * @param name name of persons
	 * @return This method returns a list of Persons. This list may contain multiple matching
	 * resources, or it may also be empty.
	 */
	@Search()
	public List<Person> findPersontsByName(@RequiredParam(name = Person.SP_NAME) StringParam name) {
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
		person = personResource.createFHIRPerson(person);
		MethodOutcome retVal = new MethodOutcome();
		retVal.setId(new IdDt(FHIRConstants.PERSON, person.getId().getIdPart()));
		OperationOutcome outcome = new OperationOutcome();
		outcome.addIssue().setDetails("Person is successfully created");
		retVal.setOperationOutcome(outcome);
		return retVal;
	}

	@Update
	public MethodOutcome updatePersonConditional(@ResourceParam Person thePerson, @IdParam IdDt theId) {
		MethodOutcome retVal = new MethodOutcome();
		OperationOutcome outcome = new OperationOutcome();
		try {
			Person person = personResource.updateFHIRPerson(thePerson, theId.getIdPart());
		} catch (Exception e) {
			outcome.addIssue()
					.setDetails(
							"No Person is associated with the given UUID to update. Please"
							+ " make sure you have set at lease one non-delete name, Gender and Birthdate to create a new "
							+ "Person with the given UUID");
			retVal.setOperationOutcome(outcome);
			return retVal;
		}
		outcome.addIssue().setDetails("Person is successfully updated");
		retVal.setOperationOutcome(outcome);
		return retVal;
	}

	/**
	 * Delete person by unique id
	 *
	 * @param theId object containing the id
	 */
	@Delete()
	public void deletePerson(@IdParam IdDt theId) {
		personResource.deletePerson(theId);
	}
}
