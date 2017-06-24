package org.openmrs.module.fhir.api.diagnosticreport.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hl7.fhir.dstu3.model.DiagnosticReport;
import org.openmrs.Encounter;
import org.openmrs.api.EncounterService;
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
		DiagnosticReport diagnosticReport = new DiagnosticReport();

		// Get Obs and set as `Name`
		// Get Obs and set as `Status`
		// Get EncounterDateTime and set as `Issued` date
		// Get Encounter Patient and set as `Subject`
		// Get Encounter Provider and set as `Performer`
		// Get EncounterType and Set `ServiceCategory`
		// Get valueDateTime in Obs and Set `Diagnosis[x]->DateTime`
		// Get valueDateTime in Obs and Set `Diagnosis[x]->Period`

		// ObsSet set as `Result`
		// Binary Obs Handler
		return diagnosticReport;
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

		// Set `Name` as a Obs
		// Set `Status` as a Obs
		// Set `Issued` date as EncounterDateTime
		// Set `Subject` as Encounter Patient
		// Set `Performer` as Encounter Provider
		// Set `ServiceCategory` as EncounterType
		// Set `Diagnosis[x]->DateTime` as valueDateTime in an Obs
		// Set `Diagnosis[x]->Period` as valueDateTime in an Obs

		// Set parsed obsSet (`Result` as Set of Obs)
		// Set Binary Obs Handler which used to store `PresentedForm`

		// Create resource in OpenMRS Database
		EncounterService encounterService = Context.getEncounterService();
		// encounterService.saveEncounter(omrsDiagnosticReport);
		return diagnosticReport;
	}

	@Override
	public DiagnosticReport updateFHIRDiagnosticReport(DiagnosticReport diagnosticReport, String theId) {
		return null;
	}

	@Override
	public void retireFHIRDiagnosticReport(String id) {
		// Delete Binary Obs Handler which used to store `PresentedForm`
		// Delete Encounter
		return;
	}
}
