package org.openmrs.module.fhir.api.strategies.group;

import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.FHIRUtils;

public class GroupStrategyUtil {

    public static GenericGroupStrategy getGroupStrategy() {
        String strategy = FHIRUtils.getGroupStrategy();

        return strategy == null ? new GroupApiStrategy()
                : Context.getRegisteredComponent(strategy, GenericGroupStrategy.class);
    }
}
