package org.openmrs.module.fhir.api;

import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.dstu3.model.PlanDefinition;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.mother.ProblemMother;
import org.openmrs.module.fhir.api.util.FHIRPlanDefinitionUtil;
import org.openmrs.module.fhir.api.util.FHIRUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.openmrs.module.fhir.api.mother.ProblemMother.INCORRECT_UUID;
import static org.openmrs.module.fhir.api.mother.ProblemMother.NEW_UUID;
import static org.openmrs.module.fhir.api.mother.ProblemMother.UUID;

public class PlanDefinitionServiceTest extends BaseModuleContextSensitiveTest {

	private static final String INITIAL_DATA_XML = "Program_testDataset.xml";

	public PlanDefinitionService getService() {
		return Context.getService(PlanDefinitionService.class);
	}

	@Before
	public void runBeforeEachTest() throws Exception {
		executeDataSet(INITIAL_DATA_XML);
	}

	@Test
	public void getPlanDefinition_shouldReturnResourceIfExists() throws Exception {
		PlanDefinition planDefinition = getService().getPlanDefinitionByUuid(UUID);
		assertNotNull(planDefinition);
		assertEquals(FHIRUtils.getObjectUuidByIdentifier(planDefinition.getIdentifierFirstRep()), UUID);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void getPlanDefinition_shouldThrowResourceNotFoundExceptionIfNotExists() throws Exception {
		getService().getPlanDefinitionByUuid(INCORRECT_UUID);
	}

	@Test
	public void deletePlanDefinition_shouldDeletePlanDefinition() throws Exception {
		getService().deletePlanDefinition(UUID);
		Program planDefinition = Context.getProgramWorkflowService().getProgramByUuid(UUID);
		assertTrue(planDefinition.isRetired());
	}

	@Test(expected = ResourceNotFoundException.class)
	public void deletePlanDefinition_shouldThrowResourceNotFoundExceptionIfNotExists() throws Exception {
		getService().deletePlanDefinition(INCORRECT_UUID);
	}

	@Test
	public void createPlanDefinition() throws Exception {
		Program program = ProblemMother.createValidInstanceWithoutUuid();
		PlanDefinition planDefinition = FHIRPlanDefinitionUtil.generatePlanDefinition(program);
		planDefinition = getService().createPlanDefinition(planDefinition);

		assertNotNull(planDefinition);

		Program result = FHIRPlanDefinitionUtil.generateProgram(planDefinition);
		assertNotNull(result.getUuid());
		assertEquals(program.getName(), result.getName());
		assertEquals(program.getDescription(), result.getDescription());
		assertEquals(program.getConcept().getConceptId(), result.getConcept().getConceptId());
		assertEquals(program.getOutcomesConcept().getConceptId(), result.getOutcomesConcept().getConceptId());
	}

	@Test
	public void updatePlanDefinition_shouldUpdateIfExists() throws Exception {
		Program program = ProblemMother.getProgramFromDb(UUID);
		program.setName("Changed program name");
		PlanDefinition planDefinition = FHIRPlanDefinitionUtil.generatePlanDefinition(program);
		getService().updatePlanDefinition(UUID, planDefinition);

		Program result = ProblemMother.getProgramFromDb(UUID);
		assertEquals(program.getName(), result.getName());
		assertEquals(program.getUuid(), result.getUuid());
		assertEquals(program.getId(), result.getId());
	}

	@Test
	public void updatePlanDefinition_shouldCreateIfNotExists() throws Exception {
		Program program = ProblemMother.createValidInstanceWithoutUuid();
		PlanDefinition planDefinition = FHIRPlanDefinitionUtil.generatePlanDefinition(program);

		assertNull(ProblemMother.getProgramFromDb(NEW_UUID));
		getService().updatePlanDefinition(NEW_UUID, planDefinition);

		Program result = ProblemMother.getProgramFromDb(NEW_UUID);
		assertNotNull(result);
		assertEquals(program.getName(), result.getName());
		assertEquals(program.getDescription(), result.getDescription());
		assertEquals(program.getConcept().getConceptId(), result.getConcept().getConceptId());
		assertEquals(program.getOutcomesConcept().getConceptId(), result.getOutcomesConcept().getConceptId());
	}
}
