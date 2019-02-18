package org.openmrs.module.fhir.api.validator;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Handler(supports = { Obs.class }, order = 100)
public class SpecificObsValidator implements Validator {

	@Override
	public boolean supports(Class<?> aClass) {
		return Obs.class.isAssignableFrom(aClass);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		Obs obs = (Obs) obj;
		if (StringUtils.isNotBlank(obs.getValueText()) &&
				obs.getValueText().equalsIgnoreCase(FHIRConstants.OBS_GROUP_MEMBER_TEXT_VALUE)) {
			//TODO Used to correctly save obs when the parent obs of group is save as first, see FHIRObsUtil.buildObsGroup
			obs.setValueText(null);
		}
	}
}
