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
package org.openmrs.module.fhir.providers;

import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu.resource.Patient;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.StringDt;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.NotImplementedOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.fhir.exception.FHIRModuleOmodException;
import org.openmrs.module.fhir.exception.FHIRValidationException;
import org.openmrs.module.fhir.resources.FHIRPatientResource;

import java.util.List;

public class RestfulPatientResourceProvider implements IResourceProvider {

	private static final Log log = LogFactory.getLog(RestfulPatientResourceProvider.class);

	@Override
	public Class<? extends IResource> getResourceType() {
		return Patient.class;
	}

	/**
	 * Get patient by patient uuid
	 *
	 * @param id id object contaning the requested id
	 * @return Returns a resource matching this identifier, or null if none exists.
	 */
	@Read()
	public Patient getResourceById(@IdParam IdDt id) {
		FHIRPatientResource patientResource = new FHIRPatientResource();
		Patient result = null;
		result = patientResource.getByUniqueId(id);
		return result;
	}

	/**
	 * Get patients by family name
	 *
	 * @param theFamilyName object contaning the requested family name
	 */
	@Search()
	public List<Patient> findPatientsByFamilyName(@RequiredParam(name = Patient.SP_FAMILY) StringDt theFamilyName) {
		throw new NotImplementedOperationException("Find patients by family name is not implemented yet");
	}

	/**
	 * Get patients by name
	 *
	 * @param name name of the patient
	 * @return This method returns a list of Patients. This list may contain multiple matching resources, or it may also be
	 * empty.
	 */
	@Search()
	public List<Patient> findPatientsByName(@RequiredParam(name = Patient.SP_NAME) StringDt name) {
		throw new NotImplementedOperationException("Find patients by patient name is not implemented yet");
	}

	/**
	 * Get patients by identifier
	 *
	 * @param identifier
	 * @return This method returns a list of Patients. This list may contain multiple matching resources, or it may also be
	 * empty.
	 */
	@Search()
	public List<Patient> searchPatientsByIdentifier(@RequiredParam(name = Patient.SP_IDENTIFIER) TokenParam identifier) {
		throw new NotImplementedOperationException("Find patients by identifier is not implemented yet");
	}

	/**
	 * Get active patients
	 *
	 * @param active search term
	 * @return This method returns a list of Patients. This list may contain multiple matching resources, or it may also be
	 * empty.
	 */
	@Search()
	public List<Patient> findActivePatients(@RequiredParam(name = Patient.SP_ACTIVE) StringDt active) {
		throw new NotImplementedOperationException("Find active patients is not implemented yet");
	}

	/**
	 * Get patients by telephone number
	 *
	 * @param telecom telephone number to be search
	 * @return This method returns a list of Patients. This list may contain multiple matching resources, or it may also be
	 * empty.
	 */
	@Search()
	public List<Patient> findPatientByTelecom(@RequiredParam(name = Patient.SP_TELECOM) StringDt telecom) {
		throw new NotImplementedOperationException("Find patients by telephone number is not implemented yet");
	}

	/**
	 * Find patients by given name
	 *
	 * @param givenName given name of the patient
	 * @return This method returns a list of Patients. This list may contain multiple matching resources, or it may also be
	 * empty.
	 */
	@Search()
	public List<Patient> findPatientsByGivenName(@RequiredParam(name = Patient.SP_GIVEN) StringDt givenName) {
		throw new NotImplementedOperationException("Find patients by given name is not implemented yet");
	}

	/**
	 * Find patients by birth date
	 *
	 * @param theBirthDate birth date of the patient
	 * @return This method returns a list of Patients. This list may contain multiple matching resources, or it may also be
	 * empty.
	 */
	@Search()
	public List<Patient> searchPatientsByBirthDate(@RequiredParam(name = Patient.SP_BIRTHDATE) DateParam theBirthDate) {
		throw new NotImplementedOperationException("Find patients by patients by birth date is not implemented yet");
	}

	/**
	 * Find patients by patients' language
	 *
	 * @param language language to be search
	 * @return This method returns a list of Patients. This list may contain multiple matching resources, or it may also be
	 * empty.
	 */
	@Search()
	public List<Patient> searchPatientsByLanguage(@RequiredParam(name = Patient.SP_RES_LANGUAGE) TokenParam language) {
		throw new NotImplementedOperationException("Find patients by language is not implemented yet");
	}

	/**
	 * Find patients by address
	 *
	 * @param address address to be search
	 * @return This method returns a list of Patients. This list may contain multiple matching resources, or it may also be
	 * empty.
	 */
	@Search()
	public List<Patient> searchPatientsByAddress(@RequiredParam(name = Patient.SP_ADDRESS) StringDt address) {
		throw new NotImplementedOperationException("Find patients by address is not implemented yet");
	}

	/**
	 * Find patients by gender
	 *
	 * @param gender the gender of the patients to be search
	 * @return This method returns a list of Patients. This list may contain multiple matching resources, or it may also be
	 * empty.
	 */
	@Search()
	public List<Patient> searchPatientsByGender(@RequiredParam(name = Patient.SP_GENDER) TokenParam gender) {
		throw new NotImplementedOperationException("Find patients by gender is not implemented yet");
	}

	/**
	 * Find patients by link
	 *
	 * @param link the link of the patient
	 * @return This method returns a list of Patients. This list may contain multiple matching resources, or it may also be
	 * empty.
	 */
	@Search()
	public List<Patient> searchPatientsByLink(@RequiredParam(name = Patient.SP_LINK) ReferenceParam link) {
		throw new NotImplementedOperationException("Find patients by link is not implemented yet");
	}

	/**
	 * Find patients by provider
	 *
	 * @param provider the provider of the patient
	 * @return This method returns a list of Patients. This list may contain multiple matching resources, or it may also be
	 * empty.
	 */
	@Search()
	public List<Patient> searchPatientsByProvider(@RequiredParam(name = Patient.SP_PROVIDER) ReferenceParam provider) {
		throw new NotImplementedOperationException("Find patients by provider is not implemented yet");
	}

	/**
	 * Find patients by phonetic
	 *
	 * @param phonetic the phonetic of the patient
	 * @return This method returns a list of Patients. This list may contain multiple matching resources, or it may also be
	 * empty.
	 */
	@Search()
	public List<Patient> searchPatientsByPhonetic(@RequiredParam(name = Patient.SP_PHONETIC) StringDt phonetic) {
		throw new NotImplementedOperationException("Find patients by phonetic is not implemented yet");
	}

	/**
	 * Find patients by language
	 *
	 * @param language birth date of the patient
	 * @return This method returns a list of Patients. This list may contain multiple matching resources, or it may also be
	 * empty.
	 */
	@Search()
	public List<Patient> searchPatientsByLanguageCode(@RequiredParam(name = Patient.SP_LANGUAGE) TokenParam language) {
		throw new NotImplementedOperationException("Find patients by language is not implemented yet");
	}
}
