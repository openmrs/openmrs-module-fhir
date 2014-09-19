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

import ca.uhn.fhir.model.dstu.resource.FamilyHistory;
import org.openmrs.*;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.FHIR;
import org.openmrs.module.fhir.util.FHIRFamilyHistoryUtil;
import org.openmrs.module.fhir.util.FHIRObsUtil;
import org.openmrs.module.fhir.util.FHIRPatientUtil;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.*;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.module.webservices.rest.web.response.ObjectNotFoundException;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.PatientResource1_8;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.PersonResource1_8;

import java.util.*;

/**
 * {@link org.openmrs.module.webservices.rest.web.annotation.Resource} for Patients, supporting standard CRUD operations
 */
@Resource(name = RestConstants.VERSION_1 + "/FamilyHistory", supportedClass = Patient.class, supportedOpenmrsVersions = {"1.8.*", "1.9.*", "1.10.*"})
public class FamilyHistoryResource extends DelegatingCrudResource {

    @Override
    public Object retrieve(String uuid, RequestContext context) throws ResponseException {

        if(!uuid.equals("_search")) {
            String resource = context.getRequest().getParameter("Patient");
            String name = context.getRequest().getParameter("name");

            String contentType = context.getRequest().getContentType();
            Object delegate = getByUniqueId(uuid, contentType);
            if (delegate == null)
                throw new ObjectNotFoundException();

            return delegate;
        }else{

            String patientUUid = context.getRequest().getParameter("subject:Patient");
            //String[] concepts = context.getRequest().getParameter("name").split(",");


            System.out.println(patientUUid);

            Patient patient = Context.getPatientService().getPatientByUuid(patientUUid);
            System.out.println(patient);




            String resultString = FHIRFamilyHistoryUtil.generateBundle();

            return resultString;

        }
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#getRepresentationDescription(org.openmrs.module.webservices.rest.web.representation.Representation)
     */
    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        return null;
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getCreatableProperties()
     */
    @Override
    public DelegatingResourceDescription getCreatableProperties() {
        DelegatingResourceDescription description = new DelegatingResourceDescription();
        return description;
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getUpdatableProperties()
     */
    @Override
    public DelegatingResourceDescription getUpdatableProperties() {
        DelegatingResourceDescription description = new DelegatingResourceDescription();
        return description;
    }


    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#getByUniqueId(String)
     */
    //@Override
    public String getByUniqueId(String uuid, String contentType) {
        Patient patient = Context.getPatientService().getPatientByUuid(uuid);
        FamilyHistory familyHistory = FHIRFamilyHistoryUtil.generateFamilyHistory();

        return FHIRFamilyHistoryUtil.parseFamilyHistory(familyHistory,contentType);
    }

    @Override
    public Object getByUniqueId(String s) {
        return null;
    }

    @Override
    protected void delete(Object o, String s, RequestContext requestContext) throws ResponseException {

    }

    @Override
    public Object newDelegate() {
        return null;
    }

    @Override
    public Object save(Object o) {
        return null;
    }

    @Override
    public void purge(Object o, RequestContext requestContext) throws ResponseException {

    }

}
