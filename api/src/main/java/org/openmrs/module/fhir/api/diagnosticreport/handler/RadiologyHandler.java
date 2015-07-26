package org.openmrs.module.fhir.api.diagnosticreport.handler;

import ca.uhn.fhir.model.api.Bundle;
import ca.uhn.fhir.model.api.BundleEntry;
import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu2.composite.AttachmentDt;
import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.DiagnosticReport;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.dstu2.resource.Practitioner;
import ca.uhn.fhir.model.primitive.Base64BinaryDt;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.gclient.ICriterion;
import ca.uhn.fhir.rest.gclient.ReferenceClientParam;
import ca.uhn.fhir.rest.gclient.TokenClientParam;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptComplex;
import org.openmrs.Encounter;
import org.openmrs.EncounterRole;
import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.Provider;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.ObsService;
import org.openmrs.module.fhir.api.PatientService;
import org.openmrs.module.fhir.api.PractitionerService;
import org.openmrs.module.fhir.api.diagnosticreport.DiagnosticReportHandler;
import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.openmrs.module.fhir.api.util.FHIRObsUtil;
import org.openmrs.module.fhir.api.util.FHIRPatientUtil;
import org.openmrs.module.fhir.api.util.FHIRPractitionerUtil;
import org.openmrs.module.fhir.api.util.FHIRRESTfulGenericClient;
import org.openmrs.module.fhir.api.util.FHIRUtils;
import org.openmrs.obs.ComplexData;
import org.openmrs.util.OpenmrsConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RadiologyHandler extends AbstractHandler implements DiagnosticReportHandler {

	protected final Log log = LogFactory.getLog(this.getClass());

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
		return getFHIRDiagnosticReport(Context.getEncounterService().getEncounterByUuid(id));
	}

	/**
	 * Getting the data from a third party server, storing metadata in OpenMRS and passing on the bundle to the requester.
	 *
	 * @param name Given Name of the Subject/Patient
	 * @return A bundle of DiagnosticReport which is matching with Given Name
	 */
	@Override
	public List<DiagnosticReport> getFHIRDiagnosticReportBySubjectName(String name) {
		log.debug("In Radiology Handler : getFHIRDiagnosticReportBySubjectName : " + name);
		ArrayList<DiagnosticReport> diagnosticReports = new ArrayList<DiagnosticReport>();

		String serverBase = FHIRUtils.getDiagnosticReportRadiologyBaseServerURL();
		ICriterion<ReferenceClientParam> diagnosticReportBySubject = DiagnosticReport.SUBJECT.hasChainedProperty(
				Patient.GIVEN.matches().value(name));
		ICriterion<TokenClientParam> diagnosticReportByService = DiagnosticReport.SERVICE.exactly().code("RAD");
		Bundle bundle = FHIRRESTfulGenericClient.search(serverBase, DiagnosticReport.class, diagnosticReportBySubject,
				diagnosticReportByService);
		log.debug("Bundle size : " + bundle.size());

		for (BundleEntry entry : bundle.getEntries()) {
			IResource resource = entry.getResource();
			log.debug("Resource Type : " + resource.getResourceName());
			if (resource.getResourceName().equals("DiagnosticReport")) {
				DiagnosticReport diagnosticReport = (DiagnosticReport) resource;
				/*diagnosticReport = this.saveFHIRDiagnosticReport(diagnosticReport);
				diagnosticReport = this.getFHIRDiagnosticReportById(diagnosticReport.getId().getIdPart());*/
				diagnosticReports.add(diagnosticReport);
			}
		}

		return diagnosticReports;
	}

	private DiagnosticReport getFHIRDiagnosticReport(Encounter omrsDiagnosticReport) {
		log.debug("Laboratory Handler : GetFHIRDiagnosticReport");
		DiagnosticReport diagnosticReport = new DiagnosticReport();

		// Separate Obs into different field based on Concept Id
		Map<String, Set<Obs>> obsSetsMap = this.separateObs(omrsDiagnosticReport.getObsAtTopLevel(false));

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
		List<ResourceReferenceDt> resultReferenceDtList = new ArrayList<ResourceReferenceDt>();
		for (Obs resultObs : obsSetsMap.get(FHIRConstants.DIAGNOSTIC_REPORT_RESULT)) {
			for (Obs obs : resultObs.getGroupMembers()) {
				Observation observation = FHIRObsUtil.generateObs(obs);
				// To make it contained in side Diagnostic Report
				observation.setId(new IdDt());
				resultReferenceDtList.add(new ResourceReferenceDt(observation));
			}
		}
		if (!resultReferenceDtList.isEmpty()) {
			diagnosticReport.setResult(resultReferenceDtList);
		}

		// Binary Obs Handler `PresentedForm`
		List<AttachmentDt> attachmentDtList = new ArrayList<AttachmentDt>();
		for (Obs attachmentObs : obsSetsMap.get(FHIRConstants.DIAGNOSTIC_REPORT_PRESENTED_FORM)) {
			attachmentDtList.add(getAttachmentDt(attachmentObs));
		}
		if (!attachmentDtList.isEmpty()) {
			diagnosticReport.setPresentedForm(attachmentDtList);
		}

		return diagnosticReport;
	}

	private Map<String, Set<Obs>> separateObs(Set<Obs> obsSet) {
		Map<String, Set<Obs>> obsSetsMap = new HashMap<String, Set<Obs>>();
		obsSetsMap.put(FHIRConstants.DIAGNOSTIC_REPORT_NAME, new HashSet<Obs>());
		obsSetsMap.put(FHIRConstants.DIAGNOSTIC_REPORT_STATUS, new HashSet<Obs>());
		obsSetsMap.put(FHIRConstants.DIAGNOSTIC_REPORT_RESULT, new HashSet<Obs>());
		obsSetsMap.put(FHIRConstants.DIAGNOSTIC_REPORT_PRESENTED_FORM, new HashSet<Obs>());

		for (Obs obs : obsSet) {
			try {
				obsSetsMap.get(this.getFieldName(obs.getConcept())).add(obs);
			}
			catch (NoSuchFieldException e) {
				log.error(e.getMessage());
			}
		}
		return obsSetsMap;
	}

	private String getFieldName(Concept concept) throws NoSuchFieldException {
		if (FHIRUtils.getDiagnosticReportResultConcept().getConceptId().equals(concept.getConceptId())) {
			return FHIRConstants.DIAGNOSTIC_REPORT_RESULT;
		} else if (FHIRUtils.getDiagnosticReportStatusConcept().getConceptId().equals(concept.getConceptId())) {
			return FHIRConstants.DIAGNOSTIC_REPORT_STATUS;
		} else if (FHIRUtils.getDiagnosticReportNameConcept().getConceptId().equals(concept.getConceptId())) {
			return FHIRConstants.DIAGNOSTIC_REPORT_NAME;
		} else if (FHIRUtils.getDiagnosticReportPresentedFormConcept().getConceptId().equals(concept.getConceptId())) {
			return FHIRConstants.DIAGNOSTIC_REPORT_PRESENTED_FORM;
		} else {
			throw new NoSuchFieldException("Can't find a concept for " + concept.getConceptId());
		}
	}

	private AttachmentDt getAttachmentDt(Obs attachmentObs) {
		AttachmentDt attachmentDt = new AttachmentDt();
		int obsId = attachmentObs.getObsId();

		Obs complexObs = Context.getObsService().getComplexObs(obsId, OpenmrsConstants.RAW_VIEW);
		ComplexData complexData = complexObs.getComplexData();
		attachmentDt.setTitle(complexData.getTitle());
		attachmentDt.setData(new Base64BinaryDt(((byte[]) complexData.getData())));
		attachmentDt.setCreation(new DateTimeDt(attachmentObs.getObsDatetime()));
		/**
		 * TODO: Not available in OpenMRS 1.10.0 version
		 * attachmentDt.setContentType(complexData.getMimeType());
		 * attachmentDt.setSize(complexData.getLength());
		 */
		return attachmentDt;
	}

	@Override
	public DiagnosticReport saveFHIRDiagnosticReport(DiagnosticReport diagnosticReport) {
		log.debug("Radiology Handler : SaveFHIRDiagnosticReport");
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
			// Retrieve Patient
			String serverBase = FHIRUtils.getDiagnosticReportRadiologyBaseServerURL();
			Patient patient = FHIRRESTfulGenericClient.readPatientById(serverBase, patientID);
			System.out.println("Patient " + patient.getId().getIdPart());

			List<String> errors = new ArrayList<String>();
			omrsPatient = FHIRPatientUtil.generateOmrsPatient(patient, errors);
			if (errors.size() < 1) {
				omrsDiagnosticReport.setPatient(omrsPatient);
			} else {
				log.error("Error while creating OpenMRS Patient " + errors.size());
			}
		}

		// Set `Performer`(Practitioner) as Encounter Provider
		if (diagnosticReport.getSubject().getReference().isLocal()) {
			/* TODO: Practitioner practitioner = (Practitioner) diagnosticReport.getPerformer().getResource();
			org.openmrs.Provider omrsProvider = FHIRPractitionerUtil.generatePractitioner();*/
			Provider omrsProvider = new Provider();
			omrsDiagnosticReport.setProvider(new EncounterRole(), omrsProvider);
		} else {
			// Get Id of the Performer
			String practitionerID = diagnosticReport.getPerformer().getReference().getIdPart();
			// Retrieve Practitioner
			String serverBase = FHIRUtils.getDiagnosticReportRadiologyBaseServerURL();
			Practitioner practitioner = FHIRRESTfulGenericClient.readPractitionerById(serverBase, practitionerID);
			System.out.println("Practitioner " + practitioner.getId().getIdPart());
			//TODO: Provider omrsProvider = FHIRPractitionerUtil.generateOpenMRSPractitioner();
			Provider omrsProvider = Context.getProviderService().getProviderByUuid(practitionerID);

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

		/**
		 * Create resource in OpenMRS Database RATIONALE: Due to encounter.setObs(obsList) method is
		 * not working properly and need to set encounter for the Obs before create them to link
		 * with the Encounter. In order to set the Encounter, it has to be save before set.
		 */
		Encounter omrsEncounter = encounterService.saveEncounter(omrsDiagnosticReport);

		// Set parsed obsSet (`Result` as Set of Obs)
		Set<Obs> resultObsGroupMembersSet = new HashSet<Obs>();
		// Iterate through 'result' Observations and adding to the OpenMRS Obs group
		for (ResourceReferenceDt referenceDt : diagnosticReport.getResult()) {
			List<String> errors = new ArrayList<String>();
			Observation observation;

			if (referenceDt.getReference().isLocal()) {
				observation = (Observation) referenceDt.getResource();
			} else {
				// Get Id of the Observation
				String observationID = referenceDt.getReference().getIdPart();
				// Assume that the given Observation is stored in the OpenMRS database
				observation = Context.getService(ObsService.class).getObs(observationID);
			}

			observation = prepareForGenerateOpenMRSObs(observation, diagnosticReport);
			Obs obs = FHIRObsUtil.generateOpenMRSObs(observation, errors);
			/**
			 * TODO: Unable to check for errors because it's sending errors also for not mandatory
			 * fields if(errors.isEmpty()) {}
			 */
			obs = Context.getObsService().saveObs(obs, null);
			resultObsGroupMembersSet.add(obs);
		}

		if (!resultObsGroupMembersSet.isEmpty()) {
			Concept resultConcept = FHIRUtils.getDiagnosticReportResultConcept();
			Obs resultObsGroup = new Obs(Context.getPersonService().getPersonByUuid(omrsPatient.getUuid()), resultConcept,
					diagnosticReport.getIssued(), null);
			/**
			 * TODO: This method is not working properly. Need more testing.
			 * resultObsGroup.setGroupMembers(resultObsGroupMembersSet);
			 */
			for (Obs obs : resultObsGroupMembersSet) {
				resultObsGroup.addGroupMember(obs);
			}
			resultObsGroup.setEncounter(omrsEncounter);
			Context.getObsService().saveObs(resultObsGroup, null);
		}

		// Set Binary Obs Handler which used to store `PresentedForm`
		for (AttachmentDt attachment : diagnosticReport.getPresentedForm()) {
			int conceptId = FHIRUtils.getDiagnosticReportPresentedFormConcept().getConceptId();
			if (attachment.getCreation() == null) {
				attachment.setCreation(diagnosticReport.getIssuedElement());
			}
			Obs complexObs = saveComplexData(omrsDiagnosticReport, conceptId, omrsPatient, attachment);
			obsList.add(complexObs);
		}
		/**
		 * TODO: Not working properly. Need to test it. omrsDiagnosticReport.setObs(obsList);
		 */

		diagnosticReport.setId(new IdDt("DiagnosticReport", omrsEncounter.getUuid()));
		return diagnosticReport;
	}

	public Obs saveComplexData(Encounter encounter, int complexConceptId, org.openmrs.Patient patient,
	                           AttachmentDt attachment) {
		Person person = Context.getPersonService().getPersonByUuid(patient.getUuid());
		ConceptComplex conceptComplex = Context.getConceptService().getConceptComplex(complexConceptId);

		Obs complexObs = new Obs(person, conceptComplex, attachment.getCreation(), null);
		complexObs.setEncounter(encounter);
		ComplexData complexData = new ComplexData(attachment.getTitle(), attachment.getData());
		/**
		 * TODO: Not available in OpenMRS 1.10.0 version
		 * complexData.setMimeType(attachment.getContentType());
		 * complexData.setLength(attachment.getSize().longValue());
		 */
		complexObs.setComplexData(complexData);
		Context.getObsService().saveObs(complexObs, null);

		Integer obsId = complexObs.getObsId();
		return Context.getObsService().getComplexObs(obsId, OpenmrsConstants.RAW_VIEW);
	}

	public Observation prepareForGenerateOpenMRSObs(Observation observation, DiagnosticReport diagnosticReport) {
		observation.setSubject(diagnosticReport.getSubject());
		observation.setApplies(diagnosticReport.getDiagnostic());
		return observation;
	}

	@Override
	public DiagnosticReport updateFHIRDiagnosticReport(DiagnosticReport diagnosticReport, String theId) {
		return diagnosticReport;
	}

	@Override
	public void retireFHIRDiagnosticReport(String id) {
	}
}
