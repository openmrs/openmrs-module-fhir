package org.openmrs.module.fhir.providers;

import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.Delete;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Update;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.PlanDefinition;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.openmrs.module.fhir.resources.FHIRPlanDefinitionResource;
import org.openmrs.module.fhir.util.MethodOutcomeBuilder;

public class RestfulPlanDefinitionResourceProvider implements IResourceProvider {

	private FHIRPlanDefinitionResource planDefinitionResource;

	public RestfulPlanDefinitionResourceProvider() {
		this.planDefinitionResource = new FHIRPlanDefinitionResource();
	}

	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return PlanDefinition.class;
	}

	@Create
	public MethodOutcome createResource(@ResourceParam PlanDefinition planDefinitionRequest) {
		return MethodOutcomeBuilder.buildCreate(planDefinitionResource.createPlanDefinition(planDefinitionRequest));
	}

	@Read
	public PlanDefinition getResourceByUuid(@IdParam IdType uuid) {
		return planDefinitionResource.getPlanDefinitionByUuid(uuid.getIdPart());
	}

	@Update
	public MethodOutcome updateResource(@ResourceParam PlanDefinition planDefinitionRequest, @IdParam IdType uuid) {
		return MethodOutcomeBuilder
				.buildUpdate(planDefinitionResource.updatePlanDefinition(uuid.getIdPart(), planDefinitionRequest));
	}

	@Delete
	public void deleteResource(@IdParam IdType uuid) {
		planDefinitionResource.deletePlanDefinition(uuid.getIdPart());
	}
}
