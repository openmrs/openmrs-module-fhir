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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.EncounterProvider;
import org.openmrs.EncounterRole;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.PersonName;
import org.openmrs.api.context.Context;

import ca.uhn.fhir.model.dstu2.composite.BoundCodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.Composition;
import ca.uhn.fhir.model.dstu2.resource.Composition.Section;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Encounter.Location;
import ca.uhn.fhir.model.dstu2.resource.Encounter.Participant;
import ca.uhn.fhir.model.dstu2.valueset.CompositionStatusEnum;
import ca.uhn.fhir.model.dstu2.valueset.EncounterClassEnum;
import ca.uhn.fhir.model.dstu2.valueset.EncounterStateEnum;
import ca.uhn.fhir.model.dstu2.valueset.EncounterTypeEnum;
import ca.uhn.fhir.model.dstu2.valueset.ParticipantTypeEnum;
import ca.uhn.fhir.model.primitive.CodeDt;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.IdDt;

public class FHIREncounterUtil {

	public static Composition generateComposition(org.openmrs.Encounter omrsEncounter) {

		Composition composition = new Composition();

		//Set id of the composition from omrs encounter id
		IdDt uuid = new IdDt();
		uuid.setValue(omrsEncounter.getUuid());
		composition.setId(uuid);

		//Set composition date
		DateTimeDt encounterDate = new DateTimeDt();
		encounterDate.setValue(omrsEncounter.getEncounterDatetime());
		composition.setDate(encounterDate);

		//Set composition status
		composition.setStatus(CompositionStatusEnum.FINAL);

		//Set document confidentiality
		CodeDt confidentialityCode = new CodeDt();
		confidentialityCode.setValue(FHIRConstants.CONFIDENTIALITY_CODING_R);
		composition.setConfidentiality(confidentialityCode);

		//Set composition subject as patient resource
		composition.setSubject(buildPatientReference(omrsEncounter));

		//Setting composition author
		if (omrsEncounter.getEncounterProviders().size() > 0) {
			List<ResourceReferenceDt> authors = new ArrayList<ResourceReferenceDt>();
			ResourceReferenceDt author;
			for (EncounterProvider provider : omrsEncounter.getEncounterProviders()) {
				author = new ResourceReferenceDt();
				StringBuilder providerNameDisplay = new StringBuilder();
				providerNameDisplay.append(provider.getProvider().getName());
				providerNameDisplay.append("(");
				providerNameDisplay.append(FHIRConstants.IDENTIFIER);
				providerNameDisplay.append(":");
				providerNameDisplay.append(provider.getProvider().getIdentifier());
				providerNameDisplay.append(")");
				author.setDisplay(providerNameDisplay.toString());
				IdDt providerRef = new IdDt();
				String providerUri = FHIRConstants.PRACTITIONER + "/" + provider.getProvider().getUuid();
				providerRef.setValue(providerUri);
				author.setReference(providerRef);
				authors.add(author);
			}
			composition.setAuthor(authors);
		}

		//Set encounter reference
		ResourceReferenceDt encounterRef = new ResourceReferenceDt();
		IdDt encounterRefId = new IdDt();
		encounterRefId.setValue(FHIRConstants.ENCOUNTER + "/" + omrsEncounter.getUuid());
		encounterRef.setReference(encounterRefId);
		composition.setEncounter(encounterRef);

		//Set location
		Section locationSection = composition.addSection();
		ResourceReferenceDt locationRef = new ResourceReferenceDt();
		locationRef.setDisplay(FHIRConstants.LOCATION);
		String locationUri = FHIRConstants.LOCATION + "/" + omrsEncounter.getLocation().getUuid();

		IdDt locatioId = new IdDt();
		locatioId.setValue(locationUri);
		locationRef.setReference(locatioId);
		locationSection.setContent(locationRef);

		//Set observation section
		if (omrsEncounter.getAllObs(false).size() > 0) {
			Section obsSection = composition.addSection();
			ResourceReferenceDt obsRef;
			String obsUri;
			IdDt obsId;
			for (Obs obs : omrsEncounter.getAllObs(false)) {
				obsRef = new ResourceReferenceDt();
				obsUri = FHIRConstants.OBSERVATION + "/" + obs.getUuid();
				obsId = new IdDt();
				obsId.setValue(obsUri);
				obsSection.setContent(obsRef);
			}
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

		//Set patient reference
		encounter.setPatient(buildPatientReference(omrsEncounter));

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
		if (omrsEncounter.getLocation() != null) {
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

		if (omrsEncounter.getVisit() != null) {
			//Set visit resource as a part of a encounter
			ResourceReferenceDt visitRef = new ResourceReferenceDt();
			visitRef.setDisplay(omrsEncounter.getVisit().getVisitType().getName());
			IdDt visitRefId = new IdDt();
			String visitRefUri = FHIRConstants.ENCOUNTER + "/" + omrsEncounter.getVisit().getUuid();
			visitRefId.setValue(visitRefUri);
			visitRef.setReference(visitRefId);
			encounter.setPartOf(visitRef);
		}
		//TODO uncomment the validation and check what's going wrong
		//FHIRUtils.validate(encounter);
		return encounter;
	}

	private static ResourceReferenceDt buildPatientReference(org.openmrs.Encounter omrsEncounter) {
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
		return patientReference;
	}

	/**
	 * Filter which obs need to be added to the encounter everything operation. Because some openmrs
	 * installations store allergies as obs. In that case we need to omit obs getting included in
	 * everything operation and return them in patient everything operation
	 *
	 * @param encounter encounter containing obs
	 * @param bundle bundle containg encounter everything contents
	 * @return bundle with only required obs
	 */
	public static void addFilteredObs(org.openmrs.Encounter encounter, Bundle bundle) {
		String strategy = FHIRUtils.getAllergyStrategy();
		Bundle.Entry observation;
		if (FHIRConstants.OBS_ALLERGY_STRATEGY.equals(strategy)) {
			String allergyCode = FHIRUtils.getObsAllergyStrategyConceptUuid();
			Concept concept = Context.getConceptService().getConceptByUuid(allergyCode);
			for (Obs obs : encounter.getAllObs(false)) {
				if (concept != null && !concept.equals(obs.getConcept())) {
					observation = bundle.addEntry();
					observation.setResource(FHIRObsUtil.generateObs(obs));
				}
			}
		} else {
			for (Obs obs : encounter.getAllObs(false)) {
				observation = bundle.addEntry();
				observation.setResource(FHIRObsUtil.generateObs(obs));
			}
		}
	}
	
	public static org.openmrs.Encounter generateOMRSEncounter(Encounter encounter, List<String> errors) {
		org.openmrs.Encounter omrsEncounter = new org.openmrs.Encounter();
		if (encounter.getPatient() != null) {
			ResourceReferenceDt patientref = encounter.getPatient();
			IdDt id = patientref.getReference();
			String patientUuid = id.getIdPart();
			org.openmrs.Patient patient = Context.getPatientService().getPatientByUuid(
			    "4640b84f-0cfd-4cd3-9819-7c5f2c847ec9");
			if (patient == null) {
				errors.add("There is no patient for the given uuid"); // remove to constants
			} else {
				omrsEncounter.setPatient(patient);
			}
		}
		PeriodDt period = encounter.getPeriod();
		Date start = period.getStart();
		omrsEncounter.setEncounterDatetime(start);
		
		List<BoundCodeableConceptDt<EncounterTypeEnum>> types = encounter.getType();
		for (BoundCodeableConceptDt<EncounterTypeEnum> type : types) {
			List<CodingDt> typeCodings = type.getCoding();
			CodingDt code = typeCodings.get(0);//  check null
			String value = code.getCode();
			int typeId = Integer.parseInt(value);
			EncounterType encounterType = Context.getEncounterService().getEncounterType(typeId);
			omrsEncounter.setEncounterType(encounterType);
		}
		
		List<Participant> participants = encounter.getParticipant();
		for (Participant participant : participants) {
			ResourceReferenceDt participantsref = participant.getIndividual();
			IdDt id = participantsref.getReference();
			String participantUuid = id.getIdPart();
			org.openmrs.Provider provider = Context.getProviderService().getProviderByUuid(participantUuid);
			EncounterRole role = Context.getEncounterService().getEncounterRole(1); // hard coded
			omrsEncounter.setProvider(role, provider);
		}
		List<Location> locationList = encounter.getLocation();
		if (locationList != null && !locationList.isEmpty()) {
			Location location = locationList.get(0);
			ResourceReferenceDt locationref = location.getLocation();
			IdDt locRef = locationref.getReference();
			String locationUuid = locRef.getIdPart();
			org.openmrs.Location omrsLocation = Context.getLocationService().getLocationByUuid(locationUuid);
			if (omrsLocation != null) {
				omrsEncounter.setLocation(omrsLocation);
			}
		}
		return omrsEncounter;
	}
}
