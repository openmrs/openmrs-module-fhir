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

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.Bundle;
import ca.uhn.fhir.model.api.BundleEntry;
import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu.composite.AddressDt;
import ca.uhn.fhir.model.dstu.composite.HumanNameDt;
import ca.uhn.fhir.model.dstu.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu.resource.Practitioner;
import ca.uhn.fhir.model.dstu.valueset.AddressUseEnum;
import ca.uhn.fhir.model.dstu.valueset.AdministrativeGenderCodesEnum;
import ca.uhn.fhir.model.dstu.valueset.NameUseEnum;
import ca.uhn.fhir.model.dstu.valueset.PractitionerRoleEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.InstantDt;
import ca.uhn.fhir.model.primitive.StringDt;
import ca.uhn.fhir.parser.IParser;
import org.openmrs.PersonAddress;
import org.openmrs.PersonName;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.exception.FHIRValidationException;

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
		AddressDt fhirAddress = practitioner.getAddress();
		for (PersonAddress address : provider.getPerson().getAddresses()) {
			if (address.isPreferred()) {
				fhirAddress.setCity(address.getCityVillage());
				fhirAddress.setCountry(address.getCountry());
				fhirAddress.setState(address.getStateProvince());
				fhirAddress.setZip(address.getPostalCode());
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
				practitioner.setAddress(fhirAddress);
			}
		}

		//Set gender in fhir practitioner object
		if (provider.getPerson().getGender().equals("M")) {
			practitioner.setGender(AdministrativeGenderCodesEnum.M);
		} else if (provider.getPerson().getGender().equals("F")) {
			practitioner.setGender(AdministrativeGenderCodesEnum.F);
		} else {
			practitioner.setGender(AdministrativeGenderCodesEnum.UNK);
		}

		DateTimeDt fhirBirthDate = practitioner.getBirthDate();
		fhirBirthDate.setValue(provider.getPerson().getBirthdate());
		practitioner.setBirthDate(fhirBirthDate);
		FHIRUtils.validate(practitioner);
		return practitioner;
	}

	public static Bundle generateBundle(List<Provider> providerList) throws FHIRValidationException {
		Bundle bundle = new Bundle();
        StringDt title = bundle.getTitle();
        title.setValue("Search result");

        IdDt id = new IdDt();
        id.setValue("the request uri");
        bundle.setId(id);

        for (Provider provider : providerList) {
            BundleEntry bundleEntry = new BundleEntry();

            IdDt entryId = new IdDt();
            entryId.setValue(Context.getAdministrationService().getGlobalProperty("webservices.rest.uriPrefix")
                    + "/ws/fhir/practitioner/" + provider.getUuid());

            bundleEntry.setId(entryId);

            StringDt entryTitle = bundleEntry.getTitle();
            entryTitle.setValue("practitioner'/" + provider.getUuid());

            IResource resource = new Practitioner();
            resource = generatePractitioner(provider);

            bundleEntry.setResource(resource);
            InstantDt dt = new InstantDt();
            if (provider.getDateChanged() != null) {
                dt.setValue(provider.getDateChanged());
            } else {
                dt.setValue(provider.getDateCreated());
            }
            bundleEntry.setUpdated(dt);

            bundle.addEntry(bundleEntry);
        }

        return bundle;
    }

    public static String parseBundle(Bundle bundle) {
        FhirContext ctx = new FhirContext();
        IParser jsonParser = ctx.newJsonParser();
        jsonParser.setPrettyPrint(true);
        String encoded = jsonParser.encodeBundleToString(bundle);
        return encoded;
    }
}
