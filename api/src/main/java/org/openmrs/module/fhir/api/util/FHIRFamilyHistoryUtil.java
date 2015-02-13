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

import ca.uhn.fhir.model.dstu.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu.composite.CodingDt;
import ca.uhn.fhir.model.dstu.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu.resource.FamilyHistory;
import ca.uhn.fhir.model.primitive.DateDt;
import ca.uhn.fhir.model.primitive.IdDt;
import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.Relationship;
import org.openmrs.api.context.Context;

import java.util.ArrayList;
import java.util.List;

public class FHIRFamilyHistoryUtil {

	public static FamilyHistory generateFamilyHistory(List<Relationship> relationships, Person person) {
		FamilyHistory familyHistory = new FamilyHistory();
		ResourceReferenceDt personRef = new ResourceReferenceDt();
		String personUri;
		if (Context.getPatientService().getPatientByUuid(person.getUuid()) != null) {
			personUri = FHIRConstants.PATIENT + "/" + person.getUuid();
		} else {
			personUri = FHIRConstants.WEB_SERVICES_URI_PREFIX + "/" + FHIRConstants.PERSON + "/" + person.getUuid();
		}

		//Set person
		IdDt personId = new IdDt();
		personId.setValue(personUri);
		personRef.setReference(personId);
		//Set person name
		PersonName name = person.getPersonName();
		StringBuilder nameDisplay = new StringBuilder();
		nameDisplay.append(name.getGivenName());
		nameDisplay.append(" ");
		nameDisplay.append(name.getFamilyName());
		String uri;
		if (Context.getPatientService().getPatientByUuid(person.getUuid()) != null) {
			nameDisplay.append("(");
			nameDisplay.append(FHIRConstants.IDENTIFIER);
			nameDisplay.append(":");
			nameDisplay.append(Context.getPatientService().getPatientByUuid(person.getUuid()).getPatientIdentifier()
					.getIdentifier());
			nameDisplay.append(")");
		}
		personRef.setDisplay(nameDisplay.toString());
		familyHistory.setSubject(personRef);
		FamilyHistory.Relation fhirRelation;
		List<FamilyHistory.Relation> fhirRelations = new ArrayList<FamilyHistory.Relation>();
		Person relatedPerson;
		String relationshipType;

		//TODO currently this will add all the relationships person had which needs to be filter out to add only family type relations
		//Set related person and relationship type
		for (Relationship relationship : relationships) {
			fhirRelation = new FamilyHistory.Relation();
			if (relationship.getPersonA() != person) {
				relatedPerson = relationship.getPersonA();
				relationshipType = relationship.getRelationshipType().getaIsToB();
			} else {
				relatedPerson = relationship.getPersonB();
				relationshipType = relationship.getRelationshipType().getbIsToA();
			}
			//Set related person name
			PersonName relatedPersonName = relatedPerson.getPersonName();
			StringBuilder relatedPersonNameDisplay = new StringBuilder();
			relatedPersonNameDisplay.append(relatedPersonName.getGivenName());
			relatedPersonNameDisplay.append(" ");
			nameDisplay.append(name.getFamilyName());
			fhirRelation.setName(relatedPersonNameDisplay.toString());

			CodeableConceptDt relationType = new CodeableConceptDt();
			List<CodingDt> relationshipTypeCodings = new ArrayList<CodingDt>();
			relationshipTypeCodings.add(new CodingDt().setDisplay(relationshipType));
			relationType.setCoding(relationshipTypeCodings);
			fhirRelation.setRelationship(relationType);
			//Set born date
			DateDt born = new DateDt();
			born.setValue(relatedPerson.getBirthdate());
			fhirRelation.setBorn(born);
			fhirRelations.add(fhirRelation);
		}
		familyHistory.setRelation(fhirRelations);
		return familyHistory;
	}
}
