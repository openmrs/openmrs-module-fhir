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
package org.openmrs.module.fhir.api.impl;

import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.MedicationRequest;
import org.openmrs.CareSetting;
import org.openmrs.Drug;
import org.openmrs.DrugOrder;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.fhir.api.MedicationRequestService;
import org.openmrs.module.fhir.api.db.FHIRDAO;
import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.openmrs.module.fhir.api.util.FHIRMedicationRequestUtil;
import org.openmrs.module.fhir.api.util.FHIRUtils;

import java.util.ArrayList;
import java.util.List;

public class MedicationRequestServiceImpl extends BaseOpenmrsService implements MedicationRequestService {

    protected final Log log = LogFactory.getLog(this.getClass());

    private FHIRDAO dao;

    /**
     * @return the dao
     */
    public FHIRDAO getDao() {
        return dao;
    }

    /**
     * @param dao the dao to set
     */
    public void setDao(FHIRDAO dao) {
        this.dao = dao;
    }

    /**
     * @see MedicationRequestService#getMedicationRequestById(String)
     */
    public MedicationRequest getMedicationRequestById(String uuid) {
        DrugOrder drugOrder = (DrugOrder) Context.getOrderService().getOrderByUuid(uuid);
        if (drugOrder == null || drugOrder.isVoided()) {
            return null;
        }
        return FHIRMedicationRequestUtil.generateMedicationRequest(drugOrder);
    }

    /**
     * @see MedicationRequestService#searchMedicationRequestById(String)
     */
    public List<MedicationRequest> searchMedicationRequestById(String uuid) {
        DrugOrder drugOrder = (DrugOrder) Context.getOrderService().getOrderByUuid(uuid);
        List<MedicationRequest> medicationRequests = new ArrayList<MedicationRequest>();
        if (drugOrder != null) {
            medicationRequests.add(FHIRMedicationRequestUtil.generateMedicationRequest(drugOrder));
        }
        return medicationRequests;
    }

    /**
     * @see MedicationRequestService#searchMedicationRequestById(String)
     */
    public List<MedicationRequest> searchMedicationRequestByPatientId(String patientUuid) {
        Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);
        List<MedicationRequest> medicationRequests = new ArrayList<MedicationRequest>();
        if (patient != null) {
            List<Order> orders = Context.getOrderService().getAllOrdersByPatient(patient);
            for(Order order : orders) {
                if (order instanceof DrugOrder) {
                    DrugOrder drugOrder = (DrugOrder) order;
                    medicationRequests.add(FHIRMedicationRequestUtil.generateMedicationRequest(drugOrder));
                }
            }
        }
        return medicationRequests;
    }

    /**
     * @see MedicationRequestService#deleteMedicationRequest(String)
     */
    public void deleteMedicationRequest(String uuid) {
        Order drugOrder = Context.getOrderService().getOrderByUuid(uuid);
        Context.getOrderService().voidOrder(drugOrder, FHIRConstants.FHIR_VOIDED_MESSAGE);
    }

    /**
     * @see MedicationRequestService#createFHIRMedicationRequest(MedicationRequest)
     */
    public MedicationRequest createFHIRMedicationRequest(MedicationRequest medicationRequest) {
        List<String> errors = new ArrayList<String>();
        DrugOrder drugOrder = FHIRMedicationRequestUtil.generateDrugOrder(medicationRequest, errors);
        FHIRUtils.checkGeneratorErrorList(errors);
        CareSetting careSetting = Context.getOrderService().getCareSetting(2);
        drugOrder.setCareSetting(careSetting);

        if (!StringUtils.isEmpty(drugOrder.getDrug().getUuid())) {
            Drug drug = Context.getConceptService().getDrugByUuid(drugOrder.getDrug().getUuid());
            if(drug == null) {
                Context.getConceptService().saveDrug(drugOrder.getDrug());
            }
        }

        drugOrder = (DrugOrder) Context.getOrderService().saveOrder(drugOrder, null);
        return FHIRMedicationRequestUtil.generateMedicationRequest(drugOrder);
    }

    /**
     * NOTE: OpenMRS not allow to edit existing drug order
     * @see MedicationRequestService#updateFHIRMedicationRequest(MedicationRequest, String)
     */
    public MedicationRequest updateFHIRMedicationRequest(MedicationRequest medicationRequest, String uuid) {
        List<String> errors = new ArrayList<String>();
        DrugOrder incomingDrugOrder = FHIRMedicationRequestUtil.generateDrugOrder(medicationRequest, errors);
        DrugOrder generatedDrugOrder = (DrugOrder) Context.getOrderService().getOrderByUuid(uuid);
        FHIRMedicationRequestUtil.copyObsAttributes(incomingDrugOrder, generatedDrugOrder, errors);
        if (generatedDrugOrder != null) { //medication request update
            if (!errors.isEmpty()) {
                StringBuilder errorMessage = new StringBuilder("The request cannot be processed due to the " +
                        "following issues \n");
                for (int i = 0; i < errors.size(); i++) {
                    errorMessage.append((i + 1) + " : " + errors.get(i) + "\n");
                }
                throw new UnprocessableEntityException(errorMessage.toString());
            }

            incomingDrugOrder = (DrugOrder) Context.getOrderService().saveOrder(generatedDrugOrder, null);
            return FHIRMedicationRequestUtil.generateMedicationRequest(incomingDrugOrder);
        } else {
            if (medicationRequest.getId() == null) { // since we need to PUT the medication request to a specific URI,
                // we need to set the uuid
                // here, if it is not
                // already set.
                IdType id = new IdType();
                id.setValue(uuid);
                medicationRequest.setId(uuid);
            }
            return createFHIRMedicationRequest(medicationRequest);
        }
    }
}
