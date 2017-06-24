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
package org.openmrs.module.fhir.resources;

import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.dstu3.model.Appointment;
import org.hl7.fhir.dstu3.model.IdType;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.AppointmentService;

import java.util.List;

public class FHIRAppointmentResource extends Resource {

    public Appointment getByUniqueId(IdType id) {
        AppointmentService appointmentService = Context.getService(AppointmentService.class);
        Appointment appointment = appointmentService.getAppointmentById(id.getIdPart());
        if (appointment == null) {
            throw new ResourceNotFoundException("Appointment is not found for the given Id " + id.getIdPart());
        }
        return appointment;
    }

    public List<Appointment> searchAppointmentsById(TokenParam id) {
        return Context.getService(AppointmentService.class).searchAppointmentById(id.getValue());
    }

    public List<Appointment> searchAppointmentsByPatient(ReferenceParam patient) {
        return Context.getService(AppointmentService.class).searchAppointmentsByPatient(patient.getIdPart());
    }
}
