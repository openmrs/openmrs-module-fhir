package org.openmrs.module.fhir.api.diagnosticreport.handler;

import ca.uhn.fhir.rest.server.exceptions.MethodNotAllowedException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hl7.fhir.dstu3.model.Attachment;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.DiagnosticReport;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Reference;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.APIException;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.ObsService;
import org.openmrs.module.fhir.api.diagnosticreport.DiagnosticReportHandler;
import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.openmrs.module.fhir.api.util.FHIRObsUtil;
import org.openmrs.module.fhir.api.util.FHIRUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LaboratoryHandler extends AbstractHandler implements DiagnosticReportHandler {

	private static final String ServiceCategory = "LAB";

	private static final String RESOURCE_TYPE = "DiagnosticReport";

	protected final Log log = LogFactory.getLog(this.getClass());

	public LaboratoryHandler() {
		super();
	}

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
		Map<String, Set<Obs>> obsSetsMap = separateObs(omrsDiagnosticReport.getObsAtTopLevel(false), false);

		// Set ID
		diagnosticReport.setId(new IdType(RESOURCE_TYPE, omrsDiagnosticReport.getUuid()));

		// Get Obs and set as `Name`
		// Get Obs and set as `Status`

		return generateDiagnosticReport(diagnosticReport, omrsDiagnosticReport, obsSetsMap);
	}

	@Override
	public DiagnosticReport saveFHIRDiagnosticReport(DiagnosticReport diagnosticReport) {
		log.debug("Laboratory Handler : SaveFHIRDiagnosticReport");
		EncounterService encounterService = Context.getEncounterService();
		Encounter omrsDiagnosticReport = new Encounter();

		// Set `Name` as a Obs
		// Set `Status` as a Obs

		// @require: Set `Issued` date as EncounterDateTime
		omrsDiagnosticReport.setEncounterDatetime(diagnosticReport.getIssued());

		Patient omrsPatient = getPatientFromReport(omrsDiagnosticReport, diagnosticReport.getSubject());

		List<Coding> codingList = getCodingList(diagnosticReport, omrsDiagnosticReport);

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

		addObservationsToTheGroup(diagnosticReport, omrsPatient, omrsEncounter);

		// Set Binary Obs Handler which used to store `PresentedForm`
		for (Attachment attachment : diagnosticReport.getPresentedForm()) {
			int conceptId = FHIRUtils.getDiagnosticReportPresentedFormConcept().getConceptId();
			setAttachmentCreation(diagnosticReport, attachment);
			saveComplexData(omrsDiagnosticReport, conceptId, omrsPatient, attachment);
		}
		/**
		 * TODO: Not working properly. Need to test it. omrsDiagnosticReport.setObs(obsList);
		 */

		diagnosticReport.setId(new IdType(RESOURCE_TYPE, omrsEncounter.getUuid()));
		return diagnosticReport;
	}

	@Override
	public DiagnosticReport updateFHIRDiagnosticReport(DiagnosticReport diagnosticReport, String theId) {
		log.debug("Laboratory Handler : UpdateFHIRDiagnosticReport with ID" + theId);

		org.openmrs.api.ObsService obsService = Context.getObsService();
		EncounterService encounterService = Context.getEncounterService();
		Encounter omrsDiagnosticReport = encounterService.getEncounterByUuid(theId);

		// Separate Obs into different field such as `Name`, `Status`, `Result` and `PresentedForm` based on Concept Id
		Map<String, Set<Obs>> obsSetsMap = separateObs(omrsDiagnosticReport.getObsAtTopLevel(false), false);

		// Set `Name` as a Obs
		// Set `Status` as a Obs

		// If available set `Issued` date as EncounterDateTime
		if (diagnosticReport.getIssued() != null) {
			omrsDiagnosticReport.setEncounterDatetime(diagnosticReport.getIssued());
		}

		Patient omrsPatient = getPatientFromReport(omrsDiagnosticReport, diagnosticReport.getSubject());

		List<Coding> codingList = getCodingList(diagnosticReport, omrsDiagnosticReport);

		if (!codingList.isEmpty()) {
			String encounterType = codingList.get(0).getCode();
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

		addObservationsToTheGroup(diagnosticReport, omrsPatient, omrsEncounter);

		// Update Binary Obs Handler which used to store `PresentedForm`
		// Void existing `PresentedForm` values
		for (Obs attachmentObs : obsSetsMap.get(FHIRConstants.DIAGNOSTIC_REPORT_PRESENTED_FORM)) {
			voidAttachment(attachmentObs);
		}
		obsSetsMap.remove(FHIRConstants.DIAGNOSTIC_REPORT_PRESENTED_FORM);
		// Store new `PresentedForm` values
		for (Attachment attachment : diagnosticReport.getPresentedForm()) {
			setAttachmentCreation(diagnosticReport, attachment);
			int conceptId = FHIRUtils.getDiagnosticReportPresentedFormConcept().getConceptId();
			saveComplexData(omrsDiagnosticReport, conceptId, omrsPatient, attachment);
		}

		diagnosticReport.setId(new IdType(RESOURCE_TYPE, omrsEncounter.getUuid()));
		return diagnosticReport;
	}

	private void setAttachmentCreation(DiagnosticReport diagnosticReport, Attachment attachment) {
		if (attachment.getCreation() == null) {
			if (diagnosticReport.getIssued() != null) {
				attachment.setCreation(diagnosticReport.getIssued());
			}
		}
	}

	private void voidAttachment(Obs attachmentObs) {
		org.openmrs.api.ObsService obsService = Context.getObsService();
		int obsId = attachmentObs.getObsId();
		Obs complexObs = obsService.getComplexObs(obsId, "RAW_VIEW");
		java.util.Date date = new java.util.Date();
		obsService.voidObs(complexObs, "Due to update DiagnosticReport on " + new Timestamp(date.getTime()));
	}

	private void addObservationsToTheGroup(DiagnosticReport diagnosticReport, Patient omrsPatient, Encounter omrsEncounter) {
		// Set parsed obsSet (`Result` as Set of Obs)
		Set<Obs> resultObsGroupMembersSet = new HashSet<>();
		// Iterate through 'result' Observations and adding to the OpenMRS Obs group
		for (Reference referenceDt : diagnosticReport.getResult()) {
			List<String> errors = new ArrayList<>();
			Observation observation;

			if (referenceDt.getReference() != null) {
				observation = (Observation) referenceDt.getResource();
			} else {
				// Get Id of the Observation
				String observationID = referenceDt.getId();
				// Assume that the given Observation is stored in the OpenMRS database
				observation = Context.getService(ObsService.class).getObs(observationID);
			}

			Obs obs = FHIRObsUtil.generateOpenMRSObs(prepareForGenerateOpenMRSObs(observation, diagnosticReport), errors);
			/**
			 * TODO: Unable to check for errors because it's sending errors also for not mandatory
			 * fields if(errors.isEmpty()) {}
			 */
			obs = Context.getObsService().saveObs(obs, null);
			resultObsGroupMembersSet.add(obs);
		}

		if (!resultObsGroupMembersSet.isEmpty()) {
			Obs resultObsGroup = getObsGroup(diagnosticReport, omrsPatient, omrsEncounter, resultObsGroupMembersSet,
					FHIRUtils.getDiagnosticReportResultConcept());

			Context.getObsService().saveObs(resultObsGroup, null);
		}
	}

	private Observation prepareForGenerateOpenMRSObs(Observation observation, DiagnosticReport diagnosticReport) {
		observation.setSubject(diagnosticReport.getSubject());
		observation.setIssued(diagnosticReport.getIssued());
		return observation;
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
