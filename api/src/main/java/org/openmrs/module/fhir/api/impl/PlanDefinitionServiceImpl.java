package org.openmrs.module.fhir.api.impl;

import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.dstu3.model.PlanDefinition;
import org.openmrs.api.APIException;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.fhir.api.PlanDefinitionService;
import org.openmrs.module.fhir.api.strategies.plandefinition.PlanDefinitionStrategyUtil;

public class PlanDefinitionServiceImpl extends BaseOpenmrsService implements PlanDefinitionService {

	@Override
	public PlanDefinition createPlanDefinition(PlanDefinition planDefinition) throws APIException {
		return PlanDefinitionStrategyUtil.getPlanDefinitionStrategy().createPlanDefinition(planDefinition);
	}

	@Override
	public PlanDefinition getPlanDefinitionByUuid(String uuid) throws APIException,ResourceNotFoundException {
		return PlanDefinitionStrategyUtil.getPlanDefinitionStrategy().getPlanDefinitionByUuid(uuid);
	}

	@Override
	public PlanDefinition updatePlanDefinition(String uuid, PlanDefinition planDefinition) throws APIException {
		return PlanDefinitionStrategyUtil.getPlanDefinitionStrategy().updatePlanDefinition(uuid, planDefinition);
	}

	@Override
	public void deletePlanDefinition(String uuid) throws APIException, ResourceNotFoundException {
		PlanDefinitionStrategyUtil.getPlanDefinitionStrategy().deletePlanDefinition(uuid);
	}
}
