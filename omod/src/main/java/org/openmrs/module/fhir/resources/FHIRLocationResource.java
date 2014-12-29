package org.openmrs.module.fhir.resources;

import ca.uhn.fhir.model.dstu.resource.Location;
import ca.uhn.fhir.model.api.Bundle;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.LocationService;
import org.openmrs.module.fhir.api.util.FHIRLocationUtil;
import org.openmrs.module.fhir.util.Parser;

public class FHIRLocationResource extends Resource {

	public Object retrieve(String uuid) throws Exception {

		Object delegate = getByUniqueId(uuid, null);
		System.out.println(delegate);
		if (delegate == null) {
			throw new Exception();
		}

		return delegate;
	}

    public String searchById(String id, String contentType) {

        LocationService locationService = Context.getService(LocationService.class);
        Bundle locationBundle = locationService.getLocationsById(id);
        return FHIRLocationUtil.parseBundle(locationBundle);
    }

	public String getByUniqueId(String uuid, String contentType) {

		LocationService locationService = Context.getService(LocationService.class);
		Location fhirLocation = locationService.getLocation(uuid);

		return Parser.parse(fhirLocation, contentType);
	}

}
