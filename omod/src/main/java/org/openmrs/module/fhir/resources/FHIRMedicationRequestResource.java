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
package org.openmrs.module.fhir.resources;

import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.MedicationRequest;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.MedicationRequestService;

import java.util.List;

public class FHIRMedicationRequestResource extends Resource {

	public MedicationRequest getByUniqueId(IdType id) {
		MedicationRequestService medicationRequestService = Context
				.getService(MedicationRequestService.class);
		MedicationRequest medicationRequest = medicationRequestService.getMedicationRequestById(id.getIdPart());
		if (medicationRequest == null) {
			throw new ResourceNotFoundException("Medication request is not found for the given Id " + id.getIdPart());
		}
		return medicationRequest;
	}

	public List<MedicationRequest> searchByUniqueId(TokenParam id) {
		MedicationRequestService medicationRequestService = Context
				.getService(MedicationRequestService.class);
		return medicationRequestService.searchMedicationRequestById(id.getValue());
	}

	public List<MedicationRequest> searchByPatientId(ReferenceParam patient) {
		MedicationRequestService medicationRequestService = Context
				.getService(MedicationRequestService.class);
		return medicationRequestService.searchMedicationRequestByPatientId(patient.getIdPart());
	}

	public MedicationRequest createFHIRMedicationRequest(MedicationRequest medicationRequest) {
		MedicationRequestService medicationRequestService = Context
				.getService(MedicationRequestService.class);
		return medicationRequestService.createFHIRMedicationRequest(medicationRequest);
	}

	public MedicationRequest updateFHIRMedicationRequest(MedicationRequest medicationRequest, String theId) {
		MedicationRequestService medicationRequestService = Context
				.getService(MedicationRequestService.class);
		return medicationRequestService.updateFHIRMedicationRequest(medicationRequest, theId);
	}

	public void deleteMedicationRequest(String theId) {
		MedicationRequestService medicationRequestService = Context
				.getService(MedicationRequestService.class);
		medicationRequestService.deleteMedicationRequest(theId);
	}
}
