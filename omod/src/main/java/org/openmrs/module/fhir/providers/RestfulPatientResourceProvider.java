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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.openmrs.module.fhir.resources.FHIRPatientResource;

import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.OperationOutcome;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.annotation.ConditionalUrlParam;
import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.Delete;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.annotation.Update;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.NotImplementedOperationException;
import ca.uhn.fhir.rest.server.exceptions.PreconditionFailedException;

public class RestfulPatientResourceProvider implements IResourceProvider {

	private static final Log log = LogFactory.getLog(RestfulPatientResourceProvider.class);

	private FHIRPatientResource patientResource;

	public RestfulPatientResourceProvider() {
		patientResource = new FHIRPatientResource();
	}

	@Override
	public Class<? extends IResource> getResourceType() {
		return Patient.class;
	}

	/**
	 * Get patient by patient uuid
	 *
	 * @param id id object containing the requested id
	 * @return Returns a resource matching this identifier, or null if none exists.
	 */
	@Read()
	public Patient getResourceById(@IdParam IdDt id) {
		Patient patient = null;
		patient = patientResource.getByUniqueId(id);
		return patient;
	}

	/**
	 * Search patient by unique id
	 *
	 * @param id object containing the requested id
	 */
	@Search()
	public List<Patient> searchPatientByUniqueId(@RequiredParam(name = Patient.SP_RES_ID) TokenParam id) {
		return patientResource.searchByUniqueId(id);
	}

	/**
	 * Get patients by family name
	 *
	 * @param theFamilyName object contaning the requested family name
	 */
	@Search()
	public Bundle findPatientsByFamilyName(@RequiredParam(name = Patient.SP_FAMILY) StringParam theFamilyName) {
		return patientResource.searchByFamilyName(theFamilyName);
	}

	/**
	 * Get patients by name
	 *
	 * @param name name of the patient
	 * @return This method returns a Bundle of Patients. This list may contain multiple matching
	 *         resources, or it may also be empty.
	 */
	@Search()
	public Bundle findPatientsByName(@RequiredParam(name = Patient.SP_NAME) StringParam name) {
		return patientResource.searchByName(name);
	}

	/**
	 * Get patients by identifier
	 *
	 * @param identifier
	 * @return This method returns a list of Patients. This list may contain multiple matching
	 *         resources, or it may also be empty.
	 */
	@Search()
	public List<Patient> searchPatientsByIdentifier(@RequiredParam(name = Patient.SP_IDENTIFIER) TokenParam identifier) {
		return patientResource.searchByIdentifier(identifier);
	}

	/**
	 * Get active patients
	 *
	 * @param active search term
	 * @return This method returns a list of Patients. This list may contain multiple matching
	 *         resources, or it may also be empty.
	 */
	@Search()
	public List<Patient> findActivePatients(@RequiredParam(name = Patient.SP_ACTIVE) TokenParam active) {
		return patientResource.searchPatients(active);
	}

	/**
	 * Find patients by given name
	 *
	 * @param givenName given name of the patient
	 * @return This method returns a list of Patients. This list may contain multiple matching
	 *         resources, or it may also be empty.
	 */
	@Search()
	public Bundle findPatientsByGivenName(@RequiredParam(name = Patient.SP_GIVEN) StringParam givenName) {
		return patientResource.searchByGivenName(givenName);
	}

	/**
	 * Find patients by provider
	 *
	 * @param provider the provider of the patient
	 * @return This method returns a list of Patients. This list may contain multiple matching
	 *         resources, or it may also be empty.
	 */
	@Search()
	public List<Patient> searchPatientsByProvider(@RequiredParam(name = Patient.SP_CAREPROVIDER) ReferenceParam provider) {
		throw new NotImplementedOperationException("Find patients by provider is not implemented yet");
	}

	/**
	 * Implementation of $everything operation which returns content of a patient
	 *
	 * @param patientId if of the patient
	 * @return bundle
	 */
	@Operation(name = "$everything")
	public Bundle patientInstanceOperation(@IdParam IdDt patientId) {
		return patientResource.getPatientOperationsById(patientId);
	}

	/**
	 * Delete patient by unique id
	 *
	 * @param theId
	 */
	@Delete
	public void deletePatient(@IdParam IdDt theId) {
		patientResource.deletePatient(theId);
	}
	
	/**
	 * Create Patient
	 *
	 * @param patient fhir patient oobject
	 * @return This method returns Meth codOutcome object, which contains information about the
	 *         create operation
	 */
	@Create
	public MethodOutcome createFHIRPatient(@ResourceParam Patient patient) {
		patient = patientResource.createFHIRPatient(patient);
		MethodOutcome retVal = new MethodOutcome();
		retVal.setId(new IdDt(FHIRConstants.PATIENT, patient.getId().getIdPart()));
		OperationOutcome outcome = new OperationOutcome();
		outcome.addIssue().setDetails("Patient is successfully created with");
		retVal.setOperationOutcome(outcome);
		return retVal;
	}
	
	@Update
	public MethodOutcome updatePatient(@ResourceParam Patient patient, @IdParam IdDt theId) {
		MethodOutcome retVal = new MethodOutcome();
		OperationOutcome outcome = new OperationOutcome();
		patient = patientResource.updatePatient(patient, theId.getIdPart());
		outcome.addIssue().setDetails("Patient is successfully updated");
		retVal.setOperationOutcome(outcome);
		return retVal;
	}
	
	/**
	 * Update Patient by identifier.
	 *
	 * @param patient {@link ca.uhn.fhir.model.dstu2.resource.Patient} object provided by the
	 *            {@link ca.uhn.fhir .rest.server.RestfulServer}
	 * @param theId Only one of theId or theConditional will have a value and the other will be
	 *            null, depending on the URL passed into the server
	 * @param theConditional This will have a value like "Patient?identifier=7C00001
	 * @return MethodOutcome which contains the status of the update operation
	 */
	@Update()
	public MethodOutcome updatePatientByIdentifier(@ResourceParam Patient patient, @IdParam IdDt theId,
	                                               @ConditionalUrlParam String theConditional) {
		MethodOutcome outcome = new MethodOutcome();
		OperationOutcome operationoutcome = null;
		if (theConditional != null) {
			String paramValue = null;
			List<Patient> patientList = null;
			String parameterName = null;
			try {
				String args[] = theConditional.split("?");
				String parameterPart = args[1];
				String paraArgs[] = parameterPart.split("=");
				parameterName = paraArgs[0];
				paramValue = paraArgs[1];
			} catch (Exception e) { // will catch nullpointerexceptions and indexoutofboundexceptions
				operationoutcome = new OperationOutcome();
				operationoutcome.addIssue().setDetails("Please check Condition URL format");
				outcome.setOperationOutcome(operationoutcome);
				return outcome;
			}
			if (FHIRConstants.PARAMETER_NAME.equals(parameterName)) {
				StringParam param = new StringParam();
				param.setValue(paramValue);
				Bundle patientBundle = patientResource.searchByName(param);
				if (patientBundle != null) {
					if (patientBundle.getEntry().size() > 0) {
						patientList = new ArrayList<Patient>();
					}
					for (Bundle.Entry entry : patientBundle.getEntry()) {
						Patient fhirPatient = (Patient) entry.getResource();
						patientList.add(fhirPatient);
					}
				}
			} else if (FHIRConstants.PARAMETER_IDENTIFIER.equals(parameterName)) {
				TokenParam params = new TokenParam();
				params.setValue(paramValue);
				patientList = patientResource.searchByIdentifier(params);
			} else if (FHIRConstants.PARAMETER_GIVENNAME.equals(parameterName)) {
				StringParam param = new StringParam();
				param.setValue(paramValue);
				Bundle patientBundle = patientResource.searchByGivenName(param);
				if (patientBundle != null) {
					if (patientBundle.getEntry().size() > 0) {
						patientList = new ArrayList<Patient>();
					}
					for (Bundle.Entry entry : patientBundle.getEntry()) {
						Patient fhirPatient = (Patient) entry.getResource();
						patientList.add(fhirPatient);
					}
				}
			}
			if (patientList != null) {
				if (patientList.size() == 0) {
					outcome = updatePatient(patient, null);
				} else if (patientList.size() == 1) {
					outcome = updatePatient(patient, patientList.get(0).getId());
				} else {
					throw new PreconditionFailedException("There are more than one patient for the given condition");
				}
			}
		} else {
			outcome = updatePatient(patient, theId);
		}
		return outcome;
	}

}
