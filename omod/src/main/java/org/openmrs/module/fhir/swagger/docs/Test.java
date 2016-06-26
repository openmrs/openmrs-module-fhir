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
package org.openmrs.module.fhir.swagger.docs;

public class Test {

public static String test = "{\n" +
        "  \"swagger\": \"2.0\",\n" +
        "  \"info\": {\n" +
        "    \"version\": \"1.0.0\",\n" +
        "    \"title\": \"FHIR OpenMRS APIs\",\n" +
        "    \"description\": \"Sample for demonstrate OpenMRS FHIR APIs\",\n" +
        "    \"termsOfService\": \"http://swagger.io/terms/\"\n" +
        "  },\n" +
        "  \"host\": \"localhost\",\n" +
        "  \"basePath\": \"/openmrs/ws/fhir/\",\n" +
        "  \"schemes\": [\n" +
        "    \"http\"\n" +
        "  ],\n" +
        "  \"consumes\": [\n" +
        "    \"application/json\"\n" +
        "  ],\n" +
        "  \"produces\": [\n" +
        "    \"application/json\"\n" +
        "  ],\n" +
        "  \"paths\": {\n" +
        "    \"/Patient\": {\n" +
        "      \"post\": {\n" +
        "        \"description\": \"Creates a new new patient\",\n" +
        "        \"operationId\": \"create new patient\",\n" +
        "        \"produces\": [\n" +
        "          \"application/json\",\n" +
        "          \"application/xml\"\n" +
        "        ],\n" +
        "        \"parameters\": [\n" +
        "          {\n" +
        "            \"name\": \"Patient\",\n" +
        "            \"in\": \"body\",\n" +
        "            \"description\": \"Add patient to the OpenMRS\",\n" +
        "            \"required\": true,\n" +
        "            \"schema\": {\n" +
        "              \"$ref\": \"#/definitions/Patient\"\n" +
        "            }\n" +
        "          }\n" +
        "        ],\n" +
        "        \"responses\": {\n" +
        "          \"200\": {\n" +
        "            \"description\": \"FHIR server response\",\n" +
        "            \"schema\": {\n" +
        "              \"$ref\": \"#/definitions/Patient\"\n" +
        "            }\n" +
        "          },\n" +
        "          \"default\": {\n" +
        "            \"description\": \"unexpected error\",\n" +
        "            \"schema\": {\n" +
        "              \"$ref\": \"#/definitions/ErrorModel\"\n" +
        "            }\n" +
        "          }\n" +
        "        }\n" +
        "      },\n" +
        "      \"get\": {\n" +
        "        \"description\": \"Returns a Patient based on a single ID\",\n" +
        "        \"operationId\": \"findPatientById\",\n" +
        "        \"produces\": [\n" +
        "          \"application/json\",\n" +
        "          \"application/xml\"\n" +
        "        ],\n" +
        "        \"parameters\": [\n" +
        "          {\n" +
        "            \"name\": \"id\",\n" +
        "            \"in\": \"path\",\n" +
        "            \"description\": \"ID of patient to fetch\",\n" +
        "            \"required\": true,\n" +
        "            \"type\": \"integer\",\n" +
        "            \"format\": \"int64\"\n" +
        "          },\n" +
        "          {\n" +
        "            \"name\": \"identifier\",\n" +
        "            \"in\": \"query\",\n" +
        "            \"description\": \"Identifire of patient to fetch\",\n" +
        "            \"required\": true,\n" +
        "            \"type\": \"integer\",\n" +
        "            \"format\": \"int64\"\n" +
        "          },\n" +
        "          {\n" +
        "            \"name\": \"_id\",\n" +
        "            \"in\": \"query\",\n" +
        "            \"description\": \"ID of patient to fetch\",\n" +
        "            \"required\": true,\n" +
        "            \"type\": \"integer\",\n" +
        "            \"format\": \"int64\"\n" +
        "          },\n" +
        "          {\n" +
        "            \"name\": \"name\",\n" +
        "            \"in\": \"query\",\n" +
        "            \"description\": \"Name of patient to fetch\",\n" +
        "            \"required\": true,\n" +
        "            \"type\": \"integer\",\n" +
        "            \"format\": \"int64\"\n" +
        "          },\n" +
        "          {\n" +
        "            \"name\": \"given\",\n" +
        "            \"in\": \"query\",\n" +
        "            \"description\": \"Given Name of patient to fetch\",\n" +
        "            \"required\": true,\n" +
        "            \"type\": \"integer\",\n" +
        "            \"format\": \"int64\"\n" +
        "          },\n" +
        "          {\n" +
        "            \"name\": \"active\",\n" +
        "            \"in\": \"query\",\n" +
        "            \"description\": \"Active Records\",\n" +
        "            \"required\": true,\n" +
        "            \"type\": \"integer\",\n" +
        "            \"format\": \"int64\"\n" +
        "          }\n" +
        "        ],\n" +
        "        \"responses\": {\n" +
        "          \"200\": {\n" +
        "            \"description\": \"FHIR response\",\n" +
        "            \"schema\": {\n" +
        "              \"$ref\": \"#/definitions/Patient\"\n" +
        "            }\n" +
        "          },\n" +
        "          \"default\": {\n" +
        "            \"description\": \"unexpected error\",\n" +
        "            \"schema\": {\n" +
        "              \"$ref\": \"#/definitions/ErrorModel\"\n" +
        "            }\n" +
        "          }\n" +
        "        }\n" +
        "      }\n" +
        "    },\n" +
        "    \"/Patient/{id}\": {\n" +
        "      \"get\": {\n" +
        "        \"description\": \"Returns a Patient based on a single ID\",\n" +
        "        \"operationId\": \"findPatientById\",\n" +
        "        \"produces\": [\n" +
        "          \"application/json\",\n" +
        "          \"application/xml\"\n" +
        "        ],\n" +
        "        \"parameters\": [\n" +
        "          {\n" +
        "            \"name\": \"id\",\n" +
        "            \"in\": \"path\",\n" +
        "            \"description\": \"ID of patient to fetch\",\n" +
        "            \"required\": true,\n" +
        "            \"type\": \"integer\",\n" +
        "            \"format\": \"int64\"\n" +
        "          }\n" +
        "        ],\n" +
        "        \"responses\": {\n" +
        "          \"200\": {\n" +
        "            \"description\": \"FHIR response\",\n" +
        "            \"schema\": {\n" +
        "              \"$ref\": \"#/definitions/Patient\"\n" +
        "            }\n" +
        "          },\n" +
        "          \"default\": {\n" +
        "            \"description\": \"unexpected error\",\n" +
        "            \"schema\": {\n" +
        "              \"$ref\": \"#/definitions/ErrorModel\"\n" +
        "            }\n" +
        "          }\n" +
        "        }\n" +
        "      },\n" +
        "      \"put\": {\n" +
        "        \"description\": \"Updates patient\",\n" +
        "        \"operationId\": \"update patient\",\n" +
        "        \"produces\": [\n" +
        "          \"application/json\",\n" +
        "          \"application/xml\"\n" +
        "        ],\n" +
        "        \"parameters\": [\n" +
        "          {\n" +
        "            \"name\": \"Patient\",\n" +
        "            \"in\": \"body\",\n" +
        "            \"description\": \"Add patient to the OpenMRS\",\n" +
        "            \"required\": true,\n" +
        "            \"schema\": {\n" +
        "              \"$ref\": \"#/definitions/Patient\"\n" +
        "            }\n" +
        "          }\n" +
        "        ],\n" +
        "        \"responses\": {\n" +
        "          \"200\": {\n" +
        "            \"description\": \"FHIR server response\",\n" +
        "            \"schema\": {\n" +
        "              \"$ref\": \"#/definitions/Patient\"\n" +
        "            }\n" +
        "          },\n" +
        "          \"default\": {\n" +
        "            \"description\": \"unexpected error\",\n" +
        "            \"schema\": {\n" +
        "              \"$ref\": \"#/definitions/ErrorModel\"\n" +
        "            }\n" +
        "          }\n" +
        "        }\n" +
        "      },\n" +
        "      \"delete\": {\n" +
        "        \"description\": \"deletes a single Patient based on the ID supplied\",\n" +
        "        \"operationId\": \"deletePatient\",\n" +
        "        \"parameters\": [\n" +
        "          {\n" +
        "            \"name\": \"id\",\n" +
        "            \"in\": \"path\",\n" +
        "            \"description\": \"ID of Patient to delete\",\n" +
        "            \"required\": true,\n" +
        "            \"type\": \"integer\",\n" +
        "            \"format\": \"int64\"\n" +
        "          }\n" +
        "        ],\n" +
        "        \"responses\": {\n" +
        "          \"204\": {\n" +
        "            \"description\": \"Patient deleted\"\n" +
        "          },\n" +
        "          \"default\": {\n" +
        "            \"description\": \"unexpected error\",\n" +
        "            \"schema\": {\n" +
        "              \"$ref\": \"#/definitions/ErrorModel\"\n" +
        "            }\n" +
        "          }\n" +
        "        }\n" +
        "      }\n" +
        "    }\n" +
        "  },\n" +
        "  \"definitions\": {\n" +
        "    \"Patient\": {\n" +
        "      \"type\": \"object\",\n" +
        "      \"required\": [\n" +
        "        \"name\"\n" +
        "      ],\n" +
        "      \"properties\": {\n" +
        "        \"name\": {\n" +
        "          \"type\": \"string\"\n" +
        "        },\n" +
        "        \"identifier\": {\n" +
        "          \"type\": \"string\"\n" +
        "        }\n" +
        "      }\n" +
        "    },\n" +
        "    \"ErrorModel\": {\n" +
        "      \"type\": \"object\",\n" +
        "      \"required\": [\n" +
        "        \"code\",\n" +
        "        \"message\"\n" +
        "      ],\n" +
        "      \"properties\": {\n" +
        "        \"code\": {\n" +
        "          \"type\": \"integer\",\n" +
        "          \"format\": \"int32\"\n" +
        "        },\n" +
        "        \"message\": {\n" +
        "          \"type\": \"string\"\n" +
        "        }\n" +
        "      }\n" +
        "    }\n" +
        "  }\n" +
        "}";
}
