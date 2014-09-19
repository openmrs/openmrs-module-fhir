package org.openmrs.module.fhir.web.controller;

/**
 * Created by snkasthu on 9/9/14.
 */
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

import ca.uhn.fhir.model.dstu.resource.Observation;
import org.apache.commons.lang.StringUtils;
import org.openmrs.*;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.util.FHIRObsUtil;
import org.openmrs.module.fhir.util.FHIRPatientUtil;
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
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.*;
import org.openmrs.module.webservices.rest.web.response.*;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.EncounterResource1_8;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.PatientResource1_8;

import java.text.ParseException;
import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.ConceptNumeric;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
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
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@link org.openmrs.module.webservices.rest.web.annotation.Resource} for Obs, supporting standard CRUD operations
 */
@Resource(name = RestConstants.VERSION_1 +  "/Observation", supportedClass = Obs.class, supportedOpenmrsVersions = {"1.8.*", "1.9.*", "1.10.*"})
public class FHIRObservationResource extends DelegatingCrudResource {

    //@Override
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
            String[] concepts = context.getRequest().getParameter("name").split(",");


            System.out.println(patientUUid);

            Patient patient = Context.getPatientService().getPatientByUuid(patientUUid);
            System.out.println(patient);

            List<Concept> conceptList = new ArrayList<Concept>();
            for (String s : concepts) {
                Concept concept = Context.getConceptService().getConceptByMapping(s, "LOINC");
                conceptList.add(concept);

            }

            List<Obs> totalObsList = new ArrayList<Obs>();

            System.out.println(conceptList);


            for (Concept concept : conceptList) {
                List<Obs> obsList = Context.getObsService().getObservationsByPersonAndConcept(patient, concept);
                totalObsList.addAll(obsList);
            }

            String resultString = FHIRObsUtil.generateBundle(totalObsList);

            return resultString;

        }

    }


    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getByUniqueId(java.lang.String)
     */
    // @Override
    public String getByUniqueId(String uniqueId, String contentType) {
        Obs obs = Context.getObsService().getObsByUuid(uniqueId);
        Observation observation = FHIRObsUtil.generateObs(obs);

        return FHIRObsUtil.parseObservation(observation, contentType);
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

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getRepresentationDescription(org.openmrs.module.webservices.rest.web.representation.Representation)
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
     * Gets obs by patient or encounter (paged according to context if necessary) only if a patient
     * or encounter parameter exists respectively in the request set on the {@link RequestContext}
     * otherwise searches for obs that match the specified query
     *
     * @param context
     * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doSearch(org.openmrs.module.webservices.rest.web.RequestContext)
     */
    protected Result doSearch(RequestContext context) {

        String patientUUid = context.getRequest().getParameter("subject:Patient");
        String[] concepts = context.getRequest().getParameter("name").split(",");

        Patient patient = Context.getPatientService().getPatientByUuid(patientUUid);

        List<Concept> conceptList = new ArrayList<Concept>();
        for (String s : concepts) {
            Concept concept = Context.getConceptService().getConceptByMapping(s, "LOINC");
            conceptList.add(concept);

        }

        List<Obs> totalObsList = new ArrayList<Obs>();

        for (Concept concept : conceptList) {
            List<Obs> obsList = Context.getObsService().getObservationsByPersonAndConcept(patient, concept);
            totalObsList.addAll(obsList);
        }

        String resultString = FHIRObsUtil.generateBundle(totalObsList);

        Result result = new Result();
        result.toSimpleObject().add("response", resultString);

        return result;
    }

}

