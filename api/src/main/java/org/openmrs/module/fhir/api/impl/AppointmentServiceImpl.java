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

import ca.uhn.fhir.model.dstu2.resource.Appointment;
import org.openmrs.module.fhir.api.AppointmentService;

import java.util.List;

public class AppointmentServiceImpl implements AppointmentService {

    /**
     * Get appointment by uuid
     *
     * @param uuid of the requesting appointment
     * @return appointment obj
     */
    public Appointment getAppointmentById(String uuid) {
        //TODO
        return null;
    }

    /**
     * Search appointment list by uuid
     *
     * @param uuid of the appointment
     * @return appointment obj
     */
    public List<Appointment> searchAppointmentById(String uuid) {
        //TODO
        return null;
    }


    /**
     * Searcj appointment list by pattient uuid
     *
     * @param patientUuid the id of the patient which needs to search the appoinments
     * @return appointment obj
     */
    public List<Appointment> searchAppointmentsByPatient(String patientUuid) {
        //TODO
        return null;
    }

}
