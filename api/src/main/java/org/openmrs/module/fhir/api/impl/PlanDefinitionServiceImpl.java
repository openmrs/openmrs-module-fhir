package org.openmrs.module.fhir.api.impl;

import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.dstu3.model.PlanDefinition;
import org.openmrs.api.APIException;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.fhir.api.PlanDefinitionService;
import org.openmrs.module.fhir.api.strategies.plandefinition.PlanDefinitionStrategyUtil;
import org.springframework.stereotype.Service;

@Service
public class PlanDefinitionServiceImpl extends BaseOpenmrsService implements PlanDefinitionService {

	@Override
	public PlanDefinition createPlanDefinition(PlanDefinition planDefinition) throws APIException {
		return PlanDefinitionStrategyUtil.getPersonStrategy().createPlanDefinition(planDefinition);
	}

	@Override
	public PlanDefinition getPlanDefinitionByUuid(String uuid) throws APIException {
		return PlanDefinitionStrategyUtil.getPersonStrategy().getPlanDefinitionByUuid(uuid);
	}

	@Override
	public PlanDefinition updatePlanDefinition(String uuid, PlanDefinition planDefinition) throws APIException {
		return PlanDefinitionStrategyUtil.getPersonStrategy().updatePlanDefinition(uuid, planDefinition);
	}

	@Override
	public void deletePlanDefinition(String uuid) throws APIException, ResourceNotFoundException {
		PlanDefinitionStrategyUtil.getPersonStrategy().deletePlanDefinition(uuid);
	}
}
