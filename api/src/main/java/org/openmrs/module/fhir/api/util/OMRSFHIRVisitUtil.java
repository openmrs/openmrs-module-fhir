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
import ca.uhn.fhir.model.dstu.resource.Encounter;
import ca.uhn.fhir.model.dstu.valueset.EncounterClassEnum;
import ca.uhn.fhir.model.dstu.valueset.EncounterStateEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.IdDt;
import org.openmrs.PersonName;
import org.openmrs.Visit;

import java.util.ArrayList;
import java.util.List;

public class OMRSFHIRVisitUtil {

	public static Encounter generateEncounter(Visit omrsVisit) {
		Encounter encounter = new Encounter();

		IdDt uuid = new IdDt();
		uuid.setValue(omrsVisit.getUuid());
		encounter.setId(uuid);
		encounter.setStatus(EncounterStateEnum.FINISHED);
		//TODO what class element needs to be set
		encounter.setClassElement(EncounterClassEnum.INPATIENT);

		//Build and set patient reference
		ResourceReferenceDt patientReference = new ResourceReferenceDt();
		PersonName name = omrsVisit.getPatient().getPersonName();
		StringBuilder nameDisplay = new StringBuilder();
		nameDisplay.append(name.getGivenName());
		nameDisplay.append("");
		nameDisplay.append(name.getFamilyName());
		String patientUri;
		nameDisplay.append("(");
		nameDisplay.append(FHIRConstants.IDENTIFIER);
		nameDisplay.append(":");
		nameDisplay.append(omrsVisit.getPatient().getPatientIdentifier().getIdentifier());
		nameDisplay.append(")");
		patientUri = FHIRConstants.PATIENT + "/" + omrsVisit.getPatient().getUuid();
		IdDt patientRef = new IdDt();
		patientRef.setValue(patientUri);
		patientReference.setReference(patientRef);
		patientReference.setDisplay(nameDisplay.toString());
		encounter.setSubject(patientReference);

		//Set encounter period from omrs encounter
		DateTimeDt encounterStartDate = new DateTimeDt();
		encounterStartDate.setValue(omrsVisit.getStartDatetime());
		DateTimeDt encounterEndDate = new DateTimeDt();
		encounterEndDate.setValue(omrsVisit.getStopDatetime());
		PeriodDt period = encounter.getPeriod();
		period.setStart(encounterStartDate);
		period.setEnd(encounterStartDate);
		encounter.setPeriod(period);

		//Set encounter location from omrs location
		if (omrsVisit.getLocation() != null) {
			List<Encounter.Location> locations = new ArrayList<Encounter.Location>();
			Encounter.Location location = new Encounter.Location();
			//set encounter period
			encounter.setPeriod(period);
			location.setPeriod(period);
			ResourceReferenceDt locationReference = new ResourceReferenceDt();
			locationReference.setDisplay(omrsVisit.getLocation().getName());
			IdDt locationRefId = new IdDt();
			String locationRefUri = FHIRConstants.LOCATION + "/" + omrsVisit.getLocation().getUuid();
			locationRefId.setValue(locationRefUri);
			locationReference.setReference(locationRefId);
			locations.add(location);
			encounter.setLocation(locations);
		}
		FHIRUtils.validate(encounter);
		return encounter;
	}
}
