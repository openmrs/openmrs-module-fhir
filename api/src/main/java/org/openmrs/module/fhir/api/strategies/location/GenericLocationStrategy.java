package org.openmrs.module.fhir.api.strategies.location;

import org.hl7.fhir.dstu3.model.Location;

import java.util.List;

public interface GenericLocationStrategy {

    Location getLocation(String uuid);

    List<Location> searchLocationsByUuid(String uuid);

    List<Location> searchLocationsByStatus(boolean status);

    List<Location> searchLocationsByName(String name);

    void deleteLocation(String uuid);

    Location updateLocation(String uuid, Location location);

    Location createLocation(Location location);

}
