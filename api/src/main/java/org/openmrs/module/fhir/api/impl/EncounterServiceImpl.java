/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.fhir.api.impl;

import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Composition;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.instance.model.api.IIdType;
import org.openmrs.EncounterProvider;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.fhir.api.EncounterService;
import org.openmrs.module.fhir.api.db.FHIRDAO;
import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.openmrs.module.fhir.api.util.FHIREncounterUtil;
import org.openmrs.module.fhir.api.util.FHIRLocationUtil;
import org.openmrs.module.fhir.api.util.FHIRPatientUtil;
import org.openmrs.module.fhir.api.util.FHIRPractitionerUtil;
import org.openmrs.module.fhir.api.util.OMRSFHIRVisitUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * It is a default implementation of {@link org.openmrs.module.fhir.api.PatientService}.
 */
public class EncounterServiceImpl extends BaseOpenmrsService implements EncounterService {

	protected final Log log = LogFactory.getLog(this.getClass());

	private FHIRDAO dao;

	/**
	 * @return the dao
	 */
	public FHIRDAO getDao() {
		return dao;
	}

	/**
	 * @param dao the dao to set
	 */
	public void setDao(FHIRDAO dao) {
		this.dao = dao;
	}

	/**
	 * @see org.openmrs.module.fhir.api.EncounterService#getEncounter(String)
	 */
	@Override
	public Encounter getEncounter(String id) {

		org.openmrs.Encounter omrsEncounter = Context.getEncounterService().getEncounterByUuid(id);

		if (omrsEncounter == null || omrsEncounter.isVoided()) {
			Visit visit = Context.getVisitService().getVisitByUuid(id);
			if (visit != null && !visit.isVoided()) {
				return OMRSFHIRVisitUtil.generateEncounter(visit);
			} else {
				return null;
			}
		}
		return FHIREncounterUtil.generateEncounter(omrsEncounter);
	}

	/**
	 * @see org.openmrs.module.fhir.api.EncounterService#searchEncounterById(String)
	 */
	public List<Encounter> searchEncounterById(String id) {
		org.openmrs.Encounter omrsEncounter = Context.getEncounterService().getEncounterByUuid(id);
		List<Encounter> encounterList = new ArrayList<Encounter>();
		if (omrsEncounter != null) {
			encounterList.add(FHIREncounterUtil.generateEncounter(omrsEncounter));
		} else {
			Visit visit = Context.getVisitService().getVisitByUuid(id);
			if (visit != null) {
				encounterList.add(OMRSFHIRVisitUtil.generateEncounter(visit));
			}
		}
		return encounterList;
	}

	/**
	 * @see org.openmrs.module.fhir.api.EncounterService#searchEncountersByPatientIdentifier(String)
	 */
	public List<Encounter> searchEncountersByPatientIdentifier(String identifier) {
		org.openmrs.api.PatientService patientService = Context.getPatientService();
		List<PatientIdentifierType> allPatientIdentifierTypes = patientService.getAllPatientIdentifierTypes();
		List<org.openmrs.Patient> patientList = patientService
		        .getPatients(identifier, null, allPatientIdentifierTypes, true);
		List<Encounter> fhirEncountersList = new ArrayList<Encounter>();

		for (Patient patient : patientList) {
			List<org.openmrs.Encounter> encounters = Context.getEncounterService().getEncountersByPatient(patient);
			for (org.openmrs.Encounter encounter : encounters) {
				fhirEncountersList.add(FHIREncounterUtil.generateEncounter(encounter));
			}
		}

		for (Patient patient : patientList) {
			List<Visit> visits = Context.getVisitService().getVisitsByPatient(patient);
			for (Visit visit : visits) {
				fhirEncountersList.add(OMRSFHIRVisitUtil.generateEncounter(visit));
			}
		}
		return fhirEncountersList;
	}

	/**
	 * @see org.openmrs.module.fhir.api.EncounterService#searchEncounterComposition(String)
	 */
	public List<Composition> searchEncounterComposition(String id) {
		org.openmrs.Encounter omrsEncounter = Context.getEncounterService().getEncounterByUuid(id);
		List<Composition> encounterList = new ArrayList<Composition>();
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
	public List<Composition> searchEncounterCompositionByPatientId(String patientId) {
		Patient patient = Context.getPatientService().getPatientByUuid(patientId);
		List<org.openmrs.Encounter> omrsEncounters = Context.getEncounterService().getEncountersByPatient(patient);
		List<Composition> fhirEncounters = new ArrayList<Composition>();
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
	public List<Composition> searchEncounterCompositionByEncounterId(String encounterId) {
		org.openmrs.Encounter omrsEncounter = Context.getEncounterService().getEncounterByUuid(encounterId);
		List<Composition> encounterList = new ArrayList<Composition>();
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
	public Bundle getEncounterOperationsById(String encounterId) {
		return getEncounterOperationsById(encounterId, new Bundle(), true);
	}

	/**
	 * @see org.openmrs.module.fhir.api.EncounterService#getEncounterOperationsById(String,
	 *      org.hl7.fhir.dstu3.model.Bundle, boolean)
	 */
	public Bundle getEncounterOperationsById(String encounterId, Bundle bundle, boolean includePatient) {
		org.openmrs.Encounter omsrEncounter = null;
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
				Context.getVisitService().voidVisit(visit, FHIRConstants.ENCOUNTER_DELETE_MESSAGE);
			}
		} else {
			Context.getEncounterService().voidEncounter(encounter, FHIRConstants.ENCOUNTER_DELETE_MESSAGE);
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
		List<Encounter> fhirEncountersList = new ArrayList<Encounter>();

		for (Patient patient : patientList) {
			List<org.openmrs.Encounter> encounters = Context.getEncounterService().getEncountersByPatient(patient);
			for (org.openmrs.Encounter encounter : encounters) {
				if (encounter.getVisit() == null) {
					if (FHIRConstants.NONE.equalsIgnoreCase(partOf)) {
						fhirEncountersList.add(FHIREncounterUtil.generateEncounter(encounter));
					}
				} else {
					if (encounter.getVisit().getUuid().equals(partOf)) {
						fhirEncountersList.add(FHIREncounterUtil.generateEncounter(encounter));
					}
				}
			}
		}

		for (Patient patient : patientList) {
			List<Visit> visits = Context.getVisitService().getVisitsByPatient(patient);
			if (FHIRConstants.NONE.equalsIgnoreCase(partOf)) {
				for (Visit visit : visits) {
					fhirEncountersList.add(OMRSFHIRVisitUtil.generateEncounter(visit));
				}
			}
		}
		return fhirEncountersList;
	}

	/**
	 * @see org.openmrs.module.fhir.api.EncounterService#searchEncountersByEncounterIdAndPartOf(String,
	 *      String)
	 */
	@Override
	public List<Encounter> searchEncountersByEncounterIdAndPartOf(String encounterId, String partOf) {
		org.openmrs.Encounter encounter = Context.getEncounterService().getEncounterByUuid(encounterId);
		List<Encounter> fhirEncountersList = new ArrayList<Encounter>();
		if (encounter != null) {
			if (encounter.getVisit() == null) {
				if (FHIRConstants.NONE.equalsIgnoreCase(partOf)) {
					fhirEncountersList.add(FHIREncounterUtil.generateEncounter(encounter));
				}
			} else {
				if (encounter.getVisit().getUuid().equals(partOf)) {
					fhirEncountersList.add(FHIREncounterUtil.generateEncounter(encounter));
				}
			}
		}

		if (fhirEncountersList.size() == 0) {
			Visit visit = Context.getVisitService().getVisitByUuid(encounterId);
			if (visit != null) {
				if (FHIRConstants.NONE.equalsIgnoreCase(partOf)) {
					fhirEncountersList.add(OMRSFHIRVisitUtil.generateEncounter(visit));
				}
			}
		}
		return fhirEncountersList;
	}
	
	/**
	 * @see org.openmrs.module.fhir.api.EncounterService#createFHIREncounter(Encounter)
	 */
	@Override
	public Encounter createFHIREncounter(Encounter encounter) {
		List<String> errors = new ArrayList<String>();
		org.openmrs.Encounter encounterToCreate = null;
		Reference encounterRef = encounter.getPartOf();
		Visit visit = null;
		if (encounterRef != null && !encounterRef.isEmpty()) { // if partOf is not empty, This Encounter should be created under an Visit
			encounterToCreate = FHIREncounterUtil.generateOMRSEncounter(encounter, errors);
			Identifier ref = encounterRef.getIdentifier();
			String encounterRefUuid = ref.getId();

			if(StringUtils.isEmpty(encounterRefUuid) && ref != null) {
				encounterRefUuid = ref.getId();
			}

			if(StringUtils.isEmpty(encounterRefUuid)) {
				String encounterRefStr = encounterRef.getReference();
				String[] encounterRefStrSplit = encounterRefStr.split("/");
				if(encounterRefStrSplit.length > 1) {
					encounterRefUuid = encounterRefStrSplit[1];
				}
			}

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
				Identifier id = patientRef.getIdentifier();
				String patientUuid = patientRef.getId();
				if(StringUtils.isEmpty(patientUuid) && id != null) {
					patientUuid = id.getId();
				}

				if(StringUtils.isEmpty(patientUuid)) {
					String patientRefStr = patientRef.getReference();
					String[] patientRefStrSplit = patientRefStr.split("/");
					if(patientRefStrSplit.length > 1) {
						patientUuid = patientRefStrSplit[1];
					}
				}

				patient = Context.getPatientService().getPatientByUuid(patientUuid);
				if (patient == null) {
					errors.add("There is no patient for the given uuid " + patientUuid); // remove to constants
				}
			}
			List<Visit> activeVisits = Context.getVisitService().getActiveVisitsByPatient(patient);
			for (Visit activeVisit : activeVisits) {
				activeVisit.setStopDatetime(new Date());
			}
			visit = FHIREncounterUtil.generateOMRSVisit(encounter, errors);
			
		}
		if (!errors.isEmpty()) {
			StringBuilder errorMessage = new StringBuilder("The request cannot be processed due to the following issues \n");
			for (int i = 0; i < errors.size(); i++) {
				errorMessage.append((i + 1) + " : " + errors.get(i) + "\n");
			}
			throw new UnprocessableEntityException(errorMessage.toString());
		}

		if (encounterRef != null && !encounterRef.isEmpty()) {
			encounterToCreate = Context.getEncounterService().saveEncounter(encounterToCreate);
			return FHIREncounterUtil.generateEncounter(encounterToCreate);
		} else {
			visit = Context.getVisitService().saveVisit(visit);
			return OMRSFHIRVisitUtil.generateEncounter(visit);
		}
	}
}
