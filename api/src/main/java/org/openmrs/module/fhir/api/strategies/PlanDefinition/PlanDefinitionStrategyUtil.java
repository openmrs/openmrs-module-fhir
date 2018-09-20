package org.openmrs.module.fhir.api.strategies.PlanDefinition;

import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.FHIRUtils;

public class PlanDefinitionStrategyUtil {

	private PlanDefinitionStrategyUtil() {
	}

	public static GenericPlanDefinitionStrategy getPersonStrategy() {
		String strategy = FHIRUtils.getPlanDefinitionSrategy();

		return strategy == null ? new PlanDefinitionStrategy() : Context
				.getRegisteredComponent(strategy, GenericPlanDefinitionStrategy.class);
	}
}
