package org.openmrs.module.fhir.api;

import ca.uhn.fhir.model.dstu.resource.FamilyHistory;
import org.openmrs.api.OpenmrsService;

public interface FamilyHistoryService extends OpenmrsService {

	public FamilyHistory getFamilyHistory(String id);
}
