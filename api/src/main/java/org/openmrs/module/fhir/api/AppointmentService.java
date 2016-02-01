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

import ca.uhn.fhir.model.dstu2.resource.Appointment;

import java.util.List;

public interface AppointmentService {

	/**
	 * Get appointment by uuid
	 *
	 * @param uuid of the requesting appointment
	 * @return appointment obj
	 */
	Appointment getAppointmentById(String uuid);

	/**
	 * Search appointment list by uuid
	 *
	 * @param uuid of the appointment
	 * @return appointment list
	 */
	List<Appointment> searchAppointmentById(String uuid);

	/**
	 * Search appointment list by patient
	 *
	 * @param patientUuid of the patient
	 * @return appointment list
	 */
	List<Appointment> searchAppointmentsByPatient(String patientUuid);

}
