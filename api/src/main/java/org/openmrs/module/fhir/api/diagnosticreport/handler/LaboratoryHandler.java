package org.openmrs.module.fhir.api.diagnosticreport.handler;

import ca.uhn.fhir.rest.server.exceptions.MethodNotAllowedException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hl7.fhir.dstu3.model.Attachment;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.DiagnosticReport;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Reference;
import org.openmrs.Concept;
import org.openmrs.ConceptComplex;
import org.openmrs.Encounter;
import org.openmrs.EncounterRole;
import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.Provider;
import org.openmrs.api.APIException;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.ObsService;
import org.openmrs.module.fhir.api.diagnosticreport.DiagnosticReportHandler;
import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.openmrs.module.fhir.api.util.FHIRObsUtil;
import org.openmrs.module.fhir.api.util.FHIRPatientUtil;
import org.openmrs.module.fhir.api.util.FHIRUtils;
import org.openmrs.obs.ComplexData;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LaboratoryHandler extends AbstractHandler implements DiagnosticReportHandler {

	protected final Log log = LogFactory.getLog(this.getClass());

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

	@Override
	public List<DiagnosticReport> getFHIRDiagnosticReportBySubjectName(String name) {
		return null;
	}

	private DiagnosticReport getFHIRDiagnosticReport(Encounter omrsDiagnosticReport) {
		log.debug("Laboratory Handler : GetFHIRDiagnosticReport");
		DiagnosticReport diagnosticReport = new DiagnosticReport();

		// Separate Obs into different field based on Concept Id
		Map<String, Set<Obs>> obsSetsMap = separateObs(omrsDiagnosticReport.getObsAtTopLevel(false));

		// Set ID
		diagnosticReport.setId(new IdType("DiagnosticReport", omrsDiagnosticReport.getUuid()));

		// Get Obs and set as `Name`
		// Get Obs and set as `Status`

		// @required: Get EncounterDateTime and set as `Issued` date
		diagnosticReport.setIssued(omrsDiagnosticReport.getEncounterDatetime());

		// @required: Get Encounter Patient and set as `Subject`
		org.openmrs.Patient omrsPatient = omrsDiagnosticReport.getPatient();
		diagnosticReport.getSubject().setResource(FHIRPatientUtil.generatePatient(omrsPatient));

		// Get Encounter Provider and set as `Performer`
		EncounterRole omrsEncounterRole = FHIRUtils.getEncounterRole();
		Set<Provider> omrsProviderList = omrsDiagnosticReport.getProvidersByRole(omrsEncounterRole);
		// If at least one provider is set (1..1 mapping in FHIR Diagnostic Report)
		if (!omrsProviderList.isEmpty()) {
			//Role name to a coding display. Is that correct?
			for(Provider practitioner : omrsProviderList) {
				CodeableConcept roleConcept = new CodeableConcept();
				Coding role = new Coding();
				role.setDisplay(omrsEncounterRole.getName());
				roleConcept.addCoding(role);
				Reference practitionerReference = FHIRUtils.buildPractitionerReference(omrsProviderList.iterator().next());
				DiagnosticReport.DiagnosticReportPerformerComponent performer = diagnosticReport.addPerformer();
				performer.setRole(roleConcept);
				performer.setActor(practitionerReference);
			}
		}

		// Get EncounterType and Set `ServiceCategory`
		String serviceCategory = omrsDiagnosticReport.getEncounterType().getName();
		List<Coding> serviceCategoryList = new ArrayList<Coding>();
		//Set service category as the display name as well
		serviceCategoryList.add(new Coding(FHIRConstants.CODING_0074, serviceCategory, serviceCategory));
		diagnosticReport.getCategory().setCoding(serviceCategoryList);

		// Get valueDateTime in Obs and Set `Diagnosis[x]->DateTime`
		// Get valueDateTime in Obs and Set `Diagnosis[x]->Period`

		// ObsSet set as `Result`
		List<Reference> resultReferenceDtList = new ArrayList<Reference>();
		for (Obs resultObs : obsSetsMap.get(FHIRConstants.DIAGNOSTIC_REPORT_RESULT)) {
			for (Obs obs : resultObs.getGroupMembers()) {
				Observation observation = FHIRObsUtil.generateObs(obs);
				// To make it contained in side Diagnostic Report
				observation.setId(new IdType());
				resultReferenceDtList.add(new Reference(observation));
			}
		}
		if (!resultReferenceDtList.isEmpty()) {
			diagnosticReport.setResult(resultReferenceDtList);
		}

		// Binary Obs Handler `PresentedForm`
		List<Attachment> attachmentDtList = new ArrayList<Attachment>();
		for (Obs attachmentObs : obsSetsMap.get(FHIRConstants.DIAGNOSTIC_REPORT_PRESENTED_FORM)) {
			attachmentDtList.add(getAttachmentDt(attachmentObs));
		}
		if (!attachmentDtList.isEmpty()) {
			diagnosticReport.setPresentedForm(attachmentDtList);
		}

		return diagnosticReport;
	}

	public Map<String, Set<Obs>> separateObs(Set<Obs> obsSet) {
		Map<String, Set<Obs>> obsSetsMap = new HashMap<String, Set<Obs>>();
		obsSetsMap.put(FHIRConstants.DIAGNOSTIC_REPORT_NAME, new HashSet<Obs>());
		obsSetsMap.put(FHIRConstants.DIAGNOSTIC_REPORT_STATUS, new HashSet<Obs>());
		obsSetsMap.put(FHIRConstants.DIAGNOSTIC_REPORT_RESULT, new HashSet<Obs>());
		obsSetsMap.put(FHIRConstants.DIAGNOSTIC_REPORT_PRESENTED_FORM, new HashSet<Obs>());

		for (Obs obs : obsSet) {
			try {
				obsSetsMap.get(getFieldName(obs.getConcept())).add(obs);
			}
			catch (NoSuchFieldException e) {
				log.error(e.getMessage());
			}
		}
		return obsSetsMap;
	}

	public String getFieldName(Concept concept) throws NoSuchFieldException {
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

	public Attachment getAttachmentDt(Obs attachmentObs) {
		Attachment attachmentDt = new Attachment();
		int obsId = attachmentObs.getObsId();

		Obs complexObs = Context.getObsService().getComplexObs(obsId, "RAW_VIEW");
		ComplexData complexData = complexObs.getComplexData();
		attachmentDt.setTitle(complexData.getTitle());
		attachmentDt.setData((byte[]) complexData.getData());
		attachmentDt.setCreation(attachmentObs.getObsDatetime());
		/**
		 * TODO: Not available in OpenMRS 1.10.0 version
		 * attachmentDt.setContentType(complexData.getMimeType());
		 * attachmentDt.setSize(complexData.getLength());
		 */
		return attachmentDt;
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
		Reference subjectReference = diagnosticReport.getSubject();
		if (!subjectReference.isEmpty()) {
			if (subjectReference.isEmpty()) {
				//TODO: org.openmrs.Patient omrsParient = FHIRPatientUtil.generateOpenMRSPatient(patient);
				omrsPatient = new org.openmrs.Patient();
				omrsDiagnosticReport.setPatient(omrsPatient);
			} else {
				// Get Id of the Patient
				Identifier patientIdentifier = subjectReference.getIdentifier();
				String patientId = "";
				if(patientIdentifier != null) {
					// Assume that Patient is stored in the OpenMRS database
					patientId = patientIdentifier.getId();
				} else {
					String patientIdReference = subjectReference.getReference();
					if(!StringUtils.isEmpty(patientIdReference) && "/".contains(patientIdReference)) {
						patientId = patientIdReference.split("/")[1];
					}
				}
				omrsPatient = Context.getPatientService().getPatientByUuid(patientId);
				omrsDiagnosticReport.setPatient(omrsPatient);
			}
		} else {
			omrsPatient = omrsDiagnosticReport.getPatient();
		}

		List<DiagnosticReport.DiagnosticReportPerformerComponent> performers = diagnosticReport.getPerformer();
		if (!performers.isEmpty()) {
			EncounterRole encounterRole = FHIRUtils.getEncounterRole();
			Provider omrsProvider = null;
			for (DiagnosticReport.DiagnosticReportPerformerComponent performerComponent : performers) {
				if (performerComponent.isEmpty()) {
					//TODO: org.openmrs.Provider omrsProvider = FHIRPractitionerUtil.generatePractitioner();
					omrsProvider = new Provider();
					omrsDiagnosticReport.addProvider(encounterRole, omrsProvider);
				} else {
					// Get Id of the Performer
					Identifier practitionerIdentifier = performerComponent.getActor().getIdentifier();
					String practitionerId = "";
					if (practitionerIdentifier != null) {
						// Assume that Performer is stored in the OpenMRS database
						//TODO: org.openmrs.Provider omrsProvider = FHIRPractitionerUtil.generateOpenMRSPractitioner();
						practitionerId = practitionerIdentifier.getId();
						//TODO: Get EncounterRole from DiagnosticReport (remove hard coded value)
					} else {
						String practitionerIdReference = performerComponent.getActor().getReference();
						if (!StringUtils.isEmpty(practitionerIdReference) && "/".contains(practitionerIdReference)) {
							practitionerId = practitionerIdReference.split("/")[1];
						}
					}
					omrsProvider = Context.getProviderService().getProviderByUuid(practitionerId);
					omrsDiagnosticReport.addProvider(encounterRole, omrsProvider);
				}
			}
		}

		// Set `ServiceCategory` as EncounterType
		List<Coding> codingList = diagnosticReport.getCategory().getCoding();
		String encounterType = FHIRConstants.DEFAULT; // If serviceCategory is not present in the DiagnosticReport, then use "DEFAULT"
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
		for (Reference referenceDt : diagnosticReport.getResult()) {
			List<String> errors = new ArrayList<String>();
			Observation observation;

			if (referenceDt.getReference() != null) {
				observation = (Observation) referenceDt.getResource();
			} else {
				// Get Id of the Observation
				String observationID = referenceDt.getId();
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
		for (Attachment attachment : diagnosticReport.getPresentedForm()) {
			int conceptId = FHIRUtils.getDiagnosticReportPresentedFormConcept().getConceptId();
			if (attachment.getCreation() == null) {
				if(diagnosticReport.getIssued() != null) {
					attachment.setCreation(diagnosticReport.getIssued());
				}
			}
			Obs complexObs = saveComplexData(omrsDiagnosticReport, conceptId, omrsPatient, attachment);
			obsList.add(complexObs);
		}
		/**
		 * TODO: Not working properly. Need to test it. omrsDiagnosticReport.setObs(obsList);
		 */

		diagnosticReport.setId(new IdType("DiagnosticReport", omrsEncounter.getUuid()));
		return diagnosticReport;
	}

	public Obs saveComplexData(Encounter encounter, int complexConceptId, org.openmrs.Patient patient,
	                           Attachment attachment) {
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
		return Context.getObsService().getComplexObs(obsId, "RAW_VIEW");
	}

	public Observation prepareForGenerateOpenMRSObs(Observation observation, DiagnosticReport diagnosticReport) {
		observation.setSubject(diagnosticReport.getSubject());
		observation.setIssued(diagnosticReport.getIssued());
		return observation;
	}

	@Override
	public DiagnosticReport updateFHIRDiagnosticReport(DiagnosticReport diagnosticReport, String theId) {
		log.debug("Laboratory Handler : UpdateFHIRDiagnosticReport with ID" + theId );

		org.openmrs.api.ObsService obsService = Context.getObsService();
		EncounterService encounterService = Context.getEncounterService();
		Encounter omrsDiagnosticReport = encounterService.getEncounterByUuid(theId);

		// Separate Obs into different field such as `Name`, `Status`, `Result` and `PresentedForm` based on Concept Id
		Map<String, Set<Obs>> obsSetsMap = separateObs(omrsDiagnosticReport.getObsAtTopLevel(false));

		// Set `Name` as a Obs
		// Set `Status` as a Obs

		// If available set `Issued` date as EncounterDateTime
		if (diagnosticReport.getIssued() != null) {
			omrsDiagnosticReport.setEncounterDatetime(diagnosticReport.getIssued());
		}

		// Set `Subject` as Encounter Patient
		org.openmrs.Patient omrsPatient = null;
		Reference subjectReference = diagnosticReport.getSubject();
		if (!subjectReference.isEmpty()) {
			if (subjectReference.isEmpty()) {
				//TODO: org.openmrs.Patient omrsParient = FHIRPatientUtil.generateOpenMRSPatient(patient);
				omrsPatient = new org.openmrs.Patient();
				omrsDiagnosticReport.setPatient(omrsPatient);
			} else {
				// Get Id of the Patient
				Identifier patientIdentifier = subjectReference.getIdentifier();
				String patientId = "";
				if(patientIdentifier != null) {
					// Assume that Patient is stored in the OpenMRS database
					patientId = patientIdentifier.getId();
				} else {
					String patientIdReference = subjectReference.getReference();
					if(!StringUtils.isEmpty(patientIdReference) && "/".contains(patientIdReference)) {
						patientId = patientIdReference.split("/")[1];
					}
				}
				omrsPatient = Context.getPatientService().getPatientByUuid(patientId);
				omrsDiagnosticReport.setPatient(omrsPatient);
			}
		} else {
			omrsPatient = omrsDiagnosticReport.getPatient();
		}

		// Set `Performer`(Practitioner) as Encounter Provider
		List<DiagnosticReport.DiagnosticReportPerformerComponent> performers = diagnosticReport.getPerformer();
		if (!performers.isEmpty()) {
			EncounterRole encounterRole = FHIRUtils.getEncounterRole();
			Provider omrsProvider = null;
			for (DiagnosticReport.DiagnosticReportPerformerComponent performerComponent : performers) {
				if (performerComponent.isEmpty()) {
					//TODO: org.openmrs.Provider omrsProvider = FHIRPractitionerUtil.generatePractitioner();
					omrsProvider = new Provider();
					omrsDiagnosticReport.addProvider(encounterRole, omrsProvider);
				} else {
					// Get Id of the Performer
					Identifier practitionerIdentifier = performerComponent.getActor().getIdentifier();
					String practitionerId = "";
					if (practitionerIdentifier != null) {
						// Assume that Performer is stored in the OpenMRS database
						//TODO: org.openmrs.Provider omrsProvider = FHIRPractitionerUtil.generateOpenMRSPractitioner();
						practitionerId = practitionerIdentifier.getId();
						//TODO: Get EncounterRole from DiagnosticReport (remove hard coded value)
					} else {
						String practitionerIdReference = performerComponent.getActor().getReference();
						if (!StringUtils.isEmpty(practitionerIdReference) && "/".contains(practitionerIdReference)) {
							practitionerId = practitionerIdReference.split("/")[1];
						}
					}
					omrsProvider = Context.getProviderService().getProviderByUuid(practitionerId);
					omrsDiagnosticReport.addProvider(encounterRole, omrsProvider);
				}
			}
		}

		// Set `ServiceCategory` as EncounterType
		List<Coding> codingList = diagnosticReport.getCategory().getCoding();
		String encounterType = null;
		if (!codingList.isEmpty()) {
			encounterType = codingList.get(0).getCode();
			omrsDiagnosticReport.setEncounterType(FHIRUtils.getEncounterType(encounterType));
		}

		// Set `Diagnosis[x]->DateTime` as valueDateTime in an Obs
		// Set `Diagnosis[x]->Period` as valueDateTime in an Obs

		// Update resource in OpenMRS Database
		Encounter omrsEncounter = encounterService.saveEncounter(omrsDiagnosticReport);

		// Set parsed obsSet (`Result` as Set of Obs)
		// Void existing `Result` values. Since this field is saved as an Obs Group, all group members will be voided.
		java.util.Date date = new java.util.Date();
		for (Obs resultObs : obsSetsMap.get(FHIRConstants.DIAGNOSTIC_REPORT_RESULT)) {
			obsService.voidObs(resultObs, "Due to update DiagnosticReport on " + new Timestamp(date.getTime()));
		}
		// Store new `Result` values
		Set<Obs> resultObsGroupMembersSet = new HashSet<Obs>();
		// Iterate through 'result' Observations and adding to the OpenMRS Obs group
		for (Reference referenceDt : diagnosticReport.getResult()) {
			List<String> errors = new ArrayList<String>();
			Observation observation;
			if (referenceDt.getReference() != null) {
				observation = (Observation) referenceDt.getResource();
			} else {
				// Get Id of the Observation
				String observationID = referenceDt.getId();
				// Assume that the given Observation is stored in the OpenMRS database
				observation = Context.getService(ObsService.class).getObs(observationID);
			}

			observation = prepareForGenerateOpenMRSObs(observation, diagnosticReport);
			Obs obs = FHIRObsUtil.generateOpenMRSObs(observation, errors);
			obs = Context.getObsService().saveObs(obs, null);
			resultObsGroupMembersSet.add(obs);
		}
		if (!resultObsGroupMembersSet.isEmpty()) {
			Concept resultConcept = FHIRUtils.getDiagnosticReportResultConcept();
			Obs resultObsGroup = new Obs(Context.getPersonService().getPersonByUuid(omrsPatient.getUuid()), resultConcept,
					diagnosticReport.getIssued(), null);
			for (Obs obs : resultObsGroupMembersSet) {
				resultObsGroup.addGroupMember(obs);
			}
			resultObsGroup.setEncounter(omrsEncounter);
			Context.getObsService().saveObs(resultObsGroup, null);
		}

		// Update Binary Obs Handler which used to store `PresentedForm`
		// Void existing `PresentedForm` values
		for (Obs attachmentObs : obsSetsMap.get(FHIRConstants.DIAGNOSTIC_REPORT_PRESENTED_FORM)) {
			voidAttachment(attachmentObs);
		}
		obsSetsMap.remove(FHIRConstants.DIAGNOSTIC_REPORT_PRESENTED_FORM);
		// Store new `PresentedForm` values
		for (Attachment attachment : diagnosticReport.getPresentedForm()) {
			int conceptId = FHIRUtils.getDiagnosticReportPresentedFormConcept().getConceptId();
			if (attachment.getCreation() == null) {
				if(diagnosticReport.getIssued() != null) {
					attachment.setCreation(diagnosticReport.getIssued());
				}
			}
			saveComplexData(omrsDiagnosticReport, conceptId, omrsPatient, attachment);
		}

		diagnosticReport.setId(new IdType("DiagnosticReport", omrsEncounter.getUuid()));
		return diagnosticReport;
	}

	private Obs voidAttachment(Obs attachmentObs) {
		org.openmrs.api.ObsService obsService = Context.getObsService();
		int obsId = attachmentObs.getObsId();
		Obs complexObs = obsService.getComplexObs(obsId, "RAW_VIEW");
		java.util.Date date = new java.util.Date();
		obsService.voidObs(complexObs, "Due to update DiagnosticReport on " + new Timestamp(date.getTime()));
		return complexObs;
	}

	@Override
	public void retireFHIRDiagnosticReport(String id) {
		log.debug("Laboratory Handler : RetireFHIRDiagnosticReport with ID " + id);
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
			throw new MethodNotAllowedException(String.format("OpenMRS has failed to retire Encounter '%s' due to : %s", id,
					exAPI.getMessage()));
		}
	}
}
