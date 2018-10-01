package org.openmrs.module.fhir.api.diagnosticreport.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hl7.fhir.dstu3.model.DiagnosticReport;
import org.openmrs.Encounter;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.diagnosticreport.DiagnosticReportHandler;
import org.openmrs.module.fhir.api.util.FHIRConstants;

import java.util.ArrayList;
import java.util.List;

public class DefaultDiagnosticReportHandler extends AbstractHandler implements DiagnosticReportHandler {

	protected final Log log = LogFactory.getLog(this.getClass());

	public DefaultDiagnosticReportHandler() {
		super();
	}

	@Override
	public String getServiceCategory() {
		return FHIRConstants.DEFAULT;
	}

	@Override
	public DiagnosticReport getFHIRDiagnosticReportById(String id) {
		//TODO (Implementation proposal can be found in git history)
		return new DiagnosticReport();
	}

	@Override
	public List<DiagnosticReport> getFHIRDiagnosticReportBySubjectName(String name) {
		return new ArrayList<DiagnosticReport>();
	}

	@Override
	public DiagnosticReport saveFHIRDiagnosticReport(DiagnosticReport diagnosticReport) {
		if (log.isDebugEnabled()) {
			log.debug("Laboratory Handler : Save FHIR Diagnostic Report");
		}
		Encounter omrsDiagnosticReport = new Encounter();

		//Set ID if available
		if (diagnosticReport.getId() != null) {
			omrsDiagnosticReport.setUuid(diagnosticReport.getId());
		}

		//TODO (Implementation proposal can be found in git history)

		Context.getEncounterService();
		return diagnosticReport;
	}

	@Override
	public DiagnosticReport updateFHIRDiagnosticReport(DiagnosticReport diagnosticReport, String theId) {
		return null;
	}

	@Override
	public void retireFHIRDiagnosticReport(String id) {
		//TODO (Implementation proposal can be found in git history)
	}
}
