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

import org.hl7.fhir.dstu3.model.Location;
import org.openmrs.api.OpenmrsService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface LocationService extends OpenmrsService {

	/**
	 * Get location by id
	 *
	 * @param uuid The uuid of location
	 * @return location fhir resource
	 */
	Location getLocation(String uuid);

	/**
	 * Search locations by id
	 *
	 * @param uuid The uuid of location
	 * @return fhir locations list
	 */
	List<Location> searchLocationsByUuid(String uuid);

	/**
	 * Search all active or inactive locations
	 *
	 * @param status boolean status
	 * @return fhir location resource list
	 */
	List<Location> searchLocationsByStatus(boolean status);

	/**
	 * Search locations by name
	 *
	 * @param name location name to be search
	 * @return fhir location resource list
	 */
	List<Location> searchLocationsByName(String name);

	/**
	 * Delete location by id
	 *
	 * @param uuid The uuid of location
	 */
	void deleteLocation(String uuid);

	/**
	 * Update location
	 *
	 * @param uuid     The uuid of location
	 * @param location representation of location fhir resource
	 */
	Location updateLocation(String uuid, Location location);

	/**
	 * Create location
	 *
	 * @param location representation of location fhir resource
	 */
	Location createLocation(Location location);

}
