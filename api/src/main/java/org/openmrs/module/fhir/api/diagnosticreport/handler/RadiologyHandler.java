package org.openmrs.module.fhir.api.diagnosticreport.handler;

import ca.uhn.fhir.model.api.Bundle;
import ca.uhn.fhir.model.api.BundleEntry;
import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu2.composite.AttachmentDt;
import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import ca.uhn.fhir.model.dstu2.composite.HumanNameDt;
import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.DiagnosticReport;
import ca.uhn.fhir.model.dstu2.resource.ImagingStudy;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.dstu2.resource.Practitioner;
import ca.uhn.fhir.model.primitive.Base64BinaryDt;
import ca.uhn.fhir.model.primitive.DateDt;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.InstantDt;
import ca.uhn.fhir.model.primitive.StringDt;
import ca.uhn.fhir.rest.gclient.ICriterion;
import ca.uhn.fhir.rest.gclient.ReferenceClientParam;
import ca.uhn.fhir.rest.gclient.TokenClientParam;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
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
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.diagnosticreport.DiagnosticReportHandler;
import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.openmrs.module.fhir.api.util.FHIRImagingStudyUtil;
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

import static java.lang.String.valueOf;

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
			if (resource.getResourceName().equals("DiagnosticReport")) {
				DiagnosticReport diagnosticReport = (DiagnosticReport) resource;
				diagnosticReport = this.saveFHIRDiagnosticReport(diagnosticReport);
				diagnosticReport = this.getFHIRDiagnosticReportById(diagnosticReport.getId().getIdPart());

				diagnosticReports.add(diagnosticReport);
			}
		}

		return diagnosticReports;
	}

	private DiagnosticReport getFHIRDiagnosticReport(Encounter omrsDiagnosticReport) {
		DiagnosticReport diagnosticReport = new DiagnosticReport();

		// Separate Obs into different field based on Concept Id
		Map<String, Set<Obs>> obsSetsMap = this.separateObs(omrsDiagnosticReport.getObsAtTopLevel(false));

		// Set ID
		diagnosticReport.setId(new IdDt("DiagnosticReport", omrsDiagnosticReport.getUuid()));

		// Get Obs and set as `Name`
		// Get Obs and set as `Status`

		// @required: Get EncounterDateTime and set as `Issued` date
		diagnosticReport.setIssued(new InstantDt(omrsDiagnosticReport.getEncounterDatetime()));

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
		diagnosticReport.getCategory().setCoding(serviceCategoryList);

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
		obsSetsMap.put(FHIRConstants.DIAGNOSTIC_REPORT_IMAGING_STUDY, new HashSet<Obs>());
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

		Obs complexObs = Context.getObsService().getComplexObs(obsId, "RAW_VIEW");
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
		if (log.isDebugEnabled()) {
			log.debug("SaveFHIRDiagnosticReport " + diagnosticReport.getId().getIdPart());
		}
		EncounterService encounterService = Context.getEncounterService();
		ObsService obsService = Context.getObsService();
		Encounter omrsDiagnosticReport = new Encounter();

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
			if (log.isDebugEnabled()) {
				log.debug("Get Patient : " + patientID);
			}
			omrsPatient = this.getOpenMRSPatient(patientID);
			omrsDiagnosticReport.setPatient(omrsPatient);
		}

		// Only support Practitioner (Not support Organization)
		if ("Practitioner".equals(diagnosticReport.getPerformer().getReference().getResourceType())) {
			// Set `Performer`(Practitioner) as Encounter Provider
			if (diagnosticReport.getPerformer().getReference().isLocal()) {
				/** TODO: Practitioner practitioner = (Practitioner) diagnosticReport.getPerformer().getResource();
				 org.openmrs.Provider omrsProvider = FHIRPractitionerUtil.generatePractitioner();*/
				Provider omrsProvider = new Provider();
				omrsDiagnosticReport.setProvider(new EncounterRole(), omrsProvider);
			} else {
				// Get Id of the Performer
				String practitionerID = diagnosticReport.getPerformer().getReference().getIdPart();
				if (log.isDebugEnabled()) {
					log.debug("Get Performer : " + practitionerID);
				}
				EncounterRole encounterRole = FHIRUtils.getEncounterRole();
				omrsDiagnosticReport.setProvider(encounterRole, this.getOpenMRSProvider(practitionerID));
			}
		}

		// Set `ServiceCategory` as EncounterType
		List<CodingDt> codingList = diagnosticReport.getCategory().getCoding();
		String encounterType = "DEFAULT"; // If serviceCategory is not present in the DiagnosticReport, then use "DEFAULT"
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
				observation = Context.getService(org.openmrs.module.fhir.api.ObsService.class).getObs(observationID);
			}
			observation = this.prepareForGenerateOpenMRSObs(observation, diagnosticReport);
			Obs obs = FHIRObsUtil.generateOpenMRSObs(observation, errors);
			if (errors.isEmpty()) {
				obs = obsService.saveObs(obs, null);
				resultObsGroupMembersSet.add(obs);
			} else {
				StringBuilder errorMessage = new StringBuilder(FHIRConstants.REQUEST_ISSUE_LIST);
				for (int i = 0; i < errors.size(); i++) {
					errorMessage.append(i + 1).append(" : ").append(errors.get(i)).append("\n");
				}
				throw new UnprocessableEntityException(errorMessage.toString());
			}
		}
		if (!resultObsGroupMembersSet.isEmpty()) {
			Concept resultConcept = FHIRUtils.getDiagnosticReportResultConcept();
			Obs resultObsGroup = new Obs(Context.getPersonService().getPersonByUuid(omrsPatient.getUuid()),
					resultConcept,
					diagnosticReport.getIssued(), null);
			/**
			 * TODO: This method is not working properly. Need more testing.
			 * resultObsGroup.setGroupMembers(resultObsGroupMembersSet);
			 */
			for (Obs obs : resultObsGroupMembersSet) {
				resultObsGroup.addGroupMember(obs);
			}
			resultObsGroup.setEncounter(omrsEncounter);
			obsService.saveObs(resultObsGroup, null);
		} //-- END of set `result`

		/****************************** Set `ImagingStudy` as a set of Obs *********************************/
		Set<Obs> imagingStudyObsGroupMembersSet = new HashSet<Obs>();
		// Iterate through 'ImagingStudy', convert to the OpenMRS Obs group
		for (ResourceReferenceDt referenceDt : diagnosticReport.getImagingStudy()) {
			Obs obs;
			if (referenceDt.getReference().isLocal()) {
				List<String> errors = new ArrayList<String>();
				ImagingStudy imagingStudy = (ImagingStudy) referenceDt.getResource();
				obs = FHIRImagingStudyUtil.generateOpenMRSImagingStudy(imagingStudy, errors);
				if (!errors.isEmpty()) {
					StringBuilder errorMessage = new StringBuilder(FHIRConstants.REQUEST_ISSUE_LIST);
					for (int i = 0; i < errors.size(); i++) {
						errorMessage.append(i + 1).append(" : ").append(errors.get(i)).append("\n");
					}
					throw new UnprocessableEntityException(errorMessage.toString());
				}
			} else {
				// Get Id of the ImagingStudy
				String imagingStudyId = referenceDt.getReference().getIdPart();
				// Get `ImagingStudy` Obs from external server
				obs = this.getOpenMRSImagingStudyObs(imagingStudyId);
			}
			obs = obsService.saveObs(obs, null);
			imagingStudyObsGroupMembersSet.add(obs);
		}
		if (!imagingStudyObsGroupMembersSet.isEmpty()) {
			Concept imagingStudyConcept = FHIRUtils.getDiagnosticReportImagingStudyConcept();
			Obs imagingStudyObsGroup = new Obs(Context.getPersonService().getPersonByUuid(omrsPatient.getUuid()),
					imagingStudyConcept, diagnosticReport.getIssued(), null);
			/**
			 * TODO: This method is not working properly. Need more testing.
			 * imagingStudyObsGroup.setGroupMembers(resultObsGroupMembersSet);
			 */
			for (Obs obs : resultObsGroupMembersSet) {
				imagingStudyObsGroup.addGroupMember(obs);
			}
			imagingStudyObsGroup.setEncounter(omrsEncounter);
			obsService.saveObs(imagingStudyObsGroup, null);
		} //-- Set `ImagingStudy` as a set of Obs

		// Set Binary Obs Handler which used to store `PresentedForm`
		for (AttachmentDt attachment : diagnosticReport.getPresentedForm()) {
			int conceptId = FHIRUtils.getDiagnosticReportPresentedFormConcept().getConceptId();
			if (attachment.getCreation() == null) {
				if(diagnosticReport.getIssued() != null) {
					DateTimeDt dateDt = new DateTimeDt(diagnosticReport.getIssued());
					attachment.setCreation(dateDt);
				}
			}
			saveComplexData(omrsDiagnosticReport, conceptId, omrsPatient, attachment);
		}
		// TODO: Not working properly. Need to test it. omrsDiagnosticReport.setObs(obsList);

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
		return Context.getObsService().getComplexObs(obsId, "RAW_VIEW");
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
		observation.setIssued(diagnosticReport.getIssuedElement());
		return observation;
	}

	/**
	 * Check whether there is a Patient in the Database for given uuid. If it's existing, then retrieve it, otherwise
	 * retrieve from third party server, save as a new Patient and return it back.
	 *
	 * @param uuid Uuid or FHIR Patient Id
	 * @return Patient Resultant OpenMRS Patient
	 * TODO: Current implementation is using Patient ID. But that may change from System to System as mentioned in
	 * http://hl7.org/fhir/2015May/resource.html#identifiers. It's better to use Identifiers.
	 */
	private org.openmrs.Patient getOpenMRSPatient(String uuid) {
		if(log.isDebugEnabled()) {
			log.debug("GetOpenMRSPatient " + uuid);
		}
		//Check whether Patient is already exist
		org.openmrs.Patient omrsPatient = Context.getPatientService().getPatientByUuid(uuid);
		if (omrsPatient == null) {
			// If Patient isn't in the Database, retrieve it
			String serverBase = FHIRUtils.getDiagnosticReportRadiologyBaseServerURL();
			Patient patient = FHIRRESTfulGenericClient.readPatientById(serverBase, uuid);
			List<String> errors = new ArrayList<String>();
			omrsPatient = FHIRPatientUtil.generateOmrsPatient(patient, errors);
			if (errors.isEmpty()) {
				// Save the retrieved Patient
				omrsPatient = Context.getPatientService().savePatient(omrsPatient);
				return omrsPatient;
			} else {
				StringBuilder errorMessage = new StringBuilder(
						"The request cannot be processed due to the following issues \n");
				for (int i = 0; i < errors.size(); i++) {
					errorMessage.append(i + 1).append(" : ").append(errors.get(i)).append("\n");
				}
				if (log.isErrorEnabled()) {
					log.error("Patient create error : " + errorMessage.toString());
				}
				throw new UnprocessableEntityException(errorMessage.toString());
			}
		} else {
			return omrsPatient;
		}
	}

	/**
	 * Check whether there is a Provider in the Database for given uuid. If it's existing, then retrieve it, otherwise
	 * retrieve from third party server, save as a new Provider and return it back.
	 *
	 * @param uuid Provider Uuid or FHIR Practitioner Id
	 * @return Provider Resultant OpenMRS Provider
	 * TODO: Current implementation is using Provider ID. But that may change from System to System as mentioned in
	 * http://hl7.org/fhir/2015May/resource.html#identifiers. It's better to use Identifiers.
	 */
	private Provider getOpenMRSProvider(String uuid) {
		//Check whether Provider is already existing
		Provider omrsProvider = Context.getProviderService().getProviderByUuid(uuid);
		if (omrsProvider == null) {
			// If Provider isn't in the Database, retrieve it
			String serverBase = FHIRUtils.getDiagnosticReportRadiologyBaseServerURL();
			Practitioner practitioner = FHIRRESTfulGenericClient.readPractitionerById(serverBase, uuid);
			omrsProvider = this.createProvider(practitioner);
			omrsProvider.setUuid(uuid);
			// Save the retrieved Provider
			omrsProvider = Context.getProviderService().saveProvider(omrsProvider);
			return omrsProvider;
		} else {
			return omrsProvider;
		}
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
			List<String> errors = new ArrayList<String>();
			imagingStudyObs = FHIRImagingStudyUtil.generateOpenMRSImagingStudy(imagingStudy, errors);
			if (errors.isEmpty()) {
				return imagingStudyObs;
			} else {
				StringBuilder errorMessage = new StringBuilder(
						"The request cannot be processed due to the following issues \n");
				for (int i = 0; i < errors.size(); i++) {
					errorMessage.append(i + 1).append(" : ").append(errors.get(i)).append("\n");
				}
				if (log.isErrorEnabled()) {
					log.error("ImagingStudy create error : " + errorMessage.toString());
				}
				throw new UnprocessableEntityException(errorMessage.toString());
			}
		} else {
			return imagingStudyObs;
		}
	}

	/**
	 * Create a OpenMRS from a FHIR Practitioner resource
	 *
	 * @param practitioner FHIR Practitioner Resource
	 * @return OpenMRS Provider
	 */
	private Provider createProvider(Practitioner practitioner) {
		Provider provider = new Provider();
		List<String> errors = new ArrayList<String>();
		String practitionerName = "";
		// extracts openmrs person from the practitioner representation
		Person personFromRequest = FHIRPractitionerUtil.extractOpenMRSPerson(practitioner);
		List<IdentifierDt> identifiers = practitioner.getIdentifier();
		// identifiers can be empty
		if (identifiers != null && !identifiers.isEmpty()) {
			IdentifierDt identifierDt = identifiers.get(0);
			provider.setIdentifier(identifierDt.getValue());
		}
		// if this is true, that means the request doesn't have enough attributes to create a person
		if (personFromRequest == null) {
			// from it, or attach a person from existing ones
			HumanNameDt humanNameDt = practitioner.getName();
			// check whether at least one name is exist. if so we can create a practitioner without
			if (humanNameDt != null) {
				// attaching a person, just with a name.
				List<StringDt> givenNames = humanNameDt.getGiven();
				if (givenNames != null && !givenNames.isEmpty()) {
					StringDt givenName = givenNames.get(0);
					practitionerName = valueOf(givenName);
				}
				List<StringDt> familyNames = humanNameDt.getFamily();
				if (familyNames != null && !familyNames.isEmpty()) {
					StringDt familyName = familyNames.get(0);
					practitionerName = practitionerName + " " + valueOf(familyName); // will create a name like "John David"
				}
				if ("".equals(practitionerName)) { // there is no given name or family name. cannot proceed with the request
					errors.add("Practitioner should contain at least given name or family name");
				}
			} else {
				errors.add("Practitioner should contain at least given name or family name");
			}
		}
		if (!errors.isEmpty()) {
			StringBuilder errorMessage = new StringBuilder(FHIRConstants.REQUEST_ISSUE_LIST);
			for (int i = 0; i < errors.size(); i++) {
				errorMessage.append(i + 1).append(" : ").append(errors.get(i)).append("\n");
			}
			throw new UnprocessableEntityException(errorMessage.toString());
		}
		// if this is not null, we can have a person resource along the practitioner resource
		if (personFromRequest != null) {
			// either map to an existing person, or create a new person for the given representation
			Person personToProvider = FHIRPractitionerUtil.generateOpenMRSPerson(personFromRequest);
			provider.setPerson(personToProvider);
		} else {
			provider.setName(practitionerName); // else create the practitioner just with the name
		}
		Provider omrsProvider = Context.getProviderService().saveProvider(provider);
		if (personFromRequest == null) {
			omrsProvider.setPerson(null);
		}
		return omrsProvider;
	}

	@Override
	public DiagnosticReport updateFHIRDiagnosticReport(DiagnosticReport diagnosticReport, String theId) {
		return diagnosticReport;
	}

	@Override
	public void retireFHIRDiagnosticReport(String id) {
	}
}
