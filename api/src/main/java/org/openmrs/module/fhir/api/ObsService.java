package org.openmrs.module.fhir.api;

import ca.uhn.fhir.model.dstu.resource.Observation;
import org.openmrs.Obs;
import org.openmrs.api.OpenmrsService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface ObsService extends OpenmrsService {

	public Observation getObs(String id);

	public List<Obs> getObsByPatientandConcept(String patientUUid, String[] concepts);

}
