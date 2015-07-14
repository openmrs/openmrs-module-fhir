package org.openmrs.module.fhir.api.diagnosticreport.handler;

import ca.uhn.fhir.model.api.Bundle;
import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu2.resource.DiagnosticReport;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.primitive.IdDt;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.module.fhir.api.diagnosticreport.DiagnosticReportHandler;
import org.openmrs.module.fhir.api.util.FHIRRESTfulGenericClient;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RadiologyHandler extends AbstractHandler implements DiagnosticReportHandler {

	public RadiologyHandler() {
		super();
	}

	private static final String ServiceCategory = "RAD";

	@Override
	public String getServiceCategory() {
		return ServiceCategory;
	}

	@Override
	public DiagnosticReport getFHIRDiagnosticReportById(String id) {
		DiagnosticReport diagnosticReport = new DiagnosticReport();
		return diagnosticReport;
	}

	@Override
	public DiagnosticReport getFHIRDiagnosticReportBySubjectName(String name) {
		System.out.println("In Radiology Handler : getFHIRDiagnosticReportBySubjectName : " + name);
		DiagnosticReport diagnosticReport = new DiagnosticReport();

		String serverBase = "http://fhir.hackathon.siim.org/fhir";
		Bundle bundle = FHIRRESTfulGenericClient.search(serverBase, DiagnosticReport.class,
				DiagnosticReport.SUBJECT.hasChainedProperty(Patient.GIVEN.matches().value("siimsally")));
		System.out.println("Bundle size : " + bundle.size());
		diagnosticReport.setId("12345");
		return diagnosticReport;
	}

	@Override
	public DiagnosticReport saveFHIRDiagnosticReport(DiagnosticReport diagnosticReport) {
		return diagnosticReport;
	}

	@Override
	public DiagnosticReport updateFHIRDiagnosticReport(DiagnosticReport diagnosticReport, String theId) {
		return diagnosticReport;
	}

	@Override
	public void retireFHIRDiagnosticReport(String id) {
	}
}
