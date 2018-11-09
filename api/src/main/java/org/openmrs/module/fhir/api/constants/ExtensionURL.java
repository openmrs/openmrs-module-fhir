package org.openmrs.module.fhir.api.constants;

public final class ExtensionURL {

    private ExtensionURL() { }

    /** https://simplifier.net/SRxProject/resource-date-created/~json */
    public static final String DATE_CREATED_URL = "http://fhir-es.transcendinsights.com/stu3/StructureDefinition/resource-date-created";

    /** https://simplifier.net/eLabTest/Creator-crew-version1/~json */
    public static final String CREATOR_URL = "https://purl.org/elab/fhir/StructureDefinition/Creator-crew-version1";

    // Local extensions
    public static final String CHANGED_BY_URL = "changedBy";
    public static final String DATE_CHANGED_URL = "dateChanged";
    public static final String VOIDED_URL = "voided";
    public static final String DATE_VOIDED_URL = "dateVoided";
    public static final String VOIDED_BY_URL = "voidedBy";
    public static final String VOID_REASON_URL = "voidReason";
    public static final String RETIRED_URL = "retired";
    public static final String DATE_RETIRED_URL = "dateRetired";
    public static final String RETIRED_BY_URL = "retiredBy";
    public static final String RETIRE_REASON_URL = "retireReason";

    public static final String DESCRIPTION_URL = "description";

    public static final String AS_NEEDED_CONDITION = "asNeededCondition";
    public static final String DOSING_TYPE = "dosingType";
    public static final String NUM_REFILLS = "numRefills";
    public static final String BRAND_NAME = "brandName";
    public static final String DISPENSE_AS_WRITTEN = "dispenseAsWritten";
    public static final String DRUG_NON_CODED = "drugNonCoded";
    public static final String CARE_SETTING = "careSetting";
    public static final String ORDER_CONCEPT_URL = "orderConcept";
    public static final String LATERALITY_URL = "laterality";
    public static final String CLINICAL_HISTORY_URL = "clinicalHistory";
    public static final String ORDER_FREQUENCY_URL = "orderFrequency";
}
