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
package org.openmrs.module.fhir.api.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.fhir.api.LocationService;
import org.openmrs.module.fhir.api.db.FHIRDAO;
import org.openmrs.module.fhir.api.util.FHIRLocationUtil;
import org.apache.commons.lang.StringUtils;

import ca.uhn.fhir.model.dstu2.resource.Location;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;

/**
 * It is a default implementation of {@link org.openmrs.module.fhir.api.PatientService}.
 */
public class LocationServiceImpl extends BaseOpenmrsService implements LocationService {

	protected final Log log = LogFactory.getLog(this.getClass());

	private FHIRDAO dao;

	/**
	 * @return the dao
	 */
	public FHIRDAO getDao() {
		return dao;
	}

	/**
	 * @param dao the dao to set
	 */
	public void setDao(FHIRDAO dao) {
		this.dao = dao;
	}

	/**
	 * @see org.openmrs.module.fhir.api.LocationService#getLocation(String)
	 */
	public Location getLocation(String id) {

		org.openmrs.Location omrsLocation = Context.getLocationService().getLocationByUuid(id);
		if (omrsLocation == null || omrsLocation.isRetired()) {
			return null;
		}
		return FHIRLocationUtil.generateLocation(omrsLocation);

	}

	/**
	 * @see org.openmrs.module.fhir.api.LocationService#searchLocationsById(String)
	 */
	public List<Location> searchLocationsById(String id) {

		org.openmrs.Location omrsLocation = Context.getLocationService().getLocationByUuid(id);
		List<Location> locationList = new ArrayList<Location>();
		if (omrsLocation != null && !omrsLocation.isRetired()) {
			locationList.add(FHIRLocationUtil.generateLocation(omrsLocation));
		}
		return locationList;
	}

	/**
	 * @see org.openmrs.module.fhir.api.LocationService#searchLocationsById(String)
	 */
	public List<Location> searchLocationsByName(String name) {
		List<org.openmrs.Location> omrsLocations = Context.getLocationService().getLocations(name);
		List<Location> locationList = new ArrayList<Location>();
		for (org.openmrs.Location location : omrsLocations) {
			if (StringUtils.startsWithIgnoreCase(location.getName(), name)){
				locationList.add(FHIRLocationUtil.generateLocation(location));
			}
		}
		return locationList;
	}

	/**
	 * @see org.openmrs.module.fhir.api.LocationService#searchLocationsById(String)
	 */
	public List<Location> searchLocationsByStatus(boolean status) {
		//TODO this method looks for all the locations which is inefficient. Reimplement after API revamp
		List<org.openmrs.Location> omrsLocations = Context.getLocationService().getAllLocations(true);
		List<Location> locationList = new ArrayList<Location>();
		for (org.openmrs.Location location : omrsLocations) {
			if (status) {
				if (!location.isRetired()) {
					locationList.add(FHIRLocationUtil.generateLocation(location));
				}
			} else {
				if (location.isRetired()) {
					locationList.add(FHIRLocationUtil.generateLocation(location));
				}
			}
		}
		return locationList;
	}

	/**
	 * @see org.openmrs.module.fhir.api.LocationService#deleteLocation(String)
	 */
	public void deleteLocation(String id) {
		org.openmrs.Location location = Context.getLocationService().getLocationByUuid(id);
		Context.getLocationService().purgeLocation(location);
	}

	/**
	 * @see org.openmrs.module.fhir.api.LocationService#updateLocationById(String, ca.uhn.fhir.model.dstu2.resource
	 * .Location)
	 */
	public Location updateLocation(String id, Location location) {
		org.openmrs.Location omrsLocation;
		List<String> errors = new ArrayList<String>();
		omrsLocation = FHIRLocationUtil.generateOpenMRSLocation(location, errors);
		if (!errors.isEmpty()) {
			StringBuilder errorMessage = new StringBuilder("The request cannot be processed due to following issues \n");
			for (int i = 0; i < errors.size(); i++) {
				errorMessage.append((i + 1) + " : " + errors.get(i) + "\n");
			}
			throw new UnprocessableEntityException(errorMessage.toString());
		}
		omrsLocation = Context.getLocationService().saveLocation(omrsLocation);
		return FHIRLocationUtil.generateLocation(omrsLocation);
	}
	
	/**
	 * @see org.openmrs.module.fhir.api.LocationService#createLocation(ca.uhn.fhir.model.dstu2.resource
	 * .Location)
	 */
	public Location createLocation(Location location) {
		org.openmrs.Location omrsLocation=null;
		List<String> errors = new ArrayList<String>();
		omrsLocation = FHIRLocationUtil.generateOpenMRSLocation(location, errors);
		if (!errors.isEmpty()) {
			StringBuilder errorMessage = new StringBuilder("The request cannot be processed due to the following issues \n");
			for (int i = 0; i < errors.size(); i++) {
				errorMessage.append((i + 1) + " : " + errors.get(i) + "\n");
			}
			throw new UnprocessableEntityException(errorMessage.toString());
		}
		omrsLocation=Context.getLocationService().saveLocation(omrsLocation);
		return FHIRLocationUtil.generateLocation(omrsLocation);
	}

}
