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

import ca.uhn.fhir.model.dstu.resource.Observation;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ObsServiceTest extends BaseModuleContextSensitiveTest {

	protected static final String OBS_INITIAL_DATA_XML = "org/openmrs/api/include/ObsServiceTest-initial.xml";

	public ObsService getService() {
		return Context.getService(ObsService.class);
	}

	@Before
	public void runBeforeEachTest() throws Exception {
		executeDataSet(OBS_INITIAL_DATA_XML);
	}

	@Test
	public void shouldSetupContext() {
		assertNotNull(getService());
	}

	@Test
	public void getObservation_shouldReturnResourceIfExists() {
		String obsUuid = "be3a4d7a-f9ab-47bb-aaad-bc0b452fcda4";
		Observation fhirObservation = getService().getObs(obsUuid);
		assertNotNull(fhirObservation);
		assertEquals(fhirObservation.getId().toString(), obsUuid);
	}

}
