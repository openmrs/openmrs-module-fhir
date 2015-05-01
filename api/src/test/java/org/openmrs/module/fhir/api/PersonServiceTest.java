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

import ca.uhn.fhir.model.dstu2.composite.AddressDt;
import ca.uhn.fhir.model.dstu2.composite.HumanNameDt;
import ca.uhn.fhir.model.dstu2.resource.Person;
import ca.uhn.fhir.model.dstu2.valueset.AddressUseEnum;
import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
import ca.uhn.fhir.model.dstu2.valueset.NameUseEnum;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.StringDt;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.PersonAddress;
import org.openmrs.PersonName;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.FHIRPersonUtil;
import org.openmrs.module.fhir.exception.FHIRValidationException;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class PersonServiceTest extends BaseModuleContextSensitiveTest {

	protected static final String PERSOM_INITIAL_DATA_XML = "org/openmrs/api/include/PersonServiceTest-createPersonPurgeVoidTest.xml";

	public PersonService getService() {
		return Context.getService(PersonService.class);
	}

	@Before
	public void runBeforeEachTest() throws Exception {
		executeDataSet(PERSOM_INITIAL_DATA_XML);
	}

	@Test
	public void shouldSetupContext() {
		assertNotNull(getService());
	}

	@Test
	public void getPerson_shouldReturnResourceIfExists() throws FHIRValidationException {
		String personUuid = "dagh524f-27ce-4bb2-86d6-6d1d05312bd5";
		Person fhirPerson = getService().getPerson(personUuid);
		assertNotNull(fhirPerson);
		assertEquals(fhirPerson.getId().toString(), personUuid);

	}

	@Test
	public void searchPatientsById_shouldReturnBundleIfExists() throws FHIRValidationException {
		String personUuid = "dagh524f-27ce-4bb2-86d6-6d1d05312bd5";
		List<Person> persons = getService().searchPersonById(personUuid);
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
	public void generateOpenMRSPerson_shouldGenerateOmsPerson() throws Exception {
		String personUuid = "dagh524f-27ce-4bb2-86d6-6d1d05312bd5";
		org.openmrs.Person person = Context.getPersonService().getPersonByUuid(personUuid);
		person.setUuid(""); // remove the uuid value from the Person. This will let this resource to be persist on the db with random uuid
		Person fhirPerson = FHIRPersonUtil.generatePerson(person);
		fhirPerson = Context.getService(PersonService.class).createFHIRPerson(fhirPerson);
		assertNotNull(fhirPerson);
	}

	/**
	 * @verifies update Person, where there is no person associates with the uuid
	 */
	@Test
	public void updateperson_shouldGenerateOmsPerson() throws Exception {
		String personUuid = "dagh524f-27ce-4bb2-86d6-6d1d05312bd5";
		org.openmrs.Person person = Context.getPersonService().getPersonByUuid(personUuid);
		Person fhirPerson = FHIRPersonUtil.generatePerson(person);
		String requestnUuid = "vvvv524f-27ce-4bb2-86d6-6d1d05312bd5";
		IdDt uuid = new IdDt();
		uuid.setValue(requestnUuid); // set a uuid which is not associated with any Person
		fhirPerson.setId(uuid);
		fhirPerson = Context.getService(PersonService.class).updateFHIRPerson(fhirPerson, requestnUuid);
		org.openmrs.Person retrievedPerson = Context.getPersonService().getPersonByUuid(requestnUuid);
		assertNotNull(retrievedPerson);
	}

	/**
	 * @verifies update Person
	 */
	@Test
	public void updateperson_shouldUpdateOmsPerson() throws Exception {
		String personUuid = "dagh524f-27ce-4bb2-86d6-6d1d05312bd5";
		org.openmrs.Person person = Context.getPersonService().getPersonByUuid(personUuid);
		Person fhirPerson = FHIRPersonUtil.generatePerson(person);
		person.setUuid(null);
		List<HumanNameDt> humanNames = new ArrayList<HumanNameDt>(); // add a new name to update
		HumanNameDt fhirName = new HumanNameDt();
		StringDt familyName = new StringDt();
		familyName.setValue("Bais");
		List<StringDt> familyNames = new ArrayList<StringDt>();
		familyNames.add(familyName);
		fhirName.setFamily(familyNames);
		StringDt givenName = new StringDt();
		givenName.setValue("cope");
		List<StringDt> givenNames = new ArrayList<StringDt>();
		givenNames.add(givenName);
		fhirName.setGiven(givenNames);
		fhirName.setUse(NameUseEnum.USUAL);
		humanNames.add(fhirName);
		fhirPerson.setName(humanNames);
		fhirPerson.setGender(AdministrativeGenderEnum.FEMALE); // change the gender
		fhirPerson.setActive(false); // delete the person		
		List<AddressDt> addressList = new ArrayList<AddressDt>(); // add a new address to update
		AddressDt fhirAddress = new AddressDt();
		fhirAddress.setCity("abc");
		fhirAddress.setCountry("bcd");
		fhirAddress.setState("cde");
		fhirAddress.setPostalCode("def");
		List<StringDt> addressStrings = new ArrayList<StringDt>();
		addressStrings.add(new StringDt("pqr"));
		addressStrings.add(new StringDt("qrs"));
		addressStrings.add(new StringDt("rst"));
		addressStrings.add(new StringDt("stu"));
		addressStrings.add(new StringDt("tuv"));
		fhirAddress.setLine(addressStrings);
		fhirAddress.setUse(AddressUseEnum.HOME);
		addressList.add(fhirAddress);
		fhirPerson.setAddress(addressList);
		//update the person
		fhirPerson = Context.getService(PersonService.class).updateFHIRPerson(fhirPerson, personUuid);
		//retreive the updated person
		org.openmrs.Person updatedPerson = Context.getPersonService().getPersonByUuid(personUuid);
		assertNotNull(updatedPerson);
		// check whether each attribute updated correctly
		for (PersonName name : updatedPerson.getNames()) {
			if (name.isPreferred()) {
				Assert.assertEquals(name.getGivenName(), "cope");
				Assert.assertEquals(name.getFamilyName(), "Bais");
			}
		}
		for (PersonAddress addrss : updatedPerson.getAddresses()) {
			if (fhirAddress.getUse().equalsIgnoreCase(String.valueOf(String.valueOf(AddressUseEnum.HOME)))) {
				Assert.assertEquals(addrss.getCityVillage(), "abc");
				Assert.assertEquals(addrss.getCountry(), "bcd");
				Assert.assertEquals(addrss.getStateProvince(), "cde");
				Assert.assertEquals(addrss.getPostalCode(), "def");
				Assert.assertEquals(addrss.getAddress1(), "pqr");
				Assert.assertEquals(addrss.getAddress2(), "qrs");
				Assert.assertEquals(addrss.getAddress3(), "rst");
				Assert.assertEquals(addrss.getAddress4(), "stu");
				Assert.assertEquals(addrss.getAddress5(), "tuv");
			}
		}
		Assert.assertEquals(updatedPerson.getVoided(), false);
		Assert.assertEquals(updatedPerson.getGender(), "F");
	}

	/**
	 * @verifies(value="make person void", method="retirePerson(String))
	 */
	@Test
	public void retirePerson_shouldMakePersonVoid() {
		String personId = "dagh524f-27ce-4bb2-86d6-6d1d05312bd5";
		org.openmrs.Person person = Context.getPersonService().getPersonByUuid(personId);
		assertFalse(person.isVoided());
		Context.getService(PersonService.class).retirePerson(personId.toString());
		person = Context.getPersonService().getPersonByUuid(personId);
		assertTrue(person.isVoided());
	}

	/**
	 * @verifies(value="throw ResourceNotFoundException if person with given id not found", method="retirePerson(String))
	 */
	@Test(expected = ResourceNotFoundException.class)
	public void retirePerson_shouldthrowResourceNotFoundExceptionIfPersonWithGivenIdNotFound() {
		String personId = "--Not exists--";
		Context.getService(PersonService.class).retirePerson(personId.toString());
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
