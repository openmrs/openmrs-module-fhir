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

import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.resource.AllergyIntolerance;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import java.util.Iterator;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 *
 * @author leonard
 */
public class AllergyIntoleranceServiceTest extends BaseModuleContextSensitiveTest{
    protected static final String ALLERGY_INITIAL_DATA_XML = "org/openmrs/api/include/AllergyIntoleranceServiceTest-initial.xml";
     protected static final String PAT_SEARCH_DATA_XML = "org/openmrs/api/include/PatientServiceTest-findPatients.xml";
    
    public AllergyIntoleranceService getService() {
		return Context.getService(AllergyIntoleranceService.class);
	}
    
    @Before
    public void runBeforeEachTest() throws Exception {
        executeDataSet(ALLERGY_INITIAL_DATA_XML);
        executeDataSet(PAT_SEARCH_DATA_XML);
    }
    
    @Test
	public void shouldSetupContext() {
		assertNotNull(getService());
        }
    /**
     * Test of getAllergyById method, of class AllergyIntoleranceService.
     */
    @Test
    public void getAllergyByIds_shouldReturnAllergyIfExists() {
        String allergyUuid = "12345";
        AllergyIntolerance allergy = getService().getAllergyById(allergyUuid);
        assertNotNull(allergy);
        assertEquals(allergy.getId().toString(), allergyUuid);
    }

    /**
     * Test of searchAllergiesById method, of class AllergyIntoleranceService.
     */
    @Test
    public void searchAllergiesById_shouldReturnMatchingAllergyIntoleranceList() {
        String allergiesUuid = "be3a4d7a-f9ab-47bb-aaad-bc0b452fcda4";
		List<AllergyIntolerance> fhirAllergies = getService().searchAllergiesById(allergiesUuid);
		assertNotNull(fhirAllergies);
		assertEquals(fhirAllergies.get(0).getId().getIdPart(), allergiesUuid);
    }

    /**
     * Test of searchAllergiesByPatientIdentifier method, of class AllergyIntoleranceService.
     */
    @Test
    public void searchAllergiesByPatientIdentifier_shouldReturnMatchingAllergyIntoleranceList() {
        String patientUuid = "7e2acadc-5073-4a39-914a-debcbec8c1c9";
		String identifierValue = "2";
		List<AllergyIntolerance> patients = getService().searchAllergiesByPatientIdentifier(identifierValue);
		assertNotNull(patients);
		assertEquals(1, patients.size());
		AllergyIntolerance fhirPatient = patients.get(0);
		assertEquals(fhirPatient.getId().toString(), patientUuid);
		boolean exist = false;
        for (AllergyIntolerance patient : patients) {
            for (IdentifierDt identifierDt : patient.getIdentifier()) {
                if (identifierValue.equals(identifierDt.getValue())) {
                    exist = true;
                }
            }
        }
		assertTrue(exist);
    }

}