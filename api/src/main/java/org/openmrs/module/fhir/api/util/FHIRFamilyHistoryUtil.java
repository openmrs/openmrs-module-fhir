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

import ca.uhn.fhir.model.dstu2.composite.AgeDt;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import ca.uhn.fhir.model.dstu2.resource.FamilyMemberHistory;
import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
import ca.uhn.fhir.model.primitive.DateDt;
import ca.uhn.fhir.model.primitive.IdDt;
import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.Relationship;

import java.util.ArrayList;
import java.util.List;

public class FHIRFamilyHistoryUtil {

	public static FamilyMemberHistory generateFamilyHistory(Relationship relationship, Person person) {
		FamilyMemberHistory familyMemberHistory = new FamilyMemberHistory();
		IdDt id = new IdDt();
		id.setValue(person.getUuid());
		familyMemberHistory.setId(id);
		familyMemberHistory.setPatient(FHIRUtils.buildPatientOrPersonResourceReference(person));
		CodeableConceptDt relationshipCode = familyMemberHistory.getRelationship();
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
		List<CodingDt> relations = new ArrayList<CodingDt>();
		CodingDt openmrsRelation = new CodingDt();
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

		//Set born date
		DateDt born = new DateDt();
		born.setValue(relatedPerson.getBirthdate());
		familyMemberHistory.setBorn(born);

		//Set gender in fhir relation person gender
		if (relatedPerson.getGender().equals("M")) {
			familyMemberHistory.setGender(AdministrativeGenderEnum.MALE);
		} else if (relatedPerson.getGender().equals("F")) {
			familyMemberHistory.setGender(AdministrativeGenderEnum.FEMALE);
		} else {
			familyMemberHistory.setGender(AdministrativeGenderEnum.UNKNOWN);
		}

		if (relatedPerson.getAge() != null) {
			AgeDt age = new AgeDt();
			age.setValue(relatedPerson.getAge());
			familyMemberHistory.setAge(age);
		}

		return familyMemberHistory;
	}
}
