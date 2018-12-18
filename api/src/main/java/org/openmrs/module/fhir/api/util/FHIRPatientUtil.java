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
import org.openmrs.PatientIdentifier;
import org.openmrs.PersonAddress;
import org.openmrs.PersonName;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.openmrs.module.fhir.api.util.FHIRUtils.extractUuid;

public class FHIRPatientUtil {

	public static Patient generatePatient(org.openmrs.Patient omrsPatient) {

		Patient patient = new Patient();

		BaseOpenMRSDataUtil.setBaseExtensionFields(patient, omrsPatient);

		//Set patient id to fhir patient
		IdType uuid = new IdType();
		uuid.setValue(omrsPatient.getUuid());
		patient.setId(uuid);

		//Set patient identifiers to fhir patient
		for (PatientIdentifier identifier : omrsPatient.getActiveIdentifiers()) {
			String urn = FHIRUtils.buildURN(FHIRConstants.UUID, identifier.getIdentifierType().getUuid());
			if (identifier.isPreferred()) {
				patient.addIdentifier()
						.setUse(Identifier.IdentifierUse.USUAL)
						.setSystem(identifier.getIdentifierType().getName())
						.setValue(identifier.getIdentifier())
						.setId(identifier.getUuid());
			} else {
				patient.addIdentifier()
						.setUse(Identifier.IdentifierUse.SECONDARY)
						.setSystem(identifier.getIdentifierType()
						.getName()).setValue(identifier.getIdentifier())
						.setId(identifier.getUuid());
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
		org.openmrs.Patient omrsPatient = new org.openmrs.Patient(); // add eror handli
		BaseOpenMRSDataUtil.readBaseExtensionFields(omrsPatient, patient);

		if (patient.getId() != null) {
			omrsPatient.setUuid(extractUuid(patient.getId()));
		}

		List<Identifier> fhirIdList = patient.getIdentifier();
		Set<PatientIdentifier> idList = new TreeSet<PatientIdentifier>();

		if (fhirIdList == null || fhirIdList.isEmpty()) {
			errors.add("Identifiers cannot be empty");
		}

		for (Identifier fhirIdentifier : fhirIdList) {
			PatientIdentifier identifier = FHIRIdentifierUtil.generateOpenmrsIdentifier(fhirIdentifier, errors);
			idList.add(identifier);
		}
		omrsPatient.setIdentifiers(idList);

		Set<PersonName> names = new TreeSet<PersonName>();
		if (patient.getName().size() == 0) {
			errors.add("Name cannot be empty");
		}
		for (HumanName humanNameDt : patient.getName()) {
			PersonName name = FHIRHumanNameUtil.generatePersonName(humanNameDt);
			names.add(name);
		}
		omrsPatient.setNames(names);
		if (!validateNames(omrsPatient.getNames())) {
			errors.add("Person should have at least one name with family name and given name");
		}

		Set<PersonAddress> addresses = new TreeSet<PersonAddress>();
		for (Address fhirAddress : patient.getAddress()) {
			PersonAddress address = FHIRAddressUtil.generatePersonAddress(fhirAddress);
			addresses.add(address);
		}
		omrsPatient.setAddresses(addresses);

		if (patient.getGender() != null) {
			if (patient.getGender().toCode().equalsIgnoreCase(Enumerations.AdministrativeGender.MALE.toCode())) {
				omrsPatient.setGender(FHIRConstants.MALE);
			} else if (patient.getGender().toCode()
					.equalsIgnoreCase(String.valueOf(Enumerations.AdministrativeGender.FEMALE))) {
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
		for (PersonName newName : omrsPatient.getNames()) {
			boolean exist = false;
			for (PersonName existingName : all) {
				if (existingName.getUuid().equals(newName.getUuid())) {
					FHIRHumanNameUtil.updatePersonName(existingName, newName);
					exist = true;
				}
			}
			if (!exist) {
				retrievedPatient.addName(newName);
			}
		}
		Set<PersonAddress> allAddress = retrievedPatient.getAddresses();
		for (PersonAddress newAddress : omrsPatient.getAddresses()) {
			boolean exist = false;
			for (PersonAddress existingAddress : allAddress) {
				if (existingAddress.getUuid().equals(newAddress.getUuid())) {
					FHIRAddressUtil.updatePersonAddress(existingAddress, newAddress);
					exist = true;
				}
			}
			if (!exist) {
				retrievedPatient.addAddress(newAddress);
			}
		}
		Set<PatientIdentifier> allIdentifiers = retrievedPatient.getIdentifiers();
		retrievedPatient.setPersonVoided(omrsPatient.getVoided());
		for (PatientIdentifier newIdentifier : omrsPatient.getIdentifiers()) {
			boolean exist = false;
			for (PatientIdentifier existingIdentifier : allIdentifiers) {
				if (existingIdentifier.getUuid().equals(newIdentifier.getUuid())) {
					FHIRIdentifierUtil.updatePatientIdentifier(existingIdentifier, newIdentifier);
					exist = true;
				}
			}
			if (!exist) {
				retrievedPatient.addIdentifier(newIdentifier);
			}
		}
		if (omrsPatient.getVoided()) {
			retrievedPatient.setPersonVoidReason(FHIRConstants.FHIR_VOIDED_MESSAGE); // deleted reason is compulsory
		}
		retrievedPatient.setBirthdate(omrsPatient.getBirthdate());
		retrievedPatient.setGender(omrsPatient.getGender());
		return retrievedPatient;
	}

	private static boolean validateNames(Set<PersonName> names) {
		boolean valid = false;
		for (PersonName name : names) {
			if (StringUtils.isNotBlank(name.getGivenName())
					&& StringUtils.isNotBlank(name.getFamilyName())) {
				valid = true;
				break;
			}
		}
		return valid;
	}

	/**
	 * Build FhIRe reference from Patient
	 *
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

	/**
	 * Compares patient objects with only current name and current address.
	 *
	 * @param patient1
	 * @param patient2
	 * @return true if the patient are equalse, otherwise it returns false.
	 */
	public static boolean compareCurrentPatients(Object patient1, Object patient2) {
		Patient p1 = (Patient) patient1;
		Patient p2 = (Patient) patient2;

		if (p1.getIdentifier().size() == p2.getIdentifier().size()) {
			for (int i = 0; i < p1.getIdentifier().size(); i++) {
				if (!p1.getIdentifier().get(i).equalsDeep(p2.getIdentifier().get(i))) {
					return false;
				}
			}
		} else {
			return false;
		}

		//It's work around. Compare only the first name (it's preferred) cause after the patient update,
		// the name is set to old and creates the same again. During synchronization between the OpenMRS instances,
		// it would compare the same object with the different size of the name list without that.
		if (p1.getName().size() > 0 && p2.getName().size() > 0) {
			if (!p1.getName().get(0).equalsDeep(p2.getName().get(0))) {
				return false;
			}
		}

		if (p1.getTelecom().size() > 0 && p2.getTelecom().size() > 0) {
			if (!p1.getTelecom().get(0).equalsDeep(p2.getTelecom().get(0))) {
				return false;
			}
		}

		if (null != p1.getGender()) {
			if (!p1.getGender().equals(p2.getGender())) {
				return false;
			}
		}

		if (null != p1.getDeceased()) {
			if (!p1.getDeceased().equalsDeep(p2.getDeceased())) {
				return false;
			}
		}

		//The same as the issue with the name list.
		if (p1.getAddress().size() > 0 && p2.getAddress().size() > 0) {
			if (!p1.getAddress().get(0).equalsDeep(p2.getAddress().get(0))) {
				return false;
			}
		}

		if (null != p1.getMaritalStatus()) {
			if (!p1.getMaritalStatus().equalsDeep(p2.getMaritalStatus())) {
				return false;
			}
		}

		if (null != p1.getMultipleBirth()) {
			if (!p1.getMultipleBirth().equalsDeep(p2.getMultipleBirth())) {
				return false;
			}
		}

		if (null != p1.getGeneralPractitioner()) {
			if (!p1.getGeneralPractitioner().equals(p2.getGeneralPractitioner())) {
				return false;
			}
		}

		return p1.getActive() == p2.getActive();
	}
}
