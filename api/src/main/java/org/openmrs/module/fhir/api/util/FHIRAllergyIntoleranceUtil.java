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
package org.openmrs.module.fhir.api.util;

import ca.uhn.fhir.model.dstu2.resource.AllergyIntolerance;
import org.openmrs.Obs;
import org.openmrs.module.allergyapi.Allergy;

public class FHIRAllergyIntoleranceUtil {

	public static AllergyIntolerance generateAllergyTolerance(Obs obs) {
		return null;
	}

	public static AllergyIntolerance generateAllergyTolerance(Allergy allergy) {
		return null;
	}

	public static AllergyIntolerance generateAllergyTolerance(org.openmrs.activelist.Allergy allergy) {
		AllergyIntolerance allergyIntolerance = new AllergyIntolerance();
		allergyIntolerance.setId(allergy.getUuid());
		//Build and set patient reference
		allergyIntolerance.setSubject(FHIRUtils.buildPatientOrPersonResourceReference(allergy.getPerson()));
		return null;
	}

	public static Obs generateAllergyObs(AllergyIntolerance allergy) {
		return null;
	}

	public static Allergy generateAllergyModuleAllergy(AllergyIntolerance allergy) {
		return null;
	}

	public static org.openmrs.activelist.Allergy generateActiveListAllergy(AllergyIntolerance allergy) {
		return null;
	}
}
