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

public class SwaggerDocConstants {

    public static final String VERSION = "1.0.0";
    public static final String UTF_8 = "UTF-8";
    public static final String HTTP = "http";
    public static final String HTTPS = "https";
    public static final String STR_EMPTY = "";
    public static final String HTTP_WITH_SLASHES = "http://";
    public static final String HTTPS_WITH_SLASHES = "https://";
    public static final String SLASHES = "://";
    public static final String COLON = ":";
    public static final String OPERATION_OUTCOME = "OperationOutcome";
    public static final String TITLE = "OpenMRS FHIR REST Services";
    public static final String DESCRIPTION = "Auto-generated documentation for OpenMRS FHIR Rest services";
    public static final String CONTACT_NAME = "OpenMRS FHIR Module Team";
    public static final String CONTACT_URL = "https://talk.openmrs.org/c/dev";
    public static final String CONTACT_EMAIL = "community@openmrs.org";
    public static final String LICENSE_NAME = "Mozilla Public License, v. 2.0";
    public static final String LICENSE_URL = "http://openmrs.org/license/";
    public static final String PRODUCES_XML = "application/xml";
    public static final String PRODUCES_JSON = "application/json";
    public static final String CONSUMES_XML = "application/xml";
    public static final String CONSUMES_JSON = "application/json";
    public static final String TERMS_AND_CONDITIONS = "https://www.mozilla.org/en-US/MPL/2.0/";
    public static final String BODY = "body";
    public static final String BUNDLE = "Bundle";
    public static final String FHIR_RESOURCE_CREATE = "create";
    public static final String FHIR_RESOURCE_update = "update";
    public static final String FHIR_RESOURCE_DELETE = "delete";
    public static final String FHIR_RESOURCE_READ= "read";
    public static final String FHIR_RESOURCE_SEARCH = "search";
    public static final String API_RESOURCE_ID = "{id}";
    public static final String SCHEMA_HTTP = "http";
    public static final String SCHEMA_HTTPS = "https";
    public static final String MORE_INFO = "Find more info here";
    public static final String DOCS_URL = "https://wiki.openmrs.org/display/projects/OpenMRS+FHIR+Module";
    public static final String READ = "read";
    public static final String CREATE = "create";
    public static final String UPDATE = "update";
    public static final String SEARCH_TYPE = "search-type";
    public static final String DELETE = "delete";
    public static final String GET = "get";
    public static final String PUT = "put";
    public static final String POST = "post";
    public static final String GET_DESCRIPTION = "Get";
    public static final String DELETE_DESCRIPTION = "Delete";
    public static final String BUNDLE_DESCRIPTION = "Bundle of";
    public static final String RESOURCES = "resources";
    public static final String CREATE_RESOURCE = "Create";
    public static final String UPDATR_RESOURCE = "Update";
    public static final String RESOURCE_BY_ID = "resource by id";
    public static final String READ_RESOURCE_PATH = "{id}";
    public static final String POST_RESOURCE_PATH = "{id}";
    public static final String UPDATE_RESOURCE_PATH = "{id}";
    public static final String DELETE_RESOURCE_PATH = "{id}";
    public static final String DETAILS_OF_GIVEN_ID = "details of given id";
    public static final String EVERYTHING_OF_GIVEN_ID = "everything of given id";
    public static final String CONTENT_OF_THE_REQUEST = "from the content of the request";
    public static final String RETURNS = "Returns";
    public static final String ID_DESCRIPTION = "Id of the ";
    public static final String RESOURCE = "resource";
    public static final String BODY_SAMPLE_VALUE = "constant body parameter";
    public static final String ID = "id";
    public static final String _ID = "_id";
    public static final String IN_PATH = "path";
    public static final String IN_QUERY = "query";
    public static final String IN_BODY = "body";
    public static final String SUCCESS_RESPONSE = "successResponse";
    public static final String SUCCESS_RESPONSE_CODE = "200";
    public static final String ERROR_RESPONSE = "errorResponse";
    public static final String OBJECT = "object";
    public static final String ARRAY = "array";
    public static final String ERROR_RESPONSE_CODE = "404";
    public static final String FORMAT_PARAM = "_format";
    public static final String GENERAL_ERROR = "GeneralError";
    public static final String EVERYTHING = "everything";
    public static final String EVERYTHING_WITH_DOLLAR_PREFIX = "$everything";
    public static final String OPERATION_DEFINITON = "OperationDefinition";
    public static final String STRUCTURE_DEFINITON = "StructureDefinition";
    public static final String ERROR_OCCURRED = "When error occurred";
    public static final String FORMAT_PARAM_NAME = "formatParam";
    public static final String NAME_PATIENT = "patient";
    public static final String SEARCH_RESOURCE = "Search";
    public static final String SEARCH_RESOURCE_BY_PARAMETERS = "by parameters";
    public static final String STRUCTURE_DEFINITION= "StructureDefinition";
    public static final String RETURNS_MATCHING_RESTULS = "matching results";
    public static final String RETURNS_SUCCESS_OPERATION_OUTCOME = "Returns success operation outcome";
    public static final String FORMAT_PARAM_DESC = "Format parameter can use to get response by setting _fromat param value " +
                                                    " from xml by _format=xml and response from json by _format=json";
    public static final String PATIENT_RESOURCE = "Patient";
    public static final String PERSON_RESOURCE = "Person";
    public static final String ENCOUNTER_RESOURCE = "Encounter";
    public static final String ALLERGY_RESOURCE = "AllergyIntolerance";
    public static final String OBSERVATION_RESOURCE = "Observation";
    public static final String LOCATION_RESOURCE = "Location";
    public static final String PLAN_DEFINITION_RESOURCE = "PlanDefinition";
    public static final String FAMILY_HISTORY_RESOURCE = "FamilyMemberHistory";
    public static final String PRACTITIONER_RESOURCE = "Practitioner";
    public static final String EMPTY = "{\n" +
            "  \"message\": \"Please refer docs\"\n" +
            "}";
    public static final String ERROR_PAYLOAD = "{\"resourceType\":\"OperationOutcome\",\"issue\":[{\"severity\":\"error\",\"details\":\"Details of error\"}]}";
    public static final String SUCCESS_PAYLOAD = "{\"resourceType\":\"OperationOutcome\",\"issue\":[{\"severity\":\"information\",\"details\":\"Details of operation\"}]}";
    public static final String PATIENT_PAYLOAD = "{\n" +
            "  \"resourceType\": \"Patient\",\n" +
            "  \"id\": \"example\",\n" +
            "  \"identifier\": [\n" +
            "    {\n" +
            "      \"use\": \"usual\",\n" +
            "      \"label\": \"MRN\",\n" +
            "      \"system\": \"urn:oid:1.2.36.146.595.217.0.1\",\n" +
            "      \"value\": \"12345\",\n" +
            "      \"period\": {\n" +
            "        \"start\": \"2001-05-06\"\n" +
            "      },\n" +
            "      \"assigner\": {\n" +
            "        \"display\": \"Acme Healthcare\"\n" +
            "      }\n" +
            "    }\n" +
            "  ],\n" +
            "  \"name\": [\n" +
            "    {\n" +
            "      \"use\": \"official\",\n" +
            "      \"family\": [\n" +
            "        \"Chalmers\"\n" +
            "      ],\n" +
            "      \"given\": [\n" +
            "        \"Peter\",\n" +
            "        \"James\"\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"use\": \"usual\",\n" +
            "      \"given\": [\n" +
            "        \"Jim\"\n" +
            "      ]\n" +
            "    }\n" +
            "  ],\n" +
            "  \"telecom\": [\n" +
            "    {\n" +
            "      \"use\": \"home\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"system\": \"phone\",\n" +
            "      \"value\": \"(03) 5555 6473\",\n" +
            "      \"use\": \"work\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"gender\": \"male\",\n" +
            "  \"birthDate\": \"1974-12-25\",\n" +
            "  \"deceasedBoolean\": false,\n" +
            "  \"address\": [\n" +
            "    {\n" +
            "      \"use\": \"home\",\n" +
            "      \"line\": [\n" +
            "        \"534 Erewhon St\"\n" +
            "      ],\n" +
            "      \"city\": \"PleasantVille\",\n" +
            "      \"state\": \"Vic\",\n" +
            "      \"postalCode\": \"3999\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"active\": true\n" +
            "}";

    public static final String OBSERVATION_PAYLOAD = "{\n" +
            "  \"resourceType\": \"Observation\",\n" +
            "  \"name\": [\n" +
            "    {\n" +
            "      \"coding\": {\n" +
            "        \"system\": \"UNSPECIFIED\",\n" +
            "        \"code\": \"39156-5\",\n" +
            "        \"display\": \"Body Mass Index\"\n" +
            "      }\n" +
            "    }\n" +
            "  ],\n" +
            "  \"valueQuantity\": {\n" +
            "    \"value\": 18.14,\n" +
            "    \"units\": \"kg/m2\",\n" +
            "    \"system\": \"http://unitsofmeasure.org\",\n" +
            "    \"code\": \"kg/m2\"\n" +
            "  },\n" +
            "  \"appliesDateTime\": \"2011-05-17T00:00:00\",\n" +
            "  \"issued\": \"2014-09-13T22:02:34.000\",\n" +
            "  \"status\": \"final\",\n" +
            "  \"reliability\": \"ok\",\n" +
            "  \"subject\": {\n" +
            "    \"reference\": \"Patient/dda12af7-1691-11df-97a5-7038c432aabf\",\n" +
            "    \"display\": \"Valentine Ekero(Identifier:8079AM-6)\"\n" +
            "  },\n" +
            "  \"referenceRange\": [\n" +
            "    {\n" +
            "      \"low\": {\n" +
            "        \"value\": 0\n" +
            "      },\n" +
            "      \"high\": {\n" +
            "        \"value\": 100\n" +
            "      }\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    public static final String LOCATION_PAYLOAD = "{\n" +
            "  \"resourceType\": \"Location\",\n" +
            "  \"id\": \"a846f32f-5401-4d53-871a-68354c22c3f9\",\n" +
            "  \"name\": \"South Wing, second floor\",\n" +
            "  \"description\": \"Second floor of the Old South Wing, formerly in use by Psychiatry\",\n" +
            "  \"address\": {\n" +
            "    \"use\": \"work\",\n" +
            "    \"line\": [\n" +
            "      \"Galapagosweg 91, Building A\",\n" +
            "      \"line 3\",\n" +
            "      \"line 4\",\n" +
            "      \"line 5\"\n" +
            "    ],\n" +
            "    \"city\": \"Den Burg\",\n" +
            "    \"postalCode\": \"9105 PZ\",\n" +
            "    \"country\": \"NLD\",\n" +
            "    \"state\": \"southern\"\n" +
            "  },\n" +
            "  \"position\": {\n" +
            "    \"longitude\": -83.6945691,\n" +
            "    \"latitude\": 42.25475478\n" +
            "  },\n" +
            "  \"partOf\": {\n" +
            "    \"reference\": \"Location/c0937d4f-1691-11df-97a5-7038c432aabf\",\n" +
            "    \"display\": \"Mosoriot Hospital\"\n" +
            "  },\n" +
            "  \"status\": \"active\"\n" +
            "}";

    public static final String ENCOUNTER_PAYLOAD = "{\n" +
            "  \"resourceType\": \"Encounter\",\n" +
            "  \"status\": \"finished\",\n" +
            "  \"class\": \"inpatient\",\n" +
            "  \"subject\": {\n" +
            "    \"reference\": \"Patient/dd738d54-1691-11df-97a5-7038c432aabf\",\n" +
            "    \"display\": \"Daisylene Ekeno(Identifier:1865TU-8)\"\n" +
            "  },\n" +
            "  \"participant\": [\n" +
            "    {\n" +
            "      \"individual\": {\n" +
            "        \"reference\": \"Practitioner/bf218490-1691-11df-97a5-7038c432aabf\",\n" +
            "        \"display\": \"Super User(Identifier:admin)\"\n" +
            "      }\n" +
            "    }\n" +
            "  ],\n" +
            "  \"period\": {\n" +
            "    \"start\": \"2006-02-07T00:00:00\",\n" +
            "    \"end\": \"2006-02-07T00:00:00\"\n" +
            "  },\n" +
            "  \"location\": [\n" +
            "    {\n" +
            "      \"location\": {\n" +
            "        \"reference\": \"Location/8d6c993e-c2cc-11de-8d13-0010c6dffd0f\",\n" +
            "        \"display\": \"Inpatient Ward\"\n" +
            "      },\n" +
            "      \"period\": {\n" +
            "        \"start\": \"2006-02-07T00:00:00\",\n" +
            "        \"end\": \"2006-02-07T00:00:00\"\n" +
            "      }\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    public static final String PRACTITIONER_PAYLOAD = "{\n" +
            "  \"resourceType\": \"Practitioner\",\n" +
            "  \"id\": \"a846f32f-5401-4d53-871a-68354c22c3f9\",\n" +
            "  \"identifier\": [\n" +
            "    {\n" +
            "      \"system\": \"http://www.acme.org/practitioners\",\n" +
            "      \"value\": \"clerk\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"name\": {\n" +
            "    \"family\": [\n" +
            "      \"Careful\"\n" +
            "    ],\n" +
            "    \"given\": [\n" +
            "      \"Adam\"\n" +
            "    ]\n" +
            "  },\n" +
            "  \"address\": [\n" +
            "    {\n" +
            "      \"use\": \"home\",\n" +
            "      \"city\": \"E. Kanateng\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"gender\": {\n" +
            "    \"coding\": [\n" +
            "      {\n" +
            "        \"system\": \"http://hl7.org/fhir/v3/AdministrativeGender\",\n" +
            "        \"code\": \"M\"\n" +
            "      }\n" +
            "    ]\n" +
            "  },\n" +
            "  \"birthDate\": \"2009-08-11T00:00:00\"\n" +
            "}";

    public static final String FAMILY_HISTORY_PAYLOAD = "{\n" +
            "  \"resourceType\": \"FamilyHistory\",\n" +
            "  \"id\": \"dda12af7-1691-11df-97a5-7038c432aabf\",\n" +
            "  \"patient\": {\n" +
            "    \"reference\": \"Patient/example\",\n" +
            "    \"display\": \"Peter Patient\"\n" +
            "  },\n" +
            "  \"relation\": [\n" +
            "    {\n" +
            "      \"relationship\": {\n" +
            "        \"coding\": [\n" +
            "          {\n" +
            "            \"system\": \"http://hl7.org/fhir/familial-relationship\",\n" +
            "            \"code\": \"father\"\n" +
            "          }\n" +
            "        ]\n" +
            "      },\n" +
            "      \"condition\": [\n" +
            "        {\n" +
            "          \"type\": {\n" +
            "            \"coding\": [\n" +
            "              {\n" +
            "                \"system\": \"http://snomed.info/sct\",\n" +
            "                \"code\": \"315619001\",\n" +
            "                \"display\": \"Myocardial Infarction\"\n" +
            "              }\n" +
            "            ],\n" +
            "            \"text\": \"Heart Attack\"\n" +
            "          },\n" +
            "          \"onsetAge\": {\n" +
            "            \"value\": 74,\n" +
            "            \"units\": \"a\",\n" +
            "            \"system\": \"http://unitsofmeasure.org\"\n" +
            "          },\n" +
            "          \"note\": \"Was fishing at the time. At least he went doing someting he loved.\"\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  ]\n" +
            "}";
    public static final String PERSON_PAYLOAD = "{\n" +
            "  \"resourceType\": \"Person\",\n" +
            "  \"id\": \"dda12af7-1691-11df-97a5-7038c432aabf\",\n" +
            "  \"name\": [\n" +
            "    {\n" +
            "      \"use\": \"official\",\n" +
            "      \"family\": [\n" +
            "        \"Chalmers\"\n" +
            "      ],\n" +
            "      \"given\": [\n" +
            "        \"Peter\",\n" +
            "        \"James\"\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"use\": \"usual\",\n" +
            "      \"given\": [\n" +
            "        \"Jim\"\n" +
            "      ]\n" +
            "    }\n" +
            "  ],\n" +
            "  \"gender\": \"male\",\n" +
            "  \"birthDate\": \"1974-12-25\",\n" +
            "  \"address\": [\n" +
            "    {\n" +
            "      \"use\": \"home\",\n" +
            "      \"line\": [\n" +
            "        \"534 Erewhon St\"\n" +
            "      ],\n" +
            "      \"city\": \"PleasantVille\",\n" +
            "      \"state\": \"Vic\",\n" +
            "      \"postalCode\": \"3999\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"active\": true\n" +
            "}";

    public static final String ALLERGY_PAYLOAD = "{\n" +
            "  \"resourceType\": \"AllergyIntolerance\",\n" +
            "  \"id\": \"249d8c08-f453-4e7d-a46d-f68659e7d9e7\",\n" +
            "  \"recordedDate\": \"2015-03-31T00:00:00\",\n" +
            "  \"subject\": {\n" +
            "    \"reference\": \"Patient/dd9a7551-1691-11df-97a5-7038c432aabf\",\n" +
            "    \"display\": \"John Shavola(Identifier:7279MP-4)\"\n" +
            "  },\n" +
            "  \"substance\": {\n" +
            "    \"coding\": [\n" +
            "      {\n" +
            "        \"system\": \"http://openmrs.org\",\n" +
            "        \"code\": \"be4d5acd-1691-11df-97a5-7038c432aabf\",\n" +
            "        \"display\": \"AMYLASE\"\n" +
            "      }\n" +
            "    ]\n" +
            "  },\n" +
            "  \"criticality\": \"unassessible\"\n" +
            "}";

    public static final String PLAN_DEFINITION_PAYLOAD = "{\n"
            + "  \"resourceType\": \"PlanDefinition\",\n"
            + "  \"id\": \"644eef7d-087a-48ac-80b5-9f5a9db1a0a6\",\n"
            + "  \"identifier\": [{\n"
            + "    \"value\": \"644eef7d-087a-48ac-80b5-9f5a9db1a0a6\"\n"
            + "  }],\n"
            + "  \"name\": \"Program name\",\n"
            + "  \"description\": \"Test program description\",\n"
            + "  \"goal\": [{\n"
            + "    \"description\": {\n"
            + "      \"coding\": [{\n"
            + "          \"system\": \"http://ciel.org\",\n"
            + "          \"code\": \"138405\"\n"
            + "        },\n"
            + "        {\n"
            + "          \"system\": \"http://hl7.org/fhir/sid/icd-10\",\n"
            + "          \"code\": \"B24\"\n"
            + "        }\n"
            + "      ],\n"
            + "      \"text\": \"HUMAN IMMUNODEFICIENCY VIRUS (HIV) DISEASE\"\n"
            + "    },\n"
            + "    \"target\": [{\n"
            + "      \"detailCodeableConcept\": {\n"
            + "        \"coding\": [{\n"
            + "            \"system\": \"http://ciel.org\",\n"
            + "            \"code\": \"5340\"\n"
            + "          },\n"
            + "          {\n"
            + "            \"system\": \"http://www.pih.org/\",\n"
            + "            \"code\": \"5340\"\n"
            + "          }\n"
            + "        ],\n"
            + "        \"text\": \"HIV STAGING  - CANDIDIASIS, ORORESPIRATORY TRACT\"\n"
            + "      }\n"
            + "    }]\n"
            + "  }]\n"
            + "}";
}
