package org.openmrs.module.fhir.api.diagnosticreport.handler;

import ca.uhn.fhir.model.dstu2.resource.DiagnosticReport;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.dstu2.resource.Practitioner;
import org.openmrs.Encounter;
import org.openmrs.EncounterRole;
import org.openmrs.Person;
import org.openmrs.Provider;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ServiceContext;
import org.openmrs.module.fhir.api.PatientService;
import org.openmrs.module.fhir.api.PractitionerService;
import org.openmrs.module.fhir.api.diagnosticreport.DiagnosticReportHandler;
import org.openmrs.module.fhir.api.impl.PatientServiceImpl;
import org.openmrs.module.fhir.api.util.FHIRPatientUtil;
import org.openmrs.module.fhir.api.util.FHIRPractitionerUtil;

public class LaboratoryHandler extends AbstractHandler implements DiagnosticReportHandler {

	public LaboratoryHandler() {
		super();
	}

	@Override
	public DiagnosticReport getFHIRDiagnosticReport(DiagnosticReport diagnosticReport) {
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
	public DiagnosticReport saveFHIRDiagnosticReport(DiagnosticReport diagnosticReport) {
		System.out.println("Laboratory Handler : diSave FHIR Diagnostic Report");
		Encounter omrsDiagnosticReport = new Encounter();

		//Set ID if available
		if (diagnosticReport.getId() != null) {
			omrsDiagnosticReport.setUuid(diagnosticReport.getId().getIdPart());
			System.out.println("Set UUid: " + omrsDiagnosticReport.getUuid());
		}

		// Set `Name` as a Obs
		// Set `Status` as a Obs

		// @require: Set `Issued` date as EncounterDateTime
		omrsDiagnosticReport.setEncounterDatetime(diagnosticReport.getIssued());

		// Set `Subject` as Encounter Patient
		if (diagnosticReport.getSubject().getReference().isLocal()) {
			Patient patient = (Patient) diagnosticReport.getSubject().getResource();
			// org.openmrs.Patient omrsParient = FHIRPatientUtil.generatePatient(patient);
			org.openmrs.Patient omrsParient = new org.openmrs.Patient();
			omrsDiagnosticReport.setPatient(omrsParient);
		} else {
			// Get Id of the Patient
			String id = diagnosticReport.getSubject().getReference().getIdPart();
			System.out.println("Patient id : " + id);

			// Assume that Patient is stored in the OpenMRS database
			PatientService fhirPatientService = Context.getService(PatientService.class);
			Patient patient = fhirPatientService.getPatient("123");
			// org.openmrs.Patient omrsPatient = FHIRPatientUtil.generatePatient(patient);
			org.openmrs.Patient omrsPatient = new org.openmrs.Patient();
			omrsDiagnosticReport.setPatient(omrsPatient);
		}

		// Set `Performer` as Encounter Provider
		if (diagnosticReport.getSubject().getReference().isLocal()) {
			Practitioner practitioner = (Practitioner) diagnosticReport.getPerformer().getResource();
			// org.openmrs.Provider omrsProvider = FHIRPractitionerUtil.generatePractitioner();
			Provider omrsProvider = new Provider();
			omrsDiagnosticReport.setProvider(new EncounterRole(), omrsProvider);
		} else {
			// Get Id of the Performer
			String id = diagnosticReport.getPerformer().getReference().getIdPart();
			System.out.println("Performer id : " + id);

			// Assume that Performer is stored in the OpenMRS database
			PractitionerService fhirPractitionerService = Context.getService(PractitionerService.class);
			Practitioner practitioner = fhirPractitionerService.getPractitioner("123");
			// org.openmrs.Provider omrsProvider = FHIRPractitionerUtil.generatePractitioner();
			Provider omrsProvider = new Provider();
			omrsDiagnosticReport.setProvider(new EncounterRole(), omrsProvider);
		}

		// Set `ServiceCategory` as EncounterType
		// Set `Diagnosis[x]->DateTime` as valueDateTime in an Obs
		// Set `Diagnosis[x]->Period` as valueDateTime in an Obs

		// Set parsed obsSet (`Result` as Set of Obs)
		// Set Binary Obs Handler which used to store `PresentedForm`

		// Create resource in OpenMRS Database
		EncounterService encounterService = Context.getEncounterService();
		Encounter omrsEncounter = encounterService.saveEncounter(omrsDiagnosticReport);

		// Testing purpose
		System.out.println("Created OpenMRS encounter : " + omrsEncounter.getUuid());
		encounterService.purgeEncounter(omrsEncounter, true);
		return diagnosticReport;
	}

	@Override
	public DiagnosticReport purgeFHIRDiagnosticReport(DiagnosticReport diagnosticReport) {
		// Delete `Name` Obs
		// Delete `Status` Obs

		// Delete Obs (`Result` as Set of Obs)
		// Delete Binary Obs Handler which used to store `PresentedForm`
		return diagnosticReport;
	}
}
