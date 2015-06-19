package org.openmrs.module.fhir.api.diagnosticreport.handler;

import ca.uhn.fhir.model.dstu2.resource.DiagnosticReport;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.module.fhir.api.diagnosticreport.DiagnosticReportHandler;

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
	public DiagnosticReport saveFHIRDiagnosticReport(DiagnosticReport diagnosticReport) {
		return diagnosticReport;
	}

	@Override
	public DiagnosticReport purgeFHIRDiagnosticReport(DiagnosticReport diagnosticReport) {
		return diagnosticReport;
	}
}
