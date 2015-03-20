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

import ca.uhn.fhir.model.dstu2.resource.Composition;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class EncounterServiceTest extends BaseModuleContextSensitiveTest {
	protected static final String ENCOUNTER_INITIAL_DATA_XML = "org/openmrs/api/include/EncounterServiceTest-initialData.xml";
	protected static final String VISIT_INITIAL_DATA_XML = "org/openmrs/api/include/VisitServiceTest-includeVisitsAndTypeToAutoClose.xml";

	public EncounterService getService() {
		return Context.getService(EncounterService.class);
	}

	@Before
	public void runBeforeEachTest() throws Exception {
		executeDataSet(ENCOUNTER_INITIAL_DATA_XML);
		executeDataSet(VISIT_INITIAL_DATA_XML);
	}

	@Test
	public void getFHIREncounterFromOmrsEncounter_shouldReturnResourceIfExists() {
		String encounterUuid = "33d70956-b359-452a-b3da-b69c8ab459ce";
		Encounter fhirEncounter = getService().getEncounter(encounterUuid);
		assertNotNull(fhirEncounter);
		assertEquals(fhirEncounter.getId().toString(), encounterUuid);
	}

	@Test
	public void getFHIREncounterFromOmrsVisit_shouldReturnResourceIfExists() {
		String visitUuid = "4c48b0c0-1ade-11e1-9c71-00248140a5eb";
		Encounter fhirEncounter = getService().getEncounter(visitUuid);
		assertNotNull(fhirEncounter);
		assertEquals(fhirEncounter.getId().toString(), visitUuid);
	}

	@Test
	public void searchEncounterByIdFromOmrsEncounter_shouldReturnBundle() {
		String encounterUuid = "33d70956-b359-452a-b3da-b69c8ab459ce";
		List<Encounter> fhirEncounters = getService().searchEncounterById(encounterUuid);
		assertNotNull(fhirEncounters);
		assertEquals(1, fhirEncounters.size());
	}

	@Test
	public void searchEncounterByIdFromOmrsVisit_shouldReturnBundle() {
		String visitUuid = "4c48b0c0-1ade-11e1-9c71-00248140a5eb";
		List<Encounter> fhirEncounters = getService().searchEncounterById(visitUuid);
		assertNotNull(fhirEncounters);
		assertEquals(1, fhirEncounters.size());
	}

	@Test
	public void searchEncountersByPatientIdentifier_shouldReturnBundle() {
		String identifier = "12345";
		List<Encounter> fhirEncounters = getService().searchEncountersByPatientIdentifier(identifier);
		assertNotNull(fhirEncounters);
		assertEquals(2, fhirEncounters.size());
	}

	@Test
	public void searchEncounterComposition_shouldReturnBundle() {
		String encounterUuid = "33d70956-b359-452a-b3da-b69c8ab459ce";
		List<Composition> fhirCompositon = getService().searchEncounterComposition(encounterUuid);
		assertNotNull(fhirCompositon);
		assertEquals(1, fhirCompositon.size());
	}

	@Test
	public void searchEncounterCompositionByPatient_shouldReturnBundle() {
		String personUuid = "4b3f42da-2029-4e47-9396-a1b6a969e802";
		List<Composition> fhirCompositon = getService().searchEncounterCompositionByPatient(personUuid);
		assertNotNull(fhirCompositon);
		assertEquals(2, fhirCompositon.size());
	}
}
