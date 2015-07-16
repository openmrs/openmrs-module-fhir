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
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonName;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;

import ca.uhn.fhir.model.dstu2.composite.AddressDt;
import ca.uhn.fhir.model.dstu2.composite.HumanNameDt;
import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.resource.Practitioner;
import ca.uhn.fhir.model.dstu2.valueset.AddressUseEnum;
import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
import ca.uhn.fhir.model.dstu2.valueset.NameUseEnum;
import ca.uhn.fhir.model.primitive.DateDt;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.StringDt;

public class FHIRPractitionerUtil {

	public static Practitioner generatePractitioner(Provider provider) {

		Practitioner practitioner = new Practitioner();
		//Set practitioner ID
		IdDt uuid = new IdDt();
		uuid.setValue(provider.getUuid());
		practitioner.setId(uuid);

		//Set patient identifiers to fhir practitioner
		IdentifierDt identifier = new IdentifierDt();
		identifier.setValue(provider.getIdentifier());
		List<IdentifierDt> identifiers = new ArrayList<IdentifierDt>();
		identifiers.add(identifier);
		practitioner.setIdentifier(identifiers);
		if (provider.getPerson() != null) {
			for (PersonName name : provider.getPerson().getNames()) {
				if (name.isPreferred()) {
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
					fhirName.setUse(NameUseEnum.USUAL);
					practitioner.setName(fhirName);
				}
			}
			//Set address in FHIR patient
			List<AddressDt> addressList = new ArrayList<AddressDt>();
			AddressDt fhirAddress;
			for (PersonAddress address : provider.getPerson().getAddresses()) {
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
			practitioner.setAddress(addressList);
			//Set gender in fhir practitioner object
			if (provider.getPerson().getGender().equals("M")) {
				practitioner.setGender(AdministrativeGenderEnum.MALE);
			} else if (provider.getPerson().getGender().equals("F")) {
				practitioner.setGender(AdministrativeGenderEnum.FEMALE);
			} else {
				practitioner.setGender(AdministrativeGenderEnum.UNKNOWN);
			}

			DateDt fhirBirthDate = new DateDt();
			fhirBirthDate.setValue(provider.getPerson().getBirthdate());
			practitioner.setBirthDate(fhirBirthDate);
		} else {
			HumanNameDt fhirName = new HumanNameDt();
			StringDt givenName = new StringDt();
			givenName.setValue(provider.getName());
			List<StringDt> givenNames = new ArrayList<StringDt>();
			givenNames.add(givenName);
			fhirName.setGiven(givenNames);
			fhirName.setUse(NameUseEnum.USUAL);
			practitioner.setName(fhirName);
			practitioner.setGender(AdministrativeGenderEnum.UNKNOWN);
		}
		FHIRUtils.validate(practitioner);
		return practitioner;
	}
	
	public static Person generateOpenMRSPerson(Person personFromRequest) {
		Set<PersonName> nameList = personFromRequest.getNames();
		PersonName usedName = null;
		for (PersonName prsnName : nameList) {
			if (prsnName.isPreferred()) {
				usedName = prsnName;
				break;
			}
		}
		
		Set<Person> personList = Context.getPersonService().getSimilarPeople(usedName.getFullName(),
		    1900 + personFromRequest.getBirthdate().getYear(), personFromRequest.getGender()); // filter Persons
		boolean createPerson = false;
		if (personList.isEmpty()) {
			createPerson = true;
		} else {
			if (personList.size() != 1) {
				createPerson = true;
			}
		}
		
		Person personForProvider = null;
		if (createPerson) {
			personForProvider = Context.getPersonService().savePerson(personFromRequest);
		} else {
			for (Iterator<Person> pers = personList.iterator(); pers.hasNext();) {
				personForProvider = pers.next();
			}
		}
		return personForProvider;
	}
	
	public static Person extractOpenMRSPerson(Practitioner practitioner) {
		Person omrsPerson = new Person();
		HumanNameDt humanNameDt = practitioner.getName();
		PersonName nam = new PersonName();
		if (humanNameDt != null) {
			List<StringDt> givenNames = humanNameDt.getGiven();
			if (givenNames != null && !givenNames.isEmpty()) {
				StringDt givenName = givenNames.get(0);
				nam.setGivenName(valueOf(givenName));
			} else {
				return null;
			}
			List<StringDt> familyNames = humanNameDt.getFamily();
			if (familyNames != null && !familyNames.isEmpty()) {
				StringDt familyName = familyNames.get(0);
				nam.setFamilyName(valueOf(familyName));
			} else {
				return null;
			}
			nam.setPreferred(true);
			if (humanNameDt.getPrefix() != null) {
				List<StringDt> prefixes = humanNameDt.getPrefix();
				if (prefixes != null && !prefixes.isEmpty()) {
					StringDt prefix = prefixes.get(0);
					nam.setPrefix(valueOf(prefix));
				}
			}
			if (humanNameDt.getSuffix() != null) {
				List<StringDt> suffixes = humanNameDt.getSuffix();
				if (suffixes != null && !suffixes.isEmpty()) {
					StringDt suffix = suffixes.get(0);
					nam.setFamilyNameSuffix(valueOf(suffix));
				}
			}
			Set<PersonName> names = new TreeSet<PersonName>();
			names.add(nam);
			omrsPerson.setNames(names);
		} else {
			return null;
		}
		
		Set<PersonAddress> addresses = new TreeSet<PersonAddress>();
		PersonAddress address;
		for (AddressDt fhirAddress : practitioner.getAddress()) {
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
		if (String.valueOf(AdministrativeGenderEnum.MALE).equalsIgnoreCase(practitioner.getGender())) {
			omrsPerson.setGender(FHIRConstants.MALE);
		} else if (String.valueOf(AdministrativeGenderEnum.FEMALE).equalsIgnoreCase(practitioner.getGender())) {
			omrsPerson.setGender(FHIRConstants.FEMALE);
		} else if (String.valueOf(AdministrativeGenderEnum.MALE).equalsIgnoreCase(practitioner.getGender())) {
			omrsPerson.setGender(FHIRConstants.OTHER_GENDER);
		} else {
			return null;
		}
		omrsPerson.setBirthdate(practitioner.getBirthDate());
		return omrsPerson;
	}
	
	public static org.openmrs.Provider updatePractitionerAttributes(Practitioner practitioner,
	                                                                org.openmrs.Provider retrievedProvider) {
		String gender = practitioner.getGender();
		String givenname = null;
		String familyname = null; // these three attributes are mandotory for create a new person
		
		HumanNameDt humanNameDt = practitioner.getName();
		PersonName name = new PersonName();
		List<StringDt> givenNames = humanNameDt.getGiven();
		if (givenNames != null) {
			StringDt givenName = givenNames.get(0);
			givenname = valueOf(givenName); // initialize vatiables
		}
		List<StringDt> familyNames = humanNameDt.getFamily();
		if (familyNames != null) {
			StringDt familyName = familyNames.get(0);
			familyname = valueOf(familyName);
		}
		
		Person providerPerson = retrievedProvider.getPerson(); // get the person associates with the practitioner resource
		
		if (providerPerson == null) { // if the person is null, create a new person first. 
			if (gender != null && givenname != null && familyname != null) {
				Person newPerson = new Person();
				Set<PersonName> nameSet = new TreeSet<PersonName>();
				name.setFamilyName(familyname);
				name.setGivenName(familyname);
				name.setPreferred(true);
				nameSet.add(name);
				newPerson.setNames(nameSet);
				
				if (String.valueOf(AdministrativeGenderEnum.MALE).equalsIgnoreCase(practitioner.getGender())) {
					newPerson.setGender(FHIRConstants.MALE);
				} else if (String.valueOf(AdministrativeGenderEnum.FEMALE).equalsIgnoreCase(practitioner.getGender())) {
					newPerson.setGender(FHIRConstants.FEMALE);
				} else {
					newPerson.setGender("o");
				}
				newPerson = Context.getPersonService().savePerson(newPerson);
				retrievedProvider.setPerson(newPerson);
				retrievedProvider.setName("");
			}
		} else { // if , there is a person, associates with the practitioner, update its persons attributes
			Set<PersonName> all = providerPerson.getNames();
			if (all.size() != 0) {
				for (PersonName pname : all) {
					pname.setPreferred(false);
				}
				HumanNameDt humanName = practitioner.getName();
				PersonName nam = new PersonName();
				List<StringDt> giveNames = humanName.getGiven();
				if (giveNames != null) {
					StringDt givenName = giveNames.get(0);
					nam.setGivenName(valueOf(givenName));
				}
				List<StringDt> famlyNames = humanName.getFamily();
				if (famlyNames != null) {
					StringDt familyName = famlyNames.get(0);
					nam.setFamilyName(valueOf(familyName));
				}
				nam.setPreferred(true);
				if (humanName.getPrefix() != null) {
					List<StringDt> prefixes = humanName.getPrefix();
					if (prefixes.size() > 0) {
						StringDt prefix = prefixes.get(0);
						nam.setPrefix(valueOf(prefix));
					}
				}
				if (humanName.getSuffix() != null) {
					List<StringDt> suffixes = humanName.getSuffix();
					if (suffixes.size() > 0) {
						StringDt suffix = suffixes.get(0);
						nam.setFamilyNameSuffix(valueOf(suffix));
					}
				}
				nam.setPreferred(true);
				all.add(nam);
				providerPerson.setNames(all);
			}
			Set<PersonAddress> allAddress = providerPerson.getAddresses();
			if (allAddress.size() != 0) {
				for (PersonAddress address : allAddress) {
					address.setPreferred(false);
				}
				PersonAddress address;
				for (AddressDt fhirAddress : practitioner.getAddress()) {
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
					allAddress.add(address);
				}
				providerPerson.setAddresses(allAddress);
			}
		}

		if (practitioner.getGender() != null) {
			if (String.valueOf(AdministrativeGenderEnum.MALE).equalsIgnoreCase(practitioner.getGender())) {
				providerPerson.setGender(FHIRConstants.MALE);
			} else if (String.valueOf(AdministrativeGenderEnum.FEMALE).equalsIgnoreCase(practitioner.getGender())) {
				providerPerson.setGender(FHIRConstants.FEMALE);
			} else {
				providerPerson.setGender("o");
			}
		}
		if (practitioner.getBirthDate() != null) {
			providerPerson.setBirthdate(practitioner.getBirthDate());
		}
		retrievedProvider.setPerson(providerPerson);
		return retrievedProvider;
	}

}
