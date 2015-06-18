package org.openmrs.module.fhir.api.diagnosticreport.handler;

import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import ca.uhn.fhir.model.dstu2.resource.DiagnosticReport;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.dstu2.resource.Practitioner;
import ca.uhn.fhir.model.primitive.IdDt;
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

import java.util.List;

public class LaboratoryHandler extends AbstractHandler implements DiagnosticReportHandler {

	public LaboratoryHandler() {
		super();
	}

    private static final String ServiceCategory = "LAB";

    public String getServiceCategory() {
        return ServiceCategory;
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
		log.info("Laboratory Handler : Save FHIR Diagnostic Report");
		Encounter omrsDiagnosticReport = new Encounter();

		// Set `Name` as a Obs
		// Set `Status` as a Obs

		// @require: Set `Issued` date as EncounterDateTime
		omrsDiagnosticReport.setEncounterDatetime(diagnosticReport.getIssued());

		// Set `Subject` as Encounter Patient
		if (diagnosticReport.getSubject().getReference().isLocal()) {
			Patient patient = (Patient) diagnosticReport.getSubject().getResource();
			//TODO: org.openmrs.Patient omrsParient = FHIRPatientUtil.generateOpenMRSPatient(patient);
			org.openmrs.Patient omrsParient = new org.openmrs.Patient();
			omrsDiagnosticReport.setPatient(omrsParient);
		} else {
			// Get Id of the Patient
			String patientID = diagnosticReport.getSubject().getReference().getIdPart();
			// Assume that Patient is stored in the OpenMRS database
			PatientService fhirPatientService = Context.getService(PatientService.class);
			Patient patient = fhirPatientService.getPatient(patientID);
			// org.openmrs.Patient omrsPatient = FHIRPatientUtil.generatePatient(patient);
			org.openmrs.Patient omrsPatient = Context.getPatientService().getPatientByUuid(patientID);
			omrsDiagnosticReport.setPatient(omrsPatient);
		}

		// Set `Performer`(Practitioner) as Encounter Provider
		if (diagnosticReport.getSubject().getReference().isLocal()) {
			Practitioner practitioner = (Practitioner) diagnosticReport.getPerformer().getResource();
			//TODO: org.openmrs.Provider omrsProvider = FHIRPractitionerUtil.generatePractitioner();
			Provider omrsProvider = new Provider();
			omrsDiagnosticReport.setProvider(new EncounterRole(), omrsProvider);
		} else {
			// Get Id of the Performer
			String practitionerID = diagnosticReport.getPerformer().getReference().getIdPart();
			// Assume that Performer is stored in the OpenMRS database
			PractitionerService fhirPractitionerService = Context.getService(PractitionerService.class);
			Practitioner practitioner = fhirPractitionerService.getPractitioner(practitionerID);
			//TODO: org.openmrs.Provider omrsProvider = FHIRPractitionerUtil.generateOpenMRSPractitioner();
			Provider omrsProvider = Context.getProviderService().getProviderByUuid(practitionerID);
			//TODO: Get EncounterRole from DiagnosticReport (remove hard coded value)
			String encounterRoleID = "73bbb069-9781-4afc-a9d1-54b6b2270e03";
			EncounterRole encounterRole = Context.getEncounterService().getEncounterRoleByUuid(encounterRoleID);
			omrsDiagnosticReport.setProvider(encounterRole, omrsProvider);
		}

		// Set `ServiceCategory` as EncounterType
		List<CodingDt> codingList = diagnosticReport.getServiceCategory().getCoding();
		String encounterType = "DEFAULT"; // If serviceCategory is not present in the DiagnosticReport, then use "DEFAULT"
		if (!codingList.isEmpty()) {
			encounterType = codingList.get(0).getCode();
		}
		omrsDiagnosticReport.setEncounterType(Context.getEncounterService().getEncounterType(encounterType));

		// Set `Diagnosis[x]->DateTime` as valueDateTime in an Obs
		// Set `Diagnosis[x]->Period` as valueDateTime in an Obs

		// Set parsed obsSet (`Result` as Set of Obs)
		// Set Binary Obs Handler which used to store `PresentedForm`

		// Create resource in OpenMRS Database
		EncounterService encounterService = Context.getEncounterService();
		Encounter omrsEncounter = encounterService.saveEncounter(omrsDiagnosticReport);
		diagnosticReport.setId(new IdDt("DiagnosticReport", omrsEncounter.getUuid()));
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
