package org.openmrs.module.fhir.api.diagnosticreport.handler;

import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import ca.uhn.fhir.model.dstu2.resource.DiagnosticReport;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.dstu2.resource.Practitioner;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.server.exceptions.MethodNotAllowedException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.openmrs.Encounter;
import org.openmrs.EncounterRole;
import org.openmrs.Provider;
import org.openmrs.api.APIException;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.PatientService;
import org.openmrs.module.fhir.api.PractitionerService;
import org.openmrs.module.fhir.api.diagnosticreport.DiagnosticReportHandler;
import org.openmrs.module.fhir.api.util.FHIRPatientUtil;
import org.openmrs.module.fhir.api.util.FHIRPractitionerUtil;
import org.openmrs.module.fhir.api.util.FHIRUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class LaboratoryHandler extends AbstractHandler implements DiagnosticReportHandler {

	public LaboratoryHandler() {
		super();
	}

	private static final String ServiceCategory = "LAB";

	public String getServiceCategory() {
		return ServiceCategory;
	}

	@Override
	public DiagnosticReport getFHIRDiagnosticReportById(String id) {
		return getFHIRDiagnosticReport(Context.getEncounterService().getEncounterByUuid(id));
	}

	private DiagnosticReport getFHIRDiagnosticReport(Encounter omrsiagnosticReport) {
		log.info("Laboratory Handler : GetFHIRDiagnosticReport");
		DiagnosticReport diagnosticReport = new DiagnosticReport();

		// Set ID
		diagnosticReport.setId(new IdDt("DiagnosticReport", omrsiagnosticReport.getUuid()));

		// Get Obs and set as `Name`
		// Get Obs and set as `Status`

		// @required: Get EncounterDateTime and set as `Issued` date
		diagnosticReport.setIssued(new DateTimeDt(omrsiagnosticReport.getEncounterDatetime()));

		// @required: Get Encounter Patient and set as `Subject`
		org.openmrs.Patient omrsPatient = omrsiagnosticReport.getPatient();
		diagnosticReport.getSubject().setResource(FHIRPatientUtil.generatePatient(omrsPatient));

		// Get Encounter Provider and set as `Performer`
		EncounterRole omrsEncounterRole = FHIRUtils.getEncounterRole();
		Set<Provider> omrsProviderList = omrsiagnosticReport.getProvidersByRole(omrsEncounterRole);
		// If at least one provider is set (1..1 mapping in FHIR Diagnostic Report)
		if (!omrsProviderList.isEmpty()) {
			Practitioner practitioner = FHIRPractitionerUtil.generatePractitioner(omrsProviderList.iterator().next());
			diagnosticReport.getPerformer().setResource(practitioner);
		}

		// Get EncounterType and Set `ServiceCategory`
		String serviceCategory = omrsiagnosticReport.getEncounterType().getName();
		List<CodingDt> serviceCategoryList = new ArrayList<CodingDt>();
		serviceCategoryList.add(new CodingDt("http://hl7.org/fhir/v2/0074", serviceCategory));
		diagnosticReport.getServiceCategory().setCoding(serviceCategoryList);

		// Get valueDateTime in Obs and Set `Diagnosis[x]->DateTime`
		// Get valueDateTime in Obs and Set `Diagnosis[x]->Period`

		// ObsSet set as `Result`
		// Binary Obs Handler
		return diagnosticReport;
	}

	@Override
	public DiagnosticReport saveFHIRDiagnosticReport(DiagnosticReport diagnosticReport) {
		log.info("Laboratory Handler : SaveFHIRDiagnosticReport");
		EncounterService encounterService = Context.getEncounterService();
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
			EncounterRole encounterRole = FHIRUtils.getEncounterRole();
			omrsDiagnosticReport.setProvider(encounterRole, omrsProvider);
		}

		// Set `ServiceCategory` as EncounterType
		List<CodingDt> codingList = diagnosticReport.getServiceCategory().getCoding();
		String encounterType = "DEFAULT"; // If serviceCategory is not present in the DiagnosticReport, then use "DEFAULT"
		if (!codingList.isEmpty()) {
			encounterType = codingList.get(0).getCode();
		}
		omrsDiagnosticReport.setEncounterType(FHIRUtils.getEncounterType(encounterType));

		// Set `Diagnosis[x]->DateTime` as valueDateTime in an Obs
		// Set `Diagnosis[x]->Period` as valueDateTime in an Obs

		// Set parsed obsSet (`Result` as Set of Obs)
		// Set Binary Obs Handler which used to store `PresentedForm`

		// Create resource in OpenMRS Database
		Encounter omrsEncounter = encounterService.saveEncounter(omrsDiagnosticReport);
		diagnosticReport.setId(new IdDt("DiagnosticReport", omrsEncounter.getUuid()));
		return diagnosticReport;
	}

	@Override
	public DiagnosticReport updateFHIRDiagnosticReport(DiagnosticReport diagnosticReport, String theId) {
		log.info("Laboratory Handler : UpdateFHIRDiagnosticReport");
		EncounterService encounterService = Context.getEncounterService();
		Encounter omrsDiagnosticReport = encounterService.getEncounterByUuid(theId);

		// Set `Name` as a Obs
		// Set `Status` as a Obs

		// If available set `Issued` date as EncounterDateTime
		if (diagnosticReport.getIssued() != null) {
			omrsDiagnosticReport.setEncounterDatetime(diagnosticReport.getIssued());
		}

		// Set `Subject` as Encounter Patient
		IdDt subjectReference = diagnosticReport.getSubject().getReference();
		if (!subjectReference.isEmpty()) {
			if (subjectReference.isLocal()) {
				Patient patient = (Patient) diagnosticReport.getSubject().getResource();
				//TODO: org.openmrs.Patient omrsParient = FHIRPatientUtil.generateOpenMRSPatient(patient);
				org.openmrs.Patient omrsParient = new org.openmrs.Patient();
				omrsDiagnosticReport.setPatient(omrsParient);
			} else {
				// Get Id of the Patient
				String patientID = subjectReference.getIdPart();
				// Assume that Patient is stored in the OpenMRS database
				PatientService fhirPatientService = Context.getService(PatientService.class);
				Patient patient = fhirPatientService.getPatient(patientID);
				// org.openmrs.Patient omrsPatient = FHIRPatientUtil.generatePatient(patient);
				org.openmrs.Patient omrsPatient = Context.getPatientService().getPatientByUuid(patientID);
				omrsDiagnosticReport.setPatient(omrsPatient);
			}
		}

		// Set `Performer`(Practitioner) as Encounter Provider
		IdDt performerReference = diagnosticReport.getPerformer().getReference();
		if (!performerReference.isEmpty()) {
			if (performerReference.isLocal()) {
				Practitioner practitioner = (Practitioner) diagnosticReport.getPerformer().getResource();
				//TODO: org.openmrs.Provider omrsProvider = FHIRPractitionerUtil.generatePractitioner();
				Provider omrsProvider = new Provider();
				omrsDiagnosticReport.setProvider(new EncounterRole(), omrsProvider);
			} else {
				// Get Id of the Performer
				String practitionerID = performerReference.getIdPart();
				// Assume that Performer is stored in the OpenMRS database
				PractitionerService fhirPractitionerService = Context.getService(PractitionerService.class);
				Practitioner practitioner = fhirPractitionerService.getPractitioner(practitionerID);
				//TODO: org.openmrs.Provider omrsProvider = FHIRPractitionerUtil.generateOpenMRSPractitioner();
				Provider omrsProvider = Context.getProviderService().getProviderByUuid(practitionerID);
				//TODO: Get EncounterRole from DiagnosticReport (remove hard coded value)
				EncounterRole encounterRole = FHIRUtils.getEncounterRole();
				omrsDiagnosticReport.setProvider(encounterRole, omrsProvider);
			}
		}

		// Set `ServiceCategory` as EncounterType
		List<CodingDt> codingList = diagnosticReport.getServiceCategory().getCoding();
		String encounterType = null;
		if (!codingList.isEmpty()) {
			encounterType = codingList.get(0).getCode();
			omrsDiagnosticReport.setEncounterType(FHIRUtils.getEncounterType(encounterType));
		}

		// Set `Diagnosis[x]->DateTime` as valueDateTime in an Obs
		// Set `Diagnosis[x]->Period` as valueDateTime in an Obs

		// Set parsed obsSet (`Result` as Set of Obs)
		// Set Binary Obs Handler which used to store `PresentedForm`

		// Create resource in OpenMRS Database
		Encounter omrsEncounter = encounterService.saveEncounter(omrsDiagnosticReport);
		diagnosticReport.setId(new IdDt("DiagnosticReport", omrsEncounter.getUuid()));
		return diagnosticReport;
	}

	@Override
	public void retireFHIRDiagnosticReport(String id) {
		EncounterService encounterService = Context.getEncounterService();
		// Delete Binary Obs Handler which used to store `PresentedForm`

		// Delete Encounter OpenMRS Object
		Encounter omrsDiagnosticReport = encounterService.getEncounterByUuid(id);

		if (omrsDiagnosticReport == null) {
			throw new ResourceNotFoundException(String.format("Diagnostic Report with id '%s' not found.", id));
		}
		if (omrsDiagnosticReport.isVoided()) {
			return;
		}
		try {
			encounterService.voidEncounter(omrsDiagnosticReport, "Voided by FHIR Request.");
		}
		catch (APIException exAPI) {
			throw new MethodNotAllowedException(String.format("OpenMRS has failed to retire Encounter '%s' due to : %s",
					id, exAPI.getMessage()));
		}
	}
}
