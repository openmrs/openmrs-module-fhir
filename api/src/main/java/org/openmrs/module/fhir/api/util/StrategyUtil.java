package org.openmrs.module.fhir.api.util;

import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.AllergyIntoleranceService;
import org.openmrs.module.fhir.api.FHIRService;
import org.openmrs.module.fhir.strategy.OpenMRS20AllergyStrategy;
import org.openmrs.util.OpenmrsConstants;

public class StrategyUtil {

	public static void strategize() {

        AllergyIntoleranceService allergyIntoleranceService = Context.getService(AllergyIntoleranceService.class);
		//String omrsVersion = Context.getAdministrationService().getSystemInformation().get("SystemInfo
		// .OpenMRSInstallation.openmrsVersion");

		if (OpenmrsConstants.OPENMRS_VERSION_SHORT.equals("1.10.0")) {
            allergyIntoleranceService.setAllergyStrategy(new OpenMRS20AllergyStrategy());

		}
	}
}
