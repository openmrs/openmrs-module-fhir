package org.openmrs.module.fhir.resources;

import ca.uhn.fhir.rest.param.TokenParam;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Medication;

import java.util.List;

public class FHIRMedicationResource {
    public Medication getByUniqueId(IdType id) {
        return null;
    }

    public List<Medication> searchMedicationById(TokenParam id) {
        return null;
    }
}
