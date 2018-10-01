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

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Reference;
import org.openmrs.module.appointmentscheduling.Appointment;

import java.util.ArrayList;
import java.util.List;

public class FHIRAppointmentUtil {

	public static org.hl7.fhir.dstu3.model.Appointment generateFHIRAppointment(Appointment appointment) {
		org.hl7.fhir.dstu3.model.Appointment fhirAppointment = new org.hl7.fhir.dstu3.model.Appointment();
		IdType id = new IdType();
		id.setValue(appointment.getUuid());
		fhirAppointment.setId(id);

		//Set appointment id as a identifier
		Identifier identifier = new Identifier();
		identifier.setValue(Integer.toString(appointment.getAppointmentId()));
		fhirAppointment.addIdentifier(identifier);

		//Set patient reference
		Reference patient = FHIRUtils.buildPatientOrPersonResourceReference(appointment.getPatient());

		//Set practitioner reference
		Reference practitioner = FHIRUtils
				.buildPractitionerReference(appointment.getTimeSlot().getAppointmentBlock().getProvider());

		org.hl7.fhir.dstu3.model.Appointment.AppointmentParticipantComponent practitionerParticipant =
				fhirAppointment.addParticipant();
		List<CodeableConcept> types = new ArrayList<>();
		CodeableConcept type = new CodeableConcept();
		type.addCoding(new Coding(FHIRConstants.PRACTITIONER, FHIRConstants.PRACTITIONER, FHIRConstants.PRACTITIONER));
		types.add(type);
		practitionerParticipant.setType(types);
		practitionerParticipant.setActor(practitioner);

		org.hl7.fhir.dstu3.model.Appointment.AppointmentParticipantComponent patientParticipant =
				fhirAppointment.addParticipant();
		practitionerParticipant.setActor(practitioner);
		types = new ArrayList<CodeableConcept>();
		type = new CodeableConcept();
		type.addCoding(new Coding(FHIRConstants.PRACTITIONER, FHIRConstants.PRACTITIONER, FHIRConstants.PRACTITIONER));
		types.add(type);
		patientParticipant.setType(types);
		patientParticipant.setActor(patient);

		//Set appointment status
		Appointment.AppointmentStatus appointmentStatus = appointment.getStatus();

		if (Appointment.AppointmentStatus.CANCELLED.getName().equalsIgnoreCase(appointmentStatus.getName())) {
			fhirAppointment.setStatus(org.hl7.fhir.dstu3.model.Appointment.AppointmentStatus.CANCELLED);
		} else if (Appointment.AppointmentStatus.SCHEDULED.getName().equalsIgnoreCase(appointmentStatus.getName())) {
			fhirAppointment.setStatus(org.hl7.fhir.dstu3.model.Appointment.AppointmentStatus.BOOKED);
		} else if (Appointment.AppointmentStatus.RESCHEDULED.getName().equalsIgnoreCase(appointmentStatus.getName())) {
			fhirAppointment.setStatus(org.hl7.fhir.dstu3.model.Appointment.AppointmentStatus.BOOKED);
		} else if (Appointment.AppointmentStatus.WALKIN.getName().equalsIgnoreCase(appointmentStatus.getName())) {
			fhirAppointment.setStatus(org.hl7.fhir.dstu3.model.Appointment.AppointmentStatus.PENDING);
		} else if (Appointment.AppointmentStatus.INCONSULTATION.getName().equalsIgnoreCase(appointmentStatus.getName())) {
			fhirAppointment.setStatus(org.hl7.fhir.dstu3.model.Appointment.AppointmentStatus.ARRIVED);
		} else if (Appointment.AppointmentStatus.CANCELLED.getName().equalsIgnoreCase(appointmentStatus.getName())) {
			fhirAppointment.setStatus(org.hl7.fhir.dstu3.model.Appointment.AppointmentStatus.CANCELLED);
		} else if (Appointment.AppointmentStatus.CANCELLED_AND_NEEDS_RESCHEDULE.getName()
				.equalsIgnoreCase(appointmentStatus.getName())) {
			fhirAppointment.setStatus(org.hl7.fhir.dstu3.model.Appointment.AppointmentStatus.CANCELLED);
		} else if (Appointment.AppointmentStatus.MISSED.getName().equalsIgnoreCase(appointmentStatus.getName())) {
			fhirAppointment.setStatus(org.hl7.fhir.dstu3.model.Appointment.AppointmentStatus.NOSHOW);
		} else if (Appointment.AppointmentStatus.COMPLETED.getName().equalsIgnoreCase(appointmentStatus.getName())) {
			fhirAppointment.setStatus(org.hl7.fhir.dstu3.model.Appointment.AppointmentStatus.FULFILLED);
		} else {
			fhirAppointment.setStatus(org.hl7.fhir.dstu3.model.Appointment.AppointmentStatus.PENDING);
		}

		//Set start date
		fhirAppointment.setStart(appointment.getTimeSlot().getStartDate());

		//Set end date
		fhirAppointment.setStart(appointment.getTimeSlot().getEndDate());

		//Set reason
		List<CodeableConcept> codeableConcepts = new ArrayList<>();
		CodeableConcept reason = new CodeableConcept();
		reason.setText(appointment.getReason());
		codeableConcepts.add(reason);
		fhirAppointment.setReason(codeableConcepts);

		//Set appointment type
		CodeableConcept appointmentType = new CodeableConcept();
		type.setText(appointment.getAppointmentType().getName());
		fhirAppointment.setAppointmentType(appointmentType);

		return fhirAppointment;
	}

	public static Appointment generateOpenMRSAppointmentModuleAppointment() {
		return null;
	}
}
