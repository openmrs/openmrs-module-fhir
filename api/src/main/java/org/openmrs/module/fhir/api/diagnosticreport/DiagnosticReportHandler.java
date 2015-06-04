package org.openmrs.module.fhir.api.diagnosticreport;

import ca.uhn.fhir.model.dstu2.resource.DiagnosticReport;

public interface DiagnosticReportHandler {
	
	public DiagnosticReport generateFHIRDiagnosticReport(DiagnosticReportTemplate omrsDiagnosticReport,
	                                                     DiagnosticReport diagnosticReport);
	
	public DiagnosticReportTemplate generateOpenMRSDiagnosticReport(DiagnosticReport fhirDiagnosticReport,
	                                                                DiagnosticReportTemplate omrsDiagnosticReport);
	
	public String getId();
}
