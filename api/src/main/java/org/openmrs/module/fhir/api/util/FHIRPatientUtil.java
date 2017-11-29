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
import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.BooleanType;
import org.hl7.fhir.dstu3.model.ContactPoint;
import org.hl7.fhir.dstu3.model.Enumerations;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.StringType;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAddress;
import org.openmrs.PersonName;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static java.lang.String.valueOf;

public class FHIRPatientUtil {

	public static Patient generatePatient(org.openmrs.Patient omrsPatient) {
		
		Patient patient = new Patient();
		//Set patient id to fhir patient
		IdType uuid = new IdType();
		uuid.setValue(omrsPatient.getUuid());
		patient.setId(uuid);

		//Set patient identifiers to fhir patient
		for (PatientIdentifier identifier : omrsPatient.getActiveIdentifiers()) {
			String urn = FHIRUtils.buildURN(FHIRConstants.UUID, identifier.getIdentifierType().getUuid());
			if (identifier.isPreferred()) {
				patient.addIdentifier().setUse(Identifier.IdentifierUse.USUAL).setSystem(identifier.getIdentifierType().getName())
						.setValue(identifier.getIdentifier());
			} else {
				patient.addIdentifier().setUse(Identifier.IdentifierUse.SECONDARY).setSystem(identifier.getIdentifierType()
						.getName()).setValue(identifier.getIdentifier());
			}
		}

		//Set patient name to fhir patient
		List<HumanName> humanNameDts = new ArrayList<HumanName>();
		for (PersonName name : omrsPatient.getNames()) {
			humanNameDts.add(FHIRUtils.buildHumanName(name));
		}
		patient.setName(humanNameDts);

		//Set gender in fhir patient object
		if ("M".equals(omrsPatient.getGender())) {
			patient.setGender(Enumerations.AdministrativeGender.MALE);
		} else if ("F".equals(omrsPatient.getGender())) {
			patient.setGender(Enumerations.AdministrativeGender.FEMALE);
		} else {
			patient.setGender(Enumerations.AdministrativeGender.UNKNOWN);
		}

		List<Address> fhirAddresses = patient.getAddress();
		for (PersonAddress address : omrsPatient.getAddresses()) {
			fhirAddresses.add(FHIRUtils.buildAddress(address));
		}
		patient.setAddress(fhirAddresses);

		if (omrsPatient.getBirthdate() != null) {
			patient.setBirthDate(omrsPatient.getBirthdate());
		}

		patient.setActive(!omrsPatient.isVoided());

		if (omrsPatient.isDead()) {
			patient.setDeceased(new BooleanType().setValue(omrsPatient.isDead()));
		} else {
			BooleanType isDeceased = new BooleanType();
			isDeceased.setValue(omrsPatient.getDead());
			patient.setDeceased(isDeceased);
		}

		List<ContactPoint> dts = new ArrayList<ContactPoint>();
		// Add global property for telephone / email address. These properties will be used to identify the name of the
		// person attribute (if any) being used to store a phone number and/or email.
		if (omrsPatient.getAttribute(FHIRUtils.PATIENT_PHONE_NUMBER_ATTRIBUTE) != null) {
			ContactPoint telecom = new ContactPoint();
			telecom.setSystem(ContactPoint.ContactPointSystem.PHONE).setValue(omrsPatient.getAttribute(
					FHIRUtils.PATIENT_PHONE_NUMBER_ATTRIBUTE).getValue());
			dts.add(telecom);
		}
		patient.setTelecom(dts);
		FHIRUtils.validate(patient);
		return patient;
	}
	
	public static org.openmrs.Patient generateOmrsPatient(Patient patient, List<String> errors) {
		boolean preferedPresent = false, givennamePresent = false, familynamePresent = false, doCheckName = true;

		org.openmrs.Patient omrsPatient = new org.openmrs.Patient(); // add eror handli
		
		if (patient.getId() != null) {
			omrsPatient.setUuid(extractUuid(patient.getId()));
		}

		List<Identifier> fhirIdList = patient.getIdentifier();
		Set<PatientIdentifier> idList = new TreeSet<PatientIdentifier>();
		
		if (fhirIdList == null || fhirIdList.isEmpty()) {
			errors.add("Identifiers cannot be empty");
		}

		for (Identifier fhirIentifier : fhirIdList) {
			PatientIdentifier patientIdentifier = new PatientIdentifier();
			patientIdentifier.setIdentifier(fhirIentifier.getValue());
			String identifierTypeName = fhirIentifier.getSystem();
			if (String.valueOf(Identifier.IdentifierUse.USUAL).equalsIgnoreCase(fhirIentifier.getUse().getDefinition())) {
				patientIdentifier.setPreferred(true);
			} else {
				patientIdentifier.setPreferred(false);
			}
			PatientIdentifierType type = Context.getPatientService().getPatientIdentifierTypeByName(identifierTypeName);
			if (type == null) {
				errors.add("No PatientIdentifierType exists for the given PatientIdentifierTypeName");
			}
			patientIdentifier.setIdentifierType(type);

			if(type != null) {
				PatientIdentifierType.LocationBehavior lb = type.getLocationBehavior();
				if (lb == null || lb == PatientIdentifierType.LocationBehavior.REQUIRED) {
					LocationService locationService = Context.getLocationService();
					patientIdentifier.setLocation(locationService.getLocation(1));
				}
			}

			idList.add(patientIdentifier);
		}
		omrsPatient.setIdentifiers(idList);

		Set<PersonName> names = new TreeSet<PersonName>();
		if (patient.getName().size() == 0) {
			errors.add("Name cannot be empty");
		}
		for (HumanName humanNameDt : patient.getName()) {
			PersonName personName = new PersonName();
			if (humanNameDt.getUse() != null) {
				String getUse = humanNameDt.getUse().toCode();
				if (String.valueOf(HumanName.NameUse.OFFICIAL).equalsIgnoreCase(getUse)
				        || String.valueOf(HumanName.NameUse.USUAL).equalsIgnoreCase(getUse)) {
					preferedPresent = true;
					personName.setPreferred(true);
				}
				if (String.valueOf(HumanName.NameUse.OLD).equalsIgnoreCase(getUse)) {
					personName.setPreferred(false);
				}
			}
			if (humanNameDt.getSuffix() != null) {
				List<StringType> prefixes = humanNameDt.getSuffix();
				if (prefixes.size() > 0) {
					StringType prefix = prefixes.get(0);
					personName.setPrefix(valueOf(prefix));
				}
			}
			if (humanNameDt.getSuffix() != null) {
				List<StringType> suffixes = humanNameDt.getSuffix();
				if (suffixes.size() > 0) {
					StringType suffix = suffixes.get(0);
					personName.setFamilyNameSuffix(valueOf(suffix));
				}
			}
			
			List<StringType> givenNames = humanNameDt.getGiven();
			if (givenNames != null) {
				givennamePresent = true;
				StringType givenName = givenNames.get(0);
				personName.setGivenName(valueOf(givenName));
			}
			String familyName = humanNameDt.getFamily();
			if (!StringUtils.isEmpty(familyName)) {
				familynamePresent = true;
				personName.setFamilyName(familyName);
			}
			names.add(personName);
			if (preferedPresent && givennamePresent && familynamePresent) { //if all are present in one name, further checkings are not needed
				doCheckName = false; // cancel future checkings
			}
			if (doCheckName) { // if no suitable names found, these variables should be reset
				preferedPresent = false;
				givennamePresent = false;
				familynamePresent = false;
			}
		}
		omrsPatient.setNames(names);
		if (doCheckName) {
			errors.add("Person should have atleast one prefered name with family name and given name");
		}

		Set<PersonAddress> addresses = new TreeSet<PersonAddress>();
		PersonAddress address;
		for (Address fhirAddress : patient.getAddress()) {
			address = new PersonAddress();
			address.setCityVillage(fhirAddress.getCity());
			address.setCountry(fhirAddress.getCountry());
			address.setStateProvince(fhirAddress.getState());
			address.setPostalCode(fhirAddress.getPostalCode());
			List<StringType> addressStrings = fhirAddress.getLine();
			
			if (addressStrings != null) {
				for (int i = 0; i < addressStrings.size(); i++) {
					if (i == 0) {
						address.setAddress1(valueOf(addressStrings.get(0)));
					} else if (i == 1) {
						address.setAddress2(valueOf(addressStrings.get(1)));
					} else if (i == 2) {
						address.setAddress3(valueOf(addressStrings.get(2)));
					} else if (i == 3) {
						address.setAddress4(valueOf(addressStrings.get(3)));
					} else if (i == 4) {
						address.setAddress5(valueOf(addressStrings.get(4)));
					}
				}
			}
			
			if (String.valueOf(Address.AddressUse.HOME.toCode()).equalsIgnoreCase(fhirAddress.getUse().toCode())) {
				address.setPreferred(true);
			}
			if (String.valueOf(Address.AddressUse.OLD.toCode()).equalsIgnoreCase(fhirAddress.getUse().toCode())) {
				address.setPreferred(false);
			}
			addresses.add(address);
		}
		omrsPatient.setAddresses(addresses);
		
		if (patient.getGender() != null) {
			if (patient.getGender().toCode().equalsIgnoreCase(Enumerations.AdministrativeGender.MALE.toCode())) {
				omrsPatient.setGender(FHIRConstants.MALE);
			} else if (patient.getGender().toCode().equalsIgnoreCase(String.valueOf(Enumerations.AdministrativeGender.FEMALE))) {
				omrsPatient.setGender(FHIRConstants.FEMALE);
			}
		} else {
			errors.add("Gender cannot be empty");
		}
		omrsPatient.setBirthdate(patient.getBirthDate());

		BooleanType Isdeceased = (BooleanType) patient.getDeceased();
		omrsPatient.setDead(Isdeceased.getValue());
		
		if (patient.getActive()) {
			omrsPatient.setPersonVoided(false);
		} else {
			omrsPatient.setPersonVoided(true);
			omrsPatient.setPersonVoidReason("Deleted from FHIR module"); // deleted reason is compulsory
		}
		return omrsPatient;
	}
	
	public static org.openmrs.Patient updatePatientAttributes(org.openmrs.Patient omrsPatient,
	                                                          org.openmrs.Patient retrievedPatient) {
		Set<PersonName> all = retrievedPatient.getNames();
		boolean needToSetPrefferedName = false; // indicate wheter any preffered names are in the request body. 
		for (PersonName name : omrsPatient.getNames()) {
			if (name.getPreferred()) { // detecting any preffered names are in the request body
				needToSetPrefferedName = true;
			}
		}
		if (needToSetPrefferedName) { // unset the existing preffered name, 
			for (PersonName name : all) {
				name.setPreferred(false);
			}
		}
		for (PersonName name : omrsPatient.getNames()) {
			all.add(name); // add all the new names to the person
		}
		retrievedPatient.setNames(all);
		Set<PersonAddress> allAddress = retrievedPatient.getAddresses();
		boolean needToSetHome = false;
		for (PersonAddress address : omrsPatient.getAddresses()) {
			if (address.isPreferred()) {
				needToSetHome = true;
			}
		}
		if (needToSetHome) {
			for (PersonAddress address : allAddress) {
				address.setPreferred(false);
			}
		}
		for (PersonAddress address : omrsPatient.getAddresses()) {
			allAddress.add(address);
		}
		retrievedPatient.setAddresses(allAddress);
		retrievedPatient.setPersonVoided(omrsPatient.getVoided());
		if (omrsPatient.getVoided()) {
			retrievedPatient.setPersonVoidReason(FHIRConstants.PATIENT_VOIDED_MESSAGE); // deleted reason is compulsory
		}
		retrievedPatient.setBirthdate(omrsPatient.getBirthdate());
		retrievedPatient.setGender(omrsPatient.getGender());
		return retrievedPatient;
	}

	/**
	 * Build FhIRe reference from Patient
	 * @param patient patient resource
	 * @return FHIR Reference
	 */
	public static Reference buildPatientReference(org.openmrs.Patient patient) {
		//Build and set patient reference
		Reference patientReference = new Reference();
		PersonName name = patient.getPersonName();
		StringBuilder nameDisplay = new StringBuilder();
		nameDisplay.append(name.getGivenName());
		nameDisplay.append(" ");
		nameDisplay.append(name.getFamilyName());
		String patientUri;
		nameDisplay.append("(");
		nameDisplay.append(FHIRConstants.IDENTIFIER);
		nameDisplay.append(":");
		nameDisplay.append(patient.getPatientIdentifier().getIdentifier());
		nameDisplay.append(")");
		patientUri = FHIRConstants.PATIENT + "/" + patient.getUuid();
		patientReference.setReference(patientUri);
		patientReference.setDisplay(nameDisplay.toString());
		patientReference.setId(patient.getUuid());
		return patientReference;
	}

	private static String extractUuid(String uuid) {
		return uuid.contains("/") ? uuid.substring(uuid.indexOf("/") + 1) : uuid;
	}
}
