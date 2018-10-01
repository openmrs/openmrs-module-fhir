package org.openmrs.module.fhir.api;

import org.hl7.fhir.dstu3.model.RelatedPerson;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.openmrs.module.fhir.api.util.FHIRRelatedPersonUtil;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class RelatedPersonServiceTest extends BaseModuleContextSensitiveTest {

	private static final String LOC_INITIAL_DATA_XML = "RelatedPersonServiceTest_additionalTestData.xml";

	private static final String UUID = "ee232368-cf80-4a22-9d75-5d0d9dce";

	private static final String INCORRECT_UUID = "XXXXXXXX-XXXX-XXXX-XXXXXXXXXXXXXX";

	public RelatedPersonService getService() {
		return Context.getService(RelatedPersonService.class);
	}

	@Before
	public void runBeforeEachTest() throws Exception {
		executeDataSet(LOC_INITIAL_DATA_XML);
	}

	@Test
	public void getRelatedPerson_shouldReturnResourceIfExists() {
		RelatedPerson relatedPerson = getService().getRelatedPerson(UUID);
		assertNotNull(relatedPerson);
		assertEquals(relatedPerson.getId().toString(), UUID);
	}

	@Test
	public void getRelatedPerson_shouldReturnNullIfNotExists() {
		RelatedPerson relatedPerson = getService().getRelatedPerson(INCORRECT_UUID);
		assertNull(relatedPerson);
	}

	@Test
	public void deleteRelatedPerson_shouldDeleteTheSpecifiedRelatedPerson() {
		org.openmrs.api.PersonService personService = Context.getPersonService();
		org.openmrs.Relationship relationship = personService.getRelationshipByUuid(UUID);
		assertNotNull(relationship);

		getService().deleteRelatedPerson(UUID);
		relationship = personService.getRelationshipByUuid(UUID);
		assertTrue(relationship.isVoided());

		RelatedPerson relatedPerson = getService().getRelatedPerson(UUID);
		assertNull(relatedPerson);
	}

	@Test
	public void createRelatedPerson() throws Exception {
		org.openmrs.api.PersonService personService = Context.getPersonService();
		org.openmrs.Relationship relationship = personService.getRelationshipByUuid(UUID);

		relationship.setUuid(null);
		RelatedPerson relatedPerson = FHIRRelatedPersonUtil.generateRelationshipObject(relationship);

		relatedPerson = getService().createRelatedPerson(relatedPerson);

		assertNotNull(relatedPerson);
		org.openmrs.Relationship createdRelationship = personService.getRelationshipByUuid(relatedPerson.getId());

		assertEquals(relationship.getPersonA(), createdRelationship.getPersonA());
		assertEquals(relationship.getPersonB(), createdRelationship.getPersonB());
		assertEquals(relationship.getRelationshipType(), createdRelationship.getRelationshipType());
		assertEquals(relationship.getStartDate(), createdRelationship.getStartDate());
		assertEquals(relationship.getEndDate(), createdRelationship.getEndDate());
	}

	@Test
	public void updateRelatedPerson_shouldUpdateIfExists() throws Exception {
		org.openmrs.api.PersonService personService = Context.getPersonService();
		org.openmrs.Relationship relationship = personService.getRelationshipByUuid(UUID);

		// swap the direction of relationship
		org.openmrs.Person person = relationship.getPersonA();
		relationship.setPersonA(relationship.getPersonB());
		relationship.setPersonB(person);

		RelatedPerson relatedPerson = FHIRRelatedPersonUtil.generateRelationshipObject(relationship);
		relatedPerson = getService().updateRelatedPerson(UUID, relatedPerson);

		List<String> errors = new ArrayList<String>();
		org.openmrs.Relationship updatedRelationship =
				FHIRRelatedPersonUtil.generateOmrsRelationshipObject(relatedPerson, errors);
		assertEquals(0, errors.size());
		assertEquals(relationship.getPersonA(), updatedRelationship.getPersonA());
		assertEquals(relationship.getPersonB(), updatedRelationship.getPersonB());
	}

	@Test
	public void updateRelatedPerson_shouldCreateIfNotExists() throws Exception {
		org.openmrs.api.PersonService personService = Context.getPersonService();
		org.openmrs.Relationship relationship = personService.getRelationshipByUuid(UUID);

		relationship.setUuid(null);

		RelatedPerson relatedPerson = FHIRRelatedPersonUtil.generateRelationshipObject(relationship);

		personService.voidRelationship(relationship, FHIRConstants.FHIR_VOIDED_MESSAGE);
		relatedPerson = getService().updateRelatedPerson(INCORRECT_UUID, relatedPerson);

		List<String> errors = new ArrayList<String>();
		org.openmrs.Relationship updatedRelationship =
				FHIRRelatedPersonUtil.generateOmrsRelationshipObject(relatedPerson, errors);
		assertEquals(0, errors.size());
		assertEquals(relationship.getPersonA(), updatedRelationship.getPersonA());
		assertEquals(relationship.getPersonB(), updatedRelationship.getPersonB());
	}
}
