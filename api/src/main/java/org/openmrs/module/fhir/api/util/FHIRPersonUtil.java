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

import org.openmrs.Patient;
import org.openmrs.PersonAddress;
import org.openmrs.PersonName;
import org.openmrs.api.context.Context;

import ca.uhn.fhir.model.dstu2.composite.AddressDt;
import ca.uhn.fhir.model.dstu2.composite.HumanNameDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Person;
import ca.uhn.fhir.model.dstu2.valueset.AddressUseEnum;
import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
import ca.uhn.fhir.model.dstu2.valueset.NameUseEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.StringDt;

public class FHIRPersonUtil {

	public static Person generatePerson(org.openmrs.Person omrsPerson) {
		Person person = new Person();
		//Set person ID
		IdDt uuid = new IdDt();
		uuid.setValue(omrsPerson.getUuid());
		person.setId(uuid);
		List<HumanNameDt> humanNames = new ArrayList<HumanNameDt>();
		for (PersonName name : omrsPerson.getNames()) {
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

			if (name.getFamilyNamePrefix() != null) {
				StringDt prefix = fhirName.addPrefix();
				prefix.setValue(name.getPrefix());
				List<StringDt> prefixes = new ArrayList<StringDt>();
				prefixes.add(prefix);
				fhirName.setPrefix(prefixes);
			}
			if (name.isPreferred()) {
				fhirName.setUse(NameUseEnum.USUAL);
			} else {
				fhirName.setUse(NameUseEnum.OLD);
			}
			humanNames.add(fhirName);
		}
		person.setName(humanNames);

		//Set address in FHIR person
		List<AddressDt> addressList = new ArrayList<AddressDt>();
		AddressDt fhirAddress;
		for (PersonAddress address : omrsPerson.getAddresses()) {
			fhirAddress = new AddressDt();
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
			addressList.add(fhirAddress);
		}
		person.setAddress(addressList);
		//Set gender in fhir person object
		if (omrsPerson.getGender().equals("M")) {
			person.setGender(AdministrativeGenderEnum.MALE);
		} else if (omrsPerson.getGender().equals("F")) {
			person.setGender(AdministrativeGenderEnum.FEMALE);
		} else {
			person.setGender(AdministrativeGenderEnum.UNKNOWN);
		}

		DateTimeDt fhirBirthDate = new DateTimeDt();
		fhirBirthDate.setValue(omrsPerson.getBirthdate());
		person.setBirthDate(fhirBirthDate);
		if (!omrsPerson.isVoided()) {
			person.setActive(true);
		} else {
			person.setActive(false);
		}

		//Check whether person converted to a patient
		Patient patient = Context.getPatientService().getPatientByUuid(omrsPerson.getUuid());
		if (patient != null) {
			List<Person.Link> links = new ArrayList<Person.Link>();
			Person.Link link = new Person.Link();
			String uri = FHIRConstants.PATIENT + "/" + omrsPerson.getUuid();
			ResourceReferenceDt other = new ResourceReferenceDt();
			PersonName name = omrsPerson.getPersonName();
			StringBuilder nameDisplay = new StringBuilder();
			nameDisplay.append(name.getGivenName());
			nameDisplay.append(" ");
			nameDisplay.append(name.getFamilyName());
			other.setDisplay(nameDisplay.toString());
			IdDt patientRef = new IdDt();
			patientRef.setValue(uri);
			other.setReference(patientRef);
			link.setTarget(other);
			links.add(link);
			person.setLink(links);
		}

		FHIRUtils.validate(person);
		return person;
	}

	/**
	 * @param personFHIR
	 * @return OpenMRS person after giving a FHIR person
	 * @should generate Oms Person
	 */
	public static org.openmrs.Person generateOpenMRSPerson(ca.uhn.fhir.model.dstu2.resource.Person personFHIR,
	                                                       List<String> errors) {
		org.openmrs.Person omrsPerson = new org.openmrs.Person();
		boolean preferedPresent = false, givennamePresent = false, familynamePresent = false, doCheckName = true;

		if (personFHIR.getId() != null) {
			omrsPerson.setUuid(personFHIR.getId().getIdPart());
		}
		Set<PersonName> names = new TreeSet<PersonName>();
		if (personFHIR.getName().size() == 0) {
			errors.add("Name cannot be empty");
		}
		for (HumanNameDt humanNameDt : personFHIR.getName()) {
			PersonName personName = new PersonName();
			if (humanNameDt.getUse() != null) {
				String getUse = humanNameDt.getUse();
				if (String.valueOf(NameUseEnum.USUAL).equalsIgnoreCase(getUse)) {
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
		omrsPerson.setNames(names);
		if (doCheckName) {
			errors.add("Person should have atleast one prefered name with family name and given name");
		}

		Set<PersonAddress> addresses = new TreeSet<PersonAddress>();
		PersonAddress address;
		for (AddressDt fhirAddress : personFHIR.getAddress()) {
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
		omrsPerson.setAddresses(addresses);
		
		if (personFHIR.getGender() != null && !personFHIR.getGender().isEmpty()) {
			if (personFHIR.getGender().equalsIgnoreCase(String.valueOf(AdministrativeGenderEnum.MALE))) {
				omrsPerson.setGender(FHIRConstants.MALE);
			} else if (personFHIR.getGender().equalsIgnoreCase(String.valueOf(AdministrativeGenderEnum.FEMALE))) {
				omrsPerson.setGender(FHIRConstants.FEMALE);
			}
		} else {
			errors.add("Gender cannot be empty");
		}


		omrsPerson.setBirthdate(personFHIR.getBirthDate());
		if (personFHIR.getActive()) {
			omrsPerson.setPersonVoided(false);
		} else {
			omrsPerson.setPersonVoided(true);
			omrsPerson.setPersonVoidReason("Deleted from FHIR module"); // deleted reason is compulsory
		}
		
		return omrsPerson;
	}
	
	/**
	 * @param omrsPerson      which contains OpenMRS Person who has the same attributes of the json request body
	 * @param retrievedPerson the OpenMRS person which was read from the DB for the given uuid in the PUT request.
	 * @return OpenMRS person after copying all the attributes of the PUT request to the retrievedPerson
	 * @should generate OpenMRS Person
	 */
	public static org.openmrs.Person updatePersonAttributes(org.openmrs.Person omrsPerson,
	                                                        org.openmrs.Person retrievedPerson) {
		Set<PersonName> all = retrievedPerson.getNames();
		boolean needToSetPrefferedName = false; // indicate wheter any preffered names are in the request body. 
		for (PersonName name : omrsPerson.getNames()) {
			if (name.getPreferred()) { // detecting any preffered names are in the request body
				needToSetPrefferedName = true;
			}
		}
		if (needToSetPrefferedName) { // unset the existing preffered name, 
			for (PersonName name : all) {
				name.setPreferred(false);
			}
		}
		for (PersonName name : omrsPerson.getNames()) {
			all.add(name); // add all the new names to the person
		}
		retrievedPerson.setNames(all);
		Set<PersonAddress> allAddress = retrievedPerson.getAddresses();
		boolean needToSetHome = false;
		for (PersonAddress address : omrsPerson.getAddresses()) {
			if (address.isPreferred()) {
				needToSetHome = true;
			}
		}
		if (needToSetHome) {
			for (PersonAddress address : allAddress) {
				address.setPreferred(false);
			}
		}
		for (PersonAddress address1 : omrsPerson.getAddresses()) {
			allAddress.add(address1);
		}
		retrievedPerson.setAddresses(allAddress);
		retrievedPerson.setPersonVoided(omrsPerson.getVoided());
		if (omrsPerson.getVoided()) {
			retrievedPerson.setPersonVoidReason("Deleted from FHIR module"); // deleted reason is compulsory
		}
		retrievedPerson.setBirthdate(omrsPerson.getBirthdate());
		retrievedPerson.setGender(omrsPerson.getGender());
		retrievedPerson.getActiveAttributes().get(0).setValue("Test");
		retrievedPerson.getActiveAttributes().get(1).setValue("Test");
		return retrievedPerson;
	}
}
