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

import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.valueset.AppointmentStatusEnum;
import ca.uhn.fhir.model.dstu2.valueset.ParticipantTypeEnum;
import ca.uhn.fhir.model.primitive.BaseDateTimeDt;
import ca.uhn.fhir.model.primitive.DateDt;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.InstantDt;
import org.openmrs.ConceptMap;
import org.openmrs.Condition;
import org.openmrs.module.appointmentscheduling.Appointment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FHIRAppointmentUtil {

    public static ca.uhn.fhir.model.dstu2.resource.Appointment generateFHIRAppointment(Appointment appointment) {
        ca.uhn.fhir.model.dstu2.resource.Appointment fhirAppointment = new ca.uhn.fhir.model.dstu2.resource.Appointment();
        IdDt id = new IdDt();
        id.setValue(appointment.getUuid());
        fhirAppointment.setId(id);

        //Set appointment id as a identifier
        IdentifierDt identifier = new IdentifierDt();
        identifier.setValue(Integer.toString(appointment.getAppointmentId()));
        fhirAppointment.addIdentifier(identifier);

        //Set patient reference
        ResourceReferenceDt patient = FHIRUtils.buildPatientOrPersonResourceReference(appointment.getPatient());

        //Set practitioner reference
        ResourceReferenceDt practitioner = FHIRUtils.buildPractitionerReference(appointment.getTimeSlot().getAppointmentBlock().getProvider());

        List<ca.uhn.fhir.model.dstu2.resource.Appointment.Participant> participants = new ArrayList<ca.uhn.fhir.model.dstu2.resource.Appointment.Participant>();
        ca.uhn.fhir.model.dstu2.resource.Appointment.Participant participantPatient = new ca.uhn.fhir.model.dstu2.resource.Appointment.Participant();
        participantPatient.setActor(patient);

        ca.uhn.fhir.model.dstu2.resource.Appointment.Participant participantPractitioner = new ca.uhn.fhir.model.dstu2.resource.Appointment.Participant();
        participantPractitioner.setActor(practitioner);

        //Add participant and provider
        participants.add(participantPatient);
        participants.add(participantPractitioner);

        fhirAppointment.setParticipant(participants);

        //Set appointment status
        Appointment.AppointmentStatus appointmentStatus = appointment.getStatus();

        if (Appointment.AppointmentStatus.CANCELLED.getName().equalsIgnoreCase(appointmentStatus.getName())) {
            fhirAppointment.setStatus(AppointmentStatusEnum.CANCELLED);
        } else if (Appointment.AppointmentStatus.SCHEDULED.getName().equalsIgnoreCase(appointmentStatus.getName())) {
            fhirAppointment.setStatus(AppointmentStatusEnum.BOOKED);
        } else if (Appointment.AppointmentStatus.RESCHEDULED.getName().equalsIgnoreCase(appointmentStatus.getName())) {
            fhirAppointment.setStatus(AppointmentStatusEnum.BOOKED);
        } else if (Appointment.AppointmentStatus.WALKIN.getName().equalsIgnoreCase(appointmentStatus.getName())) {
            fhirAppointment.setStatus(AppointmentStatusEnum.PENDING);
        } else if (Appointment.AppointmentStatus.INCONSULTATION.getName().equalsIgnoreCase(appointmentStatus.getName())) {
            fhirAppointment.setStatus(AppointmentStatusEnum.ARRIVED);
        } else if (Appointment.AppointmentStatus.CANCELLED.getName().equalsIgnoreCase(appointmentStatus.getName())) {
            fhirAppointment.setStatus(AppointmentStatusEnum.CANCELLED);
        } else if (Appointment.AppointmentStatus.CANCELLED_AND_NEEDS_RESCHEDULE.getName().equalsIgnoreCase(appointmentStatus.getName())) {
            fhirAppointment.setStatus(AppointmentStatusEnum.CANCELLED);
        } else if (Appointment.AppointmentStatus.MISSED.getName().equalsIgnoreCase(appointmentStatus.getName())) {
            fhirAppointment.setStatus(AppointmentStatusEnum.NO_SHOW);
        } else if (Appointment.AppointmentStatus.COMPLETED.getName().equalsIgnoreCase(appointmentStatus.getName())) {
            fhirAppointment.setStatus(AppointmentStatusEnum.FULFILLED);
        } else {
            fhirAppointment.setStatus(AppointmentStatusEnum.PENDING);
        }

        //Set start date
        fhirAppointment.setStart(appointment.getTimeSlot().getStartDate(), TemporalPrecisionEnum.DAY);

        //Set end date
        fhirAppointment.setStart(appointment.getTimeSlot().getEndDate(), TemporalPrecisionEnum.DAY);

        //Set reason
        CodeableConceptDt reason = new CodeableConceptDt();
        reason.setText(appointment.getReason());
        fhirAppointment.setReason(reason);

        //Set appointment type
        CodeableConceptDt type = new CodeableConceptDt();
        type.setText(appointment.getAppointmentType().getName());
        fhirAppointment.setType(type);

        return fhirAppointment;
    }

    public static Appointment generateOpenMRSAppointmentModuleAppointment() {
        return null;
    }
}
