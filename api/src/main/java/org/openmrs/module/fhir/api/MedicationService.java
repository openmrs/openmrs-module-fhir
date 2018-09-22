package org.openmrs.module.fhir.api;

import org.hl7.fhir.dstu3.model.Medication;

import java.util.List;

public interface MedicationService {

    Medication getMedicationById(String uuid);

    List<Medication> searchMedicationById(String uuid);

    Medication createMedication(Medication medication);

    Medication updateMedication(Medication medication, String id);
}
