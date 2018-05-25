package org.openmrs.module.fhir.api.strategies.encounter;

import org.hl7.fhir.dstu3.model.Encounter;

import java.util.List;

public interface GenericEncounterStrategy {

    Encounter getEncounter(String uuid);

    List<Encounter> searchEncountersById(String id);

    List<Encounter> searchEncounters(boolean active);

    void deleteEncounter(String uuid);

    Encounter createFHIREncounter(Encounter visit);

    Encounter updateEncounter(Encounter visit, String theId);
}
