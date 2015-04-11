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

import java.util.HashMap;
import java.util.Map;

public final class FHIRConstants {

	public static Map<String, ConceptSourceNameURIPair> conceptSourceMap = new HashMap<String, ConceptSourceNameURIPair>();
	public static Map<String, String> conceptSourceURINameMap = new HashMap<String, String>();
	public static final String OPENMRS_URI = "http://openmrs.org";

	//Concept source URIs
	public static final String LOINC_URI = "http://loinc.org";
	public static final String SNOMED_CT_URI = "http://snomed.info/sct";
	public static final String SNOMED_URI = "http://snomed.info";
	public static final String SNOMED_NP_URI = "http://snomed.info/snp";
	public static final String SNOMED_MVP_URI = "http://snomed.info/smvp";
	public static final String CIEL_URI = "http://ciel.org";
	public static final String ICD_10_WHO_URI = "http://hl7.org/fhir/sid/icd-10";
	public static final String RX_NORM_URI = "http://www.nlm.nih.gov/research/umls/rxnorm/";
	public static final String PIH_URI = "http://www.pih.org/";
	public static final String PIH_MALAWI_URI = "http://www.pih.org/country/malawi";
	public static final String AMPATH_URI = "http://ampath.com/";
	public static final String MDRTB_URI = "org.openmrs.module.mdrtb";
	public static final String HL7_2X_URI = "http://www.hl7.org/implement/standards";
	public static final String BT_3_URI = "http://www.semantichealth.org/pubdoc.html";
	public static final String ICPC2_URI = "http://www.who.int/classifications/icd/adaptations/icpc2/en/";
	public static final String EMRAPI_URI = "org.openmrs.module.emrapi";
	public static final String IMO_PROBLEM_URI = "https://www.e-imo.com/releases/problem-it";
	public static final String IMP_PROCEDURE_URI = "https://www.e-imo.com/releases/problem-it";
	public static final String NDF_RT_NUI_URI = "http://www.nlm.nih.gov/research/umls/sourcereleasedocs/current/NDFRT/";
	public static final String OTHER = "UNSPECIFIED";
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
	public static final String BT_3 = "3BT";
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
	public static final String OPENMRS_CONCEPT_CODING_SYSTEM = "OPENMRS";

	static {
		conceptSourceMap.put(LOINC.toLowerCase(), new ConceptSourceNameURIPair(LOINC, LOINC_URI));
		conceptSourceMap.put(CIEL.toLowerCase(), new ConceptSourceNameURIPair(CIEL, CIEL_URI));
		conceptSourceMap.put(SNOMED.toLowerCase(), new ConceptSourceNameURIPair(SNOMED, SNOMED_URI));
		conceptSourceMap.put(SNOMED_CT.toLowerCase(), new ConceptSourceNameURIPair(SNOMED_CT, SNOMED_CT_URI));
		conceptSourceMap.put(SNOMED_NP.toLowerCase(), new ConceptSourceNameURIPair(SNOMED_NP, SNOMED_NP_URI));
		conceptSourceMap.put(ICD_10_WHO.toLowerCase(), new ConceptSourceNameURIPair(ICD_10_WHO, ICD_10_WHO_URI));
		conceptSourceMap.put(RX_NORM.toLowerCase(), new ConceptSourceNameURIPair(RX_NORM, RX_NORM_URI));
		conceptSourceMap.put(PIH_MALAWI.toLowerCase(), new ConceptSourceNameURIPair(PIH_MALAWI, PIH_MALAWI_URI));
		conceptSourceMap.put(PIH.toLowerCase(), new ConceptSourceNameURIPair(PIH, PIH_URI));
		conceptSourceMap.put(AMPATH.toLowerCase(), new ConceptSourceNameURIPair(AMPATH, AMPATH_URI));
		conceptSourceMap.put(SNOMED_MVP.toLowerCase(), new ConceptSourceNameURIPair(SNOMED_MVP, SNOMED_MVP_URI));
		conceptSourceMap.put(HL7_2X.toLowerCase(), new ConceptSourceNameURIPair(HL7_2X, HL7_2X_URI));
		conceptSourceMap.put(BT_3.toLowerCase(), new ConceptSourceNameURIPair(BT_3, BT_3_URI));
		conceptSourceMap.put(ICPC2.toLowerCase(), new ConceptSourceNameURIPair(ICPC2, ICPC2_URI));
		conceptSourceMap.put(EMRAPI.toLowerCase(), new ConceptSourceNameURIPair(EMRAPI, EMRAPI_URI));
		conceptSourceMap.put(MDRTB.toLowerCase(), new ConceptSourceNameURIPair(MDRTB, MDRTB_URI));
		conceptSourceMap.put(IMO_PROBLEM.toLowerCase(), new ConceptSourceNameURIPair(IMO_PROBLEM, IMO_PROBLEM_URI));
		conceptSourceMap.put(IMP_PROCEDURE.toLowerCase(), new ConceptSourceNameURIPair(IMP_PROCEDURE, IMP_PROCEDURE_URI));
		conceptSourceMap.put(NDF_RT_NUI.toLowerCase(), new ConceptSourceNameURIPair(NDF_RT_NUI, NDF_RT_NUI_URI));

		conceptSourceURINameMap.put(LOINC_URI, LOINC);
		conceptSourceURINameMap.put(CIEL_URI, CIEL);
		conceptSourceURINameMap.put(SNOMED_MVP_URI, SNOMED_MVP);
		conceptSourceURINameMap.put(SNOMED_NP_URI, SNOMED_NP);
		conceptSourceURINameMap.put(SNOMED_URI, SNOMED);
		conceptSourceURINameMap.put(SNOMED_CT_URI, SNOMED_CT);
		conceptSourceURINameMap.put(ICD_10_WHO_URI, ICD_10_WHO);
		conceptSourceURINameMap.put(RX_NORM_URI, RX_NORM);
		conceptSourceURINameMap.put(PIH_MALAWI_URI, PIH_MALAWI);
		conceptSourceURINameMap.put(AMPATH_URI, AMPATH);
		conceptSourceURINameMap.put(PIH_URI, PIH);
		conceptSourceURINameMap.put(HL7_2X_URI, HL7_2X);
		conceptSourceURINameMap.put(BT_3_URI, BT_3);
		conceptSourceURINameMap.put(ICPC2_URI, ICPC2);
		conceptSourceURINameMap.put(IMO_PROBLEM_URI, EMRAPI);
		conceptSourceURINameMap.put(MDRTB_URI, MDRTB);
		conceptSourceURINameMap.put(IMO_PROBLEM_URI, IMO_PROBLEM);
		conceptSourceURINameMap.put(IMP_PROCEDURE_URI, IMP_PROCEDURE);
		conceptSourceURINameMap.put(NDF_RT_NUI_URI, NDF_RT_NUI);
	}
}
