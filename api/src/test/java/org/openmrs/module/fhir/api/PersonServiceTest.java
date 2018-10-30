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

import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.Enumerations;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Person;
import org.hl7.fhir.dstu3.model.StringType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.PersonAddress;
import org.openmrs.PersonName;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.FHIRPersonUtil;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class PersonServiceTest extends BaseModuleContextSensitiveTest {

	protected static final String PERSON_INITIAL_DATA_XML =
			"org/openmrs/api/include/PersonServiceTest-createPersonPurgeVoidTest.xml";

	public PersonService getService() {
		return Context.getService(PersonService.class);
	}

	@Before
	public void runBeforeEachTest() throws Exception {
		executeDataSet(PERSON_INITIAL_DATA_XML);
		updateSearchIndex();
	}

	@Test
	public void shouldSetupContext() {
		assertNotNull(getService());
	}

	@Test
	public void getPerson_shouldReturnResourceIfExists() {
		String personUuid = "dagh524f-27ce-4bb2-86d6-6d1d05312bd5";
		Person fhirPerson = getService().getPerson(personUuid);
		assertNotNull(fhirPerson);
		assertEquals(fhirPerson.getId().toString(), personUuid);

	}

	@Test
	public void searchPatientsById_shouldReturnBundleIfExists() {
		String personUuid = "dagh524f-27ce-4bb2-86d6-6d1d05312bd5";
		List<Person> persons = getService().searchPersonByUuid(personUuid);
		assertNotNull(persons);
		assertEquals(1, persons.size());
		Person fhirPerson = persons.get(0);
		assertEquals(fhirPerson.getId().toString(), personUuid);
	}

	@Test
	public void searchPersons_shouldReturnBundle() {
		String name = "Anet";
		Integer birthYear = 1975;
		String gender = "M";
		List<Person> persons = getService().searchPersons(name, birthYear, gender);
		assertNotNull(persons);
		assertEquals(1, persons.size());
	}

	@Test
	public void searchPersonsByName_shouldReturnBundle() {
		String name = "Anet";
		List<Person> persons = getService().searchPersonsByName(name);
		assertNotNull(persons);
		assertEquals(2, persons.size());
	}

	/**
	 * @verifies generate oms person
	 */
	@Test
	public void generateOpenMRSPerson_shouldGenerateOmrsPerson() throws Exception {
		String personUuid = "dagh524f-27ce-4bb2-86d6-6d1d05312bd5";
		org.openmrs.Person person = Context.getPersonService().getPersonByUuid(personUuid);
		person.setUuid(""); // remove the uuid value from the Person. This will let this
		// resource to be persist on the db with random uuid
		Person fhirPerson = FHIRPersonUtil.generatePerson(person);
		fhirPerson = Context.getService(PersonService.class).createFHIRPerson(fhirPerson);
		assertNotNull(fhirPerson);
	}

	/**
	 * @verifies update Person, where there is no person associates with the uuid
	 */
	@Test
	public void updatePerson_shouldGenerateOmrsPerson() throws Exception {
		String personUuid = "dagh524f-27ce-4bb2-86d6-6d1d05312bd5";
		org.openmrs.Person person = Context.getPersonService().getPersonByUuid(personUuid);
		Person fhirPerson = FHIRPersonUtil.generatePerson(person);
		String requestUuid = "vvvv524f-27ce-4bb2-86d6-6d1d05312bd5";
		IdType uuid = new IdType();
		uuid.setValue(requestUuid); // set a uuid which is not associated with any Person
		fhirPerson.setId(uuid);
		Context.getService(PersonService.class).updateFHIRPerson(fhirPerson, requestUuid);
		org.openmrs.Person retrievedPerson = Context.getPersonService().getPersonByUuid(requestUuid);
		assertNotNull(retrievedPerson);
	}

	/**
	 * @verifies update Person
	 */
	@Test
	public void updatePerson_shouldUpdateOmrsPerson() throws Exception {
		String personUuid = "dagh524f-27ce-4bb2-86d6-6d1d05312bd5";
		org.openmrs.Person person = Context.getPersonService().getPersonByUuid(personUuid);
		Person fhirPerson = FHIRPersonUtil.generatePerson(person);

		fhirPerson.setGender(Enumerations.AdministrativeGender.FEMALE);

		List<HumanName> humanNames = new ArrayList<>();
		List<StringType> givenNames = new ArrayList<>();

		HumanName fhirName = new HumanName();
		fhirName.setFamily("Bais");

		StringType givenName = new StringType();
		givenName.setValue("cope");

		givenNames.add(givenName);
		fhirName.setGiven(givenNames);
		fhirName.setUse(HumanName.NameUse.USUAL);
		humanNames.add(fhirName);
		fhirPerson.setName(humanNames);

		List<Address> addressList = new ArrayList<>();
		Address fhirAddress = new Address();
		fhirAddress.setCity("abc");
		fhirAddress.setCountry("bcd");
		fhirAddress.setState("cde");
		fhirAddress.setPostalCode("def");
		List<StringType> addressStrings = new ArrayList<>();
		addressStrings.add(new StringType("pqr"));
		addressStrings.add(new StringType("qrs"));
		addressStrings.add(new StringType("rst"));
		addressStrings.add(new StringType("stu"));
		addressStrings.add(new StringType("tuv"));
		fhirAddress.setLine(addressStrings);
		fhirAddress.setUse(Address.AddressUse.HOME);
		addressList.add(fhirAddress);
		fhirPerson.setAddress(addressList);

		List<String> errorList = new ArrayList<>();
		org.openmrs.Person omrsPerson = FHIRPersonUtil.generateOpenMRSPerson(fhirPerson, errorList);
		person = FHIRPersonUtil.updatePersonAttributes(omrsPerson, person);
		assertNotNull(person);
		assertEquals(person.getPersonName(), omrsPerson.getPersonName());
		assertEquals(person.getPersonAddress(), omrsPerson.getPersonAddress());
		assertEquals(person.getPersonVoided(), omrsPerson.getPersonVoided());
		assertEquals(person.getBirthdate(), omrsPerson.getBirthdate());
		assertEquals(person.getGender(), omrsPerson.getGender());
	}

	/**
	 * @verifies(value="make person void", method="retirePerson(String))
	 */
	@Test
	public void retirePerson_shouldMakePersonVoid() {
		String personId = "dagh524f-27ce-4bb2-86d6-6d1d05312bd5";
		org.openmrs.Person person = Context.getPersonService().getPersonByUuid(personId);
		assertFalse(person.getPersonVoided());
		Context.getService(PersonService.class).retirePerson(personId);
		person = Context.getPersonService().getPersonByUuid(personId);
		assertTrue(person.getPersonVoided());
	}

	/**
	 * @verifies(value="throw ResourceNotFoundException if person with given id not found", method="retirePerson(String))
	 */
	@Test(expected = ResourceNotFoundException.class)
	public void retirePerson_shouldthrowResourceNotFoundExceptionIfPersonWithGivenIdNotFound() {
		String personId = "--Not exists--";
		Context.getService(PersonService.class).retirePerson(personId);
		fail("attempt to void non existent user should throw an exception");
	}

	/**
	 * @verifies(value="do nothing if person already void", method="retirePerson(String))
	 */
	@Test
	public void retirePerson_shouldDoNothingIfPersonAlreadyVoid() {
		//No idea how to test it
	}

	/**
	 * @verifies(value="throw MethodNotAllowedException if API has refused the operation", method="retirePerson(String))
	 */
	@Test
	public void retirePerson_shouldThrowMethodNotAllowedExceptionIfAPIHasRefusedTheOperation() {
		//No idea how to test it
	}
}
