package org.openmrs.module.fhir.api.strategies.plandefinition;

import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.FHIRUtils;

public class PlanDefinitionStrategyUtil {

	private PlanDefinitionStrategyUtil() {
	}

	public static GenericPlanDefinitionStrategy getPlanDefinitionStrategy() {
		String strategy = FHIRUtils.getPlanDefinitionStrategy();

		return strategy == null ? new PlanDefinitionStrategy() : Context
				.getRegisteredComponent(strategy, GenericPlanDefinitionStrategy.class);
	}
}
