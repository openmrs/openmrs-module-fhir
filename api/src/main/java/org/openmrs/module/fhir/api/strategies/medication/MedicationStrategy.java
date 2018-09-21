package org.openmrs.module.fhir.api.strategies.medication;

import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import org.hl7.fhir.dstu3.model.Medication;
import org.openmrs.Drug;
import org.openmrs.api.APIException;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.FHIRMedicationUtil;
import org.openmrs.module.fhir.api.util.FHIRUtils;

import java.util.ArrayList;
import java.util.List;

public class MedicationStrategy implements GenericMedicationStrategy {
    @Override
    public Medication getMedicationById(String uuid) {
        ConceptService conceptService = Context.getConceptService();
        Drug drug = conceptService.getDrugByUuid(uuid);
        if (drug == null) {
            return null;
        }
        return FHIRMedicationUtil.generateMedication(drug);
    }

    @Override
    public List<Medication> searchMedicationById(String uuid) {
        Drug drug = Context.getConceptService().getDrugByUuid(uuid);
        List<Medication> medicationList = new ArrayList<>();
        if (drug != null) {
            medicationList.add(FHIRMedicationUtil.generateMedication(drug));
        }
        return medicationList;
    }

    @Override
    public Medication createMedication(Medication medication) {
        List<String> errors = new ArrayList<>();

        Drug drug = FHIRMedicationUtil.generateDrug(medication, errors);

        FHIRUtils.checkGeneratorErrorList(errors);

        ConceptService conceptService = Context.getConceptService();
        try {
            drug = conceptService.saveDrug(drug);
        } catch (APIException e) {
            throw new UnprocessableEntityException(
                    "The request cannot be processed due to the following issues \n" + e.getMessage());
        }

        return FHIRMedicationUtil.generateMedication(drug);
    }
}