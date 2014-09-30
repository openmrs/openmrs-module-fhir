package org.openmrs.module.fhir.api;

import ca.uhn.fhir.model.dstu.resource.FamilyHistory;
import ca.uhn.fhir.model.dstu.resource.Observation;
import org.openmrs.api.OpenmrsService;

/**
 * Created by snkasthu on 9/29/14.
 */
public interface FamilyHistoryService extends OpenmrsService {

    public FamilyHistory getFamilyHistory(String id);
}
