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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Patient;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.fhir.api.PatientService;
import org.openmrs.module.fhir.api.db.FHIRDAO;
import org.openmrs.module.fhir.api.strategies.patient.PatientStrategyUtil;

import java.util.List;

/**
 * It is a default implementation of {@link org.openmrs.module.fhir.api.PatientService}.
 */
public class PatientServiceImpl extends BaseOpenmrsService implements PatientService {

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
	 * @see org.openmrs.module.fhir.api.PatientService#getPatient(String)
	 */
	public Patient getPatient(String uuid) {
		return PatientStrategyUtil.getPatientStrategy().getPatient(uuid);
	}

	/**
	 * @see org.openmrs.module.fhir.api.PatientService#searchPatientsById(String)
	 */
	public List<Patient> searchPatientsById(String uuid) {
		return PatientStrategyUtil.getPatientStrategy().searchPatientsById(uuid);
	}

	/**
	 * @see org.openmrs.module.fhir.api.PatientService#searchPatientsByIdentifier(String, String)
	 */
	public List<Patient> searchPatientsByIdentifier(String identifierValue, String identifierTypeName) {
		return PatientStrategyUtil.getPatientStrategy().searchPatientsByIdentifier(identifierValue, identifierTypeName);
	}

	/**
	 * @see org.openmrs.module.fhir.api.PatientService#searchPatientsByIdentifier(String)
	 */
	public List<Patient> searchPatientsByIdentifier(String identifier) {
		return PatientStrategyUtil.getPatientStrategy().searchPatientsByIdentifier(identifier);
	}

	/**
	 * @see org.openmrs.module.fhir.api.PatientService#searchPatients(boolean)
	 */
	public List<Patient> searchPatients(boolean active) {
		return PatientStrategyUtil.getPatientStrategy().searchPatients(active);
	}

	/**
	 * @see org.openmrs.module.fhir.api.PatientService#searchPatientsByGivenName(String)
	 */
	public Bundle searchPatientsByGivenName(String givenName) {
		return PatientStrategyUtil.getPatientStrategy().searchPatientsByGivenName(givenName);
	}

	/**
	 * @see org.openmrs.module.fhir.api.PatientService#searchPatientsByFamilyName(String)
	 */
	public Bundle searchPatientsByFamilyName(String familyName) {
		return PatientStrategyUtil.getPatientStrategy().searchPatientsByFamilyName(familyName);
	}

	/**
	 * @see org.openmrs.module.fhir.api.PatientService#searchPatientsByName(String) (String)
	 */
	public Bundle searchPatientsByName(String name) {
		return PatientStrategyUtil.getPatientStrategy().searchPatientsByName(name);
	}

	/**
	 * @see org.openmrs.module.fhir.api.PatientService#getPatientOperationsById(String)
	 */
	public Bundle getPatientOperationsById(String patientId) {
		return PatientStrategyUtil.getPatientStrategy().getPatientOperationsById(patientId);
	}

	/**
	 * @see org.openmrs.module.fhir.api.PatientService#deletePatient(String)
	 */
	@Override
	public void deletePatient(String id) {
		PatientStrategyUtil.getPatientStrategy().deletePatient(id);
	}

	/**
	 * @see org.openmrs.module.fhir.api.PatientService#createFHIRPatient(Patient)
	 */
	@Override
	public Patient createFHIRPatient(Patient patient) {
		return PatientStrategyUtil.getPatientStrategy().createFHIRPatient(patient);
	}

	/**
	 * @see org.openmrs.module.fhir.api.PatientService#updatePatient(Patient, String)
	 */
	@Override
	public Patient updatePatient(Patient patient, String uuid) {
		return PatientStrategyUtil.getPatientStrategy().updatePatient(patient, uuid);
	}
}
