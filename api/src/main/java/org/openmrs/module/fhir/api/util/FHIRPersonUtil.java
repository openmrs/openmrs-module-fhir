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
import org.hl7.fhir.dstu3.model.Enumerations;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.Person;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.StringType;
import org.openmrs.Patient;
import org.openmrs.PersonAddress;
import org.openmrs.PersonName;
import org.openmrs.api.context.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static java.lang.String.valueOf;
import static org.openmrs.module.fhir.api.util.FHIRUtils.extractUuid;

public class FHIRPersonUtil {

	public static Person generatePerson(org.openmrs.Person omrsPerson) {
		Person person = new Person();
		//Set person ID
		person.setId(omrsPerson.getUuid());

		List<HumanName> humanNames = new ArrayList<>();
		for (PersonName name : omrsPerson.getNames()) {
			HumanName fhirName = new HumanName();
			fhirName.setFamily(name.getFamilyName());
			StringType givenName = new StringType();
			givenName.setValue(name.getGivenName());
			List<StringType> givenNames = new ArrayList<>();
			givenNames.add(givenName);
			fhirName.setGiven(givenNames);

			if (name.getFamilyNameSuffix() != null) {
				StringType suffix = new StringType();
				suffix.setValue(name.getFamilyNameSuffix());
				List<StringType> suffixes = new ArrayList<>();
				suffixes.add(suffix);
				fhirName.setSuffix(suffixes);
			}

			if (name.getFamilyNamePrefix() != null) {
				StringType prefix = new StringType();
				prefix.setValue(name.getPrefix());
				List<StringType> prefixes = new ArrayList<>();
				prefixes.add(prefix);
				fhirName.setPrefix(prefixes);
			}

			if (name.getPreferred()) {
				fhirName.setUse(HumanName.NameUse.USUAL);
			} else {
				fhirName.setUse(HumanName.NameUse.OLD);
			}

			humanNames.add(fhirName);
		}
		person.setName(humanNames);

		//Set address in FHIR person
		List<Address> addressList = new ArrayList<>();
		for (PersonAddress address : omrsPerson.getAddresses()) {
			addressList.add(FHIRUtils.buildAddress(address));
		}
		person.setAddress(addressList);

		//Set gender in fhir person object
		if (omrsPerson.getGender().equals("M")) {
			person.setGender(Enumerations.AdministrativeGender.MALE);
		} else if (omrsPerson.getGender().equals("F")) {
			person.setGender(Enumerations.AdministrativeGender.FEMALE);
		} else {
			person.setGender(Enumerations.AdministrativeGender.UNKNOWN);
		}

		person.setBirthDate(omrsPerson.getBirthdate());

		if (!omrsPerson.getPersonVoided()) {
			person.setActive(true);
		} else {
			person.setActive(false);
		}

		//Check whether person converted to a patient
		Patient patient = Context.getPatientService().getPatientByUuid(omrsPerson.getUuid());
		if (patient != null) {
			List<Person.PersonLinkComponent> links = new ArrayList<Person.PersonLinkComponent>();
			Person.PersonLinkComponent link = new Person.PersonLinkComponent();
			String uri = FHIRConstants.PATIENT + "/" + omrsPerson.getUuid();
			Reference other = new Reference();
			PersonName name = omrsPerson.getPersonName();
			StringBuilder nameDisplay = new StringBuilder();
			nameDisplay.append(name.getGivenName());
			nameDisplay.append(" ");
			nameDisplay.append(name.getFamilyName());
			other.setDisplay(nameDisplay.toString());
			other.setReference(uri);
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
	public static org.openmrs.Person generateOpenMRSPerson(org.hl7.fhir.dstu3.model.Person personFHIR,
			List<String> errors) {
		org.openmrs.Person omrsPerson = new org.openmrs.Person();
		boolean preferredPresent = false, givennamePresent = false, familynamePresent = false, doCheckName = true;

		if (personFHIR.getId() != null) {
			omrsPerson.setUuid(extractUuid(personFHIR.getId()));
		}

		Set<PersonName> names = new TreeSet<>();
		if (personFHIR.getName().size() == 0) {
			errors.add("Name cannot be empty");
		}

		for (HumanName humanNameDt : personFHIR.getName()) {
			PersonName personName = new PersonName();
			if (humanNameDt.getUse() != null) {
				String getUse = humanNameDt.getUse().toCode();
				if (String.valueOf(HumanName.NameUse.USUAL).equalsIgnoreCase(getUse)
						|| String.valueOf(HumanName.NameUse.OFFICIAL).equalsIgnoreCase(getUse)) {
					preferredPresent = true;
					personName.setPreferred(true);
				}
				if (String.valueOf(HumanName.NameUse.OLD).equalsIgnoreCase(getUse)) {
					personName.setPreferred(false);
				}
			}
			if (humanNameDt.getPrefix() != null) {
				List<StringType> prefixes = humanNameDt.getPrefix();
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

				if(givenNames.size() > 1) {
					StringType middleName = givenNames.get(1);
					personName.setMiddleName(valueOf(middleName));
				}
			}
			String familyName = humanNameDt.getFamily();
			if (!StringUtils.isEmpty(familyName)) {
				familynamePresent = true;
				personName.setFamilyName(familyName);
			}

			names.add(personName);
			if (preferredPresent && givennamePresent
					&& familynamePresent) { //if all are present in one name, further checking are not needed
				doCheckName = false; // cancel future checking
			}
			if (doCheckName) { // if no suitable names found, these variables should be reset
				preferredPresent = false;
				givennamePresent = false;
				familynamePresent = false;
			}
		}
		omrsPerson.setNames(names);
		if (doCheckName) {
			errors.add("Person should have atleast one preferred name with family name and given name");
		}

		Set<PersonAddress> addresses = new TreeSet<>();
		PersonAddress address;
		for (Address fhirAddress : personFHIR.getAddress()) {
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
		omrsPerson.setAddresses(addresses);

		if (personFHIR.getGender() != null) {
			if (personFHIR.getGender().toCode().equalsIgnoreCase(String.valueOf(Enumerations.AdministrativeGender.MALE))) {
				omrsPerson.setGender(FHIRConstants.MALE);
			} else if (personFHIR.getGender().toCode()
					.equalsIgnoreCase(String.valueOf(Enumerations.AdministrativeGender.FEMALE))) {
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
	 * @param omrsPerson      which contains OpenMRS Person who has the same attributes of the json
	 *                        request body
	 * @param retrievedPerson the OpenMRS person which was read from the DB for the given uuid in
	 *                        the PUT request.
	 * @return OpenMRS person after copying all the attributes of the PUT request to the
	 * retrievedPerson
	 * @should generate OpenMRS Person
	 */
	public static org.openmrs.Person updatePersonAttributes(org.openmrs.Person omrsPerson,
			org.openmrs.Person retrievedPerson) {
		Set<PersonName> all = retrievedPerson.getNames();
		boolean needToSetPreferredName = false; // indicate whether any preferred names are in the request body.
		for (PersonName name : omrsPerson.getNames()) {
			if (name.getPreferred()) { // detecting any preferred names are in the request body
				needToSetPreferredName = true;
			}
		}
		if (needToSetPreferredName) { // unset the existing preferred name,
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
			retrievedPerson.setPersonVoidReason(FHIRConstants.FHIR_VOIDED_MESSAGE); // deleted reason is compulsory
		}
		retrievedPerson.setBirthdate(omrsPerson.getBirthdate());
		retrievedPerson.setGender(omrsPerson.getGender());

		return retrievedPerson;
	}
}
