package org.openmrs.module.fhir.api.diagnosticreport.handler;

import ca.uhn.fhir.model.api.Bundle;
import ca.uhn.fhir.model.api.BundleEntry;
import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.rest.gclient.ICriterion;
import ca.uhn.fhir.rest.gclient.ReferenceClientParam;
import ca.uhn.fhir.rest.gclient.TokenClientParam;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hl7.fhir.dstu3.model.Attachment;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.DiagnosticReport;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.ImagingStudy;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Reference;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.diagnosticreport.DiagnosticReportHandler;
import org.openmrs.module.fhir.api.util.ErrorUtil;
import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.openmrs.module.fhir.api.util.FHIRImagingStudyUtil;
import org.openmrs.module.fhir.api.util.FHIRObsUtil;
import org.openmrs.module.fhir.api.util.FHIRRESTfulGenericClient;
import org.openmrs.module.fhir.api.util.FHIRUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RadiologyHandler extends AbstractHandler implements DiagnosticReportHandler {

	private static final String ServiceCategory = "RAD";

	protected final Log log = LogFactory.getLog(this.getClass());

	public RadiologyHandler() {
		super();
	}

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
		if (log.isDebugEnabled()) {
			log.debug("GetFHIRDiagnosticReportBySubjectName : " + name);
		}
		List<DiagnosticReport> diagnosticReports = new ArrayList<DiagnosticReport>();

		String serverBase = FHIRUtils.getDiagnosticReportRadiologyBaseServerURL();
		ICriterion<ReferenceClientParam> diagnosticReportBySubject = DiagnosticReport.SUBJECT.hasChainedProperty(
				Patient.GIVEN.matches().value(name));
		ICriterion<TokenClientParam> diagnosticReportByService = DiagnosticReport.CATEGORY.exactly().code("RAD");
		Bundle bundle = FHIRRESTfulGenericClient.searchWhereReferenceAndToken(serverBase, DiagnosticReport.class,
				diagnosticReportBySubject,
				diagnosticReportByService);
		if (log.isDebugEnabled()) {
			log.debug("Bundle size : " + bundle.size());
		}

		for (BundleEntry entry : bundle.getEntries()) {
			IResource resource = entry.getResource();
			if (log.isDebugEnabled()) {
				log.debug("Resource Type : " + resource.getResourceName());
			}
			if (FHIRConstants.DIAGNOSTIC_REPORT.equals(resource.getResourceName())) {
				DiagnosticReport diagnosticReport = (DiagnosticReport) resource;
				diagnosticReport = this.saveFHIRDiagnosticReport(diagnosticReport);
				diagnosticReport = this.getFHIRDiagnosticReportById(diagnosticReport.getId());

				diagnosticReports.add(diagnosticReport);
			}
		}

		return diagnosticReports;
	}

	private DiagnosticReport getFHIRDiagnosticReport(Encounter omrsDiagnosticReport) {
		DiagnosticReport diagnosticReport = new DiagnosticReport();

		// Separate Obs into different field based on Concept Id
		Map<String, Set<Obs>> obsSetsMap = separateObs(omrsDiagnosticReport.getObsAtTopLevel(false), true);

		// Set ID
		diagnosticReport.setId(new IdType(FHIRConstants.DIAGNOSTIC_REPORT, omrsDiagnosticReport.getUuid()));

		// Get Obs and set as `Name`
		// Get Obs and set as `Status`

		return generateDiagnosticReport(diagnosticReport, omrsDiagnosticReport, obsSetsMap);
	}

	@Override
	public DiagnosticReport saveFHIRDiagnosticReport(DiagnosticReport diagnosticReport) {
		if (log.isDebugEnabled()) {
			log.debug("Saving FHIR DiagnosticReport " + diagnosticReport.getId());
		}
		EncounterService encounterService = Context.getEncounterService();
		ObsService obsService = Context.getObsService();
		Encounter omrsDiagnosticReport = new Encounter();

		// Set `Name` as a Obs
		// Set `Status` as a Obs

		// @require: Set `Issued` date as EncounterDateTime
		omrsDiagnosticReport.setEncounterDatetime(diagnosticReport.getIssued());

		// @required: Set `Subject` as Encounter Patient
		Reference subjectReference = diagnosticReport.getSubject();
		org.openmrs.Patient omrsPatient = getPatientFromReport(omrsDiagnosticReport, subjectReference);

		// Set `ServiceCategory` as EncounterType
		List<Coding> codingList = getCodingList(diagnosticReport, omrsDiagnosticReport);
		String encounterType = FHIRConstants.DEFAULT; // If serviceCategory is not present in the DiagnosticReport, then use "DEFAULT"
		if (!codingList.isEmpty()) {
			//TODO: Need to fix. multiple codes
			encounterType = codingList.get(1).getCode();
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

		/****************************** Set `Result` field *************************************************/
		// Set parsed obsSet (`Result` as Set of Obs)
		Set<Obs> resultObsGroupMembersSet = new HashSet<>();
		// Iterate through 'result' Observations and adding to the OpenMRS Obs group
		for (Reference referenceDt : diagnosticReport.getResult()) {
			List<String> errors = new ArrayList<>();
			Observation observation = null;

			if (!referenceDt.getReference().isEmpty()) {
				observation = (Observation) referenceDt.getResource();
			} else {
				// Get Id of the Observation
				String observationID = referenceDt.getId();
				if (StringUtils.isEmpty(observationID)) {
					// Assume that the given Observation is stored in the OpenMRS database
					observation = Context.getService(org.openmrs.module.fhir.api.ObsService.class).getObs(observationID);
				} else {
					String observationReference = referenceDt.getReference();
					if (!StringUtils.isEmpty(observationReference) && "/".contains(observationReference)) {
						observationID = observationReference.split("/")[1];
						observation = Context.getService(org.openmrs.module.fhir.api.ObsService.class).getObs(observationID);
					}
				}
			}
			Obs obs = FHIRObsUtil.generateOpenMRSObs(prepareForGenerateOpenMRSObs(observation, diagnosticReport), errors);
			if (errors.isEmpty()) {
				obs = obsService.saveObs(obs, null);
				resultObsGroupMembersSet.add(obs);
			} else {
				String errorMessage = ErrorUtil.generateErrorMessage(errors, FHIRConstants.REQUEST_ISSUE_LIST);
				throw new UnprocessableEntityException(errorMessage);
			}
		}
		if (!resultObsGroupMembersSet.isEmpty()) {

			Obs resultObsGroup = getObsGroup(diagnosticReport, omrsPatient, omrsEncounter, resultObsGroupMembersSet,
					FHIRUtils.getDiagnosticReportResultConcept());

			obsService.saveObs(resultObsGroup, null);
		} //-- END of set `result`

		/****************************** Set `ImagingStudy` as a set of Obs *********************************/
		Set<Obs> imagingStudyObsGroupMembersSet = new HashSet<>();
		// Iterate through 'ImagingStudy', convert to the OpenMRS Obs group
		for (Reference referenceDt : diagnosticReport.getImagingStudy()) {
			Obs obs;
			if (!referenceDt.getReference().isEmpty()) {
				List<String> errors = new ArrayList<>();
				ImagingStudy imagingStudy = (ImagingStudy) referenceDt.getResource();
				obs = FHIRImagingStudyUtil.generateOpenMRSImagingStudy(imagingStudy, errors);
				if (!errors.isEmpty()) {
					String errorMessage = ErrorUtil.generateErrorMessage(errors, FHIRConstants.REQUEST_ISSUE_LIST);
					throw new UnprocessableEntityException(errorMessage);
				}
			} else {
				// Get Id of the ImagingStudy
				String imagingStudyId = referenceDt.getId();
				// Get `ImagingStudy` Obs from external server
				obs = this.getOpenMRSImagingStudyObs(imagingStudyId);
			}
			obs = obsService.saveObs(obs, null);
			imagingStudyObsGroupMembersSet.add(obs);
		}
		if (!imagingStudyObsGroupMembersSet.isEmpty()) {
			Obs imagingStudyObsGroup = getObsGroup(diagnosticReport, omrsPatient, omrsEncounter, resultObsGroupMembersSet,
					FHIRUtils.getDiagnosticReportImagingStudyConcept());
			obsService.saveObs(imagingStudyObsGroup, null);
		} //-- Set `ImagingStudy` as a set of Obs

		// Set Binary Obs Handler which used to store `PresentedForm`
		for (Attachment attachment : diagnosticReport.getPresentedForm()) {
			int conceptId = FHIRUtils.getDiagnosticReportPresentedFormConcept().getConceptId();
			if (attachment.getCreation() == null) {
				if (diagnosticReport.getIssued() != null) {
					attachment.setCreation(diagnosticReport.getIssued());
				}
			}
			saveComplexData(omrsDiagnosticReport, conceptId, omrsPatient, attachment);
		}
		// TODO: Not working properly. Need to test it. omrsDiagnosticReport.setObs(obsList);

		diagnosticReport.setId(new IdType(FHIRConstants.DIAGNOSTIC_REPORT, omrsEncounter.getUuid()));
		return diagnosticReport;
	}

	/**
	 * Simple Hack for get generateOpenMRSObs get worked
	 *
	 * @param observation      FHIR Observation
	 * @param diagnosticReport FHIR DiagnosticReport
	 * @return FHIR Observation
	 */
	private Observation prepareForGenerateOpenMRSObs(Observation observation, DiagnosticReport diagnosticReport) {
		observation.setSubject(diagnosticReport.getSubject());
		observation.setIssued(diagnosticReport.getIssued());
		return observation;
	}

	/**
	 * Check whether there is a Provider in the Database for given uuid. If it's existing, then retrieve it, otherwise
	 * retrieve from third party server, generate Obs group which represent given ImangingStudy and return it back.
	 * NOTE: This method is not saving the Obs
	 *
	 * @param imagingStudyId FHIR ImagingStudy resource
	 * @return A OpenMRS Obs group
	 */
	private Obs getOpenMRSImagingStudyObs(String imagingStudyId) {
		// Check whether ImagingStudy Obs is already exist
		Obs imagingStudyObs = Context.getObsService().getObsByUuid(imagingStudyId);
		if (imagingStudyObs == null) {
			// ImagingStudy isn't in the database, then retrieve it
			String serverBase = FHIRUtils.getDiagnosticReportRadiologyBaseServerURL();
			ImagingStudy imagingStudy = FHIRRESTfulGenericClient.readImagingStudyById(serverBase, imagingStudyId);
			// Generate OpenMRS Obs from ImagingStudy Resource
			List<String> errors = new ArrayList<>();
			imagingStudyObs = FHIRImagingStudyUtil.generateOpenMRSImagingStudy(imagingStudy, errors);
			if (errors.isEmpty()) {
				return imagingStudyObs;
			} else {
				String errorMessage = ErrorUtil.generateErrorMessage(errors);
				if (log.isErrorEnabled()) {
					log.error("ImagingStudy create error : " + errorMessage);
				}
				throw new UnprocessableEntityException(errorMessage);
			}
		} else {
			return imagingStudyObs;
		}
	}

	@Override
	public DiagnosticReport updateFHIRDiagnosticReport(DiagnosticReport diagnosticReport, String theId) {
		return diagnosticReport;
	}

	@Override
	public void retireFHIRDiagnosticReport(String id) {
	}
}
