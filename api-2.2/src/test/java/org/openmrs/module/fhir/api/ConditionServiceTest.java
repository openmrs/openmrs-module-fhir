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

import org.hl7.fhir.dstu3.model.Annotation;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Condition;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Reference;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.openmrs.module.fhir.api.util.FHIRUtils;
import org.openmrs.module.fhir.util.FHIRConditionUtil2_2;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ConditionServiceTest extends BaseModuleContextSensitiveTest {

	private static final String CONCEPT_CUSTOM_INITIAL_DATA_XML = "Concept_customTestData.xml";

	private static final String CONDITION_UUID = "75f5b373-5065-11de-80cb-001e378eb67f";

	private static final String PATIENT_SEARCH_DATA_XML = "org/openmrs/api/include/PatientServiceTest-findPatients.xml";

	private static final String CREATE_PATIENT_XML = "org/openmrs/api/include/PatientServiceTest-createPatient.xml";

	private ConditionService getService() {
		return Context.getService(ConditionService.class);
	}

	@Before
	public void runBeforeEachTest() {
		executeDataSet(CREATE_PATIENT_XML);
		executeDataSet(PATIENT_SEARCH_DATA_XML);
		executeDataSet(CONCEPT_CUSTOM_INITIAL_DATA_XML);
		updateSearchIndex();
	}

	@Test
	public void shouldSetupContext() {
		assertNotNull(getService());
	}

	@Test
	public void getConditionByUuid_shouldReturnSavedCondition() {
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

		Condition justCreatedCondition = getService().createFHIRCondition(condition);

		Condition conditionGetResult = getService().getConditionByUuid(justCreatedCondition.getId());

		assertNotNull(conditionGetResult);
		assertEquals(conditionGetResult.getId(), justCreatedCondition.getId());
		assertEquals(justCreatedCondition.getClinicalStatus(), conditionGetResult.getClinicalStatus());
		assertEquals(justCreatedCondition.getCode().getText(), conditionGetResult.getCode().getText());
		assertEquals(justCreatedCondition.getSubject().getReference(), conditionGetResult.getSubject().getReference());
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

		Condition justCreatedCondition = getService().createFHIRCondition(condition);
		Condition getSavedCondition = getService().getConditionByUuid(justCreatedCondition.getId());

		assertNotNull(justCreatedCondition);
		assertEquals(justCreatedCondition.getSubject().getReference(), patientReference.getReference());
		assertEquals(justCreatedCondition.getClinicalStatus(), Condition.ConditionClinicalStatus.ACTIVE);
		assertEquals(justCreatedCondition.getOnset(), condition.getOnset());

		assertNotNull(getSavedCondition);
		assertEquals(justCreatedCondition.getId(), getSavedCondition.getId());
		assertEquals(justCreatedCondition.getClinicalStatus(), getSavedCondition.getClinicalStatus());
		assertEquals(justCreatedCondition.getOnset(), getSavedCondition.getOnset());
	}

	@Test
	public void getConditionsByPatientUuid_shouldReturnListOfActiveConditions() {
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

		List<Condition> activeConditions = getService().getConditionsByPatientUuid(patient.getUuid());
		assertTrue(activeConditions.isEmpty());

		getService().createFHIRCondition(condition);

		activeConditions = getService().getConditionsByPatientUuid(patient.getUuid());
		assertNotNull(activeConditions);
		assertFalse(activeConditions.isEmpty());
		assertEquals(activeConditions.get(0).getClinicalStatus().getDisplay(), "Active");
		assertEquals(activeConditions.get(0).getClinicalStatus().name(), "ACTIVE");
	}

	@Test
	public void updateFHIRCondition_shouldReturnUpdatedCondition() {
		Condition condition = new Condition();
		IdType id = new IdType();
		id.setValue(CONDITION_UUID);
		condition.setId(id);
		Patient patient = Context.getPatientService().getPatient(2);
		Reference patientReference = FHIRUtils.buildPatientOrPersonResourceReference(patient);
		condition.setSubject(patientReference);
		condition.setClinicalStatus(Condition.ConditionClinicalStatus.ACTIVE);
		condition.setVerificationStatus(Condition.ConditionVerificationStatus.PROVISIONAL);
		List<Annotation> annotations = new ArrayList<>();
		annotations.add(new Annotation().setText("test data"));
		condition.setNote(annotations);
		condition.setAssertedDate(new Date());
		condition.setCode(new CodeableConcept().addCoding(
				new Coding().setCode("999").setDisplay("NO").setSystem(FHIRConstants.OPENMRS_URI)).setText("NO"));

		Condition savedCondition = getService().createFHIRCondition(condition);
		assertNotNull(savedCondition);
		assertEquals(savedCondition.getClinicalStatus(), Condition.ConditionClinicalStatus.ACTIVE);

		savedCondition = getService().getConditionByUuid(savedCondition.getId());
		assertNotNull(savedCondition);
		assertEquals(savedCondition.getClinicalStatus(), Condition.ConditionClinicalStatus.ACTIVE);

		Condition fhirCondition = new Condition();
		fhirCondition.setId(savedCondition.getId());
		fhirCondition.setSubject(patientReference);
		fhirCondition.setClinicalStatus(Condition.ConditionClinicalStatus.INACTIVE);
		fhirCondition.setCode(new CodeableConcept().addCoding(
				new Coding().setCode("999").setDisplay("NO").setSystem(FHIRConstants.OPENMRS_URI)).setText("NO"));
		fhirCondition.setVerificationStatus(Condition.ConditionVerificationStatus.CONFIRMED);
		fhirCondition.setNote(FHIRConditionUtil2_2.getListOfAnnotations("test updated data"));
		fhirCondition.setAssertedDate(new Date());

		Condition updatedCondition = getService().updateFHIRCondition(fhirCondition);
		assertNotNull(updatedCondition);
		assertEquals(updatedCondition.getClinicalStatus(), Condition.ConditionClinicalStatus.INACTIVE);
		assertEquals(updatedCondition.getAssertedDate(), fhirCondition.getAssertedDate());
		assertEquals(updatedCondition.getNote().size(), fhirCondition.getNote().size());
		assertEquals(updatedCondition.getVerificationStatus(), fhirCondition.getVerificationStatus());
		assertEquals(updatedCondition.getNote().get(0).getText(), fhirCondition.getNote().get(0).getText());

		updatedCondition = getService().getConditionByUuid(updatedCondition.getId());
		assertNotNull(updatedCondition);
		assertEquals(updatedCondition.getClinicalStatus(), Condition.ConditionClinicalStatus.INACTIVE);
		assertEquals(updatedCondition.getAssertedDate(), fhirCondition.getAssertedDate());
		assertEquals(updatedCondition.getNote().size(), fhirCondition.getNote().size());
		assertEquals(updatedCondition.getVerificationStatus(), fhirCondition.getVerificationStatus());
		assertEquals(updatedCondition.getNote().get(0).getText(), fhirCondition.getNote().get(0).getText());

	}
}
