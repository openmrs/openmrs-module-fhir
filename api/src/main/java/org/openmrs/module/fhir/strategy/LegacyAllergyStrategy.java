package org.openmrs.module.fhir.strategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by snkasthu on 10/17/14.
 */
public class LegacyAllergyStrategy implements AllergyStrategyInterface {

	@Override
	public List<String> getSupportedOpenMRSVersions() {
		List<String> supportedOpenMRSVersions = new ArrayList<String>();
		supportedOpenMRSVersions.add("1.9.0");

		return supportedOpenMRSVersions;
	}
}
