package org.openmrs.module.fhir.api;

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.fhir.strategy.AllergyStrategyInterface;
import org.openmrs.module.fhir.strategy.Strategy;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface FHIRService extends OpenmrsService {

	public void setAllergyStrategy(AllergyStrategyInterface strategy);

	public Strategy getAllergyStrategy();

}
