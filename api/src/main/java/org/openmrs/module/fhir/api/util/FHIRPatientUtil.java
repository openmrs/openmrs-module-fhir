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

import ca.uhn.fhir.model.dstu2.composite.AddressDt;
import ca.uhn.fhir.model.dstu2.composite.ContactPointDt;
import ca.uhn.fhir.model.dstu2.composite.HumanNameDt;
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
import org.openmrs.PatientIdentifier;
import org.openmrs.PersonAddress;
import org.openmrs.PersonName;

import java.util.ArrayList;
import java.util.List;

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
}
