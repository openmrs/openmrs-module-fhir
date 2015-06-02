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

import ca.uhn.fhir.model.dstu2.resource.DiagnosticReport;
import ca.uhn.fhir.model.dstu2.resource.Person;
import ca.uhn.fhir.rest.server.exceptions.NotModifiedException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;

import java.util.List;

public interface DiagnosticReportService {

	/**
	 * Get FHIR Diagnostic Report resource by uuid
	 *
	 * @param id uuid of the Diagnostic Report
	 * @return FHIR Diagnostic Report resource and will return null if patient not found for the given id
	 */
	DiagnosticReport getDiagnosticReport(String id);
	
	/**
	 * Creates OpenMRS objects from FHIR Diagnostic Report
	 *
	 * @param diagReport
	 * @return
	 */
	DiagnosticReport createFHIRDiagnosticReport(DiagnosticReport diagReport);
	
	/**
	 * Delete Diagnostic Report by id
	 *
	 * @param id uuid of the Diagnostic Report
	 * @return Diagnostic Report FHIR resource
	 */
	public void deleteDiagnosticReport(String id);
}
