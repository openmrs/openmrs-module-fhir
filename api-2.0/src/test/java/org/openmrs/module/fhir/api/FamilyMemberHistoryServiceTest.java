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

import org.hl7.fhir.dstu3.model.FamilyMemberHistory;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class FamilyMemberHistoryServiceTest extends BaseModuleContextSensitiveTest {

	protected static final String RELATIONSHIPS_INITIAL_DATA_XML =
			"org/openmrs/api/include/PersonServiceTest-createRelationship.xml";

	@Before
	public void runBeforeEachTest() throws Exception {
		executeDataSet(RELATIONSHIPS_INITIAL_DATA_XML);
	}

	public FamilyMemberHistoryService getService() {
		return Context.getService(FamilyMemberHistoryService.class);
	}

	@Test
	public void searchFamilyMemberHistoryByPerson__shouldReturnResourceIfExists() {
		String personUuid = "da7f524f-27ce-4bb2-86d6-6d1d05312bd5";
		List<FamilyMemberHistory> memberHistory = getService().searchRelationshipsById(personUuid);
		assertNotNull(memberHistory);
		assertEquals(3, memberHistory.size());
	}

	@Test
	public void getRelationshipById_shouldReturnResourceIfExists() {
		String personUuid = "da7f524f-27ce-4bb2-86d6-6d1d05312bd5";
		FamilyMemberHistory memberHistory = getService().getRelationshipById(personUuid);
		assertNotNull(memberHistory);
		assertEquals(memberHistory.getId().toString(), personUuid);
	}

	@Test
	public void searchFamilyMemberHistoryByPerson_shouldReturnBundle() {
		String personUuid = "da7f524f-27ce-4bb2-86d6-6d1d05312bd5";
		List<FamilyMemberHistory> memberHistorie = getService().searchFamilyMemberHistoryByPersonId(personUuid);
		assertNotNull(memberHistorie);
		assertEquals(3, memberHistorie.size());
	}
}
