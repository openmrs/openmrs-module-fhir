package org.openmrs.module.fhir.api.strategies.person;

import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.openmrs.module.fhir.api.util.FHIRUtils;

public class PersonStrategyUtil {
    public static GenericPersonStrategy getPersonStrategy() {
        String strategy = FHIRUtils.getPersonStrategy();

        if (FHIRConstants.PERSON_STRATEGY.equals(strategy) || strategy == null) {
            return new PersonStrategy();
        } else {
            return Context.getRegisteredComponent(strategy, GenericPersonStrategy.class);
        }
    }
}
