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
import org.openmrs.EncounterProvider;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.APIException;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.ObsService;
import org.openmrs.module.fhir.api.db.FHIRDAO;
import org.openmrs.module.fhir.api.diagnosticreport.DiagnosticReportHandler;
import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.openmrs.module.fhir.api.util.FHIRObsUtil;
import org.openmrs.module.fhir.api.util.FHIRPatientUtil;
import org.openmrs.module.fhir.api.util.FHIRUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LaboratoryHandler extends AbstractHandler implements DiagnosticReportHandler {

	private static final String ServiceCategory = "LAB";

	private static final String RESOURCE_TYPE = "DiagnosticReport";

	protected final Log log = LogFactory.getLog(this.getClass());

	public Log getLog() {
		return log;
	}

	public FHIRDAO getDao() {
		return dao;
	}

	public void setDao(FHIRDAO dao) {
		this.dao = dao;
	}

	private FHIRDAO dao;

	public LaboratoryHandler() {
		super();
	}

	public String getServiceCategory() {
		return ServiceCategory;
	}

	@Override
	public DiagnosticReport getFHIRDiagnosticReportById(String id) {
		return getFHIRDiagnosticReport(id);
	}

	@Override
	public List<DiagnosticReport> getFHIRDiagnosticReportBySubjectName(String name) {
		return null;
	}

	private Map<String, Set<Obs>> populateObsForResult(Set<Obs> obsSet, boolean includeImagingStudy) {
		Map<String, Set<Obs>> obsSetsMap = new HashMap<>();
		if (includeImagingStudy) {
			obsSetsMap.put(FHIRConstants.DIAGNOSTIC_REPORT_IMAGING_STUDY, new HashSet<Obs>());
		}
		obsSetsMap.put(FHIRConstants.DIAGNOSTIC_REPORT_RESULT, new HashSet<Obs>());

		for (Obs obs : obsSet) {
			obsSetsMap.get(FHIRConstants.DIAGNOSTIC_REPORT_RESULT).add(obs);
		}
		return obsSetsMap;
	}

	private DiagnosticReport getFHIRDiagnosticReport(String orderUuid) {
		log.debug("Laboratory Handler : GetFHIRDiagnosticReport");
		Order orderByUuid = Context.getOrderService().getOrderByUuid(orderUuid);
		Integer encounterIdForObsOrder = dao.getEncounterIdForObsOrder(orderByUuid.getOrderId());
		String orderClass = getOrderClass(orderByUuid);

		Encounter encounter = Context.getEncounterService().getEncounter(encounterIdForObsOrder);
		Set<Obs> obsAtTopLevel = encounter.getObsAtTopLevel(false);

		Set<Obs> resultObs = new HashSet<>();
		for (Obs obs : obsAtTopLevel) {
			if (obs.getOrder().getUuid().equals(orderUuid)) {
				resultObs.add(obs);
			}
		}
		Map<String, Set<Obs>> obsSetsMap = populateObsForResult(resultObs, false);
		return createDiagnosticReport(orderByUuid, encounter, obsSetsMap);
	}

	private String getOrderClass(Order orderByUuid) {
		return orderByUuid.getConcept().getConceptClass().getName();
	}

	private DiagnosticReport createDiagnosticReport(Order order, Encounter omrsDiagnosticReportEncounter,
														Map<String, Set<Obs>> obsSetsMap) {
		DiagnosticReport diagnosticReport = new DiagnosticReport();
		// Set ID
		diagnosticReport.setId(new IdType(RESOURCE_TYPE, order.getAccessionNumber()));
		// @required: Get EncounterDateTime and set as `Issued` date
		diagnosticReport.setIssued(omrsDiagnosticReportEncounter.getEncounterDatetime());

		// @required: Get Encounter Patient and set as `Subject`
		Patient omrsPatient = omrsDiagnosticReportEncounter.getPatient();
		diagnosticReport.getSubject().setResource(FHIRPatientUtil.generatePatient(omrsPatient));

		// Get Encounter Provider and set as `Performer`
		Set<EncounterProvider> encounterProviders = omrsDiagnosticReportEncounter.getEncounterProviders();
		// If at least one provider is set (1..1 mapping in FHIR Diagnostic Report)
		if (!encounterProviders.isEmpty()) {
			//Role name to a getCodingList display. Is that correct?
			for (EncounterProvider encounterProvider : encounterProviders) {
				Reference practitionerReference = FHIRUtils.buildPractitionerReference(encounterProvider.getProvider());
				DiagnosticReport.DiagnosticReportPerformerComponent performer = diagnosticReport.addPerformer();
				performer.setActor(practitionerReference);
			}
		}

		// Get EncounterType and Set `ServiceCategory`
		String serviceCategory = omrsDiagnosticReportEncounter.getEncounterType().getName();
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

		return diagnosticReport;
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
