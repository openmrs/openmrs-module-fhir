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
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.Reference;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PersonName;
import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.api.context.Context;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.openmrs.module.fhir.api.util.FHIRUtils.extractUuid;

public class FHIRVisitUtil {

	public static Encounter generateEncounter(Visit omrsVisit) {
		Encounter encounter = new Encounter();

		BaseOpenMRSDataUtil.setBaseExtensionFields(encounter, omrsVisit);

		encounter.setId(omrsVisit.getUuid());
		encounter.setStatus(Encounter.EncounterStatus.FINISHED);
		//TODO what class element needs to be set
		if (omrsVisit.getIndication() != null) {
			Reference indication = new Reference();
			indication.setDisplay(omrsVisit.getIndication().getName().getName());
			String uri =
					FHIRConstants.WEB_SERVICES_URI_PREFIX + "/" + FHIRConstants.CONCEPT + "/" + omrsVisit.getIndication()
							.getUuid();
			indication.setReference(uri);
		}
		//Build and set patient reference
		Reference patientReference = new Reference();
		PersonName name = omrsVisit.getPatient().getPersonName();
		StringBuilder nameDisplay = new StringBuilder();
		nameDisplay.append(name.getGivenName());
		nameDisplay.append(" ");
		nameDisplay.append(name.getFamilyName());
		nameDisplay.append("(");
		nameDisplay.append(FHIRConstants.IDENTIFIER);
		nameDisplay.append(":");
		nameDisplay.append(omrsVisit.getPatient().getPatientIdentifier().getIdentifier());
		nameDisplay.append(")");
		String patientUri = FHIRConstants.PATIENT + "/" + omrsVisit.getPatient().getUuid();
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
			List<Encounter.EncounterLocationComponent> locations = new ArrayList<>();
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

		VisitType visitType = omrsVisit.getVisitType();
		CodeableConcept type = new CodeableConcept();
		Coding coding = new Coding(visitType.getUuid(), visitType.getId().toString(), visitType.getName());
		FHIREncounterUtil.markAsVisitType(coding);
		type.addCoding(coding);
		encounter.addType(type);

		FHIRUtils.validate(encounter);
		return encounter;
	}

	public static Visit generateOMRSVisit(Encounter encounter, List<String> errors) {
		Visit visit = new Visit();

		BaseOpenMRSDataUtil.readBaseExtensionFields(visit, encounter);

		if (encounter.getId() != null) {
			visit.setUuid(extractUuid(encounter.getId()));
		}

		if (encounter.getSubject() != null) {
			Reference patientRef = encounter.getSubject();
			String patientUuid = FHIRUtils.getObjectUuidByReference(patientRef);
			Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);
			if (patient == null) {
				errors.add("There is no patient for the given uuid " + patientUuid); // remove to constants
			} else {
				visit.setPatient(patient);
			}
		}
		List<CodeableConcept> types = encounter.getType();
		for (CodeableConcept type : types) {
			List<Coding> typeCodings = type.getCoding();
			VisitType visitType = null;
			if (typeCodings != null && !typeCodings.isEmpty()) {
				Coding code = typeCodings.get(0);
				String typeCode = code.getCode();
				int typeId = Integer.parseInt(typeCode);
				visitType = Context.getVisitService().getVisitType(typeId);
				if (visitType == null) {
					visitType = Context.getVisitService().getVisitTypeByUuid(code.getSystem());
				}
			}

			if (visitType == null) {
				errors.add("There is no Visit Type for the given type id");
			}
			visit.setVisitType(visitType);
		}

		List<Encounter.EncounterLocationComponent> locationList = encounter.getLocation();
		if (locationList != null && !locationList.isEmpty()) {
			Encounter.EncounterLocationComponent location = locationList.get(0);
			Reference locationRef = location.getLocation();
			String locationUuid = FHIRUtils.getObjectUuidByReference(locationRef);

			Location omrsLocation = Context.getLocationService().getLocationByUuid(locationUuid);
			if (omrsLocation != null) {
				visit.setLocation(omrsLocation);
			}
		}

		Period period = encounter.getPeriod();
		Date start = period.getStart();
		if (start == null) {
			errors.add("Start date cannot be empty");
		}
		visit.setStartDatetime(start);
		visit.setStopDatetime(period.getEnd());
		return visit;
	}

	public static Visit updateVisitAttributes(Visit omrsVisit, Visit retrievedVisit) {
		retrievedVisit.setVisitType(omrsVisit.getVisitType());
		retrievedVisit.setPatient(omrsVisit.getPatient());
		retrievedVisit.setLocation(omrsVisit.getLocation());
		retrievedVisit.setStartDatetime(omrsVisit.getStartDatetime());
		retrievedVisit.setStopDatetime(omrsVisit.getStopDatetime());
		return retrievedVisit;
	}
}
