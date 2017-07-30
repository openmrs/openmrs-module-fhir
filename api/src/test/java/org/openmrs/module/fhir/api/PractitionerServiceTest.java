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

import org.hl7.fhir.dstu3.model.Enumerations;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Person;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.hl7.fhir.dstu3.model.StringType;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.PersonName;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.FHIRPersonUtil;
import org.openmrs.module.fhir.exception.FHIRValidationException;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PractitionerServiceTest extends BaseModuleContextSensitiveTest {

	protected static final String PRACTITIONER_INITIAL_DATA_XML = "org/openmrs/api/include/ProviderServiceTest-initial.xml";
	
	protected static final String PERSOM_INITIAL_DATA_XML = "org/openmrs/api/include/PersonServiceTest-createPersonPurgeVoidTest.xml";

	public PractitionerService getService() {
		return Context.getService(PractitionerService.class);
	}

	@Before
	public void runBeforeEachTest() throws Exception {
		executeDataSet(PRACTITIONER_INITIAL_DATA_XML);
		executeDataSet(PERSOM_INITIAL_DATA_XML);
	}

	@Test
	public void shouldSetupContext() {
		assertNotNull(getService());
	}

	@Test
	public void getPractitioner_shouldReturnResourceIfExists() {
		String practitionerUuid = "a3a5913e-6b94-11e0-93c3-18a905e044dc";
		Practitioner fhirPractitioner = getService().getPractitioner(practitionerUuid);
		assertNotNull(fhirPractitioner);
		assertEquals(practitionerUuid, fhirPractitioner.getId().toString());
	}

	@Test
	public void SearchPractitionerById_shouldReturnBundle() throws FHIRValidationException {
		String practitionerUuid = "a3a5913e-6b94-11e0-93c3-18a905e044dc";
		List<Practitioner> practitionerList = getService().searchPractitionersById(practitionerUuid);
		assertNotNull(practitionerList);
		assertEquals(practitionerList.size(), 1);
		assertEquals(practitionerUuid, practitionerList.get(0).getId().toString());
	}

	@Test
	public void searchPractitionersByName_shouldReturnBundle() throws FHIRValidationException {
		String name = "RobertClive";
		String practitionerUuid = "a2c3868a-6b90-11e0-93c3-18a905e044dc";
		List<Practitioner> practitionerList = getService().searchPractitionersByName(name);
		assertNotNull(practitionerList);
		assertEquals(practitionerList.size(), 1);
		assertEquals(practitionerUuid, practitionerList.get(0).getId().toString());
	}

	@Test
	public void searchPractitionersByGivenName_shouldReturnBundle() throws FHIRValidationException {
		String givenName = "Collet";
		String practitionerUuid = "ba4781f4-6b94-11e0-93c3-18a905e044dc";
		List<Practitioner> practitionerList = getService().searchPractitionersByGivenName(givenName);
		assertNotNull(practitionerList);
		assertEquals(practitionerList.size(), 1);
		assertEquals(practitionerUuid, practitionerList.get(0).getId().toString());
	}

	@Test
	public void searchPractitionersByIdentifier_shouldReturnBundle() throws FHIRValidationException {
		String identifier = "8A762";
		String practitionerUuid = "ae401f88-6b94-11e0-93c3-18a905e044dc";
		List<Practitioner> practitionerList = getService().searchPractitionersByIdentifier(identifier);
		assertNotNull(practitionerList);
		assertEquals(practitionerList.size(), 1);
		assertEquals(practitionerUuid, practitionerList.get(0).getId().toString());
	}
	
	@Test
	public void createPractitioner_shoulcreateNewPerson() throws FHIRValidationException {
		Practitioner practitioner = new Practitioner();
		
		HumanName fhirName = new HumanName();
		fhirName.setFamily("xxx");
		StringType givenName = new StringType();
		givenName.setValue("yyy");
		List<StringType> givenNames = new ArrayList<StringType>();
		givenNames.add(givenName);
		fhirName.setGiven(givenNames);
		List<HumanName> names = new ArrayList<HumanName>();
		names.add(fhirName);
		practitioner.setName(names);
		
		practitioner.setGender(Enumerations.AdministrativeGender.MALE);
		Date bdate = new Date();
		practitioner.setBirthDate(bdate);
		
		List<Identifier> identifiers = new ArrayList<Identifier>();
		Identifier identifier = new Identifier();
		identifier.setValue("fhirTest");
		identifiers.add(identifier);
		practitioner.setIdentifier(identifiers);
		
		Practitioner practitionerNew = getService().createFHIRPractitioner(practitioner);
		assertNotNull(practitionerNew);
		List<HumanName> humanNameDts = practitionerNew.getName();
		String fmlyName = humanNameDts.get(0).getFamily();
		assertEquals(fmlyName, "xxx");
		List<StringType> gvnNames =  humanNameDts.get(0).getGiven();
		assertEquals(gvnNames.get(0).getValue(), "yyy");
		assertEquals(practitionerNew.getGender(), Enumerations.AdministrativeGender.MALE);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		assertEquals(dateFormat.format(practitionerNew.getBirthDate()), dateFormat.format(bdate.getTime()));
		identifiers = practitionerNew.getIdentifier();
		identifier = identifiers.get(0);
		assertEquals(identifier.getValue(), "fhirTest");
	}
	
	@Test
	public void createPractitioner_shoulNotcreateNewPerson() throws FHIRValidationException {
		String personUuid = "dagh524f-27ce-4bb2-86d6-6d1d05312bd5";
		org.openmrs.Person person = Context.getPersonService().getPersonByUuid(personUuid);
		Person personfhir = FHIRPersonUtil.generatePerson(person);
		
		Practitioner practitioner = new Practitioner();
		practitioner.setGender(personfhir.getGenderElement().getValue());
		practitioner.setBirthDate(personfhir.getBirthDate());
		
		practitioner.setName(personfhir.getName());
		
		List<Identifier> identifiers = new ArrayList<Identifier>();
		Identifier idnt = new Identifier();
		idnt.setValue("fhirTest");
		identifiers.add(idnt);
		practitioner.setIdentifier(identifiers);
		
		Practitioner practitionerNew = getService().createFHIRPractitioner(practitioner);
		
		Set<PersonName> naa = person.getNames();
		PersonName name = null;
		for (Iterator<PersonName> naam = naa.iterator(); naam.hasNext();) {
			name = naam.next();
		}
		
		Set<org.openmrs.Person> personList = Context.getPersonService().getSimilarPeople(name.getFullName(),
		    1900 + person.getBirthdate().getYear(), person.getGender());
		assertEquals(personList.size(), 1); // which means no new person created for the given representation.
											// It has mapped the representation to a existing person.
	}
}
