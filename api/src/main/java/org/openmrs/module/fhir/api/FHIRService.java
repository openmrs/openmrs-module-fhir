package org.openmrs.module.fhir.api;

import org.openmrs.api.OpenmrsService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface FHIRService extends OpenmrsService {

    public void setStrategy(Strategy strategy);

    public Strategy getStrategy();

}
