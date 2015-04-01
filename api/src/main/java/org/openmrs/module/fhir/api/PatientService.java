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
package org.openmrs.module.fhir.api;

import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import org.openmrs.api.OpenmrsService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface PatientService extends OpenmrsService {

	/**
	 * Get fhir patient resource by uuid
	 *
	 * @param id uuid of the patient
	 * @return fhir patient resource and will return null if patient not found for the given id
	 */
	Patient getPatient(String id);

	/**
	 * Search patients by uuid
	 *
	 * @param id the uuid to be search
	 * @return fhir patient resource list
	 */
	List<Patient> searchPatientsById(String id);

	/**
	 * Search patient by identifier and identifier type id
	 *
	 * @param identifierValue  identifier to be search
	 * @param identifierTypeId id of the patient identifier type
	 * @return fhir patient resource list
	 */
	List<Patient> searchPatientsByIdentifier(String identifierValue, String identifierTypeId);

	/**
	 * Search patient by identifier
	 *
	 * @param identifierValue identifier to be search
	 * @return fhir patient resource list
	 */
	List<Patient> searchPatientsByIdentifier(String identifierValue);

	/**
	 * Search all patients either active or inactive
	 *
	 * @return active patients list
	 */
	List<Patient> searchPatients(boolean active);

	/**
	 * Search all patients by given name
	 *
	 * @return active patients list
	 */
	List<Patient> searchPatientsByGivenName(String givenName);

	/**
	 * Search all patients by given name
	 *
	 * @return active patients list
	 */
	List<Patient> searchPatientsByFamilyName(String familyName);

	/**
	 * Search all patients by  name
	 *
	 * @return active patients list
	 */
	List<Patient> searchPatientsByName(String name);

    /**
     * Get patient operations bundle resource
     *
     * @param patientId the patient id to be search patients
     * @return patient resource bundle for operations
     */
    public Bundle getPatientOperationsById(String patientId);
}
