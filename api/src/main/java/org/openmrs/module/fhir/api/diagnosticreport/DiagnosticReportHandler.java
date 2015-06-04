package org.openmrs.module.fhir.api.diagnosticreport;

import ca.uhn.fhir.model.dstu2.resource.DiagnosticReport;

public interface DiagnosticReportHandler {
	
	public DiagnosticReport generateFHIRDiagnosticReport(DiagnosticReportTemplate omrsDiagnosticReport);
	
	public DiagnosticReportTemplate generateOpenMRSDiagnosticReport(DiagnosticReport fhirDiagnosticReport);
	
	public String getId();
}
