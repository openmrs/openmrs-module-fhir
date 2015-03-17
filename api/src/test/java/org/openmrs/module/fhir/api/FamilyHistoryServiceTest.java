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

import ca.uhn.fhir.model.dstu2.resource.FamilyHistory;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class FamilyHistoryServiceTest extends BaseModuleContextSensitiveTest {

	protected static final String RELATIONSHIPS_INITIAL_DATA_XML =
			"org/openmrs/api/include/PersonServiceTest-createRelationship.xml";

	@Before
	public void runBeforeEachTest() throws Exception {
		executeDataSet(RELATIONSHIPS_INITIAL_DATA_XML);
	}

	public FamilyHistoryService getService() {
		return Context.getService(FamilyHistoryService.class);
	}

	@Test
	public void searchFamilyHistoryByPerson__shouldReturnResourceIfExists() {
		String personUuid = "da7f524f-27ce-4bb2-86d6-6d1d05312bd5";
		List<FamilyHistory> fhirFamilyHistoryList = getService().searchRelationshipsById(personUuid);
		assertNotNull(fhirFamilyHistoryList);
		assertEquals(1, fhirFamilyHistoryList.size());
	}

	@Test
	public void getRelationshipById_shouldReturnResourceIfExists() {
		String personUuid = "da7f524f-27ce-4bb2-86d6-6d1d05312bd5";
		FamilyHistory fhirFamilyHitory = getService().getRelationshipById(personUuid);
		assertNotNull(fhirFamilyHitory);
		assertEquals(fhirFamilyHitory.getId().toString(), personUuid);
	}

	@Test
	public void searchFamilyHistoryByPerson_shouldReturnBundle() {
		String personUuid = "da7f524f-27ce-4bb2-86d6-6d1d05312bd5";
		List<FamilyHistory> fhirFamilyHistoryList = getService().searchFamilyHistoryByPerson(personUuid);
		assertNotNull(fhirFamilyHistoryList);
		assertEquals(1, fhirFamilyHistoryList.size());
	}
}
