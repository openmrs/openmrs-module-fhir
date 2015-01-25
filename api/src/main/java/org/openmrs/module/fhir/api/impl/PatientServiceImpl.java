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

import ca.uhn.fhir.model.dstu.resource.Patient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonName;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.fhir.api.PatientService;
import org.openmrs.module.fhir.api.db.FHIRDAO;
import org.openmrs.module.fhir.api.util.FHIRPatientUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * It is a default implementation of {@link org.openmrs.module.fhir.api.PatientService}.
 */
public class PatientServiceImpl extends BaseOpenmrsService implements PatientService {

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
	 * @see org.openmrs.module.fhir.api.PatientService#getPatient(String)
	 */
	public Patient getPatient(String id) {
		org.openmrs.Patient omrsPatient = Context.getPatientService().getPatientByUuid(id);
		if (omrsPatient == null) {
			return null;
		}
		return FHIRPatientUtil.generatePatient(omrsPatient);

	}

	/**
	 * @see org.openmrs.module.fhir.api.PatientService#searchPatientsById(String)
	 */
	public List<Patient> searchPatientsById(String id) {
		org.openmrs.Patient omrsPatient = Context.getPatientService().getPatientByUuid(id);
		List<Patient> patientList = new ArrayList<Patient>();
		if (omrsPatient != null) {
			patientList.add(FHIRPatientUtil.generatePatient(omrsPatient));
		}
		return patientList;
	}

	/**
	 * @see org.openmrs.module.fhir.api.PatientService#searchPatientsByIdentifier(String, String)
	 */
	public List<Patient> searchPatientsByIdentifier(String identifierValue, String identifierTypeId) {
		org.openmrs.api.PatientService patientService = Context.getPatientService();
		List<PatientIdentifierType> patientIdentifierTypes = new ArrayList<PatientIdentifierType>();
		patientIdentifierTypes.add(patientService.getPatientIdentifierTypeByUuid(identifierTypeId));
		List<org.openmrs.Patient> patientList = patientService.getPatients(null, identifierValue, patientIdentifierTypes,
				true);
		List<Patient> fhirPatientList = new ArrayList<Patient>();
		for(org.openmrs.Patient patient : patientList) {
			fhirPatientList.add(FHIRPatientUtil.generatePatient(patient));
		}
		return fhirPatientList;
	}

	/**
	 * @see org.openmrs.module.fhir.api.PatientService#searchPatientsByIdentifier(String)
	 */
	public List<Patient> searchPatientsByIdentifier(String identifier) {
		org.openmrs.api.PatientService patientService = Context.getPatientService();
		List<PatientIdentifierType> allPatientIdentifierTypes = patientService.getAllPatientIdentifierTypes();
		List<org.openmrs.Patient> patientList = patientService.getPatients(null, identifier, allPatientIdentifierTypes,
				true);
		List<Patient> fhirPatientList = new ArrayList<Patient>();
		for(org.openmrs.Patient patient : patientList) {
			fhirPatientList.add(FHIRPatientUtil.generatePatient(patient));
		}
		return fhirPatientList;
	}

	/**
	 * @see org.openmrs.module.fhir.api.PatientService#searchPatients(boolean)
	 */
	public List<Patient> searchPatients(boolean active) {
		//TODO this method looks for all the patients which is inefficient. Reimplement after API revamp
		List<org.openmrs.Patient> patients = Context.getPatientService().getAllPatients(true);
		List<Patient> fhirPatientList = new ArrayList<Patient>();
		for(org.openmrs.Patient patient : patients) {
			if(active) {
				if(!patient.isVoided()) {
					fhirPatientList.add(FHIRPatientUtil.generatePatient(patient));
				}
			} else {
				if(patient.isVoided()) {
					fhirPatientList.add(FHIRPatientUtil.generatePatient(patient));
				}
			}
		}
		return fhirPatientList;
	}

	/**
	 * @see org.openmrs.module.fhir.api.PatientService#searchPatientsByGivenName(String)
	 */
	public List<Patient> searchPatientsByGivenName(String givenName) {
		List<org.openmrs.Patient> patients = searchPatientByQuery(givenName);
		List<Patient> fhirPatientList = new ArrayList<Patient>();
		//Go through the patients given by the openmrs core api and find them patient who has the givenName matching
		for(org.openmrs.Patient patient : patients) {
			if(givenName.equalsIgnoreCase(patient.getGivenName())) {
				fhirPatientList.add(FHIRPatientUtil.generatePatient(patient));
			} else {
				for(PersonName personName : patient.getNames()) {
					if(givenName.equalsIgnoreCase(personName.getGivenName())) {
						fhirPatientList.add(FHIRPatientUtil.generatePatient(patient));
					}
				}
			}
		}
		return fhirPatientList;
	}

	/**
	 * @see org.openmrs.module.fhir.api.PatientService#searchPatientsByFamilyName(String)
	 */
	public List<Patient> searchPatientsByFamilyName(String familyName) {
		List<org.openmrs.Patient> patients = searchPatientByQuery(familyName);
		List<Patient> fhirPatientList = new ArrayList<Patient>();
		//Go through the patients given by the openmrs core api and find them patient who has the familyName matching
		for(org.openmrs.Patient patient : patients) {
			if(familyName.equalsIgnoreCase(patient.getFamilyName())) {
				fhirPatientList.add(FHIRPatientUtil.generatePatient(patient));
			} else {
				for(PersonName personName : patient.getNames()) {
					if(familyName.equalsIgnoreCase(personName.getFamilyName())) {
						fhirPatientList.add(FHIRPatientUtil.generatePatient(patient));
					}
				}
			}
		}
		return fhirPatientList;
	}

	/**
	 * @see org.openmrs.module.fhir.api.PatientService#searchPatientsByName(String) (String)
	 */
	public List<Patient> searchPatientsByName(String name) {
		List<org.openmrs.Patient> patients = searchPatientByQuery(name);
		List<Patient> fhirPatientList = new ArrayList<Patient>();
		for(org.openmrs.Patient patient : patients) {
			fhirPatientList.add(FHIRPatientUtil.generatePatient(patient));
		}
		return fhirPatientList;
	}

	private List<org.openmrs.Patient> searchPatientByQuery(String query) {
		return Context.getPatientService().getPatients(query);
	}
}
