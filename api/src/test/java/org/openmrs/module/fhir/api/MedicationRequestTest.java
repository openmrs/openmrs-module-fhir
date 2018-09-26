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

import org.hl7.fhir.dstu3.model.MedicationRequest;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.openmrs.module.fhir.exception.FHIRValidationException;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MedicationRequestTest extends BaseModuleContextSensitiveTest {

	protected static final String DRUG_ORDER_INITIAL_DATA_XML = "DrugOrders_customTestData.xml";

	public MedicationRequestService getService() {
		return Context.getService(MedicationRequestService.class);
	}

	@Before
	public void runBeforeEachTest() throws Exception {
		executeDataSet(DRUG_ORDER_INITIAL_DATA_XML);
	}

	@Test
	public void shouldSetupContext() {
		assertNotNull(getService());
	}

	@Test
	public void getMedicationRequest_shouldReturnResourceIfExists() {
		String medicationRequestUuid = "56b9196c-bcac-4c2f-b3a2-123464a96439";
		MedicationRequest medicationRequest = getService().getMedicationRequestById(medicationRequestUuid);
		assertNotNull(medicationRequest);
		assertEquals(medicationRequestUuid, medicationRequest.getId().toString());
	}

	@Test
	public void searchMedicationRequestById_shouldReturnBundle() throws FHIRValidationException {
		String medicationRequestUuid = "56b9196c-bcac-4c2f-b3a2-123464a96439";
		List<MedicationRequest> medicationRequests = getService().searchMedicationRequestById(medicationRequestUuid);
		assertNotNull(medicationRequests);
		assertEquals(medicationRequests.size(), 1);
		assertEquals(medicationRequestUuid, medicationRequests.get(0).getId().toString());
	}

	@Test
	public void searchMedicationRequestByPatientId_shouldReturnBundle() throws FHIRValidationException {
		Patient patient = Context.getPatientService().getPatient(2);
		String patientUuid = patient.getUuid();
		List<MedicationRequest> medicationRequests = getService().searchMedicationRequestByPatientId(patientUuid);
		assertNotNull(medicationRequests);
		assertEquals(medicationRequests.size(), 10);
	}

	@Test
	public void deleteMedicationRequest_shouldDeleteTheSpecifiedMedicationRequest() {
		org.openmrs.api.OrderService orderService = Context.getOrderService();
		org.openmrs.Order order = orderService.getOrder(2000);
		assertNotNull(order);
		orderService.voidOrder(order, FHIRConstants.FHIR_VOIDED_MESSAGE);
		order = orderService.getOrder(2000);
		assertTrue(order.isVoided());
	}

	@Test
	public void createMedicationRequest_shouldCreateNewMedicationRequest() throws FHIRValidationException {
		String medicationRequestUuid = "56b9196c-bcac-4c2f-b3a2-123464a96439";
		MedicationRequest medicationRequest = getService().getMedicationRequestById(medicationRequestUuid);
		assertNotNull(medicationRequest);
		MedicationRequest createdMedicationRequest = getService().createFHIRMedicationRequest(medicationRequest);
		assertNotNull(createdMedicationRequest);
		assertEquals(medicationRequest.getSubject().getReference(), createdMedicationRequest.getSubject().getReference());
		assertEquals(medicationRequest.getContext().getReference(), createdMedicationRequest.getContext().getReference());
		assertEquals(medicationRequest.getRecorder().getReference(), createdMedicationRequest.getRecorder().getReference());
		assertEquals(medicationRequest.getRequester().getAgent().getReference(),
														createdMedicationRequest.getRequester().getAgent().getReference());

	}
}
