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
package org.openmrs.module.fhir.providers;

import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.hl7.fhir.dstu3.model.Condition;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Resource;
import org.openmrs.module.fhir.resources.FHIRConditionResource;

import java.util.List;

public class RestfulConditionResourceProvider implements IResourceProvider {

    private FHIRConditionResource conditionResource;

    public RestfulConditionResourceProvider() {
        conditionResource = new FHIRConditionResource();
    }

    @Override
    public Class<? extends Resource> getResourceType() {
        return Condition.class;
    }

    /**
     * The "@Read" annotation indicates that this method supports the
     * read operation. Read operations should return a single resource
     * instance.
     *
     * @param theId The read operation takes one parameter, which must be of type
     *              IdDt and must be annotated with the "@Read.IdParam" annotation.
     * @return Returns a resource matching this identifier, or null if none exists.
     */
    @Read()
    public Condition getResourceById(@IdParam IdType theId) {
        return conditionResource.getByUniqueId(theId);
    }

    /**
     * The "@Read" annotation indicates that this method supports the
     * read operation. Read operations should return a single resource
     * instance.
     *
     * @param theId id of the OpenMrs Obs
     * @return Returns a resource matching to the OpenMrs Obs which has the identifier theId, or null if none exists.
     */
    @Read()
    public Condition getResourceByObsUID(@IdParam IdType theId) {
        return conditionResource.getConditionByObsUniqueId(theId);
    }

    /**
     * Search appointments by unique id
     *
     * @param id object containing the requested id
     */
    @Search()
    public List<Condition> searchConditionsByUniqueId(
            @RequiredParam(name = Condition.SP_RES_ID) TokenParam id) {
        return conditionResource.searchConditionsById(id);
    }

    /**
     * Search appointments by unique id
     *
     * @param patient object containing the patient details
     */
    @Search()
    public List<Condition> searchConditionssByPatient(
            @RequiredParam(name = Condition.SP_PATIENT) ReferenceParam patient) {
        return conditionResource.searchConditionsByPatient(patient);
    }
}
