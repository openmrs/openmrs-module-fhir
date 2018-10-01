package org.openmrs.module.fhir.api.strategies.location;

import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.FHIRUtils;

public class LocationStrategyUtil {

	public static GenericLocationStrategy getLocationStrategy() {
		String strategy = FHIRUtils.getLocationStrategy();

		return (strategy == null) ? new LocationStrategy() :
				Context.getRegisteredComponent(strategy, GenericLocationStrategy.class);
	}
}
