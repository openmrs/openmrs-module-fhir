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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.fhir.api.AppointmentService;
import org.openmrs.module.fhir.api.db.FHIRDAO;
import org.openmrs.module.fhir.appointment.AppointmentStrategyUtil;

import java.util.List;

public class AppointmentServiceImpl implements AppointmentService {

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
     * Get appointment by uuid
     *
     * @param uuid of the requesting appointment
     * @return appointment obj
     */
    public Appointment getAppointmentById(String uuid) {
        return AppointmentStrategyUtil.getAppointmentStrategy().getAppointmentById(uuid);
    }

    /**
     * Search appointment list by uuid
     *
     * @param uuid of the appointment
     * @return appointment obj
     */
    public List<Appointment> searchAppointmentById(String uuid) {
        return AppointmentStrategyUtil.getAppointmentStrategy().searchAppointmentsById(uuid);
    }


    /**
     * Searcj appointment list by pattient uuid
     *
     * @param patientUuid the id of the patient which needs to search the appoinments
     * @return appointment obj
     */
    public List<Appointment> searchAppointmentsByPatient(String patientUuid) {
        return AppointmentStrategyUtil.getAppointmentStrategy().searchAppointmentsByPatient(patientUuid);
    }

}
