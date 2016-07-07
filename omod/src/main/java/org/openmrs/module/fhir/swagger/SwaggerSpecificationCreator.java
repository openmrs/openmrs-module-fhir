/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 * <p/>
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * <p/>
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.fhir.swagger;

import ca.uhn.fhir.model.dstu2.resource.Conformance;
import ca.uhn.fhir.model.dstu2.resource.OperationDefinition;
import ca.uhn.fhir.model.primitive.CodeDt;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.fhir.server.ConformanceProvider;
import org.openmrs.module.fhir.swagger.docs.Contact;
import org.openmrs.module.fhir.swagger.docs.Definition;
import org.openmrs.module.fhir.swagger.docs.Definitions;
import org.openmrs.module.fhir.swagger.docs.ExternalDocs;
import org.openmrs.module.fhir.swagger.docs.Info;
import org.openmrs.module.fhir.swagger.docs.License;
import org.openmrs.module.fhir.swagger.docs.NullSerializer;
import org.openmrs.module.fhir.swagger.docs.Operation;
import org.openmrs.module.fhir.swagger.docs.Parameter;
import org.openmrs.module.fhir.swagger.docs.Path;
import org.openmrs.module.fhir.swagger.docs.Paths;
import org.openmrs.module.fhir.swagger.docs.Response;
import org.openmrs.module.fhir.swagger.docs.Schema;
import org.openmrs.module.fhir.swagger.docs.SwaggerSpecification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SwaggerSpecificationCreator {
    protected Log log = LogFactory.getLog(getClass());
    private SwaggerSpecification swaggerSpecification;
    private Conformance conformance;
    private String baseUrl;
    private String basePath;
    private Map<String, Definition> definitionMap = new HashMap<String, Definition>();

    public SwaggerSpecificationCreator(String baseUrl, String basePath) {
        this.swaggerSpecification = new SwaggerSpecification();
        this.conformance = ConformanceProvider.getConformance();
        this.baseUrl = baseUrl;
        this.basePath = basePath;
    }

    public String buildJSON() {
        synchronized (this) {
            createApiDefinition();
            addPaths();
            addParameters();
            createObjectDefinitions();
        }
        return createSwaggerSpecification();
    }

    /*
     * Creating the information section of the swagger including base path licence
     */
    private void createApiDefinition() {
        Info info = new Info();
        info.setVersion(SwaggerDocConstants.VERSION);
        info.setTitle(SwaggerDocConstants.TITLE);
        info.setDescription(SwaggerDocConstants.DESCRIPTION);
        info.setTermsOfService(SwaggerDocConstants.TERMS_AND_CONDITIONS);
        //Setting the contact
        Contact contact = new Contact();
        contact.setName(SwaggerDocConstants.CONTACT_NAME);
        contact.setUrl(SwaggerDocConstants.CONTACT_URL);
        contact.setEmail(SwaggerDocConstants.CONTACT_EMAIL);
        //Setting the licence
        License license = new License();
        license.setName(SwaggerDocConstants.LICENSE_NAME);
        license.setUrl(SwaggerDocConstants.LICENSE_URL);
        info.setContact(contact);
        info.setLicense(license);
        swaggerSpecification.setInfo(info);
        List<String> produces = new ArrayList<String>();
        //Set mime type supported
        produces.add(SwaggerDocConstants.PRODUCES_JSON);
        produces.add(SwaggerDocConstants.PRODUCES_XML);
        List<CodeDt> formats = conformance.getFormat();
        for(CodeDt format : formats) {
            produces.add(format.getValue());
        }
        List<String> consumes = new ArrayList<String>();
        consumes.add(SwaggerDocConstants.CONSUMES_XML);
        consumes.add(SwaggerDocConstants.CONSUMES_JSON);
        swaggerSpecification.setHost(getBaseUrl());
        swaggerSpecification.setProduces(produces);
        swaggerSpecification.setConsumes(consumes);
        swaggerSpecification.setBasePath(basePath);
        List<String> schemas = new ArrayList<String>();
        schemas.add(SwaggerDocConstants.SCHEMA_HTTP);
        schemas.add(SwaggerDocConstants.SCHEMA_HTTPS);
        ExternalDocs externalDocs = new ExternalDocs();
        externalDocs.setDescription(SwaggerDocConstants.MORE_INFO);
        externalDocs.setUrl(SwaggerDocConstants.DOCS_URL);
        swaggerSpecification.setExternalDocs(externalDocs);
    }

    /**
     * Creating paths section swagger documentation
     */
    private void addPaths() {
        List<Conformance.Rest> resources = conformance.getRest();
        Paths fullPaths = new Paths();//Hold full path list
        Map<String, Path> pathMap = new HashMap<String, Path>();//Map holding path to path object mappings
        for (Conformance.Rest restResource : resources) {
            for (Conformance.RestResource resource : restResource.getResource()) {
                String resourceName = resource.getType();
                if(SwaggerDocConstants.STRUCTURE_DEFINITION.equalsIgnoreCase(resourceName)) {
                    continue;
                }
                List<Conformance.RestResourceInteraction> restResourceInteractions = resource.getInteraction();
                //Iterating over available opearations
                for(Conformance.RestResourceInteraction restResourceInteraction : restResourceInteractions) {
                    //Add GET operation paths
                    if(SwaggerDocConstants.READ.equalsIgnoreCase(restResourceInteraction.getCode())) {
                        String pathId = "/" + resourceName + "/" + SwaggerDocConstants.READ_RESOURCE_PATH;
                        Path read;
                        Map<String, Operation> readOperationsMap;
                        if(pathMap.containsKey(pathId)) {
                            read = pathMap.get(pathId);
                            readOperationsMap = read.getOperations();
                            if(readOperationsMap == null) {
                                readOperationsMap = new HashMap<String, Operation>();
                            }
                        } else {
                            read = new Path();
                            readOperationsMap = new HashMap<String, Operation>();
                        }
                        Operation readOperation = new Operation();
                        readOperation.setSummary(SwaggerDocConstants.RETURNS + " " + resourceName + " " + SwaggerDocConstants.DETAILS_OF_GIVEN_ID);
                        List<String> produces = new ArrayList<String>();
                        //Set mime type supported
                        produces.add(SwaggerDocConstants.PRODUCES_JSON);
                        produces.add(SwaggerDocConstants.PRODUCES_XML);
                        List<CodeDt> formats = conformance.getFormat();
                        for(CodeDt format : formats) {
                            produces.add(format.getValue());
                        }
                        readOperation.setProduces(produces);
                        //Set parameters
                        List<Parameter> parameters = new ArrayList<Parameter>();
                        Parameter parameter = new Parameter();
                        parameter.setDescription(SwaggerDocConstants.ID_DESCRIPTION + " " + resourceName + " " + SwaggerDocConstants.RESOURCE);
                        parameter.setName(SwaggerDocConstants.ID);
                        parameter.setIn(SwaggerDocConstants.IN_PATH);
                        parameter.setRequired(true);
                        parameters.add(parameter);
                        readOperation.setParameters(parameters);;

                        Map<String, Response> responseMap = new HashMap<String, Response>();
                        //Set response schemas and example responses
                        Response responseSuccess = new Response();
                        responseSuccess.setDescription(SwaggerDocConstants.RETURNS + " " + resourceName + " " + SwaggerDocConstants.DETAILS_OF_GIVEN_ID);
                        Schema schemaSuccess = new Schema();
                        schemaSuccess.setRef(getSchemaRef(resourceName));
                        schemaSuccess.setType(SwaggerDocConstants.OBJECT);
                        setExamples(resourceName, responseSuccess);
                        responseSuccess.setSchema(schemaSuccess);
                        responseMap.put(SwaggerDocConstants.SUCCESS_RESPONSE_CODE, responseSuccess);

                        Response responseError = new Response();
                        responseError.setDescription(SwaggerDocConstants.ERROR_OCCURRED);
                        Schema schemaError = new Schema();
                        schemaError.setRef(getSchemaRef(SwaggerDocConstants.GENERAL_ERROR));
                        schemaError.setType(SwaggerDocConstants.OBJECT);
                        responseError.setSchema(schemaError);
                        Map<String, String> examplesError = new HashMap<String, String>();
                        examplesError.put(SwaggerDocConstants.CONSUMES_JSON, SwaggerDocConstants.ERROR_PAYLOAD);
                        responseError.setExamples(examplesError);
                        responseMap.put(SwaggerDocConstants.ERROR_RESPONSE_CODE, responseError);
                        readOperation.setResponses(responseMap);

                        readOperationsMap.put(SwaggerDocConstants.GET, readOperation);
                        read.setOperations(readOperationsMap);
                        pathMap.put(pathId, read);
                    } else if(SwaggerDocConstants.CREATE.equalsIgnoreCase(restResourceInteraction.getCode())) {
                        //Set POST operation path properties
                        String pathId = "/" + resourceName;
                        Path create;
                        Map<String, Operation> createOperationsMap;
                        if(pathMap.containsKey(pathId)) {
                            create = pathMap.get(pathId);
                            createOperationsMap = create.getOperations();
                            if(createOperationsMap == null) {
                                createOperationsMap = new HashMap<String, Operation>();
                            }
                        } else {
                            create = new Path();
                            createOperationsMap = new HashMap<String, Operation>();
                        }
                        Operation createOperation = new Operation();
                        createOperation.setSummary(SwaggerDocConstants.CREATE_RESOURCE + " " + resourceName
                                                + " " + SwaggerDocConstants.RESOURCE + " " + SwaggerDocConstants.CONTENT_OF_THE_REQUEST);
                        List<String> produces = new ArrayList<String>();
                        //Set mime type supported
                        produces.add(SwaggerDocConstants.PRODUCES_JSON);
                        produces.add(SwaggerDocConstants.PRODUCES_XML);
                        List<CodeDt> formats = conformance.getFormat();
                        for(CodeDt format : formats) {
                            produces.add(format.getValue());
                        }
                        createOperation.setProduces(produces);
                        //Set parameters
                        List<Parameter> parameters = new ArrayList<Parameter>();
                        Parameter parameter = new Parameter();
                        parameter.setDescription(resourceName + " " + SwaggerDocConstants.RESOURCE + " " + SwaggerDocConstants.OBJECT);
                        parameter.setName(SwaggerDocConstants.BODY);
                        parameter.setIn(SwaggerDocConstants.IN_BODY);
                        parameter.setType(null);
                        parameter.setRequired(true);
                        Schema schema = new Schema();
                        schema.setRef(getSchemaRef(resourceName));
                        parameter.setSchema(schema);
                        parameters.add(parameter);
                        createOperation.setParameters(parameters);

                        //Set response properties
                        Map<String, Response> responseMap = new HashMap<String, Response>();
                        Response responseSuccess = new Response();
                        responseSuccess.setDescription(SwaggerDocConstants.RETURNS_SUCCESS_OPERATION_OUTCOME);
                        Schema schemaSuccess = new Schema();
                        schemaSuccess.setRef(getSchemaRef(SwaggerDocConstants.OPERATION_OUTCOME));
                        schemaSuccess.setType(SwaggerDocConstants.OBJECT);
                        responseSuccess.setSchema(schemaSuccess);
                        Map<String, String> examplesSuccess = new HashMap<String, String>();
                        examplesSuccess.put(SwaggerDocConstants.CONSUMES_JSON, SwaggerDocConstants.SUCCESS_PAYLOAD);
                        responseSuccess.setExamples(examplesSuccess);
                        responseMap.put(SwaggerDocConstants.SUCCESS_RESPONSE_CODE, responseSuccess);

                        Response responseError = new Response();
                        responseError.setDescription(SwaggerDocConstants.ERROR_OCCURRED);
                        Schema schemaError = new Schema();
                        schemaError.setRef(getSchemaRef(SwaggerDocConstants.GENERAL_ERROR));
                        schemaError.setType(SwaggerDocConstants.OBJECT);
                        Map<String, String> examplesError = new HashMap<String, String>();
                        examplesError.put(SwaggerDocConstants.CONSUMES_JSON, SwaggerDocConstants.ERROR_PAYLOAD);
                        responseError.setExamples(examplesError);
                        responseError.setSchema(schemaError);
                        responseMap.put(SwaggerDocConstants.ERROR_RESPONSE_CODE, responseError);
                        createOperation.setResponses(responseMap);

                        createOperationsMap.put(SwaggerDocConstants.POST, createOperation);
                        create.setOperations(createOperationsMap);
                        pathMap.put(pathId, create);
                    } else if(SwaggerDocConstants.UPDATE.equalsIgnoreCase(restResourceInteraction.getCode())) {
                        //Configure PUT operation path properties
                        String pathId = "/" + resourceName + "/" + SwaggerDocConstants.UPDATE_RESOURCE_PATH;
                        Path update;
                        Map<String, Operation> updateOperationsMap;
                        if(pathMap.containsKey(pathId)) {
                            update = pathMap.get(pathId);
                            updateOperationsMap = update.getOperations();
                            if(updateOperationsMap == null) {
                                updateOperationsMap = new HashMap<String, Operation>();
                            }
                        } else {
                            update = new Path();
                            updateOperationsMap = new HashMap<String, Operation>();
                        }
                        Operation updateOperation = new Operation();
                        updateOperation.setSummary(SwaggerDocConstants.UPDATR_RESOURCE + " " + resourceName
                                + " " + SwaggerDocConstants.RESOURCE + " " + SwaggerDocConstants.CONTENT_OF_THE_REQUEST);
                        List<String> produces = new ArrayList<String>();
                        //Set mime type supported
                        produces.add(SwaggerDocConstants.PRODUCES_JSON);
                        produces.add(SwaggerDocConstants.PRODUCES_XML);
                        List<CodeDt> formats = conformance.getFormat();
                        for(CodeDt format : formats) {
                            produces.add(format.getValue());
                        }
                        updateOperation.setProduces(produces);
                        //Set put operation parameters and responses
                        List<Parameter> parameters = new ArrayList<Parameter>();
                        Parameter parameter = new Parameter();
                        parameter.setDescription(resourceName + " " + SwaggerDocConstants.RESOURCE + " " + SwaggerDocConstants.OBJECT);
                        parameter.setName(SwaggerDocConstants.BODY);
                        parameter.setIn(SwaggerDocConstants.IN_BODY);
                        parameter.setRequired(true);
                        parameter.setType(null);

                        Schema schema = new Schema();
                        schema.setRef(getSchemaRef(resourceName));
                        parameter.setSchema(schema);

                        parameters.add(parameter);

                        Parameter parameterId = new Parameter();
                        parameterId.setDescription(SwaggerDocConstants.ID_DESCRIPTION + " " + resourceName + " " + SwaggerDocConstants.RESOURCE);
                        parameterId.setName(SwaggerDocConstants.ID);
                        parameterId.setIn(SwaggerDocConstants.IN_PATH);
                        parameterId.setRequired(true);
                        parameters.add(parameterId);

                        updateOperation.setParameters(parameters);

                        Map<String, Response> responseMap = new HashMap<String, Response>();
                        Response responseSuccess = new Response();
                        responseSuccess.setDescription(SwaggerDocConstants.RETURNS_SUCCESS_OPERATION_OUTCOME);
                        Schema schemaSuccess = new Schema();
                        schemaSuccess.setRef(getSchemaRef(SwaggerDocConstants.OPERATION_OUTCOME));
                        schemaSuccess.setType(SwaggerDocConstants.OBJECT);
                        responseSuccess.setSchema(schemaSuccess);
                        Map<String, String> examplesSuccess = new HashMap<String, String>();
                        examplesSuccess.put(SwaggerDocConstants.CONSUMES_JSON, SwaggerDocConstants.SUCCESS_PAYLOAD);
                        responseSuccess.setExamples(examplesSuccess);
                        responseMap.put(SwaggerDocConstants.SUCCESS_RESPONSE_CODE, responseSuccess);

                        Response responseError = new Response();
                        responseError.setDescription(SwaggerDocConstants.ERROR_OCCURRED);
                        Schema schemaError = new Schema();
                        schemaError.setRef(getSchemaRef(SwaggerDocConstants.GENERAL_ERROR));
                        schemaError.setType(SwaggerDocConstants.OBJECT);
                        responseError.setSchema(schemaError);
                        Map<String, String> examplesError = new HashMap<String, String>();
                        examplesError.put(SwaggerDocConstants.CONSUMES_JSON, SwaggerDocConstants.ERROR_PAYLOAD);
                        responseError.setExamples(examplesError);
                        responseMap.put(SwaggerDocConstants.ERROR_RESPONSE_CODE, responseError);
                        updateOperation.setResponses(responseMap);

                        updateOperationsMap.put(SwaggerDocConstants.PUT, updateOperation);
                        update.setOperations(updateOperationsMap);
                        pathMap.put(pathId, update);
                    } else if(SwaggerDocConstants.DELETE.equalsIgnoreCase(restResourceInteraction.getCode())) {
                        //Set DELETE operation path properties
                        String pathId = "/" + resourceName + "/" + SwaggerDocConstants.DELETE_RESOURCE_PATH;
                        Path delete;
                        Map<String, Operation> deleteOperationMap;
                        if(pathMap.containsKey(pathId)) {
                            delete = pathMap.get(pathId);
                            deleteOperationMap = delete.getOperations();
                            if(deleteOperationMap == null) {
                                deleteOperationMap = new HashMap<String, Operation>();
                            }
                        } else {
                            delete = new Path();
                            deleteOperationMap = new HashMap<String, Operation>();
                        }
                        Operation deleteOperation = new Operation();
                        deleteOperation.setSummary(SwaggerDocConstants.RETURNS + " " + resourceName + " " + SwaggerDocConstants.DETAILS_OF_GIVEN_ID);

                        List<String> produces = new ArrayList<String>();
                        //Set mime type supported
                        produces.add(SwaggerDocConstants.PRODUCES_JSON);
                        produces.add(SwaggerDocConstants.PRODUCES_XML);
                        List<CodeDt> formats = conformance.getFormat();
                        for(CodeDt format : formats) {
                            produces.add(format.getValue());
                        }
                        deleteOperation.setProduces(produces);
                        List<Parameter> parameters = new ArrayList<Parameter>();
                        Parameter parameter = new Parameter();
                        parameter.setDescription(SwaggerDocConstants.ID_DESCRIPTION + " " + resourceName + " " + SwaggerDocConstants.RESOURCE);
                        parameter.setName(SwaggerDocConstants.ID);
                        parameter.setIn(SwaggerDocConstants.IN_PATH);
                        parameter.setRequired(true);
                        parameters.add(parameter);
                        deleteOperation.setParameters(parameters);;

                        Map<String, Response> responseMap = new HashMap<String, Response>();
                        Response responseSuccess = new Response();
                        responseSuccess.setDescription(SwaggerDocConstants.DELETE_DESCRIPTION + " " + resourceName + " " + SwaggerDocConstants.DETAILS_OF_GIVEN_ID);
                        Map<String, String> examplesSuccess = new HashMap<String, String>();
                        examplesSuccess.put(SwaggerDocConstants.CONSUMES_JSON, SwaggerDocConstants.SUCCESS_PAYLOAD);
                        responseSuccess.setExamples(examplesSuccess);
                        responseMap.put(SwaggerDocConstants.SUCCESS_RESPONSE_CODE, responseSuccess);

                        Response responseError = new Response();
                        responseError.setDescription(SwaggerDocConstants.ERROR_OCCURRED);
                        Schema schemaError = new Schema();
                        schemaError.setRef(getSchemaRef(SwaggerDocConstants.GENERAL_ERROR));
                        schemaError.setType(SwaggerDocConstants.OBJECT);
                        Map<String, String> examplesError = new HashMap<String, String>();
                        examplesError.put(SwaggerDocConstants.CONSUMES_JSON, SwaggerDocConstants.ERROR_PAYLOAD);
                        responseError.setExamples(examplesError);
                        responseError.setSchema(schemaError);
                        responseMap.put(SwaggerDocConstants.ERROR_RESPONSE_CODE, responseError);
                        deleteOperation.setResponses(responseMap);

                        deleteOperationMap.put(SwaggerDocConstants.DELETE, deleteOperation);
                        delete.setOperations(deleteOperationMap);
                        pathMap.put(pathId, delete);
                    } else if(SwaggerDocConstants.SEARCH_TYPE.equalsIgnoreCase(restResourceInteraction.getCode())) {
                        //Set search operation GET method parameters
                        String pathId = "/" + resourceName;
                        Path search;
                        Map<String, Operation> searchOperationMap;
                        if(pathMap.containsKey(pathId)) {
                            search = pathMap.get(pathId);
                            searchOperationMap = search.getOperations();
                            if(searchOperationMap == null) {
                                searchOperationMap = new HashMap<String, Operation>();
                            }
                        } else {
                            search = new Path();
                            searchOperationMap = new HashMap<String, Operation>();
                        }
                        Operation searchOperation = new Operation();
                        searchOperation.setSummary(SwaggerDocConstants.RETURNS + " " + resourceName + " " + SwaggerDocConstants.RETURNS_MATCHING_RESTULS);

                        List<String> produces = new ArrayList<String>();
                        //Set mime type supported
                        produces.add(SwaggerDocConstants.PRODUCES_JSON);
                        produces.add(SwaggerDocConstants.PRODUCES_XML);
                        List<CodeDt> formats = conformance.getFormat();
                        for(CodeDt format : formats) {
                            produces.add(format.getValue());
                        }
                        searchOperation.setProduces(produces);
                        Map<String, Parameter> parameters = new HashMap<String, Parameter>();
                        List<Conformance.RestResourceSearchParam> searchParams = resource.getSearchParam();
                        for(Conformance.RestResourceSearchParam searchParam : searchParams) {
                            Parameter parameter = new Parameter();
                            parameter.setDescription(searchParam.getDocumentation());
                            parameter.setName(searchParam.getName());
                            parameter.setIn(SwaggerDocConstants.IN_QUERY);
                            parameter.setRequired(false);
                            parameters.put(searchParam.getName(), parameter);
                        }

                        Map<String, Response> responseMap = new HashMap<String, Response>();
                        Response responseSuccess = new Response();
                        responseSuccess.setDescription(SwaggerDocConstants.BUNDLE_DESCRIPTION + " " + resourceName + " " + SwaggerDocConstants.RESOURCES);
                        Schema schemaSuccess = new Schema();
                        schemaSuccess.setRef(getSchemaRef(resourceName));
                        schemaSuccess.setType(SwaggerDocConstants.ARRAY);
                        responseSuccess.setSchema(schemaSuccess);
                        setExamples(resourceName, responseSuccess);
                        responseMap.put(SwaggerDocConstants.SUCCESS_RESPONSE_CODE, responseSuccess);

                        Response responseError = new Response();
                        responseError.setDescription(SwaggerDocConstants.ERROR_OCCURRED);
                        Schema schemaError = new Schema();
                        schemaError.setRef(getSchemaRef(SwaggerDocConstants.GENERAL_ERROR));
                        schemaError.setType(SwaggerDocConstants.OBJECT);
                        responseError.setSchema(schemaError);
                        Map<String, String> examplesError = new HashMap<String, String>();
                        examplesError.put(SwaggerDocConstants.CONSUMES_JSON, SwaggerDocConstants.ERROR_PAYLOAD);
                        responseError.setExamples(examplesError);
                        responseMap.put(SwaggerDocConstants.ERROR_RESPONSE_CODE, responseError);
                        searchOperation.setResponses(responseMap);
                        List<Parameter> parametersAvailable = new ArrayList<Parameter>();
                        for(Map.Entry<String, Parameter> parameter : parameters.entrySet()) {
                            parametersAvailable.add(parameter.getValue());
                        }
                        searchOperation.setParameters(parametersAvailable);
                        searchOperationMap.put(SwaggerDocConstants.GET, searchOperation);
                        search.setOperations(searchOperationMap);
                        pathMap.put(pathId, search);
                    }
                }
                createDefinition(resourceName);
            }

            //Set $everything operation properties
            for(Conformance.RestOperation restOperation : restResource.getOperation()) {
                if(SwaggerDocConstants.EVERYTHING.equalsIgnoreCase(restOperation.getName())) {
                    OperationDefinition resource = (OperationDefinition) restOperation.getDefinition().getResource();
                    String resourceName = resource.getType().get(0).getValue();
                    String pathId = "/" + resourceName + "/" + SwaggerDocConstants.POST_RESOURCE_PATH + "/" + SwaggerDocConstants.EVERYTHING;
                    Path everything;
                    Map<String, Operation> everythingOperationsMap;
                    if(pathMap.containsKey(pathId)) {
                        everything = pathMap.get(pathId);
                        everythingOperationsMap = everything.getOperations();
                        if(everythingOperationsMap == null) {
                            everythingOperationsMap = new HashMap<String, Operation>();
                        }
                    } else {
                        everything = new Path();
                        everythingOperationsMap = new HashMap<String, Operation>();
                    }

                    Operation everythingOp = new Operation();
                    everythingOp.setSummary(SwaggerDocConstants.RETURNS + " " + resourceName + " " + SwaggerDocConstants.EVERYTHING_OF_GIVEN_ID);
                    List<String> produces = new ArrayList<String>();
                    //Set mime type supported
                    produces.add(SwaggerDocConstants.PRODUCES_JSON);
                    produces.add(SwaggerDocConstants.PRODUCES_XML);
                    List<CodeDt> formats = conformance.getFormat();
                    for(CodeDt format : formats) {
                        produces.add(format.getValue());
                    }

                    everythingOp.setProduces(produces);

                    List<Parameter> parameters = new ArrayList<Parameter>();
                    Parameter parameter = new Parameter();
                    parameter.setDescription(SwaggerDocConstants.ID_DESCRIPTION + " " + resourceName + " " + SwaggerDocConstants.RESOURCE);
                    parameter.setName(SwaggerDocConstants.ID);
                    parameter.setIn(SwaggerDocConstants.IN_PATH);
                    parameter.setRequired(true);
                    parameters.add(parameter);

                    Parameter parameterBody = new Parameter();
                    parameterBody.setDescription(resourceName + " " + SwaggerDocConstants.RESOURCE + " " + SwaggerDocConstants.BODY_SAMPLE_VALUE);
                    parameterBody.setName(SwaggerDocConstants.BODY);
                    parameterBody.setIn(SwaggerDocConstants.IN_BODY);
                    parameterBody.setType(null);
                    parameterBody.setRequired(true);
                    Schema schema = new Schema();
                    schema.setRef(getSchemaRef(resourceName));
                    parameterBody.setSchema(schema);
                    parameters.add(parameterBody);

                    everythingOp.setParameters(parameters);

                    Map<String, Response> responseMap = new HashMap<String, Response>();
                    Response responseSuccess = new Response();
                    responseSuccess.setDescription(SwaggerDocConstants.RETURNS + " " + resourceName + " " + SwaggerDocConstants.EVERYTHING_OF_GIVEN_ID);
                    Schema schemaSuccess = new Schema();
                    schemaSuccess.setRef(getSchemaRef(resourceName));
                    schemaSuccess.setType(SwaggerDocConstants.OBJECT);
                    responseSuccess.setSchema(schemaSuccess);
                    responseMap.put(SwaggerDocConstants.SUCCESS_RESPONSE_CODE, responseSuccess);

                    Response responseError = new Response();
                    responseError.setDescription(SwaggerDocConstants.ERROR_OCCURRED);
                    Schema schemaError = new Schema();
                    schemaError.setRef(getSchemaRef(SwaggerDocConstants.GENERAL_ERROR));
                    schemaError.setType(SwaggerDocConstants.OBJECT);
                    responseError.setSchema(schemaError);
                    Map<String, String> examplesError = new HashMap<String, String>();
                    examplesError.put(SwaggerDocConstants.CONSUMES_JSON, SwaggerDocConstants.ERROR_PAYLOAD);
                    responseError.setExamples(examplesError);
                    responseMap.put(SwaggerDocConstants.ERROR_RESPONSE_CODE, responseError);
                    everythingOp.setResponses(responseMap);

                    everythingOperationsMap.put(SwaggerDocConstants.POST, everythingOp);
                    everything.setOperations(everythingOperationsMap);
                    pathMap.put(pathId, everything);
                }
            }

            createDefinition(SwaggerDocConstants.GENERAL_ERROR);
            createDefinition(SwaggerDocConstants.OPERATION_OUTCOME);
            createDefinition(SwaggerDocConstants.BUNDLE);
        }
        fullPaths.setPaths(pathMap);
        swaggerSpecification.setPaths(fullPaths);
    }

    private void createObjectDefinitions() {
        Definitions definitions = new Definitions();
        definitions.setDefinitions(definitionMap);
        swaggerSpecification.setDefinitions(definitions);
    }

    private void createDefinition(String resourceName) {
        String definitionName = resourceName;
        Definition definition = new Definition();
        definition.setType(SwaggerDocConstants.OBJECT);
        definitionMap.put(definitionName, definition);
    }

    /**
     * @return the swaggerSpecification
     */
    public SwaggerSpecification getSwaggerSpecification() {
        return swaggerSpecification;
    }


    private String createSwaggerSpecification() {
        String json = "";
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
            mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, true);
            mapper.setSerializationInclusion(Include.NON_NULL);
            mapper.getSerializerProvider().setNullKeySerializer(new NullSerializer());
            json = mapper.writeValueAsString(swaggerSpecification);
        } catch (Exception exp) {
            log.error("Error while creating object mapper", exp);
        }
        return json;
    }

    /**
     * @return the baseUrl
     */
    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * @param baseUrl the baseUrl to set
     */
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void addParameters() {
        Map<String, Parameter> parameters = new HashMap<String, Parameter>();
        Parameter parameter = new Parameter();
        parameter.setName(SwaggerDocConstants.FORMAT_PARAM);
        parameter.setDescription(SwaggerDocConstants.FORMAT_PARAM_DESC);
        parameter.setIn(SwaggerDocConstants.IN_QUERY);
        parameter.setRequired(false);
        parameters.put(SwaggerDocConstants.FORMAT_PARAM_NAME, parameter);
        swaggerSpecification.setParameters(parameters);
    }

    private String getSchemaRef(String resourceName) {
        return "#/definitions/" + resourceName;
    }

    private void setExamples(String resourceName, Response responseSuccess) {
        Map<String, String> examples = new HashMap<String, String>();
        if(SwaggerDocConstants.PERSON_RESOURCE.equalsIgnoreCase(resourceName)) {
            examples.put(SwaggerDocConstants.CONSUMES_JSON, SwaggerDocConstants.PERSON_PAYLOAD);
        } else if(SwaggerDocConstants.PATIENT_RESOURCE.equalsIgnoreCase(resourceName)) {
            examples.put(SwaggerDocConstants.CONSUMES_JSON, SwaggerDocConstants.PATIENT_PAYLOAD);
        } else if(SwaggerDocConstants.PRACTITIONER_RESOURCE.equalsIgnoreCase(resourceName)) {
            examples.put(SwaggerDocConstants.CONSUMES_JSON, SwaggerDocConstants.PRACTITIONER_PAYLOAD);
        } else if(SwaggerDocConstants.ENCOUNTER_RESOURCE.equalsIgnoreCase(resourceName)) {
            examples.put(SwaggerDocConstants.CONSUMES_JSON, SwaggerDocConstants.ENCOUNTER_PAYLOAD);
        } else if(SwaggerDocConstants.FAMILY_HISTORY_RESOURCE.equalsIgnoreCase(resourceName)) {
            examples.put(SwaggerDocConstants.CONSUMES_JSON, SwaggerDocConstants.FAMILY_HISTORY_PAYLOAD);
        } else if(SwaggerDocConstants.ALLERGY_RESOURCE.equalsIgnoreCase(resourceName)) {
            examples.put(SwaggerDocConstants.CONSUMES_JSON, SwaggerDocConstants.ALLERGY_PAYLOAD);
        } else if(SwaggerDocConstants.OBSERVATION_RESOURCE.equalsIgnoreCase(resourceName)) {
            examples.put(SwaggerDocConstants.CONSUMES_JSON, SwaggerDocConstants.OBSERVATION_PAYLOAD);
        } else if(SwaggerDocConstants.LOCATION_RESOURCE.equalsIgnoreCase(resourceName)) {
            examples.put(SwaggerDocConstants.CONSUMES_JSON, SwaggerDocConstants.LOCATION_PAYLOAD);
        } else {
            examples.put(SwaggerDocConstants.CONSUMES_JSON, SwaggerDocConstants.EMPTY);
        }
        responseSuccess.setExamples(examples);
    }
}
