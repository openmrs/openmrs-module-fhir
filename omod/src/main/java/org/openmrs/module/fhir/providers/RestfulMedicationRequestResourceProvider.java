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
import ca.uhn.fhir.rest.annotation.Delete;
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
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.MedicationRequest;
import org.hl7.fhir.dstu3.model.Resource;
import org.openmrs.module.fhir.resources.FHIRMedicationRequestResource;
import org.openmrs.module.fhir.util.MethodOutcomeBuilder;

import java.util.List;

public class RestfulMedicationRequestResourceProvider implements IResourceProvider {

	private static final String ERROR_MESSAGE = "No Medication Resource is associated with the given UUID to update";

	private FHIRMedicationRequestResource fhirMedicationRequestResource;

	public RestfulMedicationRequestResourceProvider() {
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
	 *              annotated with the "@Read.IdParam" annotation.
	 * @return Returns a resource matching this identifier, or null if none exists.
	 */
	@Read
	public MedicationRequest getResourceById(@IdParam IdType theId) {
		return fhirMedicationRequestResource.getByUniqueId(theId);
	}

	/**
	 * Search Medication by unique id
	 *
	 * @param id object
	 */
	@Search
	public List<MedicationRequest> findMedicatoonRequestByUniqueId(
			@RequiredParam(name = MedicationRequest.SP_RES_ID) TokenParam id) {
		return fhirMedicationRequestResource.searchByUniqueId(id);
	}

	/**
	 * Search Medication by unique patient uuod
	 *
	 * @param patient patient reference with uuid
	 */
	@Search
	public List<MedicationRequest> findMedicatoonRequestByPatientId(
			@RequiredParam(name = MedicationRequest.SP_PATIENT) ReferenceParam patient) {
		return fhirMedicationRequestResource.searchByPatientId(patient);
	}

	/**
	 * Create Medication Request
	 *
	 * @param medicationRequest fhir medication object
	 */
	@Create
	public MethodOutcome createFHIRMedicationRequest(@ResourceParam MedicationRequest medicationRequest) {
		return MethodOutcomeBuilder
				.buildCreate(fhirMedicationRequestResource.createFHIRMedicationRequest(medicationRequest));
	}

	/**
	 * Update Medication Request
	 *
	 * @param medicationRequest fhir medication object
	 */
	@Update
	public MethodOutcome updateMedicationRequest(@ResourceParam MedicationRequest medicationRequest, @IdParam IdType theId) {
		try {
			return MethodOutcomeBuilder.buildUpdate(
					fhirMedicationRequestResource.updateFHIRMedicationRequest(medicationRequest, theId.getIdPart()));
		}
		catch (Exception e) {
			return MethodOutcomeBuilder.buildCustom(ERROR_MESSAGE);
		}
	}

	@Delete
	public void deleteMedicationRequest(@IdParam IdType theId) {
		fhirMedicationRequestResource.deleteMedicationRequest(theId.getIdPart());
	}
}
