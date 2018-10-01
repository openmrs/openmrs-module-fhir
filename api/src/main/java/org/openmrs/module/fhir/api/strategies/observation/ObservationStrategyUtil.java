package org.openmrs.module.fhir.api.strategies.observation;

import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.FHIRUtils;

public class ObservationStrategyUtil {

	public static GenericObservationStrategy getObservationStrategy() {
		String strategy = FHIRUtils.getObservationStrategy();

		return strategy == null ? new ObservationStrategy() :
				Context.getRegisteredComponent(strategy, GenericObservationStrategy.class);
	}
}
