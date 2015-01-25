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
import ca.uhn.fhir.model.dstu.resource.Practitioner;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.StringDt;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.openmrs.module.fhir.resources.FHIRPractitionerResource;

import java.util.List;

public class RestfulPractitionerResourceProvider implements IResourceProvider {
	;
	private FHIRPractitionerResource practitionerResource;

	public RestfulPractitionerResourceProvider() {
		this.practitionerResource = new FHIRPractitionerResource();
	}

	@Override
	public Class<? extends IResource> getResourceType() {
		return Practitioner.class;
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
	public Practitioner getResourceById(@IdParam IdDt theId) {
		Practitioner result = null;
		result = practitionerResource.getByUniqueId(theId);
		return result;
	}

	/**
	 * Search Practitioner by unique id
	 *
	 * @param id object contaning the requested family name
	 */
	@Search()
	public List<Practitioner> searchPractitionerByUniqueId(@RequiredParam(name = Practitioner.SP_RES_ID) TokenParam id) {
		return practitionerResource.searchByUniqueId(id);
	}

	/**
	 * Get Practitioner by family name
	 *
	 * @param theFamilyName object contaning the requested family name
	 */
	@Search()
	public List<Practitioner> findPractitionersByFamilyName(@RequiredParam(name = Practitioner.SP_FAMILY) StringDt theFamilyName) {
		return practitionerResource.searchByFamilyName(theFamilyName);
	}

	/**
	 * Get Practitioner by name
	 *
	 * @param name name of the Practitioner
	 * @return This method returns a list of Practitioners. This list may contain multiple matching resources, or it may also be
	 * empty.
	 */
	@Search()
	public List<Practitioner> findPractitionersByName(@RequiredParam(name = Practitioner.SP_NAME) StringDt name) {
		return practitionerResource.searchByName(name);
	}

	/**
	 * Get Practitioner by identifier
	 *
	 * @param identifier
	 * @return This method returns a list of Practitioners. This list may contain multiple matching resources, or it may also be
	 * empty.
	 */
	@Search()
	public List<Practitioner> searchPractitionersByIdentifier(@RequiredParam(name = Practitioner.SP_IDENTIFIER) TokenParam identifier) {
		return practitionerResource.searchByIdentifier(identifier);
	}

	/**
	 * Find Practitioner by given name
	 *
	 * @param givenName given name of the Practitioner
	 * @return This method returns a list of Practitioners. This list may contain multiple matching resources, or it may also be
	 * empty.
	 */
	@Search()
	public List<Practitioner> findPractitionersByGivenName(@RequiredParam(name = Practitioner.SP_GIVEN) StringDt givenName) {
		return practitionerResource.searchByGivenName(givenName);
	}
}
