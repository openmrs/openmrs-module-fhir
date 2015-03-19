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

import ca.uhn.fhir.model.dstu2.resource.Person;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.reverse.OpenmrsPersonUtil;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import java.util.List;
import static org.junit.Assert.assertEquals;

public class OpenmrsPersonUtilTest extends BaseModuleContextSensitiveTest {

    protected static final String PERSOM_INITIAL_DATA_XML = "org/openmrs/api/include/PersonServiceTest-createPersonPurgeVoidTest.xml";

    public PersonService getService() {
        return Context.getService(PersonService.class);
    }

    @Before
    public void runBeforeEachTest() throws Exception {
        executeDataSet(PERSOM_INITIAL_DATA_XML);
    }

    /**
     * @verifies generate oms person
     * @see org.openmrs.module.fhir.api.reverse.OpenmrsPersonUtil#generateOpenMRSPerson()
     */
    @Test
    public void generateOpenMRSPerson_shouldGenerateOmsPerson() throws Exception {
        String personUuid = "dagh524f-27ce-4bb2-86d6-6d1d05312bd5";
        List<Person> persons = getService().searchPersonById(personUuid);
        org.openmrs.Person pa = OpenmrsPersonUtil.generateOpenMRSPerson();
        assertEquals(pa.getUuid(), personUuid);

    }
}
