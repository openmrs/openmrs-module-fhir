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

import ca.uhn.fhir.model.dstu2.resource.Location;
import ca.uhn.fhir.model.dstu2.valueset.LocationStatusEnum;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.LocationService;

import java.util.List;

public class FHIRLocationResource extends Resource {
	
	public Location getByUniqueId(IdDt id) {
		LocationService locationService = Context.getService(LocationService.class);
		Location fhirLocation = locationService.getLocation(id.getIdPart());
		if (fhirLocation == null) {
			throw new ResourceNotFoundException("Location is not found for the given Id " + id.getIdPart());
		}
		return fhirLocation;
	}
	
	public List<Location> searchLocationsById(TokenParam id) {
		return Context.getService(LocationService.class).searchLocationsById(id.getValue());
	}
	
	public List<Location> searchLocationsByStatus(TokenParam active) {
		if (active != null && active.getValue().equalsIgnoreCase(LocationStatusEnum.ACTIVE.getCode())) {
			return Context.getService(LocationService.class).searchLocationsByStatus(true);
		} else {
			return Context.getService(LocationService.class).searchLocationsByStatus(false);
		}
	}
	
	public List<Location> searchLocationsByName(StringParam name) {
		return Context.getService(LocationService.class).searchLocationsByName(name.getValue());
	}
	
	public void deleteLocation(IdDt id) {
		LocationService locationService = Context.getService(LocationService.class);
		locationService.deleteLocation(id.getIdPart());
	}
	
	public void updateLocationById(String id, Location location) {
		Context.getService(LocationService.class).updateLocationById(id, location);
		
	}
	
	public Location createLocation(Location location) {
		return Context.getService(LocationService.class).createLocation(location);
	}


}
