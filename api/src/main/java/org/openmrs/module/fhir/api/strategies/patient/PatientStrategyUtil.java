package org.openmrs.module.fhir.api.strategies.patient;

import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.FHIRUtils;

public class PatientStrategyUtil {

    public static GenericPatientStrategy getPatientStrategy() {
        String strategy = FHIRUtils.getPatientStrategy();

        return strategy == null ? new PatientStrategy() : Context.getRegisteredComponent(strategy, GenericPatientStrategy.class);
    }

}
