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

import org.apache.commons.lang.StringUtils;
import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Location;
import org.openmrs.*;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static java.lang.String.valueOf;
import static org.openmrs.module.fhir.api.util.FHIRUtils.extractUuid;

public class OMRSFHIRVisitUtil {

	public static Encounter generateEncounter(Visit omrsVisit) {

		Encounter encounter = new Encounter();
		encounter.setId(omrsVisit.getUuid());
		encounter.setStatus(Encounter.EncounterStatus.FINISHED);
		//TODO what class element needs to be set
		if (omrsVisit.getIndication() != null) {
			Reference indication = new Reference();
			indication.setDisplay(omrsVisit.getIndication().getName().getName());
			String uri =
					FHIRConstants.WEB_SERVICES_URI_PREFIX + "/" + FHIRConstants.CONCEPT + "/" + omrsVisit.getIndication()
							.getUuid();
			List<Reference> indications = new ArrayList<Reference>();
			Reference indicaton = new Reference();
			indication.setReference(uri);
		}
		//Build and set patient reference
		Reference patientReference = new Reference();
		PersonName name = omrsVisit.getPatient().getPersonName();
		StringBuilder nameDisplay = new StringBuilder();
		nameDisplay.append(name.getGivenName());
		nameDisplay.append(" ");
		nameDisplay.append(name.getFamilyName());
		String patientUri;
		nameDisplay.append("(");
		nameDisplay.append(FHIRConstants.IDENTIFIER);
		nameDisplay.append(":");
		nameDisplay.append(omrsVisit.getPatient().getPatientIdentifier().getIdentifier());
		nameDisplay.append(")");
		patientUri = FHIRConstants.PATIENT + "/" + omrsVisit.getPatient().getUuid();
		patientReference.setReference(patientUri);
		patientReference.setDisplay(nameDisplay.toString());
		encounter.setSubject(patientReference);

		//Set encounter period from omrs encounter
		Period period = encounter.getPeriod();
		period.setStart(omrsVisit.getStartDatetime());
		period.setEnd(omrsVisit.getStopDatetime());
		encounter.setPeriod(period);

		//Set encounter location from omrs location
		if (omrsVisit.getLocation() != null) {
			List<Encounter.EncounterLocationComponent> locations = new ArrayList<Encounter.EncounterLocationComponent>();
			Encounter.EncounterLocationComponent location = new Encounter.EncounterLocationComponent();
			//set encounter period
			encounter.setPeriod(period);
			location.setPeriod(period);
			Reference locationReference = new Reference();
			locationReference.setDisplay(omrsVisit.getLocation().getName());
			String locationRefUri = FHIRConstants.LOCATION + "/" + omrsVisit.getLocation().getUuid();
			locationReference.setReference(locationRefUri);
			location.setLocation(locationReference);
			locations.add(location);
			encounter.setLocation(locations);
		}
		//TODO uncomment the validation and check what's going wrong
		FHIRUtils.validate(encounter);
		return encounter;
	}

	public static Visit generateOmrsVisit(Encounter visit, List<String> errors) {

		Visit omrsVisit = new Visit();
		if (visit.getId() != null) {
			omrsVisit.setUuid(extractUuid(visit.getId()));
		}

		if (visit.getLocation().size() == 0) {
			errors.add("Location cannot be empty");
		}

		org.openmrs.Location omrsLocation = new org.openmrs.Location();
		for (Encounter.EncounterLocationComponent location : visit.getLocation()) {
			if (location.getLocationTarget().getAddress() != null) {
				omrsLocation.setAddress1(location.getLocationTarget().getAddress().toString());
			}
			if (location.getLocationTarget().getDescription() != null) {
				omrsLocation.setDescription(location.getLocationTarget().getDescription());
			}
			if(location.getLocationTarget().getName() != null) {
				omrsLocation.setName(location.getLocationTarget().getName());
			}

		}
		omrsVisit.setLocation(omrsLocation);
		omrsVisit.setIndication((Concept)visit.getType());
		omrsVisit.setStartDatetime(visit.getAppointmentTarget().getStart());
		omrsVisit.setStopDatetime(visit.getAppointmentTarget().getEnd());

		org.openmrs.Encounter omrsEncounter =  new org.openmrs.Encounter();
		omrsEncounter.setEncounterDatetime(visit.getAppointmentTarget().getCreated());
//		omrsEncounter.setEncounterType(new org.openmrs.EncounterType(visit.getType().getId()));;

		omrsVisit.addEncounter(omrsEncounter);
		return omrsVisit;
	}

	public static Visit updateVisitAttributes(Visit omrsVisit, Visit retrievedVisit) {
		retrievedVisit.setEncounters(omrsVisit.getEncounters());
		retrievedVisit.setIndication(omrsVisit.getIndication());
		retrievedVisit.setStartDatetime(omrsVisit.getStartDatetime());
		retrievedVisit.setLocation(omrsVisit.getLocation());
		retrievedVisit.setPatient(omrsVisit.getPatient());
		retrievedVisit.setStopDatetime(omrsVisit.getStopDatetime());
		retrievedVisit.setVisitType(omrsVisit.getVisitType());
		return retrievedVisit;
	}

	public static boolean compareCurrentVisits(Object visit1, Object visit2) {
		Visit p1 = (Visit)visit1;
		Visit p2 = (Visit)visit2;
		if(p1.getEncounters().size() == p2.getEncounters().size()) {
			for(int i = 0; i < p1.getEncounters().size(); ++i) {
				if(!((org.openmrs.Encounter)p1.getEncounters().toArray()[i]).equals(p2.getEncounters().toArray()[i])) {
					return false;
				}
			}


		} else {
			if(p1.getIndication() != null && p2.getIndication() != null && !(p1.getIndication().equals(p2.getIndication()))) {
				return false;
			} else if(p1.getIndication() != null && !(p1.getIndication().equals(p2.getIndication()))) {
				return false;
			} else if(null != p1.getStartDatetime() && !p1.getStartDatetime().equals(p2.getStartDatetime())) {
				return false;
			} else if(null != p1.getLocation() && !p1.getLocation().equals(p2.getLocation())) {
				return false;
			} else if(p1.getPatient() != null && !(p1.getPatient().equals(p2.getPatient()))) {
				return false;
			} else if(null != p1.getStopDatetime() && !p1.getStopDatetime().equals(p2.getStopDatetime())) {
				return false;
			} else if(null != p1.getVisitType() && !p1.getVisitType().equals(p2.getVisitType())) {
				return false;
			}
			else {
				return p1.getVoided() == p2.getVoided();
			}
		}
		return false;
	}
}
