package org.openmrs.module.fhir.api.util;

import org.apache.commons.lang.mutable.MutableBoolean;
import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.ContactPoint;
import org.hl7.fhir.dstu3.model.Enumerations;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.RelatedPerson;
import org.openmrs.PersonAddress;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.api.context.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class FHIRRelatedPersonUtil {

	public static RelatedPerson generateRelationshipObject(org.openmrs.Relationship omrsRelationship) {
		RelatedPerson relatedPerson = new RelatedPerson();
		org.openmrs.Person omrsRelatedPerson = omrsRelationship.getPersonA();

		// id
		relatedPerson.setId(omrsRelationship.getUuid());

		// identifier
		Identifier identifier = new Identifier();
		identifier.setId(omrsRelatedPerson.getUuid());
		relatedPerson.setIdentifier(Collections.singletonList(identifier));

		// active
		if (omrsRelationship.getStartDate() == null || omrsRelationship.getStartDate().before(new Date())
				&& omrsRelationship.getEndDate() == null || omrsRelationship.getEndDate().after(new Date())) {
			relatedPerson.setActive(true);
		} else {
			relatedPerson.setActive(false);
		}

		// patient
		relatedPerson.setPatient(FHIRUtils.buildPatientOrPersonResourceReference(
				omrsRelationship.getPersonB()));

		// relationship
		String relationshipType = omrsRelationship.getRelationshipType().getaIsToB();
		Coding omrsRelation = new Coding();
		omrsRelation.setSystem(FHIRConstants.OPENMRS_URI).setCode(relationshipType);
		CodeableConcept relationshipCode = new CodeableConcept();
		relationshipCode.setCoding(Collections.singletonList(omrsRelation));
		relatedPerson.setRelationship(relationshipCode);

		// name
		List<HumanName> humanNameList = new ArrayList<HumanName>();
		Set<org.openmrs.PersonName> personNamesSet = omrsRelatedPerson.getNames();
		for (org.openmrs.PersonName personName : personNamesSet) {
			humanNameList.add(FHIRUtils.buildHumanName(personName));
		}
		relatedPerson.setName(humanNameList);

		// telecom
		List<ContactPoint> contactPointList = new ArrayList<ContactPoint>();
		if (omrsRelatedPerson.getAttribute(FHIRUtils.PATIENT_PHONE_NUMBER_ATTRIBUTE) != null) {
			ContactPoint contactPoint = new ContactPoint();
			contactPoint.setSystem(ContactPoint.ContactPointSystem.PHONE).setValue(omrsRelatedPerson.getAttribute(
					FHIRUtils.PATIENT_PHONE_NUMBER_ATTRIBUTE).getValue());
			contactPointList.add(contactPoint);
		}
		relatedPerson.setTelecom(contactPointList);

		// gender
		if ("M".equals(omrsRelatedPerson.getGender())) {
			relatedPerson.setGender(Enumerations.AdministrativeGender.MALE);
		} else if ("F".equals(omrsRelatedPerson.getGender())) {
			relatedPerson.setGender(Enumerations.AdministrativeGender.FEMALE);
		} else {
			relatedPerson.setGender(Enumerations.AdministrativeGender.UNKNOWN);
		}

		// birthDate
		relatedPerson.setBirthDate(omrsRelatedPerson.getBirthdate());

		// address
		List<Address> addressList = new ArrayList<Address>();
		Set<PersonAddress> personAddressSet = omrsRelatedPerson.getAddresses();
		for (PersonAddress personAddress : personAddressSet) {
			addressList.add(FHIRUtils.buildAddress(personAddress));
		}
		relatedPerson.setAddress(addressList);

		// photo

		// period
		Period period = new Period();
		period.setStart(omrsRelationship.getStartDate());
		period.setEnd(omrsRelationship.getEndDate());

		return relatedPerson;
	}

	public static org.openmrs.Relationship generateOmrsRelationshipObject(RelatedPerson relatedPerson,
			List<String> errors) {
		org.openmrs.Relationship omrsRelationship = new Relationship();

		// UUID
		omrsRelationship.setUuid(relatedPerson.getIdElement().getIdPart());

		// personA
		// Take id of an identifier as a personA UUID.
		org.openmrs.Person omrsRelatedPerson = null;
		List<Identifier> identifierList = relatedPerson.getIdentifier();
		if (identifierList == null) {
			errors.add("Identifier is missing.");
			return null;
		}
		for (Identifier identifier : identifierList) {
			if (identifier.hasId()) {
				omrsRelatedPerson = Context.getPersonService().getPersonByUuid(identifier.getId());
				break;
			}
		}
		if (omrsRelatedPerson == null) {
			errors.add("Could not find related person.");
			return null;
		}
		omrsRelationship.setPersonA(omrsRelatedPerson);

		// personB
		// Patient is the person B
		if (relatedPerson.getPatient() == null) {
			errors.add("Patient is missing.");
			return null;
		}
		org.openmrs.Person personB = Context.getPersonService().getPersonByUuid(relatedPerson.getPatient().getId());
		if (personB == null) {
			errors.add("Could not find patient.");
			return null;
		}
		omrsRelationship.setPersonB(personB);

		// relationship
		// This variable describes the direction of the relationship.
		MutableBoolean isAToB = new MutableBoolean(false); //
		CodeableConcept relationshipCode = relatedPerson.getRelationship();
		if (relationshipCode == null) {
			errors.add("Relationship is missing.");
			return null;
		}
		RelationshipType relationshipType = null;
		List<Coding> codingList = relationshipCode.getCoding();
		if (codingList == null) {
			errors.add("Coding is missing.");
			return null;
		}
		for (Coding coding : codingList) {
			if (FHIRConstants.OPENMRS_URI.equals(coding.getSystem())) {
				relationshipType = FHIRUtils.getRelationshipTypeByCoding(coding, isAToB);
				break;
			}
		}
		if (relationshipType == null) {
			errors.add("Could not determine relationship type.");
			return null;
		}
		omrsRelationship.setRelationshipType(relationshipType);
		// If the direction is not A to B then we have to swap PersonA with PersonB
		if (!isAToB.booleanValue()) {
			org.openmrs.Person tmpPerson = omrsRelationship.getPersonA();
			omrsRelationship.setPersonA(omrsRelationship.getPersonB());
			omrsRelationship.setPersonB(tmpPerson);
		}

		if (relatedPerson.getPeriod() != null) {
			omrsRelationship.setStartDate(relatedPerson.getPeriod().getStart());
			omrsRelationship.setEndDate(relatedPerson.getPeriod().getEnd());
		}

		return omrsRelationship;
	}

	public static org.openmrs.Relationship updateRelationshipAttributes(org.openmrs.Relationship omrsRelationship,
			org.openmrs.Relationship retrievedRelationship) {
		retrievedRelationship.setPersonA(omrsRelationship.getPersonA());
		retrievedRelationship.setPersonB(omrsRelationship.getPersonB());
		retrievedRelationship.setRelationshipType(omrsRelationship.getRelationshipType());
		retrievedRelationship.setStartDate(omrsRelationship.getStartDate());
		retrievedRelationship.setEndDate(omrsRelationship.getEndDate());
		return retrievedRelationship;
	}

}
