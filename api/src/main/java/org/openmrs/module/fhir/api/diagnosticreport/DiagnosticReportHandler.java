package org.openmrs.module.fhir.api.diagnosticreport;

import org.hl7.fhir.dstu3.model.DiagnosticReport;

import java.util.List;

public interface DiagnosticReportHandler {

	String getServiceCategory();

	DiagnosticReport getFHIRDiagnosticReportById(String id);

	List<DiagnosticReport> getFHIRDiagnosticReportBySubjectName(String name);

	DiagnosticReport saveFHIRDiagnosticReport(DiagnosticReport diagnosticReport);

	DiagnosticReport updateFHIRDiagnosticReport(DiagnosticReport diagnosticReport, String theId);

	void retireFHIRDiagnosticReport(String id);

}
