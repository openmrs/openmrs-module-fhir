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

import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.Reference;
import org.openmrs.PersonName;
import org.openmrs.Visit;

import java.util.ArrayList;
import java.util.List;

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
		//FHIRUtils.validate(encounter);
		return encounter;
	}
}
