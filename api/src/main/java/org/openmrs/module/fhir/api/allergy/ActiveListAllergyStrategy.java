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
package org.openmrs.module.fhir.api.allergy;

import ca.uhn.fhir.model.dstu2.resource.AllergyIntolerance;
import org.openmrs.activelist.ActiveListItem;
import org.openmrs.activelist.Allergy;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.FHIRAllergyIntoleranceUtil;

import java.util.ArrayList;
import java.util.List;

public class ActiveListAllergyStrategy implements GenericAllergyStrategy {

	public AllergyIntolerance getAllergyById(String uuid) {
		ActiveListItem allergy = Context.getActiveListService().getActiveListItemByUuid(uuid);
		if(allergy == null) {
			return null;
		}
		return FHIRAllergyIntoleranceUtil.generateAllergyTolerance((Allergy)allergy);
	}

	public List<AllergyIntolerance> searchAllergyById(String uuid) {
		ActiveListItem allergy = Context.getActiveListService().getActiveListItemByUuid(uuid);
		List<AllergyIntolerance> allergies = new ArrayList<AllergyIntolerance>();
		if(allergy != null) {
			allergies.add(FHIRAllergyIntoleranceUtil.generateAllergyTolerance((Allergy)allergy));
		}
		return allergies;
	}

	public List<AllergyIntolerance> searchAllergyByName(String name) {
		return null;
	}
}
