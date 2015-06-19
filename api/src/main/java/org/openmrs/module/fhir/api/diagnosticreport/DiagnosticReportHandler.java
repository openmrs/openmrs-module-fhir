package org.openmrs.module.fhir.api.diagnosticreport;

import ca.uhn.fhir.model.dstu2.resource.DiagnosticReport;
import org.openmrs.Obs;

import java.util.List;
import java.util.Set;

public interface DiagnosticReportHandler {

	public String getServiceCategory();

	public DiagnosticReport getFHIRDiagnosticReport(DiagnosticReport diagnosticReport);

	public DiagnosticReport saveFHIRDiagnosticReport(DiagnosticReport diagnosticReport);

	public void retireFHIRDiagnosticReport(String id);

}
