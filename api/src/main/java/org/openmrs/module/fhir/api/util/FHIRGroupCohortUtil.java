package org.openmrs.module.fhir.api.util;

import org.hl7.fhir.dstu3.model.Group;
import org.openmrs.Cohort;

public class FHIRGroupCohortUtil {

    public static Group generateGroup(Cohort cohort) {
        return new Group();
    }
}
