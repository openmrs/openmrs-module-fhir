package org.openmrs.module.fhir.helper;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Observation;
import org.openmrs.Obs;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.fhir.api.helper.ObsHelper;
import org.springframework.stereotype.Component;

@Component(value = "fhir.ObsHelper")
@OpenmrsProfile(openmrsPlatformVersion = "1.9.*")
public class ObsHelperImpl1_9 implements ObsHelper {

	@Override
	public Observation.ObservationStatus getObsStatus(Obs obs) {
		Observation.ObservationStatus status = Observation.ObservationStatus.FINAL;
		return status;
	}

	@Override
	public void setStatus(Obs obs, Observation.ObservationStatus status) {
		//The obs status field was introduced in the OpenMRS 2.1.0
	}

	@Override
	public CodeableConcept getInterpretation(Obs obs) {
		//The obs interpretation field was introduced in the OpenMRS 2.1.0
		return null;
	}

	@Override
	public void setInterpretation(Obs obs, CodeableConcept interpretation) {
		//The obs interpretation field was introduced in the OpenMRS 2.1.0
	}
}
