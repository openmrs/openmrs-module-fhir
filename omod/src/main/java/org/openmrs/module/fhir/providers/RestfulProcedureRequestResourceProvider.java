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
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Update;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.ProcedureRequest;
import org.hl7.fhir.dstu3.model.Resource;
import org.openmrs.module.fhir.resources.FHIRProcedureRequestResource;
import org.openmrs.module.fhir.util.MethodOutcomeBuilder;

public class RestfulProcedureRequestResourceProvider implements IResourceProvider {

	private static final String ERROR_MESSAGE = "No Procedure Resource is associated with the given UUID to update";

	private FHIRProcedureRequestResource resource;

	public RestfulProcedureRequestResourceProvider() {
		this.resource = new FHIRProcedureRequestResource();
	}

	@Override
	public Class<? extends Resource> getResourceType() {
		return ProcedureRequest.class;
	}

	@Read
	public ProcedureRequest getResourceById(@IdParam IdType theId) {
		return resource.getByUuid(theId);
	}

	@Create
	public MethodOutcome createFHIRMedicationRequest(@ResourceParam ProcedureRequest procedureRequest) {
		return MethodOutcomeBuilder
				.buildCreate(resource.createFHIRProcedureRequest(procedureRequest));
	}

	@Update
	public MethodOutcome updateMedicationRequest(@ResourceParam ProcedureRequest procedureRequest, @IdParam IdType theId) {
		try {
			return MethodOutcomeBuilder.buildUpdate(
					resource.updateFHIRProcedureRequest(procedureRequest, theId.getIdPart()));
		}
		catch (Exception e) {
			return MethodOutcomeBuilder.buildCustom(ERROR_MESSAGE);
		}
	}

	@Delete
	public void deleteMedicationRequest(@IdParam IdType theId) {
		resource.deleteProcedureRequest(theId.getIdPart());
	}
}
