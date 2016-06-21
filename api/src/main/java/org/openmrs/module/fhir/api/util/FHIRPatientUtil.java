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

import static java.lang.String.valueOf;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAddress;
import org.openmrs.PersonName;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;

import ca.uhn.fhir.model.dstu2.composite.AddressDt;
import ca.uhn.fhir.model.dstu2.composite.ContactPointDt;
import ca.uhn.fhir.model.dstu2.composite.HumanNameDt;
import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.dstu2.valueset.AddressUseEnum;
import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
import ca.uhn.fhir.model.dstu2.valueset.ContactPointSystemEnum;
import ca.uhn.fhir.model.dstu2.valueset.IdentifierUseEnum;
import ca.uhn.fhir.model.dstu2.valueset.NameUseEnum;
import ca.uhn.fhir.model.primitive.BooleanDt;
import ca.uhn.fhir.model.primitive.DateDt;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.StringDt;

public class FHIRPatientUtil {

	public static Patient generatePatient(org.openmrs.Patient omrsPatient) {
		
		Patient patient = new Patient();
		//Set patient id to fhir patient
		IdDt uuid = new IdDt();
		uuid.setValue(omrsPatient.getUuid());
		patient.setId(uuid);

		//Set patient identifiers to fhir patient
		for (PatientIdentifier identifier : omrsPatient.getActiveIdentifiers()) {
			String urn = FHIRUtils.buildURN(FHIRConstants.UUID, identifier.getIdentifierType().getUuid());
			if (identifier.isPreferred()) {
				patient.addIdentifier().setUse(IdentifierUseEnum.USUAL).setSystem(identifier.getIdentifierType().getName())
						.setValue(identifier.getIdentifier());
			} else {
				patient.addIdentifier().setUse(IdentifierUseEnum.SECONDARY).setSystem(identifier.getIdentifierType()
						.getName()).setValue(identifier.getIdentifier());
			}
		}

		//Set patient name to fhir patient
		List<HumanNameDt> humanNameDts = new ArrayList<HumanNameDt>();
		for (PersonName name : omrsPatient.getNames()) {
			HumanNameDt fhirName = new HumanNameDt();
			StringDt familyName = new StringDt();
			familyName.setValue(name.getFamilyName());
			List<StringDt> familyNames = new ArrayList<StringDt>();
			familyNames.add(familyName);
			fhirName.setFamily(familyNames);
			StringDt givenName = new StringDt();
			givenName.setValue(name.getGivenName());
			List<StringDt> givenNames = new ArrayList<StringDt>();
			givenNames.add(givenName);
			fhirName.setGiven(givenNames);

			if (name.getFamilyNameSuffix() != null) {
				StringDt suffix = fhirName.addSuffix();
				suffix.setValue(name.getFamilyNameSuffix());
				List<StringDt> suffixes = new ArrayList<StringDt>();
				suffixes.add(suffix);
				fhirName.setSuffix(suffixes);
			}

			if (name.getPrefix() != null) {
				StringDt prefix = fhirName.addPrefix();
				prefix.setValue(name.getPrefix());
				List<StringDt> prefixes = new ArrayList<StringDt>();
				prefixes.add(prefix);
				fhirName.setSuffix(prefixes);
			}

			//TODO needs to set catagory appropriately
			if (name.isPreferred()) {
				fhirName.setUse(NameUseEnum.USUAL);
			} else {
				fhirName.setUse(NameUseEnum.OLD);
			}
			humanNameDts.add(fhirName);
		}
		patient.setName(humanNameDts);

		//Set gender in fhir patient object
		if (omrsPatient.getGender().equals("M")) {
			patient.setGender(AdministrativeGenderEnum.MALE);
		} else if (omrsPatient.getGender().equals("F")) {
			patient.setGender(AdministrativeGenderEnum.FEMALE);
		} else {
			patient.setGender(AdministrativeGenderEnum.UNKNOWN);
		}

		List<AddressDt> fhirAddresses = patient.getAddress();
		for (PersonAddress address : omrsPatient.getAddresses()) {
			AddressDt fhirAddress = new AddressDt();
			fhirAddress.setCity(address.getCityVillage());
			fhirAddress.setCountry(address.getCountry());
			fhirAddress.setState(address.getStateProvince());
			fhirAddress.setPostalCode(address.getPostalCode());
			List<StringDt> addressStrings = new ArrayList<StringDt>();
			addressStrings.add(new StringDt(address.getAddress1()));
			addressStrings.add(new StringDt(address.getAddress2()));
			addressStrings.add(new StringDt(address.getAddress3()));
			addressStrings.add(new StringDt(address.getAddress4()));
			addressStrings.add(new StringDt(address.getAddress5()));
			fhirAddress.setLine(addressStrings);
			if (address.isPreferred()) {
				fhirAddress.setUse(AddressUseEnum.HOME);
			} else {
				fhirAddress.setUse(AddressUseEnum.OLD);
			}
			fhirAddresses.add(fhirAddress);
		}
		patient.setAddress(fhirAddresses);

		if (omrsPatient.getBirthdate() != null) {
			DateDt fhirBirthDate = new DateDt();
			fhirBirthDate.setValue(omrsPatient.getBirthdate());
			patient.setBirthDate(fhirBirthDate);
		}

		patient.setActive(!omrsPatient.isVoided());

		if (omrsPatient.isDead()) {
			DateTimeDt fhirDeathDate = new DateTimeDt();
			fhirDeathDate.setValue(omrsPatient.getDeathDate());
			patient.setDeceased(fhirDeathDate);
		} else {
			BooleanDt isDeceased = new BooleanDt();
			isDeceased.setValue(omrsPatient.getDead());
			patient.setDeceased(isDeceased);
		}

		List<ContactPointDt> dts = new ArrayList<ContactPointDt>();
		// Add global property for telephone / email address. These properties will be used to identify the name of the
		// person attribute (if any) being used to store a phone number and/or email.
		if (omrsPatient.getAttribute(FHIRUtils.PATIENT_PHONE_NUMBER_ATTRIBUTE) != null) {
			ContactPointDt telecom = new ContactPointDt();
			telecom.setSystem(ContactPointSystemEnum.PHONE).setValue(omrsPatient.getAttribute(
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
			omrsPatient.setUuid(patient.getId().getIdPart());
		}

		List<IdentifierDt> fhirIdList = patient.getIdentifier();
		Set<PatientIdentifier> idList = new TreeSet<PatientIdentifier>();
		
		if (fhirIdList == null || fhirIdList.isEmpty()) {
			errors.add("Identifiers cannot be empty");
		}

		for (IdentifierDt fhirIentifier : fhirIdList) {
			PatientIdentifier patientIdentifier = new PatientIdentifier();
			patientIdentifier.setIdentifier(fhirIentifier.getValue());
			String identifierTypeName = fhirIentifier.getSystem();
			if (String.valueOf(IdentifierUseEnum.USUAL).equalsIgnoreCase(fhirIentifier.getUse())) {
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
		for (HumanNameDt humanNameDt : patient.getName()) {
			PersonName personName = new PersonName();
			if (humanNameDt.getUse() != null) {
				String getUse = humanNameDt.getUse();
				if (String.valueOf(NameUseEnum.OFFICIAL).equalsIgnoreCase(getUse)
				        || String.valueOf(NameUseEnum.USUAL).equalsIgnoreCase(getUse)) {
					preferedPresent = true;
					personName.setPreferred(true);
				}
				if (String.valueOf(NameUseEnum.OLD).equalsIgnoreCase(getUse)) {
					personName.setPreferred(false);
				}
			}
			if (humanNameDt.getSuffix() != null) {
				List<StringDt> prefixes = humanNameDt.getSuffix();
				if (prefixes.size() > 0) {
					StringDt prefix = prefixes.get(0);
					personName.setPrefix(valueOf(prefix));
				}
			}
			if (humanNameDt.getSuffix() != null) {
				List<StringDt> suffixes = humanNameDt.getSuffix();
				if (suffixes.size() > 0) {
					StringDt suffix = suffixes.get(0);
					personName.setFamilyNameSuffix(valueOf(suffix));
				}
			}
			
			List<StringDt> givenNames = humanNameDt.getGiven();
			if (givenNames != null) {
				givennamePresent = true;
				StringDt givenName = givenNames.get(0);
				personName.setGivenName(valueOf(givenName));
			}
			List<StringDt> familyNames = humanNameDt.getFamily();
			if (familyNames != null) {
				familynamePresent = true;
				StringDt familyName = familyNames.get(0);
				personName.setFamilyName(valueOf(familyName));
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
		for (AddressDt fhirAddress : patient.getAddress()) {
			address = new PersonAddress();
			address.setCityVillage(fhirAddress.getCity());
			address.setCountry(fhirAddress.getCountry());
			address.setStateProvince(fhirAddress.getState());
			address.setPostalCode(fhirAddress.getPostalCode());
			List<StringDt> addressStrings = fhirAddress.getLine();
			
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
			
			if (String.valueOf(AddressUseEnum.HOME).equalsIgnoreCase(fhirAddress.getUse())) {
				address.setPreferred(true);
			}
			if (String.valueOf(AddressUseEnum.OLD).equalsIgnoreCase(fhirAddress.getUse())) {
				address.setPreferred(false);
			}
			addresses.add(address);
		}
		omrsPatient.setAddresses(addresses);
		
		if (patient.getGender() != null && !patient.getGender().isEmpty()) {
			if (patient.getGender().equalsIgnoreCase(String.valueOf(AdministrativeGenderEnum.MALE))) {
				omrsPatient.setGender(FHIRConstants.MALE);
			} else if (patient.getGender().equalsIgnoreCase(String.valueOf(AdministrativeGenderEnum.FEMALE))) {
				omrsPatient.setGender(FHIRConstants.FEMALE);
			}
		} else {
			errors.add("Gender cannot be empty");
		}
		omrsPatient.setBirthdate(patient.getBirthDate());

		BooleanDt Isdeceased = (BooleanDt) patient.getDeceased();
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
			retrievedPatient.setPersonVoidReason("Deleted from FHIR module"); // deleted reason is compulsory
		}
		retrievedPatient.setBirthdate(omrsPatient.getBirthdate());
		retrievedPatient.setGender(omrsPatient.getGender());
		//retrievedPerson.getActiveAttributes().get(0).setValue("Test");
		//	retrievedPerson.getActiveAttributes().get(1).setValue("Test");
		return retrievedPatient;
	}

}
