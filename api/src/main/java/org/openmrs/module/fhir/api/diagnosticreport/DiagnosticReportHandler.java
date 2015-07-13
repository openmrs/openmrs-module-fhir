package org.openmrs.module.fhir.api.diagnosticreport;

import ca.uhn.fhir.model.dstu2.resource.DiagnosticReport;
import org.openmrs.Encounter;
import org.openmrs.Obs;

import java.util.List;
import java.util.Set;

public interface DiagnosticReportHandler {

	public String getServiceCategory();

	public DiagnosticReport getFHIRDiagnosticReportById(String id);

	public DiagnosticReport getFHIRDiagnosticReportBySubjectName(String name);

	public DiagnosticReport saveFHIRDiagnosticReport(DiagnosticReport diagnosticReport);

	public DiagnosticReport updateFHIRDiagnosticReport(DiagnosticReport diagnosticReport, String theId);

	public void retireFHIRDiagnosticReport(String id);

}
