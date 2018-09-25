package org.openmrs.module.fhir.providers;

import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.Delete;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Update;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.OperationOutcome;
import org.hl7.fhir.dstu3.model.PlanDefinition;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.openmrs.module.fhir.api.util.FHIRUtils;
import org.openmrs.module.fhir.resources.FHIRPlanDefinitionResource;

public class RestfulPlanDefinitionResourceProvider implements IResourceProvider {

	private static final String SUCCESFULL_CREATE_MESSAGE = "PlanDefinition successfully created with id %s";
	private static final String SUCCESFULL_UPDATE_MESSAGE = "PlanDefinition successfully updated with id %s";

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
		PlanDefinition planDefinition = planDefinitionResource.createPlanDefinition(planDefinitionRequest);
		return createMethodOutcome(FHIRUtils.getObjectUuidByIdentifier(planDefinition.getIdentifierFirstRep())
				, SUCCESFULL_CREATE_MESSAGE);
	}

	@Read()
	public PlanDefinition getResourceByUuid(@IdParam IdType uuid) {
		String uuidValue = uuid.getIdPart();
		return planDefinitionResource.getPlanDefinitionByUuid(uuidValue);
	}

	@Update()
	public MethodOutcome updateResource(@ResourceParam PlanDefinition planDefinitionRequest, @IdParam IdType uuid) {
		String uuidValue = uuid.getIdPart();
		PlanDefinition planDefinition = planDefinitionResource.updatePlanDefinition(uuidValue, planDefinitionRequest);
		return createMethodOutcome(FHIRUtils.getObjectUuidByIdentifier(planDefinition.getIdentifierFirstRep())
				, SUCCESFULL_UPDATE_MESSAGE);
	}

	@Delete()
	public void deleteResource(@IdParam IdType uuid) {
		String uuidValue = uuid.getIdPart();
		planDefinitionResource.deletePlanDefinition(uuidValue);
	}

	private MethodOutcome createMethodOutcome(String resourceId, String messagePattern) {
		MethodOutcome retVal = new MethodOutcome();
		retVal.setId(new IdType(PlanDefinition.class.getSimpleName(), resourceId));
		OperationOutcome outcome = new OperationOutcome();
		CodeableConcept concept = new CodeableConcept();
		Coding coding = concept.addCoding();
		coding.setDisplay(String.format(messagePattern, resourceId));
		outcome.addIssue().setDetails(concept);
		retVal.setOperationOutcome(outcome);
		return retVal;
	}
}
