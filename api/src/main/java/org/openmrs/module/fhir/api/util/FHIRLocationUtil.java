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

import ca.uhn.fhir.model.dstu.composite.AddressDt;
import ca.uhn.fhir.model.dstu.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu.resource.Location;
import ca.uhn.fhir.model.dstu.resource.Location.Position;
import ca.uhn.fhir.model.dstu.valueset.AddressUseEnum;
import ca.uhn.fhir.model.dstu.valueset.LocationStatusEnum;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.StringDt;

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
		address.setZip(omrsLocation.getPostalCode());
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
			parent.setDisplay("The parent resource");
			parent.setReference(FHIRConstants.LOCATION + "/" + omrsLocation.getParentLocation().getUuid());
			location.setPartOf(parent);
		}
		FHIRUtils.validate(location);
		return location;
	}
}
