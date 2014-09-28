/**
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

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.*;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.List;


public class PatientServiceTest extends BaseModuleContextSensitiveTest {

    protected static final String ENC_INITIAL_DATA_XML = "org/openmrs/api/include/EncounterServiceTest-initialData.xml";
    protected static final String PAT_INITIAL_DATA_XML = "org/openmrs/api/include/PatientServiceTest-findPatients.xml";
    protected static final String CON_INITIAL_DATA_XML = "org/openmrs/api/include/ConceptServiceTest-initialConcepts.xml";


    @Before
    public void runBeforeEachTest() throws Exception {
        executeDataSet(ENC_INITIAL_DATA_XML);
        executeDataSet(PAT_INITIAL_DATA_XML);
        executeDataSet(CON_INITIAL_DATA_XML);

    }

	@Test
	public void shouldSetupContext() {
		assertNotNull(Context.getService(PatientService.class));
	}

    @Test
    public void createPatient(){
       // FHIRPatientUtil.generatePatient(Context.getPatientService().getPatient(2));
       // Patient patient = Context.getPatientService().getPatient(2);

        Obs obs = new Obs();
        Concept concept = new Concept();
        ConceptMap map = new ConceptMap();
        ConceptSource source = new ConceptSource();
        source.setName("loinc");
        source.setHl7Code("Loinc");
        map.setSource(source);

        map.setSource(source);
        map.setConcept(concept);
        map.setSourceCode("sourcecode");

        concept.addConceptMapping(map);
        obs.setConcept(concept);
       //  FHIRPatientUtil.generateObs(obs);
    }

    //@Test
    public void generateBundle() {

        List<Obs> obsList = Context.getObsService().getObservationsByPerson(Context.getPatientService().getPatient(3));
        obsList.size();

        FHIRPatientUtilAPI.generateBundle(obsList, "");


    }



}
