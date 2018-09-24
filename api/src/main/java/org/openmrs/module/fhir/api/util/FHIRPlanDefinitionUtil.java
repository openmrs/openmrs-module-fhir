package org.openmrs.module.fhir.api.util;

import org.apache.commons.lang.StringUtils;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.PlanDefinition;
import org.openmrs.Program;

import java.util.List;

public class FHIRPlanDefinitionUtil {

	private FHIRPlanDefinitionUtil() {
	}

	public static PlanDefinition generatePlanDefinition(Program program) {
		PlanDefinition planDefinition = new PlanDefinition();
		planDefinition.setId(program.getUuid());
		planDefinition.addIdentifier(FHIRUtils.createIdentifier(program.getUuid()));

		PlanDefinition.PlanDefinitionGoalComponent goalComponent = new PlanDefinition.PlanDefinitionGoalComponent();
		goalComponent.setDescription(FHIRUtils.createCodeableConcept(program.getConcept()));
		goalComponent.addTarget().setDetail(FHIRUtils.createCodeableConcept(program.getOutcomesConcept()));
		planDefinition.addGoal(goalComponent);

		planDefinition.setName(program.getName());
		planDefinition.setDescription(program.getDescription());

		return planDefinition;
	}

	public static Program generateProgram(PlanDefinition planDefinition) {
		Program program = new Program();
		program.setUuid(FHIRUtils.getObjectUuidByIdentifier(planDefinition.getIdentifierFirstRep()));

		PlanDefinition.PlanDefinitionGoalComponent goalComponent = planDefinition.getGoalFirstRep();
		program.setConcept(FHIRUtils.getConceptByCodeableConcept(goalComponent.getDescription()));
		program.setOutcomesConcept(FHIRUtils.getConceptByCodeableConcept(
				(CodeableConcept) goalComponent.getTargetFirstRep().getDetail()));

		program.setName(planDefinition.getName());
		program.setDescription(planDefinition.getDescription());

		return program;
	}

	public static void validatePlanDefinition(PlanDefinition planDefinition, List<String> errors) {
		Identifier identifier = planDefinition.getIdentifierFirstRep();
		if (identifier == null || StringUtils.isBlank(FHIRUtils.getObjectUuidByIdentifier(identifier))) {
			errors.add("Missing plandefinition identifier");
		}

		PlanDefinition.PlanDefinitionGoalComponent goalComponent = planDefinition.getGoalFirstRep();
		validateProgramDefinitionGoalComponent(goalComponent, errors);

		if (StringUtils.isBlank(planDefinition.getName())) {
			errors.add("Missing plandefinition name");
		}

		if (StringUtils.isBlank(planDefinition.getDescription())) {
			errors.add("Missing plandefinition description");
		}
	}

	private static void validateProgramDefinitionGoalComponent(PlanDefinition.PlanDefinitionGoalComponent goalComponent,
			List<String> errors) {

		if (goalComponent == null) {
			errors.add("Missing plandefinition goal component");
		} else if (null == goalComponent.getDescription()) {
			errors.add("Missing plandefinition goal description");
		} else if (null == goalComponent.getTargetFirstRep() ||
				null == goalComponent.getTargetFirstRep().getDetail()) {
			errors.add("Missing plandefinition concept");
		}

	}
}
