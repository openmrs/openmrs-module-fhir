package org.openmrs.module.fhir.api.strategies.procedurerequest;

import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.FHIRUtils;

public class ProcedureRequestStrategyUtil {

	public static GenericProcedureRequestStrategy getProcedureRequestStrategy() {
		String strategy = FHIRUtils.getProcedureRequestStrategy();
		return strategy == null ? new ProcedureRequestStrategy() :
				Context.getRegisteredComponent(strategy, GenericProcedureRequestStrategy.class);
	}
}
