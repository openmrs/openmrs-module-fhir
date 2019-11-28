package org.openmrs.module.fhir.api.strategies.observation;

import ca.uhn.fhir.rest.param.TokenParam;
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
	 * Search observations by patient and codings (e.g. PIH|5089,CIEL|5086)
	 *
	 * @param patientUuid patient uuid
	 * @param codings     List of TokenParam, for example PIH|5089,CIEL|5086
	 * @return List of fhir observation resources
	 */
	List<Observation> searchObservationByPatientAndCode(String patientUuid, List<TokenParam> codings);
}
