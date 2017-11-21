package org.openmrs.module.fhir.api.strategies.familymemberhistory;

import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.FHIRUtils;

public class FamilyMemberHistoryStrategyUtil {

    public static GenericFamilyMemberHistoryStrategy getFamilyMemberStrategy() {
        String strategy = FHIRUtils.getFamilyMemberHistoryStrategy();

        return (strategy == null) ? new FamilyMemberHistoryHistoryStrategy() :
                Context.getRegisteredComponent(strategy, FamilyMemberHistoryHistoryStrategy.class);
    }
}
