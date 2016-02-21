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
package org.openmrs.module.fhir.resources;

import ca.uhn.fhir.model.dstu2.resource.Condition;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.ConditionService;

import java.util.List;

public class FHIRConditionResource extends Resource {

    public Condition getByUniqueId(IdDt id) {
        ConditionService conditionService = Context.getService(ConditionService.class);
        Condition condition = conditionService.getCondition(id.getIdPart());
        if (condition == null) {
            throw new ResourceNotFoundException("Condition is not found for the given Id " + id.getIdPart());
        }
        return condition;
    }

    /**
     * Gets the FHIR Condition for the OpenMRs Obs which has the id
     * @param id id of the OpenMrs Obs
     * @return
     */
    public Condition getConditionByObsUniqueId(IdDt id) {
        ConditionService conditionService = Context.getService(ConditionService.class);
        Condition condition = conditionService.getConditionByObsId(id.getIdPart());
        if (condition == null) {
            throw new ResourceNotFoundException("Condition is not found for the given Observatoin Id " + id.getIdPart
                    ());
        }
        return condition;
    }

    public List<Condition> searchConditionsById(TokenParam id) {
        return Context.getService(ConditionService.class).searchConditionById(id.getValue());
    }

    public List<Condition> searchConditionsByPatient(ReferenceParam patient) {
        return Context.getService(ConditionService.class).searchConditionsByPatient(patient.getValue());
    }
}
