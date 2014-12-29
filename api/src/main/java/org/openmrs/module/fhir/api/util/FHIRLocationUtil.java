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
import ca.uhn.fhir.model.dstu.resource.Location;
import ca.uhn.fhir.model.dstu.resource.Patient;
import ca.uhn.fhir.model.dstu.valueset.AddressUseEnum;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.InstantDt;
import ca.uhn.fhir.model.primitive.StringDt;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ValidationFailureException;
import ca.uhn.fhir.model.dstu.resource.Location.Position;
import org.openmrs.api.context.Context;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class FHIRLocationUtil {

	public static Location generateLocation(org.openmrs.Location omrsLocation) {
		Location location = new Location();

		IdDt uuid = new IdDt();

		uuid.setValue(omrsLocation.getUuid());
        location.setId(uuid);

        location.setName(omrsLocation.getName());
        location.setDescription(omrsLocation.getDescription());

        AddressDt address = new AddressDt();
        address.setCity(omrsLocation.getCityVillage());
        address.setCountry(omrsLocation.getCountry());

        List<StringDt> addressStrings = new ArrayList<StringDt>();

        addressStrings.add(new StringDt(omrsLocation.getAddress1()));
        addressStrings.add(new StringDt(omrsLocation.getAddress2()));
        addressStrings.add(new StringDt(omrsLocation.getAddress3()));
        addressStrings.add(new StringDt(omrsLocation.getAddress4()));
        addressStrings.add(new StringDt(omrsLocation.getAddress5()));

        address.setLine(addressStrings);
        address.setUse(AddressUseEnum.WORK);

        Position position = location.getPosition();

        if(omrsLocation.getLongitude() != null) {
            BigDecimal longitude = new BigDecimal(omrsLocation.getLongitude());
            position.setLongitude(longitude);
        }

        if(omrsLocation.getLatitude() != null) {
            BigDecimal latitude = new BigDecimal(omrsLocation.getLatitude());
            position.setLatitude(latitude);
        }

        validate(location);

		return location;
	}

	public static void validate(Location location) {
		FhirContext ctx = new FhirContext();

		// Request a validator and apply it
		FhirValidator val = ctx.newValidator();
		try {
			val.validate(location);
		} catch (ValidationFailureException e) {
			// We failed validation!
			String results = ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(e.getOperationOutcome());
		}

	}

    public static Bundle generateBundle(List<org.openmrs.Location> locationList) {
        Bundle bundle = new Bundle();
        StringDt title = bundle.getTitle();
        title.setValue("Search result");

        IdDt id = new IdDt();
        id.setValue("the request uri");
        bundle.setId(id);

        for (org.openmrs.Location location : locationList) {
            BundleEntry bundleEntry = new BundleEntry();

            IdDt entryId = new IdDt();
            entryId.setValue(Context.getAdministrationService().getGlobalProperty("webservices.rest.uriPrefix")
                    + "/ws/fhir/Location/" + location.getUuid());

            bundleEntry.setId(entryId);

            StringDt entryTitle = bundleEntry.getTitle();
            entryTitle.setValue("Location'/" + location.getUuid());

            IResource resource = new Location();
            resource = generateLocation(location);

            bundleEntry.setResource(resource);
            InstantDt dt = new InstantDt();
            if (location.getDateChanged() != null) {
                dt.setValue(location.getDateChanged());
            } else {
                dt.setValue(location.getDateCreated());
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
