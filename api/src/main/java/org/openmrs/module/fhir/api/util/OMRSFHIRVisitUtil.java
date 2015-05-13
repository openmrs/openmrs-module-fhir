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

import ca.uhn.fhir.model.api.ExtensionDt;
import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.valueset.EncounterClassEnum;
import ca.uhn.fhir.model.dstu2.valueset.EncounterStateEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.StringDt;
import org.apache.commons.collections.CollectionUtils;
import org.openmrs.PersonName;
import org.openmrs.Visit;

import java.util.ArrayList;
import java.util.List;

public class OMRSFHIRVisitUtil {

	/**
	 * Generates FHIR encounter out of Visit
	 * Sets visits encounters as an extension of FHIR encounter with list of encounter ids
	 *
	 * @param omrsVisit Visit to generate fhir Encounter from
	 * @return FHIR encounter object
	 */
	public static Encounter generateEncounter(Visit omrsVisit) {
		Encounter encounter = new Encounter();

		IdDt uuid = new IdDt();
		uuid.setValue(omrsVisit.getUuid());
		encounter.setId(uuid);
		encounter.setStatus(EncounterStateEnum.FINISHED);
		//TODO what class element needs to be set
		encounter.setClassElement(EncounterClassEnum.INPATIENT);
		if (omrsVisit.getIndication() != null) {
			ResourceReferenceDt indication = new ResourceReferenceDt();
			indication.setDisplay(omrsVisit.getIndication().getName().getName());
			String uri =
					FHIRConstants.WEB_SERVICES_URI_PREFIX + "/" + FHIRConstants.CONCEPT + "/" + omrsVisit.getIndication()
							.getUuid();
			List<ResourceReferenceDt> indications = new ArrayList<ResourceReferenceDt>();
			ResourceReferenceDt indicaton = new ResourceReferenceDt();
			IdDt indicationRef = new IdDt();
			indicationRef.setValue(uri);
			indication.setReference(indicationRef);
			indication.setReference(indicationRef);
			encounter.setIndication(indications);
		}
		//Build and set patient reference
		ResourceReferenceDt patientReference = new ResourceReferenceDt();
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
		IdDt patientRef = new IdDt();
		patientRef.setValue(patientUri);
		patientReference.setReference(patientRef);
		patientReference.setDisplay(nameDisplay.toString());
		encounter.setPatient(patientReference);

		//Set encounter period from omrs encounter
		DateTimeDt encounterStartDate = new DateTimeDt();
		encounterStartDate.setValue(omrsVisit.getStartDatetime());
		DateTimeDt encounterEndDate = new DateTimeDt();
		encounterEndDate.setValue(omrsVisit.getStopDatetime());
		PeriodDt period = encounter.getPeriod();
		period.setStart(encounterStartDate);
		period.setEnd(encounterEndDate);
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
			location.setLocation(locationReference);
			locations.add(location);
			encounter.setLocation(locations);
		}

		if (CollectionUtils.isNotEmpty(omrsVisit.getEncounters())){
			for (org.openmrs.Encounter omrsEncounter : omrsVisit.getEncounters()){
				StringDt encounterDt = new StringDt();
				encounterDt.setValue(FHIRConstants.ENCOUNTER + "/" + omrsEncounter.getUuid());
				ExtensionDt encounterExt = new ExtensionDt(false, FHIRConstants.ENCOUNTER_EXTENSION_URI, encounterDt);
				encounter.addUndeclaredExtension(encounterExt);
			}
		}

		//TODO uncomment the validation and check what's going wrong
		//FHIRUtils.validate(encounter);
		return encounter;
	}
}
