package org.openmrs.module.fhir.api.helper;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Observation;
import org.openmrs.Obs;

public interface ObsHelper {

	Observation.ObservationStatus getObsStatus(Obs obs);

	void setStatus(Obs obs, Observation.ObservationStatus status);

	CodeableConcept getInterpretation(Obs obs);

	void setInterpretation(Obs obs, CodeableConcept interpretation);
}
