package org.openmrs.module.fhir.api;

import ca.uhn.fhir.model.dstu2.resource.Condition;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.GlobalProperty;
import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import static org.junit.Assert.assertNotNull;

import java.util.Date;

/**
 * This test class will test the functionalities of the Condition Service.
 */
public class ConditionServiceTest extends BaseModuleContextSensitiveTest {
    protected static final String OBS_INITIAL_DATA_XML = "org/openmrs/api/include/ObsServiceTest-initial.xml";
    protected static final String CONCEPT_CUSTOM_INITIAL_DATA_XML = "Concept_customTestData.xml";
    protected static final String GLOBAL_PROPS_CONDITION_MAPPING_CONCEPT_ID_ = "1";
    protected static final String PERSOM_INITIAL_DATA_XML =
            "org/openmrs/api/include/PersonServiceTest-createPersonPurgeVoidTest.xml";

    private ConditionService getService() {
        return Context.getService(ConditionService.class);
    }

    @Before
    public void runBeforeEachTest() throws Exception {
        executeDataSet(OBS_INITIAL_DATA_XML);
        executeDataSet(CONCEPT_CUSTOM_INITIAL_DATA_XML);
        executeDataSet(PERSOM_INITIAL_DATA_XML);
    }

    @Test
    public void getConditionByObsId_shouldReturnMatchingFHIRCondition() {
        Context.getAdministrationService().saveGlobalProperty(new GlobalProperty(FHIRConstants
                .CONCEPTS_CONVERTABLE_TO_CONDITIONS_STORED_AS_OBS, GLOBAL_PROPS_CONDITION_MAPPING_CONCEPT_ID_));
        String openmrsAdminPersonUuid = "dagh524f-27ce-4bb2-86d6-6d1d05312bd5";
        String openmrsQuestionConceptUuid = "4a5048b1-cf85-4c64-9339-7cab41e5e364";
        Date openmrsDateApplies = new Date();
        Person admin = Context.getPersonService().getPersonByUuid(openmrsAdminPersonUuid);
        Concept concept = Context.getConceptService().getConceptByUuid(openmrsQuestionConceptUuid);
        Obs problemAddedObs = new Obs(admin, concept, openmrsDateApplies, null);
        problemAddedObs.setValueNumeric(8d);
        problemAddedObs = Context.getObsService().saveObs(problemAddedObs, null);
        Condition fhirConditionForProblemAddedObs = getService().getConditionByObsId(problemAddedObs.getUuid());
        assertNotNull(fhirConditionForProblemAddedObs);
    }
}
