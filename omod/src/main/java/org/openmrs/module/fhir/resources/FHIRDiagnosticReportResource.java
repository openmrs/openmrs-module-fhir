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

import ca.uhn.fhir.model.dstu2.resource.DiagnosticReport;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.DiagnosticReportService;

import java.util.List;

public class FHIRDiagnosticReportResource extends Resource {

	public DiagnosticReport getByUniqueId(IdDt id) {
		DiagnosticReportService diagnosticReportService = Context.getService(DiagnosticReportService.class);
		DiagnosticReport fhirDiagnosticReport = diagnosticReportService.getDiagnosticReport(id.getIdPart());
		if (fhirDiagnosticReport == null) {
			throw new ResourceNotFoundException("Diagnostic Report is not found for the given Id " + id.getValue());
		}
		return fhirDiagnosticReport;
	}

	public DiagnosticReport createFHIRDiagnosticReport(DiagnosticReport diagnosticReport) {
		DiagnosticReportService diagnosticReportService = Context.getService(DiagnosticReportService.class);
		return diagnosticReportService.createFHIRDiagnosticReport(diagnosticReport);
	}

	public DiagnosticReport updateFHIRDiagnosticReport(DiagnosticReport diagnosticReport, String theId) {
		DiagnosticReportService diagnosticReportService = Context.getService(DiagnosticReportService.class);
		return diagnosticReportService.updateFHIRDiagnosticReport(diagnosticReport, theId);
	}

	public void retireDiagnosticReport(IdDt id) {
		DiagnosticReportService diagnosticReportService = Context.getService(DiagnosticReportService.class);
		diagnosticReportService.retireDiagnosticReport(id.getIdPart());
	}

	public List<DiagnosticReport> getDiagnosticReportByPatientNameAndServiceCategory(ReferenceParam theSubject,
	                                                                                 TokenParam service) {
		String name = null;
		String chain = theSubject.getChain();
		if (Patient.SP_GIVEN.equals(chain)) {
			name = theSubject.getValue();
		}
		if (name == null) {
			throw new ResourceNotFoundException("Subject Name is not found for " + theSubject.getValue());
		}

		DiagnosticReportService diagnosticReportService = Context.getService(DiagnosticReportService.class);
		return diagnosticReportService.getDiagnosticReportByPatientNameAndServiceCategory(name, service.getValue());
	}

}
