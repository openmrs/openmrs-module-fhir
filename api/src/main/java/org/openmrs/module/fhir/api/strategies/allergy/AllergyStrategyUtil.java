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
package org.openmrs.module.fhir.api.strategies.allergy;

import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.openmrs.module.fhir.api.util.FHIRUtils;

public class AllergyStrategyUtil {

	public static GenericAllergyStrategy getAllergyStrategy() {
		String strategy = FHIRUtils.getAllergyStrategy();
		if (FHIRConstants.OBS_ALLERGY_STRATEGY.equals(strategy)) {
			return new ObsAllergyStrategy();
		} else {
			return new AllergyStrategy();
		}
	}
}
