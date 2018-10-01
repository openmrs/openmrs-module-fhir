package org.openmrs.module.fhir.api.strategies.practitioner;

import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.FHIRUtils;

public class PractitionerStrategyUtil {

	public static PractitionerStrategy getPractitionerStrategy() {
		String strategy = FHIRUtils.getPractitionerStrategy();

		return (strategy == null) ? new PractitionerStrategy() :
				Context.getRegisteredComponent(strategy, PractitionerStrategy.class);
	}
}
