package org.openmrs.module.fhir.api.diagnosticreport.handler;

import ca.uhn.fhir.model.dstu2.composite.AttachmentDt;
import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import ca.uhn.fhir.model.dstu2.resource.DiagnosticReport;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.dstu2.resource.Practitioner;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.server.exceptions.MethodNotAllowedException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.openmrs.ConceptComplex;
import org.openmrs.Encounter;
import org.openmrs.EncounterRole;
import org.openmrs.Obs;
import org.openmrs.Person;
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
import org.openmrs.obs.ComplexData;
import org.openmrs.util.OpenmrsConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

	private DiagnosticReport getFHIRDiagnosticReport(Encounter omrsDiagnosticReport) {
		log.debug("Laboratory Handler : GetFHIRDiagnosticReport");
		DiagnosticReport diagnosticReport = new DiagnosticReport();

		// Separate Obs into different field based on Concept Id
		Map<String, Set<Obs>> obsSetsMap = separateObs(omrsDiagnosticReport.getObs());

		// Set ID
		diagnosticReport.setId(new IdDt("DiagnosticReport", omrsDiagnosticReport.getUuid()));

		// Get Obs and set as `Name`
		// Get Obs and set as `Status`

		// @required: Get EncounterDateTime and set as `Issued` date
		diagnosticReport.setIssued(new DateTimeDt(omrsDiagnosticReport.getEncounterDatetime()));

		// @required: Get Encounter Patient and set as `Subject`
		org.openmrs.Patient omrsPatient = omrsDiagnosticReport.getPatient();
		diagnosticReport.getSubject().setResource(FHIRPatientUtil.generatePatient(omrsPatient));

		// Get Encounter Provider and set as `Performer`
		EncounterRole omrsEncounterRole = FHIRUtils.getEncounterRole();
		Set<Provider> omrsProviderList = omrsDiagnosticReport.getProvidersByRole(omrsEncounterRole);
		// If at least one provider is set (1..1 mapping in FHIR Diagnostic Report)
		if (!omrsProviderList.isEmpty()) {
			Practitioner practitioner = FHIRPractitionerUtil.generatePractitioner(omrsProviderList.iterator().next());
			diagnosticReport.getPerformer().setResource(practitioner);
		}

		// Get EncounterType and Set `ServiceCategory`
		String serviceCategory = omrsDiagnosticReport.getEncounterType().getName();
		List<CodingDt> serviceCategoryList = new ArrayList<CodingDt>();
		serviceCategoryList.add(new CodingDt("http://hl7.org/fhir/v2/0074", serviceCategory));
		diagnosticReport.getServiceCategory().setCoding(serviceCategoryList);

		// Get valueDateTime in Obs and Set `Diagnosis[x]->DateTime`
		// Get valueDateTime in Obs and Set `Diagnosis[x]->Period`

		// ObsSet set as `Result`
		// Binary Obs Handler

		return diagnosticReport;
	}

	public Map<String, Set<Obs>> separateObs(Set<Obs> obsSet) {
		Map<String, Set<Obs>> obsSetsMap = new HashMap<String, Set<Obs>>();
		for (Obs obs : obsSet) {

		}
		return obsSetsMap;
	}

	@Override
	public DiagnosticReport saveFHIRDiagnosticReport(DiagnosticReport diagnosticReport) {
		log.debug("Laboratory Handler : SaveFHIRDiagnosticReport");
		EncounterService encounterService = Context.getEncounterService();
		Encounter omrsDiagnosticReport = new Encounter();
		Set<Obs> obsList = new HashSet<Obs>();

		// Set `Name` as a Obs
		// Set `Status` as a Obs

		// @require: Set `Issued` date as EncounterDateTime
		omrsDiagnosticReport.setEncounterDatetime(diagnosticReport.getIssued());

		// @required: Set `Subject` as Encounter Patient
		org.openmrs.Patient omrsPatient = null;
		if (diagnosticReport.getSubject().getReference().isLocal()) {
			Patient patient = (Patient) diagnosticReport.getSubject().getResource();
			//TODO: org.openmrs.Patient omrsParient = FHIRPatientUtil.generateOpenMRSPatient(patient);
			omrsPatient = new org.openmrs.Patient();
			omrsDiagnosticReport.setPatient(omrsPatient);
		} else {
			// Get Id of the Patient
			String patientID = diagnosticReport.getSubject().getReference().getIdPart();
			// Assume that Patient is stored in the OpenMRS database
			PatientService fhirPatientService = Context.getService(PatientService.class);
			Patient patient = fhirPatientService.getPatient(patientID);
			// org.openmrs.Patient omrsPatient = FHIRPatientUtil.generatePatient(patient);
			omrsPatient = Context.getPatientService().getPatientByUuid(patientID);
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
		for (AttachmentDt attachment : diagnosticReport.getPresentedForm()) {
			obsList.add(saveComplexData(omrsDiagnosticReport, "", omrsPatient, attachment));
		}

		omrsDiagnosticReport.setObs(obsList);
		// Create resource in OpenMRS Database
		Encounter omrsEncounter = encounterService.saveEncounter(omrsDiagnosticReport);
		diagnosticReport.setId(new IdDt("DiagnosticReport", omrsEncounter.getUuid()));
		return diagnosticReport;
	}

	public Obs saveComplexData(Encounter encounter, String complexConceptId, org.openmrs.Patient patient,
	                           AttachmentDt attachement) {
		Person person = Context.getPersonService().getPersonByUuid(patient.getUuid());
		ConceptComplex conceptComplex = Context.getConceptService().getConceptComplex(Integer.parseInt(complexConceptId));
		Obs obs = new Obs();
		obs.setPerson(person);
		obs.setConcept(conceptComplex);
		obs.setObsDatetime(attachement.getCreation());
		obs.setEncounter(encounter);
		ComplexData complexData = new ComplexData(attachement.getTitle(), attachement.getData());
		/** TODO: Not available in OpenMRS 1.10.0 version
		 complexData.setMimeType(attachement.getContentType());
		 complexData.setLength(attachement.getSize().longValue());
		 */
		obs.setComplexData(complexData);
		Context.getObsService().saveObs(obs, null);

		Integer obsId = obs.getObsId();
		return Context.getObsService().getComplexObs(obsId, OpenmrsConstants.RAW_VIEW);
	}

	@Override
	public DiagnosticReport updateFHIRDiagnosticReport(DiagnosticReport diagnosticReport, String theId) {
		log.debug("Laboratory Handler : UpdateFHIRDiagnosticReport");
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
		log.debug("Laboratory Handler : RetireFHIRDiagnosticReport");
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
