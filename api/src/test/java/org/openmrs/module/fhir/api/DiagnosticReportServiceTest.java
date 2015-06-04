/**
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

import ca.uhn.fhir.model.dstu2.composite.AddressDt;
import ca.uhn.fhir.model.dstu2.resource.Location;
import ca.uhn.fhir.model.dstu2.valueset.LocationStatusEnum;

import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.diagnosticreport.DiagnosticReportHandler;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
	@Verifies(value = "should have default laboratory and radiology handlers registered by spring", method = "getHandler(String)")
	public void getHandler_shouldHaveDefaultImageAndTextHandlersRegisteredBySpring() throws Exception {
		DiagnosticReportService service = getService();
		
		DiagnosticReportHandler laboratoryHandler = service.getHandler("LaboratoryHandler");
		assertNotNull(laboratoryHandler);
		System.out.println(laboratoryHandler.getId());
		laboratoryHandler.generateOpenMRSDiagnosticReport(null); //do stuff
		
		DiagnosticReportHandler radiologyHandler = service.getHandler("RadiologyHandler");
		assertNotNull(radiologyHandler);
		System.out.println(radiologyHandler.getId());
	}
	
}
