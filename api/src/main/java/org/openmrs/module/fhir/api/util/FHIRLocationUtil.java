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

import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.StringType;
import org.openmrs.api.context.Context;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class FHIRLocationUtil {

	public static Location generateLocation(org.openmrs.Location omrsLocation) {
		Location location = new Location();

		BaseOpenMRSDataUtil.setBaseExtensionFields(location, omrsLocation);

		//Set resource id
		IdType uuid = new IdType();
		uuid.setValue(omrsLocation.getUuid());
		location.setId(uuid);

		//Set name and location description
		location.setName(omrsLocation.getName());
		location.setDescription(omrsLocation.getDescription());

		//Set address
		Address address = new Address();
		address.setCity(omrsLocation.getCityVillage());
		address.setCountry(omrsLocation.getCountry());
		address.setState(omrsLocation.getStateProvince());
		address.setPostalCode(omrsLocation.getPostalCode());
		List<StringType> addressStrings = new ArrayList<StringType>();
		addressStrings.add(new StringType(omrsLocation.getAddress1()));
		addressStrings.add(new StringType(omrsLocation.getAddress2()));
		addressStrings.add(new StringType(omrsLocation.getAddress3()));
		addressStrings.add(new StringType(omrsLocation.getAddress4()));
		addressStrings.add(new StringType(omrsLocation.getAddress5()));
		address.setLine(addressStrings);
		address.setUse(Address.AddressUse.WORK);
		location.setAddress(address);

		Location.LocationPositionComponent position = location.getPosition();
		if (omrsLocation.getLongitude() != null && !omrsLocation.getLongitude().isEmpty()) {
			BigDecimal longitude = new BigDecimal(omrsLocation.getLongitude());
			position.setLongitude(longitude);
		}

		if (omrsLocation.getLatitude() != null && !omrsLocation.getLatitude().isEmpty()) {
			BigDecimal latitude = new BigDecimal(omrsLocation.getLatitude());
			position.setLatitude(latitude);
		}

		if (!omrsLocation.isRetired()) {
			location.setStatus(Location.LocationStatus.ACTIVE);
		} else {
			location.setStatus(Location.LocationStatus.INACTIVE);
		}

		if (omrsLocation.getParentLocation() != null) {
			Reference parent = new Reference();
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
		String id = location.getId();
		if (id != null && id.contains("/")) {
			String[] parts = id.split("/");
			if (parts.length > 1) {
				id = parts[1];
			}
		}
		omrsLocation = Context.getLocationService().getLocationByUuid(id);
		if (omrsLocation == null) {
			// No location found to be updated, creating new location. Should respond with 201 Http Code acc to
			// specification
			omrsLocation = new org.openmrs.Location();
		}

		BaseOpenMRSDataUtil.readBaseExtensionFields(omrsLocation, location);

		//Set name and location description
		omrsLocation.setName(location.getName());
		omrsLocation.setDescription(location.getDescription());

		//Set address
		Address address = location.getAddress();
		omrsLocation.setCityVillage(address.getCity());
		omrsLocation.setCountry(address.getCountry());
		omrsLocation.setStateProvince(address.getState());
		omrsLocation.setPostalCode(address.getPostalCode());
		List<StringType> addressStrings = address.getLine();
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

		Location.LocationPositionComponent position = location.getPosition();
		BigDecimal latitude = position.getLatitude();
		BigDecimal longitude = position.getLongitude();
		if (latitude != null && longitude != null) {
			omrsLocation.setLatitude(latitude.toString());
			omrsLocation.setLongitude(longitude.toString());
		}
		String status = location.getStatus().toString();
		if (status.equalsIgnoreCase(Location.LocationStatus.ACTIVE.toString())) {
			omrsLocation.setRetired(false);
		} else if (status.equalsIgnoreCase((Location.LocationStatus.INACTIVE.toString()))) {
			// throw error and return error message in response.? OR call locationServcice.retireLocation() instead of
			// locationService.saveLocation()
			errors.add(
					"Status cannot be set to 'inactive' with the fhir update operation. Retiring resource may require a "
							+ "Delete verb");
			omrsLocation.setRetired(true);
		}

		Reference parent = location.getPartOf();
		if (parent != null) {
			String parentUuid = parent.getId();
			org.openmrs.Location omrsLocationParent = Context.getLocationService().getLocationByUuid(parentUuid);
			if (omrsLocationParent != null) {
				omrsLocation.setParentLocation(omrsLocationParent);
			}
		}
		return omrsLocation;
	}
}
