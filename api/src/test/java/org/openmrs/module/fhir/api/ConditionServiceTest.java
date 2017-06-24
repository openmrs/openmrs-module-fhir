package org.openmrs.module.fhir.api;

import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Condition;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.GlobalProperty;
import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * This test class will test the functionalities of the Condition Service.
 */
public class ConditionServiceTest extends BaseModuleContextSensitiveTest {
    protected static final String OBS_INITIAL_DATA_XML = "org/openmrs/api/include/ObsServiceTest-initial.xml";
    protected static final String CONCEPT_CUSTOM_INITIAL_DATA_XML = "Concept_customTestData.xml";
    protected static final String GLOBAL_PROPS_CONDITION_MAPPING_CONCEPT_ID = "1";
    protected static final String PAT_INITIAL_DATA_XML = "org/openmrs/api/include/PatientServiceTest-createPatient.xml";
    protected static final String PAT_SEARCH_DATA_XML = "org/openmrs/api/include/PatientServiceTest-findPatients.xml";


    private ConditionService getService() {
        return Context.getService(ConditionService.class);
    }

    @Before
    public void runBeforeEachTest() throws Exception {
        executeDataSet(OBS_INITIAL_DATA_XML);
        executeDataSet(CONCEPT_CUSTOM_INITIAL_DATA_XML);
        executeDataSet(PAT_INITIAL_DATA_XML);
        executeDataSet(PAT_SEARCH_DATA_XML);
    }

    @Test
    public void getConditionByObsId_shouldReturnMatchingFHIRCondition() {
        Context.getAdministrationService().saveGlobalProperty(new GlobalProperty(FHIRConstants
                .CONCEPTS_CONVERTABLE_TO_CONDITIONS_STORED_AS_OBS, GLOBAL_PROPS_CONDITION_MAPPING_CONCEPT_ID));
        String patientUuid = "61b38324-e2fd-4feb-95b7-9e9a2a4400df";
        String conceptUuid = "4a5048b1-cf85-4c64-9339-7cab41e5e364";
        Date openmrsDateApplies = new Date();
        Person patient = Context.getPersonService().getPersonByUuid(patientUuid);
        Concept concept = Context.getConceptService().getConceptByUuid(conceptUuid);
        Obs problemAddedObs = new Obs(patient, concept, openmrsDateApplies, null);
        problemAddedObs.setValueNumeric(8d);
        problemAddedObs = Context.getObsService().saveObs(problemAddedObs, null);
        Condition fhirConditionForProblemAddedObs = getService().getConditionByObsId(problemAddedObs.getUuid());
        Coding fhirCoding = fhirConditionForProblemAddedObs.getCode().getCoding().get(0);
        assertNotNull(fhirConditionForProblemAddedObs);
        assertEquals(patient.getUuid(), fhirConditionForProblemAddedObs.getSubject().getId());
        assertEquals(fhirCoding.getSystem(), FHIRConstants.OPENMRS_URI);
        assertEquals(fhirCoding.getCode(), problemAddedObs.getConcept().getUuid());
        assertEquals(fhirCoding.getDisplay(), problemAddedObs.getConcept().getName().getName());
    }
}
