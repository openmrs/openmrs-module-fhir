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

import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.Composition;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.server.exceptions.MethodNotAllowedException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.EncounterProvider;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Visit;
import org.openmrs.api.APIException;
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
import java.util.List;

/**
 * It is a default implementation of {@link org.openmrs.module.fhir.api.PatientService}.
 */
public class EncounterServiceImpl extends BaseOpenmrsService implements EncounterService {

	protected final Log log = LogFactory.getLog(this.getClass());

	private FHIRDAO dao;

	/**
	 * @param dao the dao to set
	 */
	public void setDao(FHIRDAO dao) {
		this.dao = dao;
	}

	/**
	 * @return the dao
	 */
	public FHIRDAO getDao() {
		return dao;
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
		List<org.openmrs.Encounter> encounters = Context.getEncounterService().getEncountersByPatientIdentifier(identifier);
		List<Encounter> fhirEncountersList = new ArrayList<Encounter>();
		for (org.openmrs.Encounter encounter: encounters) {
			fhirEncountersList.add(FHIREncounterUtil.generateEncounter(encounter));
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
			encounterList.add(FHIREncounterUtil.generateComposition(omrsEncounter));
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
			fhirEncounters.add(FHIREncounterUtil.generateComposition(enc));
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
			encounterList.add(FHIREncounterUtil.generateComposition(omrsEncounter));
		}
		return encounterList;
	}

	/**
	 * @see org.openmrs.module.fhir.api.EncounterService#getEncounterOperationsById(String)
	 */
	public Bundle getEncounterOperationsById(String encounterId) {
		return  getEncounterOperationsById(encounterId, new Bundle(), true);
	}

	/**
	 * @see org.openmrs.module.fhir.api.EncounterService#getEncounterOperationsById(String, ca.uhn.fhir.model.dstu2.resource.Bundle, boolean)
	 */
	public Bundle getEncounterOperationsById(String encounterId, Bundle bundle, boolean includePatient) {
		org.openmrs.Encounter omsrEncounter = null;
		omsrEncounter = Context.getEncounterService().getEncounterByUuid(encounterId);
		if (omsrEncounter != null) {
			Bundle.Entry encounter = bundle.addEntry();
			encounter.setResource(FHIREncounterUtil.generateEncounter(omsrEncounter));

			//Set filtered obs if obs allergy strategy used
			FHIREncounterUtil.addFilteredObs(omsrEncounter, bundle);

			//Set location
			Bundle.Entry location;
			if (omsrEncounter.getLocation() != null) {
				location = bundle.addEntry();
				location.setResource(FHIRLocationUtil.generateLocation(omsrEncounter.getLocation()));
			}

			//Set patient
			if(includePatient) {
				Bundle.Entry patient = bundle.addEntry();
				patient.setResource(FHIRPatientUtil.generatePatient(omsrEncounter.getPatient()));
			}

			//Set providers
			Bundle.Entry provider;
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
        if(encounter==null)
            throw new ResourceNotFoundException(Encounter.class,new IdDt("Encounter",id));
        try{
        Context.getEncounterService().voidEncounter(encounter,"DELETED by FHIR request");
        } catch(APIException ex){
            throw new MethodNotAllowedException("The OpenMRS API refused to retire the Encounter with id : " + id + " via the FHIR request");
        }
    }

	/**
	 * @see org.openmrs.module.fhir.api.EncounterService#searchEncountersByPatientIdentifierAndPartOf(String, String)
	 */
	@Override
	public List<Encounter> searchEncountersByPatientIdentifierAndPartOf(String patientIdentifier, String partOf) {
		org.openmrs.api.PatientService patientService = Context.getPatientService();
		List<PatientIdentifierType> allPatientIdentifierTypes = patientService.getAllPatientIdentifierTypes();
		List<org.openmrs.Patient> patientList = patientService.getPatients(null, patientIdentifier, allPatientIdentifierTypes,
				true);
		List<Encounter> fhirEncountersList = new ArrayList<Encounter>();

		for(Patient patient : patientList) {
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

		for(Patient patient : patientList) {
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
	 * @see org.openmrs.module.fhir.api.EncounterService#searchEncountersByEncounterIdAndPartOf(String, String)
	 */
	@Override
	public List<Encounter> searchEncountersByEncounterIdAndPartOf(String encounterId, String partOf) {
		org.openmrs.Encounter encounter = Context.getEncounterService().getEncounterByUuid(encounterId);
		List<Encounter> fhirEncountersList = new ArrayList<Encounter>();
		if(encounter != null) {
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

		if(fhirEncountersList.size() == 0) {
			Visit visit = Context.getVisitService().getVisitByUuid(encounterId);
			if(visit != null) {
				if (FHIRConstants.NONE.equalsIgnoreCase(partOf)) {
					fhirEncountersList.add(OMRSFHIRVisitUtil.generateEncounter(visit));
				}
			}
		}
		return fhirEncountersList;
	}
}
