package org.openmrs.module.fhir.api.strategies.encounter;

import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Composition;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.instance.model.api.IIdType;
import org.openmrs.EncounterProvider;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Visit;
import org.openmrs.api.EncounterService;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.openmrs.module.fhir.api.util.FHIREncounterUtil;
import org.openmrs.module.fhir.api.util.FHIRLocationUtil;
import org.openmrs.module.fhir.api.util.FHIRPatientUtil;
import org.openmrs.module.fhir.api.util.FHIRPractitionerUtil;
import org.openmrs.module.fhir.api.util.FHIRUtils;
import org.openmrs.module.fhir.api.util.FHIRVisitUtil;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.openmrs.module.fhir.api.util.FHIRUtils.extractUuid;

@Component("DefaultEncounterStrategy")
public class EncounterStrategy implements GenericEncounterStrategy {

	protected final Log log = LogFactory.getLog(this.getClass());

	@Override
	public Encounter getEncounter(String id) {
		org.openmrs.Encounter omrsEncounter = Context.getEncounterService().getEncounterByUuid(id);

		if (omrsEncounter == null || omrsEncounter.isVoided()) {
			Visit visit = Context.getVisitService().getVisitByUuid(id);
			if (visit != null && !visit.isVoided()) {
				return FHIRVisitUtil.generateEncounter(visit);
			} else {
				return null;
			}
		}
		return FHIREncounterUtil.generateEncounter(omrsEncounter);
	}

	/**
	 * @see org.openmrs.module.fhir.api.EncounterService#searchEncounterById(String)
	 */
	@Override
	public List<Encounter> searchEncounterById(String id) {
		org.openmrs.Encounter omrsEncounter = Context.getEncounterService().getEncounterByUuid(id);
		List<Encounter> encounterList = new ArrayList<>();
		if (omrsEncounter != null) {
			encounterList.add(FHIREncounterUtil.generateEncounter(omrsEncounter));
		} else {
			Visit visit = Context.getVisitService().getVisitByUuid(id);
			if (visit != null) {
				encounterList.add(FHIRVisitUtil.generateEncounter(visit));
			}
		}
		return encounterList;
	}

	/**
	 * @see org.openmrs.module.fhir.api.EncounterService#searchEncountersByPatientIdentifier(String)
	 */
	@Override
	public List<Encounter> searchEncountersByPatientIdentifier(String identifier) {
		org.openmrs.api.PatientService patientService = Context.getPatientService();
		List<PatientIdentifierType> allPatientIdentifierTypes = patientService.getAllPatientIdentifierTypes();
		List<org.openmrs.Patient> patientList = patientService
				.getPatients(null, identifier, allPatientIdentifierTypes, true);
		List<Encounter> fhirEncountersList = new ArrayList<>();

		for (Patient patient : patientList) {
			List<org.openmrs.Encounter> encounters = Context.getEncounterService().getEncountersByPatient(patient);
			for (org.openmrs.Encounter encounter : encounters) {
				fhirEncountersList.add(FHIREncounterUtil.generateEncounter(encounter));
			}
		}

		for (Patient patient : patientList) {
			List<Visit> visits = Context.getVisitService().getVisitsByPatient(patient);
			for (Visit visit : visits) {
				fhirEncountersList.add(FHIRVisitUtil.generateEncounter(visit));
			}
		}
		return fhirEncountersList;
	}

	/**
	 * @see org.openmrs.module.fhir.api.EncounterService#searchEncounterComposition(String)
	 */
	@Override
	public List<Composition> searchEncounterComposition(String id) {
		org.openmrs.Encounter omrsEncounter = Context.getEncounterService().getEncounterByUuid(id);
		List<Composition> encounterList = new ArrayList<>();
		if (omrsEncounter != null) {
			try {
				encounterList.add(FHIREncounterUtil.generateComposition(omrsEncounter));
			} catch (FHIRException e) {
				String msg = "Error while searching the encounter composition with id " + id;
				log.error(msg, e);
			}
		}
		return encounterList;
	}

	/**
	 * @see org.openmrs.module.fhir.api.EncounterService#searchEncounterCompositionByPatientId(String)
	 */
	@Override
	public List<Composition> searchEncounterCompositionByPatientId(String patientId) {
		Patient patient = Context.getPatientService().getPatientByUuid(patientId);
		List<org.openmrs.Encounter> omrsEncounters = Context.getEncounterService().getEncountersByPatient(patient);
		List<Composition> fhirEncounters = new ArrayList<>();
		for (org.openmrs.Encounter enc : omrsEncounters) {
			try {
				fhirEncounters.add(FHIREncounterUtil.generateComposition(enc));
			} catch (FHIRException e) {
				String msg = "Error while searching the encounter composition by patient id " + patientId;
				log.error(msg, e);
			}
		}
		return fhirEncounters;
	}

	/**
	 * @see org.openmrs.module.fhir.api.EncounterService#searchEncounterCompositionByEncounterId(String)
	 */
	@Override
	public List<Composition> searchEncounterCompositionByEncounterId(String encounterId) {
		org.openmrs.Encounter omrsEncounter = Context.getEncounterService().getEncounterByUuid(encounterId);
		List<Composition> encounterList = new ArrayList<>();
		if (omrsEncounter != null) {
			try {
				encounterList.add(FHIREncounterUtil.generateComposition(omrsEncounter));
			} catch (FHIRException e) {
				String msg = "Error while searching the encounter composition by encounter id " + encounterId;
				log.error(msg, e);
			}
		}
		return encounterList;
	}

	/**
	 * @see org.openmrs.module.fhir.api.EncounterService#getEncounterOperationsById(String)
	 */
	@Override
	public Bundle getEncounterOperationsById(String encounterId) {
		return getEncounterOperationsById(encounterId, new Bundle(), true);
	}

	/**
	 * @see org.openmrs.module.fhir.api.EncounterService#getEncounterOperationsById(String,
	 *      org.hl7.fhir.dstu3.model.Bundle, boolean)
	 */
	@Override
	public Bundle getEncounterOperationsById(String encounterId, Bundle bundle, boolean includePatient) {
		org.openmrs.Encounter omsrEncounter;
		omsrEncounter = Context.getEncounterService().getEncounterByUuid(encounterId);
		if (omsrEncounter != null) {
			Bundle.BundleEntryComponent encounter = bundle.addEntry();
			encounter.setResource(FHIREncounterUtil.generateEncounter(omsrEncounter));

			//Set filtered obs if obs allergy strategy used
			FHIREncounterUtil.addFilteredObs(omsrEncounter, bundle);

			//Set location
			Bundle.BundleEntryComponent location;
			if (omsrEncounter.getLocation() != null) {
				location = bundle.addEntry();
				location.setResource(FHIRLocationUtil.generateLocation(omsrEncounter.getLocation()));
			}

			//Set patient
			if (includePatient) {
				Bundle.BundleEntryComponent patient = bundle.addEntry();
				patient.setResource(FHIRPatientUtil.generatePatient(omsrEncounter.getPatient()));
			}

			//Set providers
			Bundle.BundleEntryComponent provider;
			for (EncounterProvider encounterProvider : omsrEncounter.getEncounterProviders()) {
				provider = bundle.addEntry();
				provider.setResource(FHIRPractitionerUtil.generatePractitioner(encounterProvider.getProvider()));
			}
		}
		return bundle;
	}

	/**
	 * @see org.openmrs.module.fhir.api.EncounterService#deleteEncounter(String)
	 */
	@Override
	public void deleteEncounter(String id) {
		org.openmrs.Encounter encounter = Context.getEncounterService().getEncounterByUuid(id);
		if (encounter == null) {
			Visit visit = Context.getVisitService().getVisitByUuid(id);
			if (visit == null) {
				//Jira related https://issues.openmrs.org/browse/FM-194
				IIdType idType = new IdType();
				idType.setValue(id);
				throw new ResourceNotFoundException(idType);
			} else {
				Context.getVisitService().voidVisit(visit, FHIRConstants.FHIR_VOIDED_MESSAGE);
			}
		} else {
			Context.getEncounterService().voidEncounter(encounter, FHIRConstants.FHIR_VOIDED_MESSAGE);
		}
	}

	/**
	 * @see org.openmrs.module.fhir.api.EncounterService#searchEncountersByPatientIdentifierAndPartOf(String,
	 *      String)
	 */
	@Override
	public List<Encounter> searchEncountersByPatientIdentifierAndPartOf(String patientIdentifier, String partOf) {
		org.openmrs.api.PatientService patientService = Context.getPatientService();
		List<PatientIdentifierType> allPatientIdentifierTypes = patientService.getAllPatientIdentifierTypes();
		List<org.openmrs.Patient> patientList = patientService.getPatients(patientIdentifier, null,
				allPatientIdentifierTypes, true);
		List<Encounter> fhirEncounterList = new ArrayList<>();

		for (Patient patient : patientList) {
			List<org.openmrs.Encounter> encounters = Context.getEncounterService().getEncountersByPatient(patient);
			for (org.openmrs.Encounter encounter : encounters) {
				fhirEncounterList = addEncountersByPartOf(encounter, partOf, fhirEncounterList);
			}
		}

		for (Patient patient : patientList) {
			List<Visit> visits = Context.getVisitService().getVisitsByPatient(patient);
			if (FHIRConstants.NONE.equalsIgnoreCase(partOf)) {
				for (Visit visit : visits) {
					fhirEncounterList.add(FHIRVisitUtil.generateEncounter(visit));
				}
			}
		}
		return fhirEncounterList;
	}

	/**
	 * @see org.openmrs.module.fhir.api.EncounterService#searchEncountersByEncounterIdAndPartOf(String,
	 *      String)
	 */
	@Override
	public List<Encounter> searchEncountersByEncounterIdAndPartOf(String encounterId, String partOf) {
		org.openmrs.Encounter encounter = Context.getEncounterService().getEncounterByUuid(encounterId);
		List<Encounter> fhirEncounterList = new ArrayList<>();
		if (encounter != null) {
			fhirEncounterList = addEncountersByPartOf(encounter, partOf, fhirEncounterList);
		}

		if (fhirEncounterList.size() == 0) {
			Visit visit = Context.getVisitService().getVisitByUuid(encounterId);
			if (visit != null) {
				if (FHIRConstants.NONE.equalsIgnoreCase(partOf)) {
					fhirEncounterList.add(FHIRVisitUtil.generateEncounter(visit));
				}
			}
		}
		return fhirEncounterList;
	}

	/**
	 * @see org.openmrs.module.fhir.api.EncounterService#createFHIREncounter(Encounter)
	 */
	@Override
	public Encounter createFHIREncounter(Encounter encounter) {
		List<String> errors = new ArrayList<>();
		org.openmrs.Encounter encounterToCreate = null;
		Reference encounterRef = encounter.getPartOf();
		Visit visit;

		if (encounterRef != null && !encounterRef.isEmpty()) { // if partOf is not empty, This Encounter should be created under an Visit
			encounterToCreate = FHIREncounterUtil.generateOMRSEncounter(encounter, errors);
			String encounterRefUuid = FHIRUtils.getObjectUuidByReference(encounterRef);

			visit = Context.getVisitService().getVisitByUuid(encounterRefUuid);
			if (visit == null) {
				errors.add("No Encounters found for id : " + encounterRefUuid);
			} else {
				encounterToCreate.setVisit(visit); // this is an encounter of an admitted patient
			}
		} else {
			org.openmrs.Patient patient = null;
			if (encounter.getSubject() != null) {
				Reference patientRef = encounter.getSubject();
				String patientUuid = FHIRUtils.getObjectUuidByReference(patientRef);

				patient = Context.getPatientService().getPatientByUuid(patientUuid);
				if (patient == null) {
					errors.add("There is no patient for the given uuid " + patientUuid); // remove to constants
				}
			}
			List<Visit> activeVisits = Context.getVisitService().getActiveVisitsByPatient(patient);
			for (Visit activeVisit : activeVisits) {
				activeVisit.setStopDatetime(new Date());
			}
			visit = FHIRVisitUtil.generateOMRSVisit(encounter, errors);

		}
		FHIRUtils.checkGeneratorErrorList(errors);

		if (encounterRef != null && !encounterRef.isEmpty()) {
			encounterToCreate = Context.getEncounterService().saveEncounter(encounterToCreate);
			return FHIREncounterUtil.generateEncounter(encounterToCreate);
		} else {
			visit = Context.getVisitService().saveVisit(visit);
			return FHIRVisitUtil.generateEncounter(visit);
		}
	}

	/**
	 * @see org.openmrs.module.fhir.api.EncounterService#updateEncounter(Encounter, String)
	 */
	@Override
	public Encounter updateEncounter(Encounter encounter, String uuid) {
		uuid = extractUuid(uuid);
		EncounterService encounterService = Context.getEncounterService();
		org.openmrs.Encounter retrievedEncounter = encounterService.getEncounterByUuid(uuid);

		if (retrievedEncounter != null) {
			return updateRetrievedEncounter(encounter, retrievedEncounter);
		}

		VisitService visitService = Context.getVisitService();
		Visit retrievedVisit = visitService.getVisitByUuid(uuid);

		if (retrievedVisit != null) {
			return updateRetrievedVisit(encounter, retrievedVisit);
		}

		return createEncounter(encounter, uuid);
	}

	private Encounter updateRetrievedEncounter(Encounter encounter, org.openmrs.Encounter retrievedEncounter) {
		List<String> errors = new ArrayList<>();
		org.openmrs.Encounter omrsEncounter = FHIREncounterUtil.generateOMRSEncounter(encounter, errors);
		FHIRUtils.checkGeneratorErrorList(errors);

		retrievedEncounter = FHIREncounterUtil.updateEncounterAttributes(omrsEncounter, retrievedEncounter);
		try {
			Context.getEncounterService().saveEncounter(retrievedEncounter);
		} catch (Exception e) {
			throw new UnprocessableEntityException(
					"The request cannot be processed due to the following issues \n" + e.getMessage());
		}
		return FHIREncounterUtil.generateEncounter(retrievedEncounter);
	}

	private Encounter updateRetrievedVisit(Encounter encounter, Visit retrievedVisit) {
		List<String> errors = new ArrayList<>();
		Visit visit = FHIRVisitUtil.generateOMRSVisit(encounter, errors);
		FHIRUtils.checkGeneratorErrorList(errors);

		retrievedVisit = FHIRVisitUtil.updateVisitAttributes(visit, retrievedVisit);
		try {
			Context.getVisitService().saveVisit(retrievedVisit);
		} catch (Exception e) {
			throw new UnprocessableEntityException(
					"The request cannot be processed due to the following issues \n" + e.getMessage());
		}
		return FHIRVisitUtil.generateEncounter(retrievedVisit);
	}

	private Encounter createEncounter(Encounter encounter, String uuid) {
		uuid = extractUuid(uuid);
		if (encounter.getId() == null) { // since we need to PUT the encounter to a specific URI, we need to set the uuid
			IdType uuidType = new IdType();
			uuidType.setValue(uuid);
			encounter.setId(uuidType);
		}
		return createFHIREncounter(encounter);
	}

	private List<Encounter> addEncountersByPartOf(org.openmrs.Encounter encounter, String partOf, List<Encounter> fhirEncounterList) {
		if (encounter.getVisit() == null) {
			if (FHIRConstants.NONE.equalsIgnoreCase(partOf)) {
				fhirEncounterList.add(FHIREncounterUtil.generateEncounter(encounter));
			}
		} else {
			if (encounter.getVisit().getUuid().equals(partOf)) {
				fhirEncounterList.add(FHIREncounterUtil.generateEncounter(encounter));
			}
		}

		return fhirEncounterList;
	}
}
