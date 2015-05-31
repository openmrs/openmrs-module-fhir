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
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Location;
import ca.uhn.fhir.model.dstu2.resource.Location.Position;
import ca.uhn.fhir.model.dstu2.valueset.AddressUseEnum;
import ca.uhn.fhir.model.dstu2.valueset.LocationStatusEnum;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.StringDt;
import org.openmrs.api.context.Context;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class FHIRLocationUtil {

	public static Location generateLocation(org.openmrs.Location omrsLocation) {
		Location location = new Location();

		//Set resource id
		IdDt uuid = new IdDt();
		uuid.setValue(omrsLocation.getUuid());
		location.setId(uuid);

		//Set name and location description
		location.setName(omrsLocation.getName());
		location.setDescription(omrsLocation.getDescription());

		//Set address
		AddressDt address = new AddressDt();
		address.setCity(omrsLocation.getCityVillage());
		address.setCountry(omrsLocation.getCountry());
		address.setState(omrsLocation.getStateProvince());
		address.setPostalCode(omrsLocation.getPostalCode());
		List<StringDt> addressStrings = new ArrayList<StringDt>();
		addressStrings.add(new StringDt(omrsLocation.getAddress1()));
		addressStrings.add(new StringDt(omrsLocation.getAddress2()));
		addressStrings.add(new StringDt(omrsLocation.getAddress3()));
		addressStrings.add(new StringDt(omrsLocation.getAddress4()));
		addressStrings.add(new StringDt(omrsLocation.getAddress5()));
		address.setLine(addressStrings);
		address.setUse(AddressUseEnum.WORK);
		location.setAddress(address);

		Position position = location.getPosition();
		if (omrsLocation.getLongitude() != null && !omrsLocation.getLongitude().isEmpty()) {
			BigDecimal longitude = new BigDecimal(omrsLocation.getLongitude());
			position.setLongitude(longitude);
		}

		if (omrsLocation.getLatitude() != null && !omrsLocation.getLatitude().isEmpty()) {
			BigDecimal latitude = new BigDecimal(omrsLocation.getLatitude());
			position.setLatitude(latitude);
		}

		if (!omrsLocation.isRetired()) {
			location.setStatus(LocationStatusEnum.ACTIVE);
		} else {
			location.setStatus(LocationStatusEnum.INACTIVE);
		}

		if (omrsLocation.getParentLocation() != null) {
			ResourceReferenceDt parent = new ResourceReferenceDt();
			parent.setDisplay(omrsLocation.getParentLocation().getName());
			parent.setReference(FHIRConstants.LOCATION + "/" + omrsLocation.getParentLocation().getUuid());
			location.setPartOf(parent);
		}
		FHIRUtils.validate(location);
		return location;
	}

	/**
	 * @param location the fhir location
	 * @return The equivalent {@link org.openmrs.Location} instance
	 */
	public static org.openmrs.Location generateOpenMRSLocation(Location location, List<String> errors) {
		org.openmrs.Location omrsLocation;
		//Set resource id (uuid)
		IdDt id = location.getId();
		omrsLocation = Context.getLocationService().getLocationByUuid(id.getIdPart());
		if (omrsLocation == null) {
			// No location found to be updated, creating new location. Should respond with 201 Http Code acc to
			// specification
			omrsLocation = new org.openmrs.Location();
		}
		//Set name and location description
		omrsLocation.setName(location.getName());
		omrsLocation.setDescription(location.getDescription());

		//Set address
		AddressDt address = location.getAddress();
		omrsLocation.setCityVillage(address.getCity());
		omrsLocation.setCountry(address.getCountry());
		omrsLocation.setStateProvince(address.getState());
		omrsLocation.setPostalCode(address.getPostalCode());
		List<StringDt> addressStrings = address.getLine();
		for (int i = 0; i < addressStrings.size(); i++) {
			switch (i + 1) {
				case 1:
					omrsLocation.setAddress1(addressStrings.get(i).toString());
					break;
				case 2:
					omrsLocation.setAddress2(addressStrings.get(i).toString());
					break;
				case 3:
					omrsLocation.setAddress3(addressStrings.get(i).toString());
					break;
				case 4:
					omrsLocation.setAddress4(addressStrings.get(i).toString());
					break;
				case 5:
					omrsLocation.setAddress5(addressStrings.get(i).toString());
					break;
			}
		}

		Position position = location.getPosition();
		BigDecimal latitute = position.getLatitude();
		BigDecimal longitute = position.getLongitude();
		if (latitute != null && longitute != null) {
			omrsLocation.setLatitude(latitute.toString());
			omrsLocation.setLongitude(longitute.toString());
		}
		String status = location.getStatus().toString();
		if (status.equalsIgnoreCase(LocationStatusEnum.ACTIVE.toString())) {
			omrsLocation.setRetired(false);
		} else if (status.equalsIgnoreCase((LocationStatusEnum.INACTIVE.toString()))) {
			// throw error and return error message in response.? OR call locationServcice.retireLocation() instead of
			// locationService.saveLocation()
			errors.add(
					"Status cannot be set to 'inactive' with the fhir update operation. Retiring resource may require a "
					+ "Delete verb");
			omrsLocation.setRetired(true);
		}

		ResourceReferenceDt parent = location.getPartOf();
		if (parent != null) {
			String parentName = parent.getDisplay().toString();
			String parentUuid = parent.getReference().getIdPart();
			org.openmrs.Location omrsLocationParent = Context.getLocationService().getLocationByUuid(parentUuid);
			if (omrsLocationParent != null) {
				omrsLocation.setParentLocation(omrsLocationParent);
			}
		}
		return omrsLocation;
	}
}
