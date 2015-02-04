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
import ca.uhn.fhir.model.dstu.resource.Location;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.openmrs.module.fhir.resources.FHIRLocationResource;

import java.util.List;

public class RestfulLocationResourceProvider implements IResourceProvider {

	private FHIRLocationResource locationResource;

	public RestfulLocationResourceProvider() {
		this.locationResource = new FHIRLocationResource();
	}

	@Override
	public Class<? extends IResource> getResourceType() {
		return Location.class;
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
	public Location getResourceById(@IdParam IdDt theId) {
		Location result = null;
		result = locationResource.getByUniqueId(theId);
		return result;
	}

	/**
	 * Search locations by unique id
	 *
	 * @param id object containing the requested id
	 */
	@Search()
	public List<Location> searchLocationsByUniqueId(@RequiredParam(name = Location.SP_RES_ID) TokenParam id) {
		return locationResource.searchLocationsById(id);
	}

	/**
	 * Get locations by name
	 *
	 * @param name name of the location
	 * @return This method returns a list of locations. This list may contain multiple matching resources, or it may also be
	 * empty.
	 */
	@Search()
	public List<Location> findLocationsByName(@RequiredParam(name = Location.SP_NAME) StringParam name) {
		return locationResource.searchLocationsByName(name);
	}

	/**
	 * Search location by status
	 *
	 * @param active search term
	 * @return This method returns a list of locations. This list may contain multiple matching resources, or it may also be
	 * empty.
	 */
	@Search()
	public List<Location> searchLocationsByStatus(@RequiredParam(name = Location.SP_STATUS) TokenParam active) {
		return locationResource.searchLocationsByStatus(active);
	}
}
