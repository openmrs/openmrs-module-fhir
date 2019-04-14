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

import org.hl7.fhir.dstu3.model.Age;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Enumerations;
import org.hl7.fhir.dstu3.model.FamilyMemberHistory;
import org.hl7.fhir.dstu3.model.IdType;
import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.Relationship;

import java.util.ArrayList;
import java.util.List;

public class FHIRFamilyMemberHistoryUtil {

	public static FamilyMemberHistory generateFamilyMemberHistory(Relationship relationship, Person person) {
		FamilyMemberHistory familyMemberHistory = new FamilyMemberHistory();

		BaseOpenMRSDataUtil.setBaseExtensionFields(familyMemberHistory, relationship);

		IdType id = new IdType();
		id.setValue(person.getUuid());
		familyMemberHistory.setId(id);
		familyMemberHistory.setPatient(FHIRUtils.buildPatientOrPersonResourceReference(person));
		CodeableConcept relationshipCode = familyMemberHistory.getRelationship();
		Person relatedPerson;
		String relationshipType;

		// type relations
		//Set related person and relationship type
		if (relationship.getPersonA() != person) {
			relatedPerson = relationship.getPersonA();
			relationshipType = relationship.getRelationshipType().getaIsToB();
		} else {
			relatedPerson = relationship.getPersonB();
			relationshipType = relationship.getRelationshipType().getbIsToA();
		}

		//Set relationship coding
		List<Coding> relations = new ArrayList<Coding>();
		Coding openmrsRelation = new Coding();
		openmrsRelation.setSystem(FHIRConstants.OPENMRS_URI).setCode(relationshipType);
		relations.add(openmrsRelation);
		relationshipCode.setCoding(relations);
		familyMemberHistory.setRelationship(relationshipCode);

		//Set related person name
		if (relatedPerson.getPersonName() != null) {
			PersonName relatedPersonName = relatedPerson.getPersonName();
			StringBuilder relatedPersonNameDisplay = new StringBuilder();
			relatedPersonNameDisplay.append(relatedPersonName.getGivenName());
			relatedPersonNameDisplay.append(" ");
			relatedPersonNameDisplay.append(relatedPerson.getFamilyName());
			familyMemberHistory.setName(relatedPersonNameDisplay.toString());
		}

		//Set gender in fhir relation person gender
		if (relatedPerson.getGender().equals("M")) {
			familyMemberHistory.setGender(Enumerations.AdministrativeGender.MALE);
		} else if (relatedPerson.getGender().equals("F")) {
			familyMemberHistory.setGender(Enumerations.AdministrativeGender.FEMALE);
		} else {
			familyMemberHistory.setGender(Enumerations.AdministrativeGender.UNKNOWN);
		}

		if (relatedPerson.getAge() != null) {
			Age age = new Age();
			age.setValue(relatedPerson.getAge());
			familyMemberHistory.setAge(age);
		}

		return familyMemberHistory;
	}
}
