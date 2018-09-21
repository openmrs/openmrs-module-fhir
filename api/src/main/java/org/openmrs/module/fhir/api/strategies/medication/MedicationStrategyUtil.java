package org.openmrs.module.fhir.api.strategies.medication;

import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.FHIRUtils;

public class MedicationStrategyUtil {

    public static GenericMedicationStrategy getMedicationStrategy() {
        String strategy = FHIRUtils.getMedicationStrategy();
        return strategy == null ? new MedicationStrategy() :
                Context.getRegisteredComponent(strategy, GenericMedicationStrategy.class);
    }
}
