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
package org.openmrs.module.fhir.api.util;

public final class FHIRConstants {

	public static Map<String, Map<String, String>>
	static {

	}
	//Concept source URIs
	public static final String loinc = "http://loinc.org";
	public static final String openmrs = "http://openmrs.org";
	public static final String snomed = "http://snomed.info/sct";
	public static final String ciel = "http://ciel.org";
	public static final String other = "UNSPECIFIED";
	//Concept Sources
	public static final String LOINC = "LOINC";
	public static final String CIEL = "CIEL";
	public static final String SNOMED = "SNOMED";
	public static final String SNOMED_CT = "SNOMED CT";
	public static final String SNOMED_NP = "SNOMED NP";
	public static final String ICD_10_WHO = "ICD-10-WHO";
	public static final String RX_NORM = "RxNORM";
	public static final String PIH = "PIH";
	public static final String PIH_MALAWI = "PIH Malawi";
	public static final String AMPATH = "AMPATH";
	public static final String SNOMED_MVP = "SNOMED MVP";
	public static final String MDRTB = "org.openmrs.module.mdrtb";
	public static final String HL7_2X = "HL7 2.x Route of Administration";
	public static final String BT = "3BT";
	public static final String ICPC2 = "ICPC2";
	public static final String EMRAPI = "org.openmrs.module.emrapi";
	public static final String IMO_PROBLEM = "IMO ProblemIT";
	public static final String IMP_PROCEDURE = "IMO ProcedureIT";
	public static final String NDF_RT_NUI = "NDF-RT NUI";


	public static final String URN = "urn";
	public static final String UUID = "uuid";
	public static final String LOCATION = "Location";
	public static final String ENCOUNTER = "Encounter";
	public static final String IDENTIFIER = "Identifier";
	public static final String PATIENT = "Patient";
	public static final String PRACTITIONER = "Practitioner";
	public static final String OBSERVATION = "Observation";
	public static final String NUMERIC_CONCEPT_MEASURE_URI = "http://unitsofmeasure.org";

	//HL47 Abbrevations
	public static final String NM_HL7_ABBREVATION = "NM";
	public static final String CWE_HL7_ABBREVATION = "CWE";
	public static final String ST_HL7_ABBREVATION = "ST";
	public static final String ZZ_HL7_ABBREVATION = "ZZ";
	public static final String RP_HL7_ABBREVATION = "RP";
	public static final String DT_HL7_ABBREVATION = "DT";
	public static final String TM_HL7_ABBREVATION = "TM";
	public static final String TS_HL7_ABBREVATION = "TS";
	public static final String SN_HL7_ABBREVATION = "SN";
	public static final String ED_HL7_ABBREVATION = "ED";
	public static final String BIT_HL7_ABBREVATION = "BIT";
	public static final String LOCATION_EXTENTION_URI = "http://resources.openmrs.org/doc/fhir/profiles/vitalsigns"
	                                                    + ".xml#location";
	public static final String ENCOUNTER_EXTENTION_URI = "http://resources.openmrs.org/doc/fhir/profiles/vitalsigns"
	                                                     + ".xml#encounter";
	public static final String WEB_SERVICES_URI_PREFIX = "ws/rest";
	public static final String PERSON = "person";
	public static final String CONCEPT = "concept";
	public static final String CONFIDENTIALITY_CODING_R = "R";
	public static final String CONFIDENTIALITY_CODING_VALUE_RESTRICTED = "restricted";
	public static final String ENCOUNTER_ID = "encounter_id";
	public static final String MALE = "M";
	public static final String FEMALE = "F";
	public static final String ACTIVE_LIST_ALLERGY_STRATEGY = "ActiveListAllergyStrategy";
	public static final String ALLERGY_API_ALLERGY_STRATEGY = "AllergyApiModuleAllergyStrategy";
	public static final String OBS_ALLERGY_STRATEGY = "ObsAllergyStrategy";
}
