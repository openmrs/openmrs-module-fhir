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
package org.openmrs.module.fhir.api;

import org.hl7.fhir.dstu3.model.MedicationRequest;

import java.util.List;

public interface MedicationRequestService {

    /**
     * Get drrug order by uuid
     *
     * @param uuid of the requesting drug order
     * @return drug order obj
     */
    MedicationRequest getMedicationRequestById(String uuid);

    /**
     * Search drug orders list by uuid
     *
     * @param uuid of the drug order
     * @return drug order obj
     */
    List<MedicationRequest> searchMedicationRequestById(String uuid);

    /**
     * Search drug orders by patient uuid
     *
     * @param patientUuid uuid of the patient which drug orders should returned
     * @return drug orders list
     */
    List<MedicationRequest> searchMedicationRequestByPatientId(String patientUuid);

    /**
     * Delete drug order by uuid
     *
     * @param uuid of the drug order to be deleted
     * @return drug order obj
     */
    void deleteMedicationRequest(String uuid);

    /**
     * Create medication request
     * @param medicationRequest fhir medication request
     * @return created MedicationRequest
     */
    MedicationRequest createFHIRMedicationRequest(MedicationRequest medicationRequest);

    /**
     * Update medication request
     * @param medicationRequest fhir medication request
     * @return created MedicationRequest
     */
    MedicationRequest updateFHIRMedicationRequest(MedicationRequest medicationRequest, String uuid);

}
