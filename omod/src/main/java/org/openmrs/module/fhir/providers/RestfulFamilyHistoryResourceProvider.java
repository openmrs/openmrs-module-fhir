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
import ca.uhn.fhir.model.dstu.resource.FamilyHistory;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.openmrs.module.fhir.resources.FHIRFamilyHistoryResource;

import java.util.List;

public class RestfulFamilyHistoryResourceProvider implements IResourceProvider {
	private FHIRFamilyHistoryResource familyHistoryResource;

	public RestfulFamilyHistoryResourceProvider() {
		this.familyHistoryResource = new FHIRFamilyHistoryResource();
	}

	@Override
	public Class<? extends IResource> getResourceType() {
		return FamilyHistory.class;
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
	public FamilyHistory getResourceById(@IdParam IdDt theId) {
		FamilyHistory result = null;
		result = familyHistoryResource.getByUniqueId(theId);
		return result;
	}

	/**
	 * Search family history by unique id
	 *
	 * @param id object containing the requested id
	 */
	@Search()
	public List<FamilyHistory> searchFamilyHistoryByUniqueId(@RequiredParam(name = FamilyHistory.SP_RES_ID) TokenParam id) {
		return familyHistoryResource.searchFamilyHistoryByUniqueId(id);
	}

	/**
	 * Search family history by patient
	 *
	 * @param person object containing the requested patient id
	 */
	@Search()
	public List<FamilyHistory> searchFamilyHistoryByPerson(@RequiredParam(name = FamilyHistory.SP_SUBJECT) ReferenceParam  person) {
		return familyHistoryResource.searchFamilyHistoryByPerson(person);
	}
}
