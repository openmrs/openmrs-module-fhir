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
package org.openmrs.module.fhir.web.controller;

import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.Person;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.impl.PatientServiceImpl;
import org.openmrs.module.fhir.api.util.FHIRPatientUtil;
import org.openmrs.module.fhir.util.Parser;

import java.util.*;


public class FHIRPatientResource extends Resource {

    public Object retrieve(String uuid) throws Exception {
        Object delegate = getByUniqueId(uuid, null);
        System.out.println(delegate);
        if (delegate == null)
            throw new Exception();

        return delegate;
    }


    public String getByUniqueId(String uuid, String contentType) {
        Patient patient = Context.getPatientService().getPatientByUuid(uuid);
        ca.uhn.fhir.model.dstu.resource.Patient fhirPatient = Context.getService(org.openmrs.module.fhir.api.PatientService.class).getPatient(uuid);

        return Parser.parsePatient(fhirPatient, contentType);
    }

}
