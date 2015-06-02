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
package org.openmrs.module.fhir.api;

import ca.uhn.fhir.model.dstu2.resource.Location;
import org.openmrs.api.OpenmrsService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface LocationService extends OpenmrsService {

	/**
	 * Get location by id
	 *
	 * @param id location uuid
	 * @return location fhir resource
	 */
	Location getLocation(String id);

	/**
	 * Search locations by id
	 *
	 * @param id location uuid
	 * @return fhir locations list
	 */
	List<Location> searchLocationsById(String id);

	/**
	 * Search all active or inactive locations
	 *
	 * @param status boolean status
	 * @return fhir location resource list
	 */
	public List<Location> searchLocationsByStatus(boolean status);

	/**
	 * Search locations by name
	 *
	 * @param name location name to be search
	 * @return fhir location resource list
	 */
	public List<Location> searchLocationsByName(String name);
	
	/**
	 * Delete location by id
	 *
	 * @param id uuid of Location
	 */
	public void deleteLocation(String id);

	/**
	 * Update location by id
	 *
	 * @param id       location uuid
	 * @param location representation of location fhir resource
	 */
	public void updateLocationById(String id, Location location);
	
	/**
	 * Create location 
	 *
	 * @param location representation of location fhir resource
	 */
	public Location createLocation(Location location);

}
