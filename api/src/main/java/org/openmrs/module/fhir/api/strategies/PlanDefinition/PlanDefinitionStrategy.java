package org.openmrs.module.fhir.api.strategies.PlanDefinition;

import org.hl7.fhir.dstu3.model.PlanDefinition;
import org.springframework.stereotype.Component;

@Component("DefaultPlanDefinitionStrategy")
public class PlanDefinitionStrategy implements GenericPlanDefinitionStrategy {

	@Override
	public PlanDefinition createPlanDefinition(PlanDefinition person) {
		return null;
	}

	@Override
	public PlanDefinition getPlanDefinitionByUuid(String uuid) {
		return null;
	}

	@Override
	public PlanDefinition updatePlanDefinition(String uuid, PlanDefinition person) {
		return null;
	}

	@Override
	public void deletePlanDefinition(String uuid) {

	}
}
