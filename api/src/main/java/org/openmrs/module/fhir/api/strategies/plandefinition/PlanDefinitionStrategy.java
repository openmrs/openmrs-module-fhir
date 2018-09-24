package org.openmrs.module.fhir.api.strategies.plandefinition;

import ca.uhn.fhir.rest.server.exceptions.MethodNotAllowedException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.dstu3.model.PlanDefinition;
import org.openmrs.Program;
import org.openmrs.api.APIException;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.openmrs.module.fhir.api.util.FHIRPlanDefinitionUtil;
import org.openmrs.module.fhir.api.util.FHIRUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("DefaultPlanDefinitionStrategy")
public class PlanDefinitionStrategy implements GenericPlanDefinitionStrategy {

	@Autowired
	private ProgramWorkflowService programWorkflowService;

	@Override
	public PlanDefinition createPlanDefinition(PlanDefinition planDefinition) throws APIException {
		List<String> errors = new ArrayList();
		FHIRPlanDefinitionUtil.validatePlanDefinition(planDefinition, errors);
		FHIRUtils.checkGeneratorErrorList(errors);
		Program program = FHIRPlanDefinitionUtil.generateProgram(planDefinition);

		program = programWorkflowService.saveProgram(program);
		return FHIRPlanDefinitionUtil.generatePlanDefinition(program);
	}

	@Override
	public PlanDefinition getPlanDefinitionByUuid(String uuid) throws APIException {
		Program program = programWorkflowService.getProgramByUuid(uuid);
		return FHIRPlanDefinitionUtil.generatePlanDefinition(program);
	}

	@Override
	public PlanDefinition updatePlanDefinition(String uuid, PlanDefinition planDefinition) throws APIException {
		List<String> errors = new ArrayList<String>();
		FHIRPlanDefinitionUtil.validatePlanDefinition(planDefinition, errors);
		FHIRUtils.checkGeneratorErrorList(errors);
		Program newProgram = FHIRPlanDefinitionUtil.generateProgram(planDefinition);

		Program program = programWorkflowService.getProgramByUuid(uuid);
		if (program != null) {
			program = PlanDefinitionStrategyUtil.updateProgramAttributes(program, newProgram);
			program = programWorkflowService.saveProgram(program);
		} else {
			newProgram.setUuid(uuid);
			program = programWorkflowService.saveProgram(newProgram);
		}

		return FHIRPlanDefinitionUtil.generatePlanDefinition(program);
	}

	@Override
	public void deletePlanDefinition(String uuid) throws APIException, ResourceNotFoundException {
		Program program = programWorkflowService.getProgramByUuid(uuid);

		if (program == null) {
			throw new ResourceNotFoundException(String.format("Program with id '%s' not found", uuid));
		}

		try {
			programWorkflowService.retireProgram(program, FHIRConstants.FHIR_RETIRED_MESSAGE);
		} catch (APIException apie) {
			throw new MethodNotAllowedException(String.format("OpenMRS has failed to retire program '%s': %s", uuid,
					apie.getMessage()));
		}
	}
}
