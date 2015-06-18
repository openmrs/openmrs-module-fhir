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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.FHIRPersonUtil;
import org.openmrs.module.fhir.exception.FHIRValidationException;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import ca.uhn.fhir.model.dstu2.composite.HumanNameDt;
import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.resource.Person;
import ca.uhn.fhir.model.dstu2.resource.Practitioner;
import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
import ca.uhn.fhir.model.primitive.DateDt;
import ca.uhn.fhir.model.primitive.StringDt;

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
	public void createPractitioner_shoulcreatePractioner() throws FHIRValidationException {
		String personUuid = "dagh524f-27ce-4bb2-86d6-6d1d05312bd5";
		org.openmrs.Person person = Context.getPersonService().getPersonByUuid(personUuid);
		Person personfhir = FHIRPersonUtil.generatePerson(person);
		
		Practitioner practitioner = new Practitioner();
		
		HumanNameDt fhirName = new HumanNameDt();
		StringDt familyName = new StringDt();
		familyName.setValue("xxx");
		List<StringDt> familyNames = new ArrayList<StringDt>();
		familyNames.add(familyName);
		fhirName.setFamily(familyNames);
		StringDt givenName = new StringDt();
		givenName.setValue("yyy");
		List<StringDt> givenNames = new ArrayList<StringDt>();
		givenNames.add(givenName);
		fhirName.setGiven(givenNames);
		practitioner.setName(fhirName);
		
		practitioner.setGender(AdministrativeGenderEnum.MALE);
		Date bdate = new Date();
		DateDt fhirBirthDate = new DateDt();
		fhirBirthDate.setValue(bdate);
		practitioner.setBirthDate(fhirBirthDate);
		
		List<IdentifierDt> identifiers = new ArrayList<IdentifierDt>();
		IdentifierDt idnt = new IdentifierDt();
		idnt.setValue("fhirTest");
		identifiers.add(idnt);
		practitioner.setIdentifier(identifiers);
		
		Practitioner practitionerNew = getService().createFHIRPractitioner(practitioner);
		assertNotNull(practitionerNew);
		HumanNameDt humanNameDt = practitionerNew.getName();
		List<StringDt> fmlyNames = humanNameDt.getFamily();
		assertEquals(fmlyNames.get(0).getValue(), "xxx");
		List<StringDt> gvnNames = humanNameDt.getGiven();
		assertEquals(gvnNames.get(0).getValue(), "yyy");
		assertEquals(practitionerNew.getGender(), "male");
		assertEquals(practitionerNew.getBirthDate(), bdate);
		List<IdentifierDt> idtifiers = practitionerNew.getIdentifier();
		IdentifierDt ident = identifiers.get(0);
		assertEquals(ident.getValue(), "fhirTest");
	}
}
