package org.openmrs.module.fhir.api.mother;

import org.openmrs.Program;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;

public abstract class ProblemMother {

	public static final String UUID = "644eef7d-087a-48ac-80b5-9f5a9db1a0a6";

	public static final String NEW_UUID = "644eef7d-087a-48ac-80b5-9f5a9db1a0a8";

	public static final String INCORRECT_UUID = "XXXXXXXX-XXXX-XXXX-XXXXXXXXXXXXXX";

	public static Program createValidInstanceWithoutUuid() {
		ConceptService conceptService = Context.getConceptService();
		Program program = new Program();
		program.setName(String.valueOf(java.util.UUID.randomUUID()));
		program.setConcept(conceptService.getConcept(138405));
		program.setOutcomesConcept(conceptService.getConcept(5340));
		return program;
	}

	public static Program getProgramFromDb(String uuid) {
		return Context.getProgramWorkflowService().getProgramByUuid(uuid);
	}
}
