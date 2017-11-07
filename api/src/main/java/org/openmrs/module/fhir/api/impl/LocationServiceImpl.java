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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hl7.fhir.dstu3.model.Location;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.fhir.api.LocationService;
import org.openmrs.module.fhir.api.db.FHIRDAO;
import org.openmrs.module.fhir.api.strategies.location.LocationStrategyUtil;

import java.util.List;

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
	@Override
	public Location getLocation(String uuid) {
		return LocationStrategyUtil.getLocationStrategy().getLocation(uuid);
	}

	/**
	 * @see org.openmrs.module.fhir.api.LocationService#searchLocationsByUuid(String)
	 */
	@Override
	public List<Location> searchLocationsByUuid(String uuid) {
		return LocationStrategyUtil.getLocationStrategy().searchLocationsByUuid(uuid);
	}

	/**
	 * @see org.openmrs.module.fhir.api.LocationService#searchLocationsByName(String)
	 */
	@Override
	public List<Location> searchLocationsByName(String name) {
		return LocationStrategyUtil.getLocationStrategy().searchLocationsByName(name);
	}

	/**
	 * @see org.openmrs.module.fhir.api.LocationService#searchLocationsByStatus(boolean)
	 */
	@Override
	public List<Location> searchLocationsByStatus(boolean status) {
		return LocationStrategyUtil.getLocationStrategy().searchLocationsByStatus(status);
	}

	/**
	 * @see org.openmrs.module.fhir.api.LocationService#deleteLocation(String)
	 */
	@Override
	public void deleteLocation(String uuid) {
		LocationStrategyUtil.getLocationStrategy().deleteLocation(uuid);
	}

	/**
	 * @see org.openmrs.module.fhir.api.LocationService#updateLocation(String, Location)
	 */
	@Override
	public Location updateLocation(String uuid, Location location) {
		return LocationStrategyUtil.getLocationStrategy().updateLocation(uuid, location);
	}
	
	/**
	 * @see org.openmrs.module.fhir.api.LocationService#createLocation(Location)
	 */
	@Override
	public Location createLocation(Location location) {
		return LocationStrategyUtil.getLocationStrategy().createLocation(location);
	}

}
