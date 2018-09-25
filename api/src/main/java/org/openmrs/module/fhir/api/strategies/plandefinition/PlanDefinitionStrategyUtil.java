package org.openmrs.module.fhir.api.strategies.plandefinition;

import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.FHIRUtils;

public class PlanDefinitionStrategyUtil {

	private PlanDefinitionStrategyUtil() {
	}

	public static GenericPlanDefinitionStrategy getPersonStrategy() {
		String strategy = FHIRUtils.getPlanDefinitionStrategy();

		return strategy == null ? new PlanDefinitionStrategy() : Context
				.getRegisteredComponent(strategy, GenericPlanDefinitionStrategy.class);
	}

	public static Program updateProgramAttributes(Program program, Program newProgram) {
		program.setName(newProgram.getName());
		program.setDescription(newProgram.getDescription());
		program.setConcept(newProgram.getConcept());
		program.setOutcomesConcept(newProgram.getOutcomesConcept());
		return program;
	}
}
