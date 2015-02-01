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

import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu.resource.Practitioner;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.exception.FHIRValidationException;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class PractitionerServiceTest extends BaseModuleContextSensitiveTest {

	protected static final String PRACTITIONER_INITIAL_DATA_XML = "org/openmrs/api/include/ProviderServiceTest-initial.xml";

	public PractitionerService getService() {
		return Context.getService(PractitionerService.class);
	}

	@Before
	public void runBeforeEachTest() throws Exception {
		executeDataSet(PRACTITIONER_INITIAL_DATA_XML);
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
}
