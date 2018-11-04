package org.openmrs.module.fhir.api.strategies.medicationrequest;

import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.FHIRUtils;

public class MedicationRequestStrategyUtil {

	public static GenericMedicationRequestStrategy getMedicationRequestStrategy() {
		String strategy = FHIRUtils.getMedicationRequestStrategy();
		return strategy == null ? new MedicationRequestStrategy() :
				Context.getRegisteredComponent(strategy, GenericMedicationRequestStrategy.class);
	}
}
