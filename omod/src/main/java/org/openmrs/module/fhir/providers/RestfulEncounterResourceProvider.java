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
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.IResourceProvider;

import org.openmrs.module.fhir.resources.FHIREncounterResource;

import java.util.List;

public class RestfulEncounterResourceProvider implements IResourceProvider {

	private FHIREncounterResource encounterResource;

	public RestfulEncounterResourceProvider() {
		encounterResource = new FHIREncounterResource();
	}

	@Override
	public Class<? extends IResource> getResourceType() {
		return Encounter.class;
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
	@Read()
	public Encounter getResourceById(@IdParam IdDt theId) {
		Encounter result = null;
		result = encounterResource.getByUniqueId(theId);
		return result;
	}

	/**
	 * Search locations by unique id
	 *
	 * @param id object containing the requested id
	 */
	@Search()
	public List<Encounter> searchEncountersByUniqueId(@RequiredParam(name = Encounter.SP_RES_ID) TokenParam id) {
		return encounterResource.searchEncountersById(id);
	}
	
	/**
	 * Search encounters by patient identifier
	 *
	 * @param identifier object containing the patient identifier
	 */
	@Search()
	public List<Encounter> searchEncountersByPatientIdentifier(
		@RequiredParam(name=Encounter.SP_PATIENT, chainWhitelist= {Patient.SP_IDENTIFIER}) ReferenceParam identifier
		) {
		return encounterResource.searchEncountersByPatientIdentifier(identifier);
	}

	@Operation(name="$everything")
	public Bundle encounterInstanceOperation(@IdParam IdDt encounterId) {
		Encounter result = null;
		result = encounterResource.getByUniqueId(encounterId);
		Bundle bundle = new Bundle();
		Bundle.Entry entry = bundle.addEntry();
		entry.setResource(result);
		return bundle;
	}
}
