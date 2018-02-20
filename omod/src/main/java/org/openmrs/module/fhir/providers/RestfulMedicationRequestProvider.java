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

import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.annotation.Update;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.MedicationRequest;
import org.hl7.fhir.dstu3.model.OperationOutcome;
import org.hl7.fhir.dstu3.model.Resource;
import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.openmrs.module.fhir.resources.FHIRMedicationRequestResource;

import java.util.List;

public class RestfulMedicationRequestProvider implements IResourceProvider {

	;

	private FHIRMedicationRequestResource fhirMedicationRequestResource;

	public RestfulMedicationRequestProvider() {
		this.fhirMedicationRequestResource = new FHIRMedicationRequestResource();
	}

	@Override
	public Class<? extends Resource> getResourceType() {
		return MedicationRequest.class;
	}

	/**
	 * The "@Read" annotation indicates that this method supports the read operation. Read
	 * operations should return a single resource instance.
	 *
	 * @param theId The read operation takes one parameter, which must be of type IdType and must be
	 *            annotated with the "@Read.IdParam" annotation.
	 * @return Returns a resource matching this identifier, or null if none exists.
	 */
	@Read()
	public MedicationRequest getResourceById(@IdParam IdType theId) {
		MedicationRequest result = null;
		result = fhirMedicationRequestResource.getByUniqueId(theId);
		return result;
	}

	/**
	 * Search Medication by unique id
	 *
	 * @param id object
	 */
	@Search()
	public List<MedicationRequest> searchMedicatoonRequestByUniqueId(
															@RequiredParam(name = MedicationRequest.SP_RES_ID) TokenParam id) {
		return fhirMedicationRequestResource.searchByUniqueId(id);
	}

	/**
	 * Search Medication by unique patient uuod
	 *
	 * @param patient patient reference with uuid
	 */
	@Search()
	public List<MedicationRequest> searchMedicatoonRequestByPatientId(
			@RequiredParam(name = MedicationRequest.SP_PATIENT) ReferenceParam patient) {
		return fhirMedicationRequestResource.searchByPatientId(patient);
	}

	/**
	 * Create Medication Request
	 *
	 * @param medicationRequest fhir medication object
	 */
	@Create
	public MethodOutcome createFHIRMedicationRequestr(@ResourceParam MedicationRequest medicationRequest) {
		medicationRequest = fhirMedicationRequestResource.createFHIRMedicationRequest(medicationRequest);
		MethodOutcome retVal = new MethodOutcome();
		retVal.setId(new IdType(FHIRConstants.PERSON, medicationRequest.getId()));
		OperationOutcome outcome = new OperationOutcome();
		CodeableConcept concept = new CodeableConcept();
		Coding coding = concept.addCoding();
		coding.setDisplay("Medication request is successfully created with id " + medicationRequest.getId());
		outcome.addIssue().setDetails(concept);
		retVal.setOperationOutcome(outcome);
		return retVal;
	}

	/**
	 * Update Medication Request
	 *
	 * @param medicationRequest fhir medication object
	 */
	@Update
	public MethodOutcome updateMedicationRequest(@ResourceParam MedicationRequest medicationRequest, @IdParam IdType theId) {
		MethodOutcome retVal = new MethodOutcome();
		OperationOutcome outcome = new OperationOutcome();
		try {
			medicationRequest = fhirMedicationRequestResource.updateFHIRMedicationRequest(medicationRequest, medicationRequest.getId());
		} catch (Exception e) {
			retVal.setOperationOutcome(outcome);
			CodeableConcept concept = new CodeableConcept();
			Coding coding = concept.addCoding();
			coding.setDisplay(
					"No Person is associated with the given UUID to update. Please"
							+ " make sure you have set at lease one non-delete name, Gender and birthday to create a new "
							+ "Person with the given Id" + medicationRequest.getId());
			outcome.addIssue().setDetails(concept);
			retVal.setOperationOutcome(outcome);
			return retVal;
		}
		CodeableConcept concept = new CodeableConcept();
		Coding coding = concept.addCoding();
		coding.setDisplay("Medication request is successfully updated " + medicationRequest.getId());
		outcome.addIssue().setDetails(concept);
		retVal.setOperationOutcome(outcome);
		return retVal;
	}
}
