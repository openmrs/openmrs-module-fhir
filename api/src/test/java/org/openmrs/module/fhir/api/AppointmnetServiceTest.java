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

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import ca.uhn.fhir.model.dstu2.resource.Appointment;

@Ignore
public class AppointmnetServiceTest extends BaseModuleContextSensitiveTest {

	protected static final String APPOINMENT_INITIAL_DATA_XML = "org/openmrs/api/include/ProviderServiceTest-initial.xml";

	public AppointmentService getService() {
		return Context.getService(AppointmentService.class);
	}

	@Before
	public void runBeforeEachTest() throws Exception {
		executeDataSet(APPOINMENT_INITIAL_DATA_XML);
	}

	@Test
	public void shouldSetupContext() {
		assertNotNull(getService());
	}

	@Test
	public void getPractitioner_shouldReturnResourceIfExists() {
		String appointmentUUid = "c0c579b0-8e59-401d-8a4a-976a0b183601";
		Appointment fhirAppointment = getService().getAppointmentById(appointmentUUid);
		assertNotNull(fhirAppointment);
		assertEquals(appointmentUUid, fhirAppointment.getId().toString());
	}
}
