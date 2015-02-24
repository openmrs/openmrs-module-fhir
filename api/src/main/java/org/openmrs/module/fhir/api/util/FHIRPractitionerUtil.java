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
import ca.uhn.fhir.model.dstu2.composite.HumanNameDt;
import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.resource.Practitioner;
import ca.uhn.fhir.model.dstu2.valueset.AddressUseEnum;
import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
import ca.uhn.fhir.model.dstu2.valueset.NameUseEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.StringDt;
import org.openmrs.PersonAddress;
import org.openmrs.PersonName;
import org.openmrs.Provider;

import java.util.ArrayList;
import java.util.List;

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

			DateTimeDt fhirBirthDate = new DateTimeDt();
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
}
