package org.openmrs.module.fhir.api.util;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Medication;
import org.hl7.fhir.exceptions.FHIRException;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.Drug;
import org.openmrs.DrugIngredient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class FHIRMedicationUtil {

    public static Medication generateMedication(Drug drug) {
        Medication medication = new Medication();

        medication.setId(drug.getUuid());

        medication.setForm(generateForm(drug.getDosageForm()));

        medication.setCode(generateCode(drug.getConcept()));

        medication.setIngredient(generateIngredient(drug.getIngredients()));

        return medication;
    }

    public static Drug generateDrug(Medication medication, List<String> errors) {
        Drug drug = new Drug();

        if (medication.getId() != null) {
            drug.setUuid(FHIRUtils.extractUuid(medication.getId()));
        }

        drug.setDosageForm(generateConcept(medication.getForm()));

        drug.setConcept(generateConcept(medication.getCode()));

        try {
            drug.setIngredients(generateOpenMRSIngredient(medication.getIngredient()));
        } catch (FHIRException e) {
            errors.add(e.getMessage());
        }

        return drug;

    }

//region OpenMRS methods
    private static Concept generateConcept(CodeableConcept code) {
        Concept concept = new Concept();
        concept.setConceptId(Integer.valueOf(code.getCodingFirstRep().getCode()));

        List<ConceptName> names = new ArrayList<>();
        for (Coding coding : code.getCoding()) {
            ConceptName name = new ConceptName();
            name.setName(coding.getDisplay());
            names.add(name);
        }
        concept.setNames(names);

        return concept;
    }

    private static Collection<DrugIngredient> generateOpenMRSIngredient(
            List<Medication.MedicationIngredientComponent> ingredient) throws FHIRException {
        List<DrugIngredient> drugIngredients = new ArrayList<>();

        for (Medication.MedicationIngredientComponent component : ingredient) {
            DrugIngredient drugIngredient = new DrugIngredient();
            Concept ingredientConcept = generateConcept(component.getItemCodeableConcept());
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
            CodeableConcept item = new CodeableConcept();
            item.addCoding(new Coding(FHIRConstants.SNOMED_CT_URI, // todo is SNOMED_CT_URI correct
                    drugIngredient.getIngredient().getConceptId().toString(),
                    drugIngredient.getIngredient().getDisplayString()));
            Medication.MedicationIngredientComponent ingredientComponent =
                    new Medication.MedicationIngredientComponent(item);
            ingredientComponents.add(ingredientComponent);
        }

        return ingredientComponents;
    }
//endregion
}
