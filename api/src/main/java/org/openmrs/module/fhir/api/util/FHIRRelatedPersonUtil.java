package org.openmrs.module.fhir.api.util;

import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.dstu3.model.Address;
import org.openmrs.*;
import org.openmrs.api.context.Context;

import java.util.*;

public class FHIRRelatedPersonUtil {

    public static RelatedPerson generateRelationshipObject(org.openmrs.Relationship omrsRelationship) {
        RelatedPerson relatedPerson = new RelatedPerson();
        org.openmrs.Person omrsRelatedPerson = omrsRelationship.getPersonB();

        // id
        relatedPerson.setId(omrsRelationship.getUuid());

        // identifier
        Identifier identifier = new Identifier();
        identifier.setId(omrsRelatedPerson.getUuid());
        relatedPerson.setIdentifier(Collections.singletonList(identifier));

        // active

        // patient
        org.openmrs.Person personA = omrsRelationship.getPersonA();
        relatedPerson.setPatient(FHIRUtils.buildPatientOrPersonResourceReference(personA));


        // relationship
        String relationshipType = omrsRelationship.getRelationshipType().getaIsToB();
        Coding openmrsRelation = new Coding();
        openmrsRelation.setSystem(FHIRConstants.OPENMRS_URI).setCode(relationshipType);
        CodeableConcept relationshipCode = new CodeableConcept();
        relationshipCode.setCoding(Collections.singletonList(openmrsRelation));
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

    // TODO: error handling
    public static org.openmrs.Relationship generateOmrsRelationshipObject(RelatedPerson relatedPerson,
                                                                          List<String> errors) {
        org.openmrs.Relationship omrsRelationship = new Relationship();

        // UUID
        omrsRelationship.setUuid(relatedPerson.getIdElement().getIdPart());

        // personB
        // Take id of an identifier as an personB UUID.
        org.openmrs.Person omrsRelatedPerson = null;
        List<Identifier> identifierList = relatedPerson.getIdentifier();
        for (Identifier identifier : identifierList) {
            if (identifier.hasId()) {
                omrsRelatedPerson = Context.getPersonService().getPersonByUuid(identifier.getId());
                break;
            }
        }
        omrsRelationship.setPersonB(omrsRelatedPerson);

        // if omrsRelatedPerson == null add error and return null;

        // active

        // personA
        org.openmrs.Person personA = Context.getPersonService().getPersonByUuid(relatedPerson.getPatient().getId());
        // if persoNA == null add erorr and return null
        omrsRelationship.setPersonA(personA);

        // relationship
        // TODO: Improve algorithm!
        CodeableConcept relationshipCode = relatedPerson.getRelationship();
        RelationshipType relationshipType = null;
        List<Coding> codingList = relationshipCode.getCoding();
        for (Coding coding : codingList) {
            if (FHIRConstants.OPENMRS_URI.equals(coding.getSystem())) {
                relationshipType = FHIRUtils.getRelationshipTypeByCoding(coding);
                break;
            }
        }
        omrsRelationship.setRelationshipType(relationshipType);

        // start date
        omrsRelationship.setStartDate(relatedPerson.getPeriod().getStart());

        // end date
        omrsRelationship.setEndDate(relatedPerson.getPeriod().getEnd());

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