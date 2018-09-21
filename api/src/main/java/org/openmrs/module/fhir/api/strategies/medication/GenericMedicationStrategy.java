package org.openmrs.module.fhir.api.strategies.medication;

import org.hl7.fhir.dstu3.model.Medication;

import java.util.List;

public interface GenericMedicationStrategy {

    Medication getMedicationById(String uuid);

    List<Medication> searchMedicationById(String uuid);
}
