package org.openmrs.module.fhir.api.strategies.visit;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Encounter;

import java.util.List;

public interface GenericVisitStrategy {

    Encounter getVisit(String uuid);

    List<Encounter> searchVisitsById(String id);

    List<Encounter> searchVisits(boolean active);

    void deleteVisit(String uuid);

    Encounter createFHIRVisit(Encounter visit);

    Encounter updateVisit(Encounter visit, String theId);
}
