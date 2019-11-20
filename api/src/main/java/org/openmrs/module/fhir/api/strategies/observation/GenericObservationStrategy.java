package org.openmrs.module.fhir.api.strategies.observation;

import org.hl7.fhir.dstu3.model.Observation;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface GenericObservationStrategy {

	Observation getObservation(String uuid);

	List<Observation> searchObservationByPatientAndConcept(String patientUuid, Map<String, String> conceptNamesAndURIs);

	List<Observation> searchObservationByUuid(String uuid);

	List<Observation> searchObservationsByCode(Map<String, String> conceptNamesAndURIs);

	List<Observation> searchObservationByDate(Date date);

	List<Observation> searchObservationByPerson(String personUuid);

	List<Observation> searchObservationByValueConcept(String conceptName);

	List<Observation> searchObservationByPatientIdentifier(String identifier);

	void deleteObservation(String uuid);

	Observation createFHIRObservation(Observation observation);

	Observation updateFHITObservation(Observation observation, String uuid);

	/**
	 * Search observations by patient and code with system uri
	 * @param patientUuid patient uuid
	 * @param codeAndSystem code and system to search in.
	 * @return List of fhir resources
	 */
	List<Observation> searchObservationByPatientAndCode(String patientUuid, Map<String, String> codeAndSystem);
}
