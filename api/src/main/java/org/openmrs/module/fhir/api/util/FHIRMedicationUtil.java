package org.openmrs.module.fhir.api.util;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Medication;
import org.hl7.fhir.exceptions.FHIRException;
import org.openmrs.Concept;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptName;
import org.openmrs.Drug;
import org.openmrs.DrugIngredient;
import org.openmrs.DrugReferenceMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class FHIRMedicationUtil {

    // needed because name is required
    private static final String DRUG_NAME_PLACEHOLDER = "drugName";

    public static Medication generateMedication(Drug drug) {
        Medication medication = new Medication();

        medication.setId(drug.getUuid());

//        medication.setForm(generateForm(drug.getDosageForm()));
        for (ConceptMap map : drug.getDosageForm().getConceptMappings()) {
            medication.setForm(FHIRUtils.getCodeableConceptConceptMappings(map));
        }

//        medication.setCode(generateCode(drug.getConcept()));
        for (ConceptMap map : drug.getConcept().getConceptMappings()) {
            medication.setCode(FHIRUtils.getCodeableConceptConceptMappings(map));
        }

        medication.setIngredient(generateIngredient(drug.getIngredients()));

        return medication;
    }

    public static Drug generateDrug(Medication medication, List<String> errors) {
        Drug drug = new Drug();

        drug.setName(DRUG_NAME_PLACEHOLDER);

        if (medication.getId() != null) {
            drug.setUuid(FHIRUtils.extractUuid(medication.getId()));
        }

        drug.setDosageForm(FHIRUtils.getConceptFromCode(medication.getForm(), errors));

        drug.setConcept(FHIRUtils.getConceptFromCode(medication.getCode(), errors));

        try {
            drug.setIngredients(generateOpenMRSIngredient(medication.getIngredient(), errors));
        } catch (FHIRException e) {
            errors.add(e.getMessage());
        }

        return drug;

    }

    public static Drug updateDrug(Drug newDrug, Drug drugToUpdate) {
        for (DrugReferenceMap drugReferenceMap : newDrug.getDrugReferenceMaps()) {
            drugToUpdate.addDrugReferenceMap(drugReferenceMap);
        }
        drugToUpdate.setIngredients(newDrug.getIngredients());
        drugToUpdate.setConcept(newDrug.getConcept());
        drugToUpdate.setDosageForm(newDrug.getDosageForm());
        drugToUpdate.setMaximumDailyDose(newDrug.getMaximumDailyDose());
        drugToUpdate.setMinimumDailyDose(newDrug.getMinimumDailyDose());
        drugToUpdate.setCombination(newDrug.getCombination());
        drugToUpdate.setStrength(newDrug.getStrength());
        drugToUpdate.setName(newDrug.getName());

        return drugToUpdate;
    }

//region OpenMRS methods
    private static Collection<DrugIngredient> generateOpenMRSIngredient(
            List<Medication.MedicationIngredientComponent> ingredient, List<String> errors) throws FHIRException {
        Set<DrugIngredient> drugIngredients = new HashSet<>();

        for (Medication.MedicationIngredientComponent component : ingredient) {
            DrugIngredient drugIngredient = new DrugIngredient();
            Concept ingredientConcept = FHIRUtils.getConceptFromCode(component.getItemCodeableConcept(), errors);
            drugIngredient.setIngredient(ingredientConcept);
            drugIngredients.add(drugIngredient);
        }

        return drugIngredients;
    }
//endregion

//region FHIR methods
    private static CodeableConcept generateForm(Concept dosageForm) {
        CodeableConcept form = new CodeableConcept();
        form.addCoding(new Coding(FHIRConstants.SNOMED_CT_URI,
                dosageForm.getConceptId().toString(), dosageForm.getDisplayString())); // todo is SNOMED_CT_URI correct
        return form;
    }

    private static CodeableConcept generateCode(Concept concept) {
        CodeableConcept code = new CodeableConcept();
        code.addCoding(new Coding(FHIRConstants.SNOMED_CT_URI,
                concept.getConceptId().toString(), concept.getDisplayString())); // todo is SNOMED_CT_URI correct
        return code;
    }

    private static List<Medication.MedicationIngredientComponent> generateIngredient(Collection<DrugIngredient> drugIngredients) {
        List<Medication.MedicationIngredientComponent> ingredientComponents = new ArrayList<>();

        for (DrugIngredient drugIngredient : drugIngredients) {
//            CodeableConcept item = new CodeableConcept();

            for (ConceptMap map : drugIngredient.getIngredient().getConceptMappings()) {
                Medication.MedicationIngredientComponent ingredientComponent =
                        new Medication.MedicationIngredientComponent(
                                FHIRUtils.getCodeableConceptConceptMappings(map)
                        );
                ingredientComponents.add(ingredientComponent);
            }

//            item.addCoding(new Coding(FHIRConstants.SNOMED_CT_URI, // todo is SNOMED_CT_URI correct
//                    drugIngredient.getIngredient().getConceptId().toString(),
//                    drugIngredient.getIngredient().getDisplayString()));
//            Medication.MedicationIngredientComponent ingredientComponent =
//                    new Medication.MedicationIngredientComponent(item);
//            ingredientComponents.add(ingredientComponent);
        }

        return ingredientComponents;
    }
//endregion
}
