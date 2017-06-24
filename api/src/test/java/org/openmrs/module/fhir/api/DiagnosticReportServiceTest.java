/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.fhir.api;

import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.diagnosticreport.DiagnosticReportHandler;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DiagnosticReportServiceTest extends BaseModuleContextSensitiveTest {

	public DiagnosticReportService getService() {
		return Context.getService(DiagnosticReportService.class);
	}

	@Test
	public void shouldSetupContext() {
		assertNotNull(getService());
	}

	/**
	 * @see DiagnosticReportService#getHandler(String)
	 */
	@Test
	@Verifies(value = "should have default laboratory and radiology handlers registered by spring", method = "getHandler"
			+ "(String)")
	public void getHandler_shouldHaveDefaultLaboratoryAndRadiologyHandlersRegisteredBySpring() throws Exception {
		DiagnosticReportService service = getService();

		DiagnosticReportHandler defaultDiagnosticReportHandler = service.getHandler("DEFAULT");
		assertNotNull(defaultDiagnosticReportHandler);
		assertEquals("DEFAULT", defaultDiagnosticReportHandler.getServiceCategory());

		DiagnosticReportHandler laboratoryHandler = service.getHandler("LAB");
		assertNotNull(laboratoryHandler);
		assertEquals("LAB", laboratoryHandler.getServiceCategory());

		DiagnosticReportHandler radiologyHandler = service.getHandler("RAD");
		assertNotNull(radiologyHandler);
		assertEquals("RAD", radiologyHandler.getServiceCategory());
	}

}
