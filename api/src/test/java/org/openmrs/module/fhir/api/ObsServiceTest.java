/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.fhir.api;

import ca.uhn.fhir.model.dstu2.resource.Observation;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptMap;
import org.openmrs.GlobalProperty;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.FHIRObsUtil;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.AssertTrue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ObsServiceTest extends BaseModuleContextSensitiveTest {

	protected static final String OBS_INITIAL_DATA_XML = "org/openmrs/api/include/ObsServiceTest-initial.xml";
	protected static final String CONCEPT_CUSTOM_INITIAL_DATA_XML = "Concept_customTestData.xml";
	protected static final String PERSOM_INITIAL_DATA_XML =
			"org/openmrs/api/include/PersonServiceTest-createPersonPurgeVoidTest.xml";

	public ObsService getService() {
		return Context.getService(ObsService.class);
	}

	@Before
	public void runBeforeEachTest() throws Exception {
		executeDataSet(OBS_INITIAL_DATA_XML);
		executeDataSet(CONCEPT_CUSTOM_INITIAL_DATA_XML);
		executeDataSet(PERSOM_INITIAL_DATA_XML);
	}

	@Test
	public void shouldSetupContext() {
		assertNotNull(getService());
	}

	@Test
	public void getObservation_shouldReturnResource() {
		String obsUuid = "be3a4d7a-f9ab-47bb-aaad-bc0b452fcda4";
		Observation fhirObservation = getService().getObs(obsUuid);
		assertNotNull(fhirObservation);
		assertEquals(fhirObservation.getId().toString(), obsUuid);
	}

	@Test
	public void searchObsByPatientAndConcept_shouldReturnMatchingObservationList() {
		String personUuid = "da7f524f-27ce-4bb2-86d6-6d1d05312bd5";
		String conceptCode = "3143-9";
		Context.getAdministrationService().saveGlobalProperty(new GlobalProperty("fhir.concept.codingSystem<", "LOINC"));
		Map<String, String> concepts = new HashMap<String, String>();
		ConceptService conceptService = Context.getConceptService();
		Concept concept = conceptService.getConcept(1);
		ConceptMap conceptMap = new ConceptMap();
		conceptMap.setConcept(concept);
		conceptMap.setConceptReferenceTerm(conceptService.getConceptReferenceTerm(558));
		concept.addConceptMapping(conceptMap);
		conceptService.saveConcept(concept);
		concepts.put(conceptCode, "http://loinc.org");
		List<Observation> obs = getService().searchObsByPatientAndConcept(personUuid, concepts);
		assertNotNull(obs);
		assertEquals(2, obs.size());
	}

	@Test
	public void searchObsById_shouldReturnMatchingObservationList() {
		String obsUuid = "be3a4d7a-f9ab-47bb-aaad-bc0b452fcda4";
		List<Observation> fhirObservations = getService().searchObsById(obsUuid);
		assertNotNull(fhirObservations);
		assertEquals(fhirObservations.get(0).getId().getIdPart(), obsUuid);
	}

	@Test
	public void searchObsByCode_shouldReturnMatchingObservationList() {
		String code1 = "4a5048b1-cf85-4c64-9339-7cab41e5e364";
		String code2 = "95312123-e0c2-466d-b6b1-cb6e990d0d65";
		Map<String, String> codes = new HashMap<String, String>();
		codes.put(code1, null);
		codes.put(code2, null);
		List<Observation> obs = getService().searchObsByCode(codes);
		assertNotNull(obs);
		assertEquals(12, obs.size());
	}

	@Test
	public void searchObsByDate_shouldReturnMatchingObservationList() throws ParseException {
		String obsDate = "2009-01-01 00:00:00.0";
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date date = df.parse(obsDate);
		List<Observation> obs = getService().searchObsByDate(date);
		assertEquals(1, obs.size());
	}

	@Test
	public void searchObsByPerson_shouldReturnMatchingObservationList() {
		String personUuid = "da7f524f-27ce-4bb2-86d6-6d1d05312bd5";
		List<Observation> obs = getService().searchObsByPerson(personUuid);
		assertNotNull(obs);
		assertEquals(3, obs.size());
	}
	
	@Test
	public void deleteObs_shouldDeleteTheSpecifiedObs() {
		org.openmrs.api.ObsService obsService = Context.getObsService();
		org.openmrs.Obs obs = obsService.getObs(9);
		assertNotNull(obs);
		String Uuid = obs.getUuid();
		assertFalse(obs.isVoided());
		getService().deleteObs(Uuid);
		obs = obsService.getObs(9);
		assertTrue(obs.isVoided());
	}
	
	@Test
	public void createObs_shouldCreatedObs() {
		
		Person p=Context.getPersonService().getPersonByUuid("dagh524f-27ce-4bb2-86d6-6d1d05312bd5");
		if(p==null){
			assertEquals("kk","fghj");
		}
		
		Obs obsn=new Obs(p, Context.getConceptService().getConceptByUuid("4a5048b1-cf85-4c64-9339-7cab41e5e364"), new Date(),null);
		//if(obs.getConcept().isNumeric()){
			obsn.setValueNumeric(8d);
		//}

		//obsn.setId(13);
		obsn.setVoided(false);
	//	assertEquals(obsn.getPerson().getId().intValue(),6);
		
		
		//new ob
		/*obsn.setPerson(obs.getPerson());
		obsn.setObsDatetime(obs.getObsDatetime());
		obsn.setDateCreated(obs.getDateCreated());
		obsn.setConcept(obs.getConcept());
		obsn.setValueNumeric(obs.getValueNumeric());
		obsn.setCreator(obs.getCreator());
		obsn.setId(88);
		obsn.setValueNumeric(5d);*/
	//	obsn.setId(66);
		
		

		/*Obs newObs=FHIRObsUtil.generateOpenMRSObs(observation, new ArrayList<String>());
		assertEquals(newObs.getUuid(),"fghj");*/
		
		Obs newObs=Context.getObsService().saveObs(obsn, "hhh");
		assertNotNull(newObs);
		assertEquals(newObs.getConcept().getName(), obsn.getConcept().getName());
		assertEquals(newObs.getPerson().getNames(), obsn.getPerson().getNames());
		assertEquals(newObs.getLocation().getName(), obsn.getLocation().getName());
		assertEquals(newObs.getValueNumeric(), obsn.getValueNumeric());		
	}
}
