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
import org.hl7.fhir.dstu3.model.Condition;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Resource;
import org.openmrs.module.fhir.resources.FHIRConditionResource;
import org.openmrs.module.fhir.util.MethodOutcomeBuilder;

import java.util.List;

public class RestfulConditionResourceProvider implements IResourceProvider {

	private FHIRConditionResource conditionResource;

	public RestfulConditionResourceProvider() {
		conditionResource = new FHIRConditionResource();
	}

	@Override
	public Class<? extends Resource> getResourceType() {
		return Condition.class;
	}

	/**
	 * The "@Read" annotation indicates that this method supports the
	 * read operation. Read operations should return a single resource
	 * instance.
	 *
	 * @param theId The read operation takes one parameter, which must be of type
	 *              IdDt and must be annotated with the "@Read.IdParam" annotation.
	 * @return Returns a resource matching this identifier, or null if none exists.
	 */
	@Read
	public Condition getResourceById(@IdParam IdType theId) {
		return conditionResource.getConditionByUuid(theId);
	}

	/**
	 * @see org.openmrs.module.fhir.resources.FHIRConditionResource#getConditionsByPatientUuid(ca.uhn.fhir.rest.param.ReferenceParam)
	 */
	@Search
	public List<Condition> findConditionByPatient(
			@RequiredParam(name = Condition.SP_PATIENT) ReferenceParam patient) {
		return conditionResource.getConditionsByPatientUuid(patient);
	}

	/**
	 * @see org.openmrs.module.fhir.resources.FHIRConditionResource#getConditionByUuid(ca.uhn.fhir.rest.param.TokenParam)
	 */
	@Search
	public Condition findConditionByUuid(
			@RequiredParam(name = Condition.SP_RES_ID) TokenParam uuid) {
		return conditionResource.getConditionByUuid(uuid);
	}

	/**
	 * @see org.openmrs.module.fhir.resources.FHIRConditionResource#createFHIRCondition(Condition)
	 */
	@Create
	public MethodOutcome createFHIRCondition(@ResourceParam Condition condition) {
		return MethodOutcomeBuilder.buildCreate(conditionResource.createFHIRCondition(condition));
	}

	/**
	 * @see org.openmrs.module.fhir.resources.FHIRConditionResource#updateFHIRCondition(org.hl7.fhir.dstu3.model.Condition)
	 */
	@Update
	public MethodOutcome updateFHIRCondition(@ResourceParam Condition condition, @IdParam IdType uuid) {
			return MethodOutcomeBuilder.buildUpdate(conditionResource.updateFHIRCondition(condition));

	}
}
