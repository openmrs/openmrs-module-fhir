package org.openmrs.module.fhir.helper;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Observation;
import org.openmrs.Obs;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.fhir.api.helper.ObsHelper;
import org.springframework.stereotype.Component;

@Component(value = "fhir.ObsHelper")
@OpenmrsProfile(openmrsPlatformVersion = "2.0.* - 2.1.*")
public class ObsHelperImpl2_0 implements ObsHelper {

	@Override
	public Observation.ObservationStatus getObsStatus(Obs obs) {
		Observation.ObservationStatus status = Observation.ObservationStatus.FINAL;
		try {
			Obs.Status stat = obs.getStatus();
			if (stat != null) {
				status = Observation.ObservationStatus.valueOf(stat.name());
			}
		}
		catch (NoSuchMethodError ex) {
			//must be running below platform 2.1
		}
		return status;
	}

	@Override
	public void setStatus(Obs obs, Observation.ObservationStatus status) {
		try {
			if (status != null) {
				obs.setStatus(Obs.Status.valueOf(status.name()));
			}
		}
		catch (NoSuchMethodError ex) {
			//must be running below platform 2.1
		}
	}

	@Override
	public CodeableConcept getInterpretation(Obs obs) {
		CodeableConcept interpretation = null;
		try {
			Obs.Interpretation interpret = obs.getInterpretation();
			if (interpret != null) {
				interpretation = new CodeableConcept();
				interpretation.setText(interpret.name());
			}
		}
		catch (NoSuchMethodError ex) {
			//must be running below platform 2.1
		}
		return interpretation;
	}

	@Override
	public void setInterpretation(Obs obs, CodeableConcept interpretation) {
		try {
			if (interpretation != null && StringUtils.isNotBlank(interpretation.getText())) {
				obs.setInterpretation(Obs.Interpretation.valueOf(interpretation.getText()));
			}
		}
		catch (NoSuchMethodError ex) {
			//must be running below platform 2.1
		}
	}
}
