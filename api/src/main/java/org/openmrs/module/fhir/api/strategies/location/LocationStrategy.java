package org.openmrs.module.fhir.api.strategies.location;

import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import org.apache.commons.lang.StringUtils;
import org.hl7.fhir.dstu3.model.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.ErrorUtil;
import org.openmrs.module.fhir.api.util.FHIRLocationUtil;
import org.openmrs.module.fhir.api.util.FHIRUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("DefaultLocationStrategy")
public class LocationStrategy implements GenericLocationStrategy {

	@Override
	public Location getLocation(String uuid) {
		org.openmrs.Location omrsLocation = Context.getLocationService().getLocationByUuid(uuid);
		if (omrsLocation == null || omrsLocation.isRetired()) {
			return null;
		}
		return FHIRLocationUtil.generateLocation(omrsLocation);
	}

	@Override
	public List<Location> searchLocationsByUuid(String uuid) {
		org.openmrs.Location omrsLocation = Context.getLocationService().getLocationByUuid(uuid);
		List<Location> locationList = new ArrayList<Location>();
		if (omrsLocation != null && !omrsLocation.isRetired()) {
			locationList.add(FHIRLocationUtil.generateLocation(omrsLocation));
		}
		return locationList;
	}

	@Override
	public List<Location> searchLocationsByName(String name) {
		List<org.openmrs.Location> omrsLocations = Context.getLocationService().getLocations(name);
		List<Location> locationList = new ArrayList<Location>();
		for (org.openmrs.Location location : omrsLocations) {
			if (StringUtils.startsWithIgnoreCase(location.getName(), name)) {
				locationList.add(FHIRLocationUtil.generateLocation(location));
			}
		}
		return locationList;
	}

	@Override
	public List<Location> searchLocationsByStatus(boolean status) {
		//TODO this method looks for all the locations which is inefficient. Reimplement after API revamp
		List<org.openmrs.Location> omrsLocations = Context.getLocationService().getAllLocations(true);
		List<Location> locationList = new ArrayList<Location>();
		for (org.openmrs.Location location : omrsLocations) {
			if (status) {
				if (!location.isRetired()) {
					locationList.add(FHIRLocationUtil.generateLocation(location));
				}
			} else {
				if (location.isRetired()) {
					locationList.add(FHIRLocationUtil.generateLocation(location));
				}
			}
		}
		return locationList;
	}

	@Override
	public void deleteLocation(String uuid) {
		org.openmrs.Location location = Context.getLocationService().getLocationByUuid(uuid);
		Context.getLocationService().purgeLocation(location);
	}

	@Override
	public Location updateLocation(String uuid, Location location) {
		org.openmrs.Location omrsLocation;
		List<String> errors = new ArrayList<String>();
		omrsLocation = FHIRLocationUtil.generateOpenMRSLocation(location, errors);
		if (!errors.isEmpty()) {
			String errorMessage = ErrorUtil
					.generateErrorMessage(errors, "The request cannot be processed due to following issues \n");
			throw new UnprocessableEntityException(errorMessage);

		}
		omrsLocation = Context.getLocationService().saveLocation(omrsLocation);
		return FHIRLocationUtil.generateLocation(omrsLocation);
	}

	@Override
	public Location createLocation(Location location) {
		org.openmrs.Location omrsLocation = null;
		List<String> errors = new ArrayList<String>();
		omrsLocation = FHIRLocationUtil.generateOpenMRSLocation(location, errors);
		FHIRUtils.checkGeneratorErrorList(errors);
		omrsLocation = Context.getLocationService().saveLocation(omrsLocation);
		return FHIRLocationUtil.generateLocation(omrsLocation);
	}
}
