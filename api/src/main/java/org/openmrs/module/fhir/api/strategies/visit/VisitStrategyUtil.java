package org.openmrs.module.fhir.api.strategies.visit;

import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.FHIRUtils;

public class VisitStrategyUtil {

    public static GenericVisitStrategy getVisitStrategy() {
        String strategy = FHIRUtils.getVisitStrategy();

        return strategy == null ? new VisitStrategy() : Context.getRegisteredComponent(strategy, GenericVisitStrategy.class);
    }

}
