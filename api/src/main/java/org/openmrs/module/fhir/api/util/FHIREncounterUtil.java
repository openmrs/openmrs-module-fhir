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

import ca.uhn.fhir.model.dstu.composite.PeriodDt;
import ca.uhn.fhir.model.dstu.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu.resource.Composition;
import ca.uhn.fhir.model.dstu.resource.Composition.Section;
import ca.uhn.fhir.model.dstu.resource.Encounter;
import ca.uhn.fhir.model.dstu.valueset.EncounterClassEnum;
import ca.uhn.fhir.model.dstu.valueset.EncounterStateEnum;
import ca.uhn.fhir.model.dstu.valueset.ParticipantTypeEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.IdDt;
import org.openmrs.EncounterProvider;
import org.openmrs.PersonName;
import org.openmrs.api.context.Context;

import java.util.ArrayList;
import java.util.List;

public class FHIREncounterUtil {

	public static Composition generateComposition(org.openmrs.Encounter openMRSEncounter) {

		Composition composition = new Composition();

		IdDt uuid = new IdDt();
		uuid.setValue(openMRSEncounter.getUuid());
		composition.setId(uuid);

		Section patientSection = composition.addSection();
		IdDt patientUuid = new IdDt();

		patientUuid.setValue(openMRSEncounter.getPatient().getUuid());
		patientSection.setId(patientUuid);

		ResourceReferenceDt patientReference = new ResourceReferenceDt();

		patientReference.setDisplay("Patient");
		String patientUri = Context.getAdministrationService().getGlobalProperty("fhir.uriPrefix") + "/Patient/"
		                    + openMRSEncounter.getPatient().getUuid();

		IdDt patientRef = new IdDt();
		patientRef.setValue(patientUri);
		patientReference.setReference(patientRef);

		patientSection.setSubject(patientReference);

		for (EncounterProvider provider : openMRSEncounter.getEncounterProviders()) {

			Section providerSection = composition.addSection();

			IdDt providerUuid = new IdDt();

			providerUuid.setValue(provider.getUuid());
			providerSection.setId(providerUuid);

			ResourceReferenceDt providerReference = new ResourceReferenceDt();

			providerReference.setDisplay("Provider");
			String providerUri = Context.getAdministrationService().getGlobalProperty("fhir.uriPrefix") + "/Practitioner/"
			                     + provider.getUuid();

			IdDt providerRef = new IdDt();
			providerRef.setValue(providerUri);
			providerReference.setReference(providerRef);

			providerSection.setSubject(providerReference);

		}
		return composition;
	}

	public static Encounter generateEncounter(org.openmrs.Encounter omrsEncounter) {
		Encounter encounter = new Encounter();

		IdDt uuid = new IdDt();
		uuid.setValue(omrsEncounter.getUuid());
		encounter.setId(uuid);
		encounter.setStatus(EncounterStateEnum.FINISHED);
		//TODO what class element needs to be set
		encounter.setClassElement(EncounterClassEnum.INPATIENT);

		//Build and set patient reference
		ResourceReferenceDt patientReference = new ResourceReferenceDt();
		PersonName name = omrsEncounter.getPatient().getPersonName();
		StringBuilder nameDisplay = new StringBuilder();
		nameDisplay.append(name.getGivenName());
		nameDisplay.append(" ");
		nameDisplay.append(name.getFamilyName());
		String patientUri;
		nameDisplay.append("(");
		nameDisplay.append(FHIRConstants.IDENTIFIER);
		nameDisplay.append(":");
		nameDisplay.append(omrsEncounter.getPatient().getPatientIdentifier().getIdentifier());
		nameDisplay.append(")");
		patientUri = FHIRConstants.PATIENT + "/" + omrsEncounter.getPatient().getUuid();
		IdDt patientRef = new IdDt();
		patientRef.setValue(patientUri);
		patientReference.setReference(patientRef);
		patientReference.setDisplay(nameDisplay.toString());
		encounter.setSubject(patientReference);

		//Set participants
		if (omrsEncounter.getEncounterProviders().size() > 0) {
			List<Encounter.Participant> participants = new ArrayList<Encounter.Participant>();
			Encounter.Participant participant;
			for (EncounterProvider provider : omrsEncounter.getEncounterProviders()) {
				participant = new Encounter.Participant();
				for (ParticipantTypeEnum participantTypeEnum : ParticipantTypeEnum.values()) {
					if (participantTypeEnum.getCode().equalsIgnoreCase(provider.getEncounterRole().getName())) {
						participant.setType(participantTypeEnum);
					}
				}
				ResourceReferenceDt providerReference = new ResourceReferenceDt();
				StringBuilder providerNameDisplay = new StringBuilder();
				providerNameDisplay.append(provider.getProvider().getName());
				providerNameDisplay.append("(");
				providerNameDisplay.append(FHIRConstants.IDENTIFIER);
				providerNameDisplay.append(":");
				providerNameDisplay.append(provider.getProvider().getIdentifier());
				providerNameDisplay.append(")");
				providerReference.setDisplay(providerNameDisplay.toString());
				IdDt providerRef = new IdDt();
				String providerUri = FHIRConstants.PRACTITIONER + "/" + provider.getUuid();
				providerRef.setValue(providerUri);
				providerReference.setReference(providerRef);
				participant.setIndividual(providerReference);
				participants.add(participant);
		}
			encounter.setParticipant(participants);
		}
		//Set encounter period from omrs encounter
		DateTimeDt encounterDate = new DateTimeDt();
		encounterDate.setValue(omrsEncounter.getEncounterDatetime());
		PeriodDt period = encounter.getPeriod();
		period.setStart(encounterDate);
		period.setEnd(encounterDate);
		encounter.setPeriod(period);

		//Set encounter location from omrs location
		if(omrsEncounter.getLocation() != null) {
			List<Encounter.Location> locations = new ArrayList<Encounter.Location>();
			Encounter.Location location = new Encounter.Location();
			//set encounter period
			location.setPeriod(period);
			ResourceReferenceDt locationReference = new ResourceReferenceDt();
			locationReference.setDisplay(omrsEncounter.getLocation().getName());
			IdDt locationRefId = new IdDt();
			String locationRefUri = FHIRConstants.LOCATION + "/" + omrsEncounter.getLocation().getUuid();
			locationRefId.setValue(locationRefUri);
			locationReference.setReference(locationRefId);
			location.setLocation(locationReference);
			locations.add(location);
			encounter.setLocation(locations);
		}

		//Set visit resource as a part of a encounter
		ResourceReferenceDt visitRef = new ResourceReferenceDt();
		visitRef.setDisplay(omrsEncounter.getVisit().getVisitType().getName());
		IdDt visitRefId = new IdDt();
		String visitRefUri = FHIRConstants.ENCOUNTER + "/" + omrsEncounter.getVisit().getUuid();
		visitRefId.setValue(visitRefUri);
		visitRef.setReference(visitRefId);
		encounter.setPartOf(visitRef);
		//TODO uncomment the validation and check what's going wrong
		//FHIRUtils.validate(encounter);
		return encounter;
	}
}
