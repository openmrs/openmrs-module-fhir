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

import ca.uhn.fhir.model.dstu2.composite.BoundCodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.Composition;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.valueset.EncounterTypeEnum;
import ca.uhn.fhir.model.primitive.CodeDt;
import ca.uhn.fhir.model.primitive.IdDt;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class EncounterServiceTest extends BaseModuleContextSensitiveTest {

	protected static final String ENCOUNTER_INITIAL_DATA_XML = "org/openmrs/api/include/EncounterServiceTest-initialData.xml";
	
	protected static final String VISIT_INITIAL_DATA_XML = "org/openmrs/api/include/VisitServiceTest-includeVisitsAndTypeToAutoClose.xml";

	public EncounterService getService() {
		return Context.getService(EncounterService.class);
	}

	@Before
	public void runBeforeEachTest() throws Exception {
		executeDataSet(ENCOUNTER_INITIAL_DATA_XML);
		executeDataSet(VISIT_INITIAL_DATA_XML);
		updateSearchIndex();
	}

	@Test
	public void getFHIREncounterFromOmrsEncounter_shouldReturnResourceIfExists() {
		String encounterUuid = "430bbb70-6a9c-4e1e-badb-9d1034b1b5e9";
		Encounter fhirEncounter = getService().getEncounter(encounterUuid);
		assertNotNull(fhirEncounter);
		assertEquals(fhirEncounter.getId().toString(), encounterUuid);
	}
	
	@Test
	public void FHIREncounter_shouldCreateEncounter() {
		String encounterUuid = "430bbb70-6a9c-4e1e-badb-9d1034b1b5e9";
		Encounter fhirEncounter = getService().getEncounter(encounterUuid);
		
		ResourceReferenceDt visitRef = new ResourceReferenceDt();
		visitRef.setDisplay("test");
		IdDt visitRefId = new IdDt();
		String visitRefUri = FHIRConstants.ENCOUNTER + "/" + "7fffd6b9-0970-4967-88c7-0b7b50f12ab9";
		visitRefId.setValue(visitRefUri);
		visitRef.setReference(visitRefId);
		fhirEncounter.setPartOf(visitRef);
		fhirEncounter = getService().createFHIREncounter(fhirEncounter);
		assertNotNull(fhirEncounter);
		assertEquals(fhirEncounter.getPeriod().getStart().toString(), "2005-01-01 00:00:00.0");
		assertEquals(fhirEncounter.getLocation().get(0).getLocation().getReference().getIdPart(),
		    "c36006e5-9fbb-4f20-866b-0ece245615a1");
	}
	
	@Test
	public void FHIREncounter_shouldCreateVisit() {
		String encounterUuid = "430bbb70-6a9c-4e1e-badb-9d1034b1b5e9";
		Encounter fhirEncounter = getService().getEncounter(encounterUuid);
		BoundCodeableConceptDt<EncounterTypeEnum> typeAsCode = new BoundCodeableConceptDt<EncounterTypeEnum>();
		List<CodingDt> typeCoding = new ArrayList<CodingDt>();
		CodingDt code = new CodingDt();
		CodeDt codeValue = new CodeDt();
		codeValue.setValue("1");
		code.setCode(codeValue);
		typeCoding.add(code);
		typeAsCode.setCoding(typeCoding);
		List<BoundCodeableConceptDt<EncounterTypeEnum>> typeList = new ArrayList<BoundCodeableConceptDt<EncounterTypeEnum>>();
		typeList.add(typeAsCode);
		fhirEncounter.setType(typeList);
		fhirEncounter = getService().createFHIREncounter(fhirEncounter);
		Visit visit = Context.getVisitService().getVisitByUuid(fhirEncounter.getId().getIdPart());
		assertEquals(visit.getUuid(), fhirEncounter.getId().getIdPart());
		assertEquals(visit.getVisitType().getVisitTypeId().toString(), "1");
		assertEquals(visit.getStartDatetime().toString(), "2005-01-01 00:00:00.0");
		assertNotNull(visit);
	}

	@Test
	public void getFHIREncounterFromOmrsVisit_shouldReturnResourceIfExists() {
		String visitUuid = "4c48b0c0-1ade-11e1-9c71-00248140a5eb";
		Encounter fhirEncounter = getService().getEncounter(visitUuid);
		assertNotNull(fhirEncounter);
		assertEquals(fhirEncounter.getId().toString(), visitUuid);
	}

	@Test
	public void searchEncounterByIdFromOmrsEncounter_shouldReturnMatchingEncounter() {
		String encounterUuid = "33d70956-b359-452a-b3da-b69c8ab459ce";
		List<Encounter> fhirEncounters = getService().searchEncounterById(encounterUuid);
		assertNotNull(fhirEncounters);
		assertEquals(1, fhirEncounters.size());
	}

	@Test
	public void searchEncounterByIdFromOmrsVisit_shouldReturnMatchingVisitAsFHIREncounter() {
		String visitUuid = "4c48b0c0-1ade-11e1-9c71-00248140a5eb";
		List<Encounter> fhirEncounters = getService().searchEncounterById(visitUuid);
		assertNotNull(fhirEncounters);
		assertEquals(1, fhirEncounters.size());
	}

	@Test
	public void searchEncountersByPatientIdentifier_shouldReturnBundle() {
		String identifier = "12345";
		List<Encounter> fhirEncounters = getService().searchEncountersByPatientIdentifier(identifier);
		assertNotNull(fhirEncounters);
		assertEquals(3, fhirEncounters.size());
	}

	@Test
	public void searchEncounterComposition_shouldReturnMatchingComposition() {
		String encounterUuid = "33d70956-b359-452a-b3da-b69c8ab459ce";
		List<Composition> fhirCompositon = getService().searchEncounterComposition(encounterUuid);
		assertNotNull(fhirCompositon);
		assertEquals(1, fhirCompositon.size());
	}

	@Test
	public void searchEncounterOperations_shouldReturnBundle() {
		String encounterUuid = "33d70956-b359-452a-b3da-b69c8ab459ce";
		Bundle bundle = getService().getEncounterOperationsById(encounterUuid);
		assertNotNull(bundle);
		assertEquals(4, bundle.getEntry().size());
	}

	@Test
	public void searchEncounterCompositionByPatient_shouldReturnMarchingCompositionList() {
		String personUuid = "4b3f42da-2029-4e47-9396-a1b6a969e802";
		List<Composition> fhirCompositon = getService().searchEncounterCompositionByPatientId(personUuid);
		assertNotNull(fhirCompositon);
		assertEquals(2, fhirCompositon.size());
	}

	@Test
	public void deleteEncounter_shouldVoidEncounterIfExists() {
		String encounterUuid = "430bbb70-6a9c-4e1e-badb-9d1034b1b5e9";
		org.openmrs.Encounter encounter = Context.getEncounterService().getEncounterByUuid(encounterUuid);
		assertNotNull(encounter);
		assertFalse(encounter.isVoided());
		getService().deleteEncounter(encounterUuid);
		assertTrue(encounter.isVoided());
	}

	@Test
	public void searchEncounterByEncounterIdAndPartOfNone_shouldReturnsEncounterWithoutParentVisitOrVisit() {
		String visitUuid = "4c48b0c0-1ade-11e1-9c71-00248140a5eb";
		List<Encounter> fhirEncounters = getService().searchEncountersByEncounterIdAndPartOf(visitUuid, FHIRConstants.NONE);
		assertNotNull(fhirEncounters);
		assertEquals(1, fhirEncounters.size());
	}

	@Test
	public void searchEncounterByEncounterIdAndPartOf_shouldReturnsEncounterWithoutParentVisitOrVisit() {
		String encounterUuid = "7fffd6b9-0970-4967-88c7-0b7b50f12bc6";
		String visitUuid = "7fffd6b9-0970-4967-88c7-0b7b50f12ab9";
		List<Encounter> fhirEncounters = getService().searchEncountersByEncounterIdAndPartOf(encounterUuid, visitUuid);
		assertNotNull(fhirEncounters);
		assertEquals(1, fhirEncounters.size());
	}

	@Test
	public void searchEncounterByPatientIdentifierAndPartOfNone_shouldReturnsEncounterWithoutParentVisitOrVisit() {
		String patientIdentifier = "1234";
		List<Encounter> fhirEncounters = getService().searchEncountersByPatientIdentifierAndPartOf(patientIdentifier,
		    FHIRConstants.NONE);
		assertNotNull(fhirEncounters);
		assertEquals(8, fhirEncounters.size());
	}

	@Test
	public void searchEncounterByPatientIdentifierAndPartOf_shouldReturnsEncounterWithoutParentVisitOrVisit() {
		String patientIdentifier = "12345";
		String visitUuid = "7fffd6b9-0970-4967-88c7-0b7b50f12ab9";
		List<Encounter> fhirEncounters = getService().searchEncountersByPatientIdentifierAndPartOf(patientIdentifier,
		    visitUuid);
		assertNotNull(fhirEncounters);
		assertEquals(1, fhirEncounters.size());
	}
}
