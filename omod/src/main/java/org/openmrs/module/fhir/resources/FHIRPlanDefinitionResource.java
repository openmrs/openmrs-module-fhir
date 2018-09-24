package org.openmrs.module.fhir.resources;

import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.dstu3.model.PlanDefinition;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.PlanDefinitionService;

public class FHIRPlanDefinitionResource extends Resource {

	public PlanDefinition createPlanDefinition(PlanDefinition planDefinition) throws APIException {
		return getService().createPlanDefinition(planDefinition);
	}

	public PlanDefinition getPlanDefinitionByUuid(String uuid) throws APIException, ResourceNotFoundException {
		return getService().getPlanDefinitionByUuid(uuid);
	}

	public PlanDefinition updatePlanDefinition(String uuid, PlanDefinition planDefinition) throws APIException {
		return getService().updatePlanDefinition(uuid, planDefinition);
	}

	public void deletePlanDefinition(String uuid) throws APIException, ResourceNotFoundException {
		getService().deletePlanDefinition(uuid);
	}

	private PlanDefinitionService getService() {
		return Context.getService(PlanDefinitionService.class);
	}

}
