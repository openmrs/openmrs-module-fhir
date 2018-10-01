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

import org.hl7.fhir.dstu3.model.AllergyIntolerance;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author leonard
 */
@Ignore
public class AllergyIntoleranceServiceTest extends BaseModuleContextSensitiveTest {

	protected static final String ALLERGY_INITIAL_DATA_XML = "AllergyIntoleranceServiceTest-initialData.xml";

	protected static final String PAT_SEARCH_DATA_XML = "org/openmrs/api/include/PatientServiceTest-findPatients.xml";

	public AllergyIntoleranceService getService() {
		return Context.getService(AllergyIntoleranceService.class);
	}

	@Before
	public void runBeforeEachTest() throws Exception {
		executeDataSet(ALLERGY_INITIAL_DATA_XML);
		executeDataSet(PAT_SEARCH_DATA_XML);
	}

	@Test
	public void shouldSetupContext() {
		assertNotNull(getService());
	}

	/**
	 * Test of getAllergyById method, of class AllergyIntoleranceService.
	 */
	@Test
	public void getAllergyByIds_shouldReturnAllergyIfExists() {
		String allergyUuid = "1234567987";
		AllergyIntolerance allergy = getService().getAllergyById(allergyUuid);
		assertNotNull(allergy);
		assertEquals(allergy.getId().toString(), allergyUuid);
	}

	/**
	 * Test of searchAllergiesById method, of class AllergyIntoleranceService.
	 */
	@Test
	public void searchAllergiesById_shouldReturnMatchingAllergyIntoleranceList() {
		String allergyUuid = "1234567987";
		List<AllergyIntolerance> fhirAllergies = getService().searchAllergiesById(allergyUuid);
		assertNotNull(fhirAllergies);
		assertEquals(fhirAllergies.get(0).getId(), allergyUuid);
	}

	/**
	 * Test of searchAllergiesByPatientIdentifier method, of class AllergyIntoleranceService.
	 */
	@Test
	public void searchAllergiesByPatientIdentifier_shouldReturnMatchingAllergyIntoleranceList() {
		String allergyUuid = "1234567987";
		String identifierValue = "1234";
		List<AllergyIntolerance> allergies = getService().searchAllergiesByPatientIdentifier(identifierValue);
		assertNotNull(allergies);
		assertEquals(1, allergies.size());
		AllergyIntolerance allergy = allergies.get(0);
		assertEquals(allergy.getId().toString(), allergyUuid);
	}

	/**
	 * Test of searchAllergiesByPatientName method, of class AllergyIntoleranceService.
	 */
	@Test
	public void searchAllergiesByPatientName_shouldReturnMatchingAllergyIntoleranceList() {
		String patientName = "John";
		String allergyUuid = "1234567987";
		List<AllergyIntolerance> allergies = getService().searchAllergiesByPatientName(patientName);
		assertNotNull(allergies);
		assertEquals(1, allergies.size());
		AllergyIntolerance allergy = allergies.get(0);
		assertEquals(allergy.getId().toString(), allergyUuid);
	}

}
