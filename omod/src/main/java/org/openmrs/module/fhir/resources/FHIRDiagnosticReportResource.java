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
package org.openmrs.module.fhir.resources;

import ca.uhn.fhir.model.base.composite.BaseCodingDt;
import ca.uhn.fhir.model.dstu2.resource.DiagnosticReport;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.dstu2.resource.Person;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenOrListParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;

import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.DiagnosticReportService;
import org.openmrs.module.fhir.api.ObsService;
import org.openmrs.module.fhir.api.PersonService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FHIRDiagnosticReportResource extends Resource {
	
	public DiagnosticReport getByUniqueId(IdDt id) {
		DiagnosticReportService diagnosticReportService = Context.getService(DiagnosticReportService.class);
		DiagnosticReport fhirDiagnosticReport = diagnosticReportService.getDiagnosticReport(id.getIdPart());
		if (fhirDiagnosticReport == null) {
			throw new ResourceNotFoundException("Diagnostic Report is not found for the given Id " + id.getValue());
		}
		return fhirDiagnosticReport;
	}
	
	public DiagnosticReport createFHIRDiagReport(DiagnosticReport diagReport) {
		DiagnosticReportService diagnosticReportService = Context.getService(DiagnosticReportService.class);
		return diagnosticReportService.createFHIRDiagReport(diagReport);
	}
	
	public void deleteDiagnosticReport(IdDt id) {
		DiagnosticReportService diagnosticReportService = Context.getService(DiagnosticReportService.class);
		diagnosticReportService.deleteDiagnosticReport(id.getIdPart());
	}
	
}
