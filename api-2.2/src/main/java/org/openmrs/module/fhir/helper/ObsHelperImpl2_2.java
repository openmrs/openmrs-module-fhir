/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.fhir.helper;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Observation;
import org.openmrs.Obs;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.fhir.api.helper.ObsHelper;
import org.springframework.stereotype.Component;

@Component(value = "fhir.ObsHelper")
@OpenmrsProfile(openmrsPlatformVersion = "2.2.* - 2.4.*")
public class ObsHelperImpl2_2 implements ObsHelper {

	/**
	 * @see org.openmrs.module.fhir.api.helper.ObsHelper#getObsStatus(org.openmrs.Obs)
	 */
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
			throw new NoSuchMethodError(ex.getMessage());
		}
		return status;
	}

	/**
	 * @see org.openmrs.module.fhir.api.helper.ObsHelper#setStatus(org.openmrs.Obs, Observation.ObservationStatus)
	 */
	@Override
	public void setStatus(Obs obs, Observation.ObservationStatus status) {
		try {
			if (status != null) {
				obs.setStatus(Obs.Status.valueOf(status.name()));
			}
		}
		catch (NoSuchMethodError | NoClassDefFoundError ex) {
			//must be running below platform 2.1
			throw new NoSuchMethodError(ex.getMessage());
		}
	}

	/**
	 * @see org.openmrs.module.fhir.api.helper.ObsHelper#getInterpretation(org.openmrs.Obs)
	 */
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
			throw new NoSuchMethodError(ex.getMessage());
		}
		return interpretation;
	}

	/**
	 * @see org.openmrs.module.fhir.api.helper.ObsHelper#setInterpretation(org.openmrs.Obs, org.hl7.fhir.dstu3.model.CodeableConcept)
	 */
	@Override
	public void setInterpretation(Obs obs, CodeableConcept interpretation) {
		try {
			if (interpretation != null && StringUtils.isNotBlank(interpretation.getText())) {
				obs.setInterpretation(Obs.Interpretation.valueOf(interpretation.getText()));
			}
		}
		catch (NoSuchMethodError ex) {
			//must be running below platform 2.1
			throw new NoSuchMethodError(ex.getMessage());
		}
	}
}
