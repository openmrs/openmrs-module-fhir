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
import org.hl7.fhir.dstu3.model.Enumerations;
import org.hl7.fhir.dstu3.model.Person;
import org.hl7.fhir.dstu3.model.Reference;
import org.openmrs.Patient;
import org.openmrs.PersonAddress;
import org.openmrs.PersonName;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.comparator.PersonComparator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.openmrs.module.fhir.api.util.FHIRUtils.extractUuid;

public class FHIRPersonUtil {

	public static boolean arePersonsEquals(Object ob1, Object ob2) {
		PersonComparator comparator = new PersonComparator();
		return comparator.areEquals((Person) ob1, (Person) ob2);
	}

	public static Person generatePerson(org.openmrs.Person omrsPerson) {
		Person person = new Person();

		BaseOpenMRSDataUtil.setBaseExtensionFields(person, omrsPerson);

		//Set person ID
		person.setId(omrsPerson.getUuid());

		person.setName(FHIRHumanNameUtil.buildHumanNames(omrsPerson.getNames()));
		person.setGender(determineAdministrativeGender(omrsPerson));
		person.setAddress(FHIRAddressUtil.buildAddresses(omrsPerson.getAddresses()));

		person.setBirthDate(omrsPerson.getBirthdate());
		person.setActive(!omrsPerson.isVoided());

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

		BaseOpenMRSDataUtil.readBaseExtensionFields(omrsPerson, personFHIR);

		if (StringUtils.isNotBlank(personFHIR.getId())) {
			omrsPerson.setUuid(extractUuid(personFHIR.getId()));
		}

		Set<PersonName> names = new TreeSet<>();
		if (personFHIR.getName().size() == 0) {
			errors.add("Name cannot be empty");
		}
		omrsPerson.setNames(FHIRHumanNameUtil.buildOpenmrsNames(personFHIR.getName()));
		if (!FHIRHumanNameUtil.validateOpenmrsNames(omrsPerson.getNames())) {
			errors.add("Person should have at least one name with family name and given name");
		}

		omrsPerson.setAddresses(FHIRAddressUtil.buildPersonAddresses(personFHIR.getAddress()));

		String gender = FHIRPersonUtil.determineOpenmrsGender(personFHIR.getGender());
		if (StringUtils.isNotBlank(gender)) {
			omrsPerson.setGender(gender);
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
		updateNames(omrsPerson, retrievedPerson);
		updateAddresses(omrsPerson, retrievedPerson);
		retrievedPerson.setPersonVoided(omrsPerson.getVoided());
		if (omrsPerson.getVoided()) {
			retrievedPerson.setPersonVoidReason(FHIRConstants.FHIR_VOIDED_MESSAGE); // deleted reason is compulsory
		}
		retrievedPerson.setBirthdate(omrsPerson.getBirthdate());
		retrievedPerson.setGender(omrsPerson.getGender());

		return retrievedPerson;
	}

	public static void updateNames(org.openmrs.Person omrsPerson, org.openmrs.Person retrievedPerson) {
		Set<PersonName> all = retrievedPerson.getNames();
		for (PersonName newName : omrsPerson.getNames()) {
			boolean exist = false;
			for (PersonName existingName : all) {
				if (existingName.getUuid().equals(newName.getUuid())) {
					FHIRHumanNameUtil.updatePersonName(existingName, newName);
					exist = true;
					break;
				}
			}
			if (!exist) {
				if (newName.isPreferred()) {
					for (PersonName existingName : all) {
						existingName.setPreferred(false);
					}
				}
				retrievedPerson.addName(newName);
			}
		}
	}

	public static void updateAddresses(org.openmrs.Person omrsPerson, org.openmrs.Person retrievedPerson) {
		Set<PersonAddress> allAddress = retrievedPerson.getAddresses();
		for (PersonAddress newAddress : omrsPerson.getAddresses()) {
			boolean exist = false;
			for (PersonAddress existingAddress : allAddress) {
				if (existingAddress.getUuid().equals(newAddress.getUuid())) {
					FHIRAddressUtil.updatePersonAddress(existingAddress, newAddress);
					exist = true;
					break;
				}
			}
			if (!exist) {
				if (newAddress.isPreferred()) {
					for (PersonAddress existingAddress : allAddress) {
						existingAddress.setPreferred(false);
					}
				}
				retrievedPerson.addAddress(newAddress);
			}
		}
	}

	public static Enumerations.AdministrativeGender determineAdministrativeGender(org.openmrs.Person omrsPerson) {
		if (omrsPerson.getGender() != null) {
			if (omrsPerson.getGender().equals("M")) {
				return Enumerations.AdministrativeGender.MALE;
			} else if (omrsPerson.getGender().equals("F")) {
				return Enumerations.AdministrativeGender.FEMALE;
			}
		}

		return Enumerations.AdministrativeGender.UNKNOWN;
	}

	public static String determineOpenmrsGender(Enumerations.AdministrativeGender fhirGender) {
		String gender = null;
		if (fhirGender != null) {
			if (fhirGender.toCode().equalsIgnoreCase(Enumerations.AdministrativeGender.MALE.toCode())) {
				gender = FHIRConstants.MALE;
			} else if (fhirGender.toCode().equalsIgnoreCase(String.valueOf(Enumerations.AdministrativeGender.FEMALE))) {
				gender = FHIRConstants.FEMALE;
			}
		}
		return gender;
	}
}
