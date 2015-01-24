/**
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

import ca.uhn.fhir.model.dstu.resource.Location;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class LocationServiceTest extends BaseModuleContextSensitiveTest {

	protected static final String LOC_INITIAL_DATA_XML = "org/openmrs/api/include/LocationServiceTest-initialData.xml";

	public LocationService getService() {
		return Context.getService(LocationService.class);
	}

	@Before
	public void runBeforeEachTest() throws Exception {
		executeDataSet(LOC_INITIAL_DATA_XML);
	}

	@Test
	public void shouldSetupContext() {
		assertNotNull(getService());
	}

	@Test
	public void getLocation_shouldReturnResourceIfExists() {
		String locationUuid = "f08ba64b-ea57-4a41-b33c-9dfc59b0c60a";
		Location fhirLocation = getService().getLocation(locationUuid);
		assertNotNull(fhirLocation);
		assertEquals(fhirLocation.getId().toString(), locationUuid);

	}

	@Test
	public void getLocation_shouldReturnOperationOutcomeIfDoesNotExist() {

	}

	@Test
	public void getLocationsById_shouldReturnBundleIfExists() {
		String locationUuid = "f08ba64b-ea57-4a41-b33c-9dfc59b0c60a";
		List<Location> locations = getService().searchLocationsById(locationUuid);
		assertNotNull(locations);
		assertEquals(locations.size(), 1);
		assertEquals(locations.get(0).getId().getIdPart(), locationUuid);
	}

	@Test
	public void getLocationsById_shouldReturnEmptyBundleIfDoesNotExist() {

	}

	@Test
	public void getLocationsByIdentifier_shouldReturnBundleIfExists() {

	}

	@Test
	public void getLocationsByIdentifier_shouldReturnEmptyBundleIfDoesNotExist() {

	}
}
