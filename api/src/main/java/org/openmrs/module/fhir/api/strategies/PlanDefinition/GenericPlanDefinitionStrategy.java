package org.openmrs.module.fhir.api.strategies.PlanDefinition;

import org.hl7.fhir.dstu3.model.PlanDefinition;

public interface GenericPlanDefinitionStrategy {

	PlanDefinition createPlanDefinition(PlanDefinition person);

	PlanDefinition getPlanDefinitionByUuid(String uuid);

	PlanDefinition updatePlanDefinition(String uuid, PlanDefinition person);

	void deletePlanDefinition(String uuid);
}
