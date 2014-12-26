package org.openmrs.module.fhir.api.util;

import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.FHIRService;
import org.openmrs.module.fhir.strategy.OpenMRS20AllergyStrategy;
import org.openmrs.util.OpenmrsConstants;

public class StrategyUtil {

	public static void strategize() {

		FHIRService fhirService = Context.getService(FHIRService.class);
		//String omrsVersion = Context.getAdministrationService().getSystemInformation().get("SystemInfo
		// .OpenMRSInstallation.openmrsVersion");

		if (OpenmrsConstants.OPENMRS_VERSION_SHORT.equals("1.10.0")) {
			fhirService.setAllergyStrategy(new OpenMRS20AllergyStrategy());

		}
	}
}
