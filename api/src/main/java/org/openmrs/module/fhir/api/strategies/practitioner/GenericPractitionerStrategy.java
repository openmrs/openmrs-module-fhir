package org.openmrs.module.fhir.api.strategies.practitioner;

import org.hl7.fhir.dstu3.model.Practitioner;

import java.util.List;

public interface GenericPractitionerStrategy {

    Practitioner getPractitioner(String id);

    List<Practitioner> searchPractitionersByUuid(String id);

    List<Practitioner> searchPractitionersByName(String name);

    List<Practitioner> searchPractitionersByGivenName(String givenName);

    List<Practitioner> searchPractitionersByFamilyName(String familyName);

    List<Practitioner> searchPractitionersByIdentifier(String identifier);

    Practitioner createFHIRPractitioner(Practitioner practitioner);

    Practitioner updatePractitioner(Practitioner practitioner, String theId);
}
