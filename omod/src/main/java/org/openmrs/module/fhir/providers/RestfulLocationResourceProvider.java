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

import java.util.List;

import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.openmrs.module.fhir.resources.FHIRLocationResource;

import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu2.resource.Location;
import ca.uhn.fhir.model.dstu2.resource.OperationOutcome;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.annotation.ConditionalUrlParam;
import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.Delete;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.annotation.Update;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.PreconditionFailedException;

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
	 * The "@Read" annotation indicates that this method supports the read operation. Read
	 * operations should return a single resource instance.
	 *
	 * @param theId The read operation takes one parameter, which must be of type IdDt and must be
	 *            annotated with the "@Read.IdParam" annotation.
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
	 * @return This method returns a list of locations. This list may contain multiple matching
	 *         resources, or it may also be empty.
	 */
	@Search()
	public List<Location> findLocationsByName(@RequiredParam(name = Location.SP_NAME) StringParam name) {
		return locationResource.searchLocationsByName(name);
	}
	
	/**
	 * Search location by status
	 *
	 * @param active search term
	 * @return This method returns a list of locations. This list may contain multiple matching
	 *         resources, or it may also be empty.
	 */
	@Search()
	public List<Location> searchLocationsByStatus(@RequiredParam(name = Location.SP_STATUS) TokenParam active) {
		return locationResource.searchLocationsByStatus(active);
	}
	
	/**
	 * Delete Location by unique id
	 *
	 * @param theId object containing the id
	 */
	@Delete()
	public void deleteLocation(@IdParam IdDt theId) {
		locationResource.deleteLocation(theId);
	}
	
	/**
	 * Update Location
	 * 
	 * @param location fhir Location object
	 * @param theId , the uuid of the Location resource to be update
	 * @return Method outcome contains the status of the update operation
	 */
	@Update
	public MethodOutcome updateLocation(@ResourceParam Location location, @IdParam IdDt theId) {
		MethodOutcome retVal = new MethodOutcome();
		OperationOutcome outcome = new OperationOutcome();
		location = locationResource.updateLocation(theId.getIdPart(), location);
		outcome.addIssue().setDetails("Location successfully updated");
		retVal.setOperationOutcome(outcome);
		return retVal;
	}

	/**
	 * Conditionally update location by name.
	 * 
	 * @param theLocation {@link ca.uhn.fhir.model.dstu2.resource.Location} object provided by the
	 *            {@link ca.uhn.fhir .rest.server.RestfulServer}
	 * @param theId Only one of theId or theConditional will have a value and the other will be
	 *            null, depending on the URL passed into the server
	 * @param theConditional This will have a value like "Location?name=Colombo
	 * @return MethodOutcome which contains the status of the operation
	 */
	@Update()
	public MethodOutcome updateLocationByName(@ResourceParam Location theLocation, @IdParam IdDt theId,
	                                          @ConditionalUrlParam String theConditional) {
		MethodOutcome outcome = new MethodOutcome();
		OperationOutcome operationoutcome = null;
		if (theConditional != null) {
			List<Location> locationList = null;
			int startIndex = theConditional.lastIndexOf('=');
			String locationName = theConditional.substring(startIndex + 1);
			if (locationName == null) {
				operationoutcome = new OperationOutcome();
				operationoutcome.addIssue().setDetails("Please check Condition URL format");
				outcome.setOperationOutcome(operationoutcome);
				return outcome;
			}
			StringParam nameParam = new StringParam();
			nameParam.setValue(locationName);
			locationList = locationResource.searchLocationsByName(nameParam);
			if (locationList.size() == 0) {
				outcome = updateLocation(theLocation, null);
			} else if (locationList.size() == 1) {
				outcome = updateLocation(theLocation, locationList.get(0).getId());
			} else {
				throw new PreconditionFailedException("There are more than one Location for the given condition");
			}
		} else {
			outcome = updateLocation(theLocation, theId);
		}
		return outcome;
	}
	
	/**
	 * Create Location
	 * 
	 * @param location fhir Location object
	 * @return Method outcome object which contains the identity of the created resource.
	 */
	@Create
	public MethodOutcome createFHIRLocation(@ResourceParam Location location) {
		location = locationResource.createLocation(location);
		MethodOutcome retVal = new MethodOutcome();
		retVal.setId(new IdDt(FHIRConstants.LOCATION, location.getId().getIdPart()));
		OperationOutcome outcome = new OperationOutcome();
		outcome.addIssue().setDetails("Location is successfully created");
		retVal.setOperationOutcome(outcome);
		return retVal;
	}
	
}
