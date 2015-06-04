package org.openmrs.module.fhir.api.diagnosticreport.handler;

import org.openmrs.module.fhir.api.diagnosticreport.DiagnosticReportHandler;
import org.openmrs.module.fhir.api.diagnosticreport.DiagnosticReportTemplate;

import ca.uhn.fhir.model.dstu2.resource.DiagnosticReport;

public class LaboratoryHandler extends AbstractHandler implements DiagnosticReportHandler {
	
	public LaboratoryHandler() {
		super();
	}
	
	@Override
	public DiagnosticReport generateFHIRDiagnosticReport(DiagnosticReportTemplate omrsDiagnosticReport,
	                                                     DiagnosticReport diagnosticReport) {
		return null;
	}
	
	@Override
	public DiagnosticReportTemplate generateOpenMRSDiagnosticReport(DiagnosticReport fhirDiagnosticReport,
	                                                                DiagnosticReportTemplate omrsDiagnosticReport) {
		return null;
	}
	
}
