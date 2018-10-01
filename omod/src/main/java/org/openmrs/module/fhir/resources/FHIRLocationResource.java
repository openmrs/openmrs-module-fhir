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

import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.LocationService;

import java.util.List;

public class FHIRLocationResource extends Resource {

	public Location getByUniqueId(IdType id) {
		LocationService locationService = Context.getService(LocationService.class);
		Location fhirLocation = locationService.getLocation(id.getIdPart());
		if (fhirLocation == null) {
			throw new ResourceNotFoundException("Location is not found for the given Id " + id.getIdPart());
		}
		return fhirLocation;
	}

	public List<Location> searchLocationsById(TokenParam id) {
		return Context.getService(LocationService.class).searchLocationsByUuid(id.getValue());
	}

	public List<Location> searchLocationsByStatus(TokenParam active) {
		if (active != null && active.getValue().equalsIgnoreCase(Location.LocationStatus.ACTIVE.toCode())) {
			return Context.getService(LocationService.class).searchLocationsByStatus(true);
		} else {
			return Context.getService(LocationService.class).searchLocationsByStatus(false);
		}
	}

	public List<Location> searchLocationsByName(StringParam name) {
		return Context.getService(LocationService.class).searchLocationsByName(name.getValue());
	}

	public void deleteLocation(IdType id) {
		LocationService locationService = Context.getService(LocationService.class);
		locationService.deleteLocation(id.getIdPart());
	}

	public Location updateLocation(String id, Location location) {
		return Context.getService(LocationService.class).updateLocation(id, location);

	}

	public Location createLocation(Location location) {
		return Context.getService(LocationService.class).createLocation(location);
	}

}
