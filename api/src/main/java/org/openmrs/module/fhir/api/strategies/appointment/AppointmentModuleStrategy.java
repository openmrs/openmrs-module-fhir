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
package org.openmrs.module.fhir.api.strategies.appointment;

import org.hl7.fhir.dstu3.model.Appointment;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.openmrs.module.fhir.api.util.FHIRAppointmentUtil;

import java.util.List;

public class AppointmentModuleStrategy implements GenericAppointmentStrategy {

	@Override
	public Appointment getAppointmentById(String uuid) {
		AppointmentService appointmentService = Context.getService(AppointmentService.class);
		org.openmrs.module.appointmentscheduling.Appointment appointment = appointmentService.getAppointmentByUuid(uuid);
		return FHIRAppointmentUtil.generateFHIRAppointment(appointment);
	}

	@Override
	public List<Appointment> searchAppointmentsById(String uuid) {
		return null;
	}

	@Override
	public List<Appointment> searchAppointmentsByPatient(String name) {
		return null;
	}
}
