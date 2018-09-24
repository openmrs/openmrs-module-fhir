package org.openmrs.module.fhir.api.strategies.plandefinition;

import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.dstu3.model.PlanDefinition;
import org.openmrs.api.APIException;

public interface GenericPlanDefinitionStrategy {

	PlanDefinition createPlanDefinition(PlanDefinition planDefinition) throws APIException;

	PlanDefinition getPlanDefinitionByUuid(String uuid) throws APIException;

	PlanDefinition updatePlanDefinition(String uuid, PlanDefinition planDefinition) throws APIException;

	void deletePlanDefinition(String uuid) throws APIException, ResourceNotFoundException;
}
