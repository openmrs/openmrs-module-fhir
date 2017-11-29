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
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.StringType;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonName;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static java.lang.String.valueOf;

public class FHIRPractitionerUtil {

	public static Practitioner generatePractitioner(Provider provider) {

		Practitioner practitioner = new Practitioner();
		//Set practitioner ID
		practitioner.setId(provider.getUuid());

		//Set patient identifiers to fhir practitioner
		Identifier identifier = new Identifier();
		identifier.setValue(provider.getIdentifier());
		List<Identifier> identifiers = new ArrayList<Identifier>();
		identifiers.add(identifier);
		practitioner.setIdentifier(identifiers);
		if (provider.getPerson() != null) {
			List<HumanName> names = new ArrayList<HumanName>();
			for (PersonName name : provider.getPerson().getNames()) {
				if (name.isPreferred()) {
					HumanName fhirName = new HumanName();
					String familyName = name.getFamilyName();
					fhirName.setFamily(familyName);
					StringType givenName = new StringType();
					givenName.setValue(name.getGivenName());
					List<StringType> givenNames = new ArrayList<StringType>();
					givenNames.add(givenName);
					fhirName.setGiven(givenNames);

					if (name.getFamilyNameSuffix() != null) {
						StringType suffix = new StringType();
						suffix.setValue(name.getFamilyNameSuffix());
						List<StringType> suffixes = new ArrayList<StringType>();
						suffixes.add(suffix);
						fhirName.setSuffix(suffixes);
					}

					if (name.getPrefix() != null) {
						StringType prefix = new StringType();
						prefix.setValue(name.getPrefix());
						List<StringType> prefixes = new ArrayList<StringType>();
						prefixes.add(prefix);
						fhirName.setSuffix(prefixes);
					}
					fhirName.setUse(HumanName.NameUse.USUAL);
					names.add(fhirName);
				}
				practitioner.setName(names);
			}
			//Set address in FHIR patient
			List<Address> addressList = new ArrayList<Address>();
			Address fhirAddress;
			for (PersonAddress address : provider.getPerson().getAddresses()) {
				addressList.add(FHIRUtils.buildAddress(address));
			}
			practitioner.setAddress(addressList);
			//Set gender in fhir practitioner object
			if (provider.getPerson().getGender().equals("M")) {
				practitioner.setGender(Enumerations.AdministrativeGender.MALE);
			} else if (provider.getPerson().getGender().equals("F")) {
				practitioner.setGender(Enumerations.AdministrativeGender.FEMALE);
			} else {
				practitioner.setGender(Enumerations.AdministrativeGender.UNKNOWN);
			}

			practitioner.setBirthDate(provider.getPerson().getBirthdate());
		} else {
			HumanName fhirName = new HumanName();
			StringType givenName = new StringType();
			givenName.setValue(provider.getName());
			List<StringType> givenNames = new ArrayList<StringType>();
			givenNames.add(givenName); // not only given name
			fhirName.setGiven(givenNames);
			fhirName.setUse(HumanName.NameUse.USUAL);
			List<HumanName> names = new ArrayList<HumanName>();
			names.add(fhirName);
			practitioner.setName(names);
			practitioner.setGender(Enumerations.AdministrativeGender.UNKNOWN);
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
		List<HumanName> humanNameDts = practitioner.getName();

		if(humanNameDts.isEmpty()) {
			return null;
		}

		Set<PersonName> names = new TreeSet<PersonName>();
		for(HumanName humanNameDt : humanNameDts) {
			PersonName name = new PersonName();
			if (humanNameDt != null) {
				List<StringType> givenNames = humanNameDt.getGiven();
				if (givenNames != null && !givenNames.isEmpty()) {
					StringType givenName = givenNames.get(0);
					name.setGivenName(valueOf(givenName));
				} else {
					return null;
				}
				String familyName = humanNameDt.getFamily();
				if (!StringUtils.isEmpty(familyName)) {
					name.setFamilyName(familyName);
				} else {
					return null;
				}
				name.setPreferred(true);
				if (humanNameDt.getPrefix() != null) {
					List<StringType> prefixes = humanNameDt.getPrefix();
					if (prefixes != null && !prefixes.isEmpty()) {
						StringType prefix = prefixes.get(0);
						name.setPrefix(valueOf(prefix));
					}
				}
				if (humanNameDt.getSuffix() != null) {
					List<StringType> suffixes = humanNameDt.getSuffix();
					if (suffixes != null && !suffixes.isEmpty()) {
						StringType suffix = suffixes.get(0);
						name.setFamilyNameSuffix(valueOf(suffix));
					}
				}
				names.add(name);
			}
		}
		omrsPerson.setNames(names);

		Set<PersonAddress> addresses = new TreeSet<PersonAddress>();
		PersonAddress address;
		for (Address fhirAddress : practitioner.getAddress()) {
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
		if (String.valueOf(Enumerations.AdministrativeGender.MALE.toCode()).equalsIgnoreCase(practitioner.getGender().toCode())) {
			omrsPerson.setGender(FHIRConstants.MALE);
		} else if (String.valueOf(Enumerations.AdministrativeGender.FEMALE.toCode()).equalsIgnoreCase(practitioner.getGender().toCode())) {
			omrsPerson.setGender(FHIRConstants.FEMALE);
		} else if (String.valueOf(Enumerations.AdministrativeGender.MALE.toCode()).equalsIgnoreCase(practitioner.getGender().toCode())) {
			omrsPerson.setGender(FHIRConstants.OTHER_GENDER);
		} else {
			return null;
		}
		omrsPerson.setBirthdate(practitioner.getBirthDate());
		return omrsPerson;
	}
	
	public static org.openmrs.Provider updatePractitionerAttributes(Practitioner practitioner,
	                                                                org.openmrs.Provider retrievedProvider) {
		String gender = practitioner.getGender().toCode();
		
		Person providerPerson = retrievedProvider.getPerson(); // get the person associates with the practitioner resource
		List<HumanName> humanNameDts = practitioner.getName();
		if (providerPerson == null) { // if the person is null, create a new person first. 
			if (gender != null) {
				Person newPerson = new Person();
				Set<PersonName> names = new TreeSet<PersonName>();
				for(HumanName humanNameDt : humanNameDts) {
					PersonName name = new PersonName();
					if (humanNameDt != null) {
						List<StringType> givenNames = humanNameDt.getGiven();
						if (givenNames != null && !givenNames.isEmpty()) {
							StringType givenName = givenNames.get(0);
							name.setGivenName(valueOf(givenName));
						} else {
							return null;
						}
						String familyName = humanNameDt.getFamily();
						if (!StringUtils.isEmpty(familyName)) {
							name.setFamilyName(familyName);
						} else {
							return null;
						}
						name.setPreferred(true);
						if (humanNameDt.getPrefix() != null) {
							List<StringType> prefixes = humanNameDt.getPrefix();
							if (prefixes != null && !prefixes.isEmpty()) {
								StringType prefix = prefixes.get(0);
								name.setPrefix(valueOf(prefix));
							}
						}
						if (humanNameDt.getSuffix() != null) {
							List<StringType> suffixes = humanNameDt.getSuffix();
							if (suffixes != null && !suffixes.isEmpty()) {
								StringType suffix = suffixes.get(0);
								name.setFamilyNameSuffix(valueOf(suffix));
							}
						}
						names.add(name);
					}
				}
				newPerson.setNames(names);
				
				if (String.valueOf(Enumerations.AdministrativeGender.MALE.toCode()).equalsIgnoreCase(practitioner.getGender().toCode())) {
					newPerson.setGender(FHIRConstants.MALE);
				} else if (String.valueOf(Enumerations.AdministrativeGender.FEMALE).equalsIgnoreCase(practitioner.getGender().toCode())) {
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
				Set<PersonName> names = new TreeSet<PersonName>();
				for(HumanName humanNameDt : humanNameDts) {
					PersonName name = new PersonName();
					if (humanNameDt != null) {
						List<StringType> givenNames = humanNameDt.getGiven();
						if (givenNames != null && !givenNames.isEmpty()) {
							StringType givenName = givenNames.get(0);
							name.setGivenName(valueOf(givenName));
						} else {
							return null;
						}
						String familyName = humanNameDt.getFamily();
						if (!StringUtils.isEmpty(familyName)) {
							name.setFamilyName(familyName);
						} else {
							return null;
						}
						name.setPreferred(true);
						if (humanNameDt.getPrefix() != null) {
							List<StringType> prefixes = humanNameDt.getPrefix();
							if (prefixes != null && !prefixes.isEmpty()) {
								StringType prefix = prefixes.get(0);
								name.setPrefix(valueOf(prefix));
							}
						}
						if (humanNameDt.getSuffix() != null) {
							List<StringType> suffixes = humanNameDt.getSuffix();
							if (suffixes != null && !suffixes.isEmpty()) {
								StringType suffix = suffixes.get(0);
								name.setFamilyNameSuffix(valueOf(suffix));
							}
						}
						names.add(name);
					}
				}
				providerPerson.setNames(names);
			}
			Set<PersonAddress> allAddress = providerPerson.getAddresses();
			if (allAddress.size() != 0) {
				for (PersonAddress address : allAddress) {
					address.setPreferred(false);
				}
				PersonAddress address;
				for (Address fhirAddress : practitioner.getAddress()) {
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
					if (String.valueOf(Address.AddressUse.OLD).equalsIgnoreCase(fhirAddress.getUse().toCode())) {
						address.setPreferred(false);
					}
					allAddress.add(address);
				}
				providerPerson.setAddresses(allAddress);
			}
		}

		if (practitioner.getGender() != null) {
			if (String.valueOf(Enumerations.AdministrativeGender.MALE.toCode()).equalsIgnoreCase(practitioner.getGender().toCode())) {
				providerPerson.setGender(FHIRConstants.MALE);
			} else if (String.valueOf(Enumerations.AdministrativeGender.FEMALE.toCode()).equalsIgnoreCase(practitioner.getGender().toCode())) {
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

	/**
	 * Build FhIRe reference from provider
	 * @param provider provider resource
	 * @return FHIR Reference
	 */
	public static Reference buildPractionaerReference(org.openmrs.Provider provider) {
		//Build and set patient reference
		Reference practitionerRef = new Reference();
		PersonName name = provider.getPerson().getPersonName();
		StringBuilder nameDisplay = new StringBuilder();
		nameDisplay.append(name.getGivenName());
		nameDisplay.append(" ");
		nameDisplay.append(name.getFamilyName());
		String patientUri;
		nameDisplay.append("(");
		nameDisplay.append(FHIRConstants.IDENTIFIER);
		nameDisplay.append(":");
		nameDisplay.append(provider.getIdentifier());
		nameDisplay.append(")");
		patientUri = FHIRConstants.PRACTITIONER + "/" + provider.getUuid();
		practitionerRef.setReference(patientUri);
		practitionerRef.setDisplay(nameDisplay.toString());
		practitionerRef.setId(provider.getUuid());
		return practitionerRef;
	}
}
