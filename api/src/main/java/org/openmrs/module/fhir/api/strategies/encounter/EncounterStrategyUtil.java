package org.openmrs.module.fhir.api.strategies.encounter;

import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.FHIRUtils;

public class EncounterStrategyUtil {

    public static GenericEncounterStrategy getEncounterStrategy() {
        String strategy = FHIRUtils.getEncounterStrategy();

        return strategy == null ? new EncounterStrategy() : Context.getRegisteredComponent(strategy, GenericEncounterStrategy.class);
    }

}
