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

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Condition;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Reference;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.GlobalProperty;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.openmrs.module.fhir.api.util.FHIRUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * This test class will test the functionalities of the Condition Service.
 */
public class ConditionServiceTest extends BaseModuleContextSensitiveTest {

	private static final String OBS_INITIAL_DATA_XML = "org/openmrs/api/include/ObsServiceTest-initial.xml";

	private static final String CONCEPT_CUSTOM_INITIAL_DATA_XML = "Concept_customTestData.xml";

	private static final String CONDITION_MAPPING_CONCEPT_ID = "1";

	private static final String CONDITION_CONCEPTS = "fhir.concepts.conditions";

	private static final String PATIENT_INITIAL_DATA_XML = "org/openmrs/api/include/PatientServiceTest-createPatient.xml";

	private static final String PATIENT_SEARCH_DATA_XML = "org/openmrs/api/include/PatientServiceTest-findPatients.xml";

	private static final String PATIENT_UUID = "61b38324-e2fd-4feb-95b7-9e9a2a4400df";

	private static final String CONCEPT_UUID = "4a5048b1-cf85-4c64-9339-7cab41e5e364";

	private static final String CONDITION_UUID = "75f5b373-5065-11de-80cb-001e378eb67f";

	private ConditionService getService() {
		return Context.getService(ConditionService.class);
	}

	@Before
	public void runBeforeEachTest() throws Exception {
		executeDataSet(OBS_INITIAL_DATA_XML);
		executeDataSet(CONCEPT_CUSTOM_INITIAL_DATA_XML);
		executeDataSet(PATIENT_INITIAL_DATA_XML);
		executeDataSet(PATIENT_SEARCH_DATA_XML);
		updateSearchIndex();
	}

	@Test
	public void shouldSetupContext() {
		assertNotNull(getService());
	}

	@Test
	public void createFHIRCondition_shouldReturnCreatedFHIRCondition() {
		Condition condition = new Condition();
		IdType id = new IdType();
		id.setValue(CONDITION_UUID);
		condition.setId(id);
		Patient patient = Context.getPatientService().getPatient(2);
		Reference patientReference = FHIRUtils.buildPatientOrPersonResourceReference(patient);
		condition.setSubject(patientReference);
		condition.setClinicalStatus(Condition.ConditionClinicalStatus.ACTIVE);
		condition.setCode(new CodeableConcept().addCoding(
				new Coding().setCode("999").setDisplay("NO").setSystem(FHIRConstants.OPENMRS_URI)).setText("NO"));

		Condition resCondition = getService().createFHIRCondition(condition);

		assertNotNull(resCondition);
		assertEquals(resCondition.getId(), id.getValue());
		assertEquals(resCondition.getSubject().getReference(), patientReference.getReference());
		assertEquals(resCondition.getCode().getCoding().get(0).getDisplay(), "NO");
	}

	@Test
	public void getConditionByUuid_shouldReturnSavedCondition() {
		Context.getAdministrationService().saveGlobalProperty(
				new GlobalProperty(CONDITION_CONCEPTS, CONDITION_MAPPING_CONCEPT_ID));

		Date openmrsDate = new Date();
		Person patient = Context.getPersonService().getPersonByUuid(PATIENT_UUID);
		Concept concept = Context.getConceptService().getConceptByUuid(CONCEPT_UUID);

		Obs openmrsObs = new Obs(patient, concept, openmrsDate, null);
		openmrsObs.setValueNumeric(8d);
		openmrsObs = Context.getObsService().saveObs(openmrsObs, null);

		Condition fhirConditionForAddedObs = getService().getConditionByUuid(openmrsObs.getUuid());
		Coding fhirCoding = fhirConditionForAddedObs.getCode().getCoding().get(0);

		assertNotNull(fhirConditionForAddedObs);
		assertEquals(patient.getUuid(), fhirConditionForAddedObs.getSubject().getId());
		assertEquals(fhirCoding.getSystem(), FHIRConstants.OPENMRS_URI);
		assertEquals(fhirCoding.getCode(), openmrsObs.getConcept().getUuid());
		assertEquals(fhirCoding.getDisplay(), openmrsObs.getConcept().getName().getName());
	}
}
