package org.openmrs.module.fhir.api.diagnosticreport.handler;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hl7.fhir.dstu3.model.*;
import org.openmrs.*;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.*;
import org.openmrs.obs.ComplexData;

import java.util.*;

public abstract class AbstractHandler {

	protected final Log log = LogFactory.getLog(this.getClass());

	public AbstractHandler() {
		//Leave blank for first draft
	}

	protected Obs getObsGroup(DiagnosticReport diagnosticReport, Patient omrsPatient, Encounter omrsEncounter,
			Set<Obs> resultObsGroupMembersSet, Concept diagnosticReportResultConcept) {
		Obs resultObsGroup = new Obs(Context.getPersonService().getPersonByUuid(omrsPatient.getUuid()),
				diagnosticReportResultConcept,
				diagnosticReport.getIssued(), null);
		/**
		 * TODO: This method is not working properly. Need more testing.
		 * resultObsGroup.setGroupMembers(resultObsGroupMembersSet);
		 */
		for (Obs obs : resultObsGroupMembersSet) {
			resultObsGroup.addGroupMember(obs);
		}
		resultObsGroup.setEncounter(omrsEncounter);
		return resultObsGroup;
	}

	protected void saveComplexData(Encounter encounter, int complexConceptId, Patient patient,
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
		Context.getObsService().getComplexObs(obsId, "RAW_VIEW");
	}

	protected Map<String, Set<Obs>> separateObs(Set<Obs> obsSet, boolean includeImagingStudy) {
		Map<String, Set<Obs>> obsSetsMap = new HashMap<>();
		obsSetsMap.put(FHIRConstants.DIAGNOSTIC_REPORT_NAME, new HashSet<Obs>());
		obsSetsMap.put(FHIRConstants.DIAGNOSTIC_REPORT_STATUS, new HashSet<Obs>());
		obsSetsMap.put(FHIRConstants.DIAGNOSTIC_REPORT_RESULT, new HashSet<Obs>());
		obsSetsMap.put(FHIRConstants.DIAGNOSTIC_REPORT_PRESENTED_FORM, new HashSet<Obs>());
		if (includeImagingStudy) {
			obsSetsMap.put(FHIRConstants.DIAGNOSTIC_REPORT_IMAGING_STUDY, new HashSet<Obs>());
		}

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

	protected List<Coding> getCodingList(DiagnosticReport diagnosticReport, Encounter omrsDiagnosticReport) {
		// Set `Performer`(Practitioner) as Encounter Provider
		List<DiagnosticReport.DiagnosticReportPerformerComponent> performers = diagnosticReport.getPerformer();
		if (!performers.isEmpty()) {
			EncounterRole encounterRole = FHIRUtils.getEncounterRole();
			for (DiagnosticReport.DiagnosticReportPerformerComponent performerComponent : performers) {
				if (performerComponent.isEmpty()) {
					//TODO: org.openmrs.Provider omrsProvider = FHIRPractitionerUtil.generatePractitioner();
					omrsDiagnosticReport.addProvider(encounterRole,  new Provider());
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
					omrsDiagnosticReport.addProvider(encounterRole, Context.getProviderService().getProviderByUuid(practitionerId));
				}
			}
		}

		// Set `ServiceCategory` as EncounterType
		return diagnosticReport.getCategory().getCoding();
	}

	protected Patient getPatientFromReport(Encounter omrsDiagnosticReport, Reference subjectReference) {
		Patient omrsPatient;
		if (!subjectReference.isEmpty()) {
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
		} else {
			omrsPatient = omrsDiagnosticReport.getPatient();
		}
		return omrsPatient;
	}

	protected DiagnosticReport generateDiagnosticReport(DiagnosticReport diagnosticReport, Encounter omrsDiagnosticReport,
			Map<String, Set<Obs>> obsSetsMap) {
		// @required: Get EncounterDateTime and set as `Issued` date
		diagnosticReport.setIssued(omrsDiagnosticReport.getEncounterDatetime());

		// @required: Get Encounter Patient and set as `Subject`
		Patient omrsPatient = omrsDiagnosticReport.getPatient();
		diagnosticReport.getSubject().setResource(FHIRPatientUtil.generatePatient(omrsPatient));

		// Get Encounter Provider and set as `Performer`
		EncounterRole omrsEncounterRole = FHIRUtils.getEncounterRole();
		Set<Provider> omrsProviderList = omrsDiagnosticReport.getProvidersByRole(omrsEncounterRole);
		// If at least one provider is set (1..1 mapping in FHIR Diagnostic Report)
		if (!omrsProviderList.isEmpty()) {
			//Role name to a getCodingList display. Is that correct?
			for(Provider practitioner : omrsProviderList) {
				CodeableConcept roleConcept = new CodeableConcept();
				Coding role = new Coding();
				role.setDisplay(omrsEncounterRole.getName());
				roleConcept.addCoding(role);
				Reference practitionerReference = FHIRUtils.buildPractitionerReference(practitioner);
				DiagnosticReport.DiagnosticReportPerformerComponent performer = diagnosticReport.addPerformer();
				performer.setRole(roleConcept);
				performer.setActor(practitionerReference);
			}
		}

		// Get EncounterType and Set `ServiceCategory`
		String serviceCategory = omrsDiagnosticReport.getEncounterType().getName();
		List<Coding> serviceCategoryList = new ArrayList<>();
		serviceCategoryList.add(new Coding(FHIRConstants.CODING_0074, serviceCategory, serviceCategory));
		diagnosticReport.getCategory().setCoding(serviceCategoryList);

		// Get valueDateTime in Obs and Set `Diagnosis[x]->DateTime`
		// Get valueDateTime in Obs and Set `Diagnosis[x]->Period`

		// ObsSet set as `Result`
		List<Reference> resultReferenceDtList = new ArrayList<>();
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
		List<Attachment> attachmentDtList = new ArrayList<>();
		for (Obs attachmentObs : obsSetsMap.get(FHIRConstants.DIAGNOSTIC_REPORT_PRESENTED_FORM)) {
			attachmentDtList.add(getAttachmentDt(attachmentObs));
		}
		if (!attachmentDtList.isEmpty()) {
			diagnosticReport.setPresentedForm(attachmentDtList);
		}

		return diagnosticReport;
	}

	private String getFieldName(Concept concept) throws NoSuchFieldException {
		return getString(concept);
	}

	private String getString(Concept concept) throws NoSuchFieldException {
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

	private Attachment getAttachmentDt(Obs attachmentObs) {
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
}
