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
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.openmrs.module.fhir.api.util.FHIRUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ConditionServiceTest extends BaseModuleContextSensitiveTest {

	private static final String CONCEPT_CUSTOM_INITIAL_DATA_XML = "Concept_customTestData.xml";

	private static final String CONDITION_UUID = "75f5b373-5065-11de-80cb-001e378eb67f";

	private ConditionService getService() {
		return Context.getService(ConditionService.class);
	}

	@Before
	public void runBeforeEachTest() {
		executeDataSet(CONCEPT_CUSTOM_INITIAL_DATA_XML);
		updateSearchIndex();
	}

	@Test
	public void shouldSetupContext() {
		assertNotNull(getService());
	}

	@Test
	public void getConditionById_shouldReturnSavedCondition() {
		Condition condition = new Condition();

		IdType id = new IdType();
		id.setValue(CONDITION_UUID);
		condition.setId(id);
		Patient patientRef = Context.getPatientService().getPatient(2);
		Reference patient = FHIRUtils.buildPatientOrPersonResourceReference(patientRef);
		condition.setSubject(patient);
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
		Patient patientRef = Context.getPatientService().getPatient(2);
		Reference patient = FHIRUtils.buildPatientOrPersonResourceReference(patientRef);
		condition.setSubject(patient);
		condition.setClinicalStatus(Condition.ConditionClinicalStatus.ACTIVE);
		condition.setCode(new CodeableConcept().addCoding(
				new Coding().setCode("999").setDisplay("NO").setSystem(FHIRConstants.OPENMRS_URI)).setText("NO"));

		Condition justCreatedCondition = getService().createFHIRCondition(condition);
		Condition getSavedCondition = getService().getConditionByUuid(justCreatedCondition.getId());

		assertNotNull(justCreatedCondition);
		assertEquals(justCreatedCondition.getSubject().getReference(), patient.getReference());
		assertEquals(justCreatedCondition.getClinicalStatus(), Condition.ConditionClinicalStatus.ACTIVE);
		assertEquals(justCreatedCondition.getOnset(), condition.getOnset());

		assertNotNull(getSavedCondition);
		assertEquals(justCreatedCondition.getId(), getSavedCondition.getId());
		assertEquals(justCreatedCondition.getClinicalStatus(), getSavedCondition.getClinicalStatus());
		assertEquals(justCreatedCondition.getOnset(), getSavedCondition.getOnset());
	}
}
