package org.openmrs.module.fhir.api.strategies.encounter;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Composition;
import org.hl7.fhir.dstu3.model.Encounter;

import java.util.List;

public interface GenericEncounterStrategy {

	Encounter getEncounter(String uuid);

	List<Encounter> searchEncounterById(String uuid);

	List<Encounter> searchEncountersByPatientIdentifier(String identifier);

	List<Composition> searchEncounterComposition(String uuid);

	List<Composition> searchEncounterCompositionByPatientId(String patientId);

	List<Composition> searchEncounterCompositionByEncounterId(String encounterId);

	Bundle getEncounterOperationsById(String encounterId);

	Bundle getEncounterOperationsById(String encounterId, Bundle bundle, boolean includePatient);

	void deleteEncounter(String uuid);

	List<Encounter> searchEncountersByPatientIdentifierAndPartOf(String patientIdentifier, String partOf);

	List<Encounter> searchEncountersByEncounterIdAndPartOf(String encounterId, String partOf);

	Encounter createFHIREncounter(Encounter encounter);

	Encounter updateEncounter(Encounter encounter, String uuid);
}
