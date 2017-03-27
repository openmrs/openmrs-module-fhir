package org.openmrs.module.fhir.api.diagnosticreport;

import ca.uhn.fhir.model.dstu2.resource.DiagnosticReport;
import org.openmrs.Encounter;
import org.openmrs.Obs;

import java.util.List;
import java.util.Set;

public interface DiagnosticReportHandler {

	String getServiceCategory();

	DiagnosticReport getFHIRDiagnosticReportById(String id);

	List<DiagnosticReport> getFHIRDiagnosticReportBySubjectName(String name);

	DiagnosticReport saveFHIRDiagnosticReport(DiagnosticReport diagnosticReport);

	DiagnosticReport updateFHIRDiagnosticReport(DiagnosticReport diagnosticReport, String theId);

	void retireFHIRDiagnosticReport(String id);

}
