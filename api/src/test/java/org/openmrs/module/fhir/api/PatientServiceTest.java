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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.exception.FHIRValidationException;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.Patient;

public class PatientServiceTest extends BaseModuleContextSensitiveTest {

	protected static final String PAT_INITIAL_DATA_XML = "org/openmrs/api/include/PatientServiceTest-createPatient.xml";
	protected static final String PAT_SEARCH_DATA_XML = "org/openmrs/api/include/PatientServiceTest-findPatients.xml";

	public PatientService getService() {
		return Context.getService(PatientService.class);
	}

	@Before
	public void runBeforeEachTest() throws Exception {
		executeDataSet(PAT_INITIAL_DATA_XML);
		executeDataSet(PAT_SEARCH_DATA_XML);
	}

	@Test
	public void shouldSetupContext() {
		assertNotNull(getService());
	}

	@Test
	public void getPatient_shouldReturnResourceIfExists() throws FHIRValidationException {
		String patientUuid = "61b38324-e2fd-4feb-95b7-9e9a2a4400df";
		Patient fhirPatient = getService().getPatient(patientUuid);
		assertNotNull(fhirPatient);
		assertEquals(fhirPatient.getId().toString(), patientUuid);

	}

	@Test
	public void searchPatientsById_shouldReturnBundleIfExists() throws FHIRValidationException {
		String patientUuid = "61b38324-e2fd-4feb-95b7-9e9a2a4400df";
		List<Patient> patients = getService().searchPatientsById(patientUuid);
		assertNotNull(patients);
		assertEquals(1, patients.size());
		Patient fhirPatient = patients.get(0);
		assertEquals(fhirPatient.getId().toString(), patientUuid);
	}

	@Test
	public void searchPatientsByIdentifier_shouldReturnBundle() {
		String returned_patientUuid = "61b38324-e2fd-4feb-95b7-9e9a2a4400df";
		String identifierValue = "1234";
		List<Patient> patients = getService().searchPatientsByIdentifier(identifierValue);
		assertNotNull(patients);
		assertEquals(1, patients.size());
		Patient fhirPatient = patients.get(0);
		assertEquals(fhirPatient.getId().toString(), returned_patientUuid);
		boolean exist = false;
		for (Patient patient : patients) {
			for (IdentifierDt identifierDt : patient.getIdentifier()) {
				if (identifierValue.equals(identifierDt.getValue())) {
					exist = true;
				}
			}
		}
		assertTrue(exist);
	}

	@Test
	public void searchPatientsByIdentifierAndIdentifierType_shouldReturnBundle() {
		String returned_patientUuid = "61b38324-e2fd-4feb-95b7-9e9a2a4400df";
		String identifierValue = "1234";
		String identifierTypeName = "Test Identifier Type";
		List<Patient> patients = getService().searchPatientsByIdentifier(identifierValue, identifierTypeName);
		assertNotNull(patients);
		assertEquals(1, patients.size());
		Patient fhirPatient = patients.get(0);
		assertEquals(fhirPatient.getId().toString(), returned_patientUuid);
		boolean exist = false;
		for (Patient patient : patients) {
			for (IdentifierDt identifierDt : patient.getIdentifier()) {
				if (identifierValue.equals(identifierDt.getValue())) {
					exist = true;
				}
			}
		}
		assertTrue(exist);
	}

	@Test
	public void searchPatientsByName_shouldReturnBundle() {
		String name = "Jean";
		Bundle patients = getService().searchPatientsByName(name);
		assertNotNull(patients);
		assertEquals(3, patients.getEntry().size());
	}

	@Test
	public void searchPatientsByGivenName_shouldReturnBundle() {
		String name = "Jean";
		Bundle patients = getService().searchPatientsByGivenName(name);
		assertNotNull(patients);
		assertEquals(1, patients.getEntry().size());
	}

	@Test
	public void searchPatientsByFamilyName_shouldReturnBundle() {
		String name = "Doe";
		Bundle patients = getService().searchPatientsByFamilyName(name);
		assertNotNull(patients);
		assertEquals(3, patients.getEntry().size());
	}

	@Test
	public void searchActivePatients_shouldReturnBundle() {
		List<Patient> patients = getService().searchPatients(true);
		assertNotNull(patients);
		assertEquals(6, patients.size());
	}

	@Test
	public void searchInActivePatients_shouldReturnBundle() {
		List<Patient> patients = getService().searchPatients(false);
		assertNotNull(patients);
		assertEquals(3, patients.size());
	}
	
	@Test
	public void shouldFetchAllPatientsByName() {
		List<org.openmrs.Patient> patients = Context.getPatientService().getPatients("Jeannette", null, null, true);
		assertNotNull(patients);
		assertEquals(1, patients.size());
	}

	@Test
	public void getPatientEverything_shouldAllClinicalDataBelongsToPatient() {
		String returnedPatientUuid = "61b38324-e2fd-4feb-95b7-9e9a2a4400df";
		Bundle patientContent = getService().getPatientOperationsById(returnedPatientUuid);
		assertNotNull(patientContent);
	}

	@Test
	public void deletePatient_ShouldRetirePatientIfExists() {
		String patientUuid = "61b38324-e2fd-4feb-95b7-9e9a2a4400df";
		org.openmrs.Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);
		assertNotNull(patient);
		assertFalse(patient.isVoided());
		getService().deletePatient(patientUuid);
		assertTrue(patient.isVoided());

	}
}
