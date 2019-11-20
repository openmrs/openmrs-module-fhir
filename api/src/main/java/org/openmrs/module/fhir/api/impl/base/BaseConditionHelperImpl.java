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
package org.openmrs.module.fhir.api.impl.base;

import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import org.apache.commons.lang.ArrayUtils;
import org.hl7.fhir.dstu3.model.Condition;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.FHIRConditionUtil;
import org.openmrs.module.fhir.api.util.FHIRUtils;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

/**
 * @see org.openmrs.module.fhir.api.helper.ConditionHelper
 * @since 1.20.0
 */

public class BaseConditionHelperImpl {

	/**
	 * @see org.openmrs.module.fhir.api.helper.ConditionHelper#getConditionByUuid(java.lang.String)
	 */
	public Condition getConditionByUuid(String uuid) {
		int[] conceptsAsConditions = FHIRUtils.getConceptIdsOfConditions();
		Obs obs = Context.getObsService().getObsByUuid(uuid);
		if (obs == null || obs.isVoided() || conceptsAsConditions == null || !ArrayUtils.contains
				(conceptsAsConditions, obs.getConcept().getId())) {
			return null;
		}

		return FHIRConditionUtil.generateFHIRConditionFromOpenMRSObs(obs);
	}

	/**
	 * @see org.openmrs.module.fhir.api.helper.ConditionHelper#createCondition(org.hl7.fhir.dstu3.model.Condition)
	 */
	public Condition createCondition(Condition condition) {
		if (condition.isEmpty()) {
			throw new UnprocessableEntityException("condition resource cannot be null");
		}
		if (condition.getSubject().isEmpty()) {
			throw new UnprocessableEntityException("condition resource cannot be null");
		}
		Obs obs = Context.getObsService()
				.saveObs(FHIRConditionUtil.generateOpenMrsObsFromFHIRCondition(condition), "");

		return FHIRConditionUtil.generateFHIRConditionFromOpenMRSObs(obs);
	}

	/**
	 * @see org.openmrs.module.fhir.api.helper.ConditionHelper#updateFHIRCondition(org.hl7.fhir.dstu3.model.Condition)
	 */
	public Condition updateFHIRCondition(Condition condition) {
		throw new NotImplementedException();
	}

	/**
	 * @see org.openmrs.module.fhir.api.helper.ConditionHelper#getConditionsByPatientUuid(java.lang.String)
	 */
	public List<Condition> getConditionsByPatientUuid(String patientUuid) {
		throw new NotImplementedException();
	}

	/**
	 * @see org.openmrs.module.fhir.api.helper.ConditionHelper#generateOpenMrsCondition(org.hl7.fhir.dstu3.model.Condition)
	 */
	public Object generateOpenMrsCondition(Condition condition) {
		throw new NotImplementedException();
	}
}