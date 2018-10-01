package org.openmrs.module.fhir.api.strategies.plandefinition;

import ca.uhn.fhir.rest.server.exceptions.MethodNotAllowedException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.dstu3.model.PlanDefinition;
import org.openmrs.Program;
import org.openmrs.api.APIException;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.openmrs.module.fhir.api.util.FHIRPlanDefinitionUtil;
import org.openmrs.module.fhir.api.util.FHIRUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("DefaultPlanDefinitionStrategy")
public class PlanDefinitionStrategy implements GenericPlanDefinitionStrategy {

	@Override
	public PlanDefinition createPlanDefinition(PlanDefinition planDefinition) throws APIException {
		List<String> errors = new ArrayList<>();
		FHIRPlanDefinitionUtil.validatePlanDefinition(planDefinition, errors);
		FHIRUtils.checkGeneratorErrorList(errors);
		Program program = FHIRPlanDefinitionUtil.generateProgram(planDefinition);

		program = getProgramWorkflowService().saveProgram(program);
		return FHIRPlanDefinitionUtil.generatePlanDefinition(program);
	}

	@Override
	public PlanDefinition getPlanDefinitionByUuid(String uuid) throws APIException, ResourceNotFoundException {
		Program program = getProgramWorkflowService().getProgramByUuid(uuid);
		if (program == null) {
			throw new ResourceNotFoundException("PlanDefinition is not found for the given uuid " + uuid);
		}
		return FHIRPlanDefinitionUtil.generatePlanDefinition(program);
	}

	@Override
	public PlanDefinition updatePlanDefinition(String uuid, PlanDefinition planDefinition) throws APIException {
		List<String> errors = new ArrayList<String>();
		FHIRPlanDefinitionUtil.validatePlanDefinition(planDefinition, errors);
		FHIRUtils.checkGeneratorErrorList(errors);
		Program newProgram = FHIRPlanDefinitionUtil.generateProgram(planDefinition);

		Program program = getProgramWorkflowService().getProgramByUuid(uuid);
		if (program != null) {
			program = FHIRPlanDefinitionUtil.updateProgramAttributes(program, newProgram);
			program = getProgramWorkflowService().saveProgram(program);
		} else {
			newProgram.setUuid(uuid);
			program = getProgramWorkflowService().saveProgram(newProgram);
		}

		return FHIRPlanDefinitionUtil.generatePlanDefinition(program);
	}

	@Override
	public void deletePlanDefinition(String uuid) throws APIException, ResourceNotFoundException {
		Program program = getProgramWorkflowService().getProgramByUuid(uuid);

		if (program == null) {
			throw new ResourceNotFoundException(String.format("Program with id '%s' not found", uuid));
		}

		try {
			getProgramWorkflowService().retireProgram(program, FHIRConstants.FHIR_RETIRED_MESSAGE);
		} catch (APIException apie) {
			throw new MethodNotAllowedException(String.format("OpenMRS has failed to retire program '%s': %s", uuid,
					apie.getMessage()));
		}
	}

	private ProgramWorkflowService getProgramWorkflowService() {
		return Context.getProgramWorkflowService();
	}
}
