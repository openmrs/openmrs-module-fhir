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

import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.hl7.fhir.dstu3.model.AllergyIntolerance;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Resource;
import org.openmrs.module.fhir.resources.FHIRAllergyIntoleranceResource;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public class RestfulAllergyIntoleranceResourceProvider implements IResourceProvider {

	private FHIRAllergyIntoleranceResource allergyIntoleranceResource;

	public RestfulAllergyIntoleranceResourceProvider() {
		allergyIntoleranceResource = new FHIRAllergyIntoleranceResource();
	}

	@Override
	public Class<? extends Resource> getResourceType() {
		return AllergyIntolerance.class;
	}

	/**
	 * The "@Read" annotation indicates that this method supports the
	 * read operation. Read operations should return a single resource
	 * instance.
	 *
	 * @param theId The read operation takes one parameter, which must be of type
	 *              IdType and must be annotated with the "@Read.IdParam" annotation.
	 * @return Returns a resource matching this identifier, or null if none exists.
	 */
	@Read
	public AllergyIntolerance getResourceById(@IdParam IdType theId) {
		return allergyIntoleranceResource.getByUniqueId(theId);
	}

	/**
	 * Search allergies by unique id
	 *
	 * @param id object containing the requested id
	 */
	@Search
	public List<AllergyIntolerance> findAllergiesByUniqueId(
			@RequiredParam(name = AllergyIntolerance.SP_RES_ID) TokenParam id) {
		return allergyIntoleranceResource.searchAllergiesById(id);
	}

	/**
	 * Search allergies by patient identifier
	 *
	 * @param identifier object containing the patient identifier
	 */
	@Search
	public List<AllergyIntolerance> findAllergiesByPatientIdentifier(
			@RequiredParam(name = AllergyIntolerance.SP_PATIENT, chainWhitelist = {
					Patient.SP_IDENTIFIER }) ReferenceParam identifier) {
		return allergyIntoleranceResource.searchAllergiesByPatientIdentifier(identifier);
	}

	/**
	 * Search allergies by patient name
	 *
	 * @param name object containing the patient name
	 */
	@Search
	public List<AllergyIntolerance> findAllergiesByPatientName(
			@RequiredParam(name = AllergyIntolerance.SP_PATIENT, chainWhitelist = { Patient.SP_NAME }) ReferenceParam
					name) {
		return allergyIntoleranceResource.searchAllergiesByPatientName(name);
	}

	@Search
	public List<AllergyIntolerance> findAllergiesByPatientUuid(
			@RequiredParam(name = AllergyIntolerance.SP_PATIENT, chainWhitelist = { Patient.SP_RES_ID})
			ReferenceParam uuid) {
		return allergyIntoleranceResource.searchAllergiesByPatientUuid(uuid);
	}
}
