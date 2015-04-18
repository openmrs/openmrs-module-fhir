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

import ca.uhn.fhir.model.dstu2.resource.FamilyHistory;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.FamilyHistoryService;

import java.util.List;

public class FHIRFamilyHistoryResource extends Resource {

	public FamilyHistory getByUniqueId(IdDt id) {
		FamilyHistoryService familyHistoryService = Context.getService(FamilyHistoryService.class);
		FamilyHistory familyHistory = familyHistoryService.getRelationshipById(id.getIdPart());
		if (familyHistory == null) {
			throw new ResourceNotFoundException("Family history is not found for the given Id " + id.getIdPart());
		}
		return familyHistory;
	}

	public List<FamilyHistory> searchFamilyHistoryByUniqueId(TokenParam id) {
		FamilyHistoryService familyHistoryService = Context.getService(FamilyHistoryService.class);
		return familyHistoryService.searchRelationshipsById(id.getValue());
	}

	public List<FamilyHistory> searchFamilyHistoryByPerson(ReferenceParam id) {
		FamilyHistoryService familyHistoryService = Context.getService(FamilyHistoryService.class);
		return familyHistoryService.searchFamilyHistoryByPersonId(id.getIdPart());
	}
}
