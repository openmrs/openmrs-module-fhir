package org.openmrs.module.fhir.api.util;

import org.hl7.fhir.dstu3.model.Medication;
import org.hl7.fhir.exceptions.FHIRException;
import org.openmrs.Concept;
import org.openmrs.ConceptMap;
import org.openmrs.Drug;
import org.openmrs.DrugIngredient;
import org.openmrs.module.fhir.api.helper.DrugHelper;

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

		BaseOpenMRSDataUtil.setBaseExtensionFields(medication, drug);

		medication.setId(drug.getUuid());

		for (ConceptMap map : drug.getDosageForm().getConceptMappings()) {
			medication.setForm(FHIRUtils.getCodeableConceptConceptMappings(map));
		}

		for (ConceptMap map : drug.getConcept().getConceptMappings()) {
			medication.setCode(FHIRUtils.getCodeableConceptConceptMappings(map));
		}

		medication.setIngredient(generateIngredient(ContextUtil.getDrugHelper().getIngredients(drug)));

		return medication;
	}

	public static Drug generateDrug(Medication medication, List<String> errors) {
		Drug drug = new Drug();

		BaseOpenMRSDataUtil.readBaseExtensionFields(drug, medication);

		drug.setName(DRUG_NAME_PLACEHOLDER);

		if (medication.getId() != null) {
			drug.setUuid(FHIRUtils.extractUuid(medication.getId()));
		}

		drug.setDosageForm(FHIRUtils.getConceptFromCode(medication.getForm(), errors));

		drug.setConcept(FHIRUtils.getConceptFromCode(medication.getCode(), errors));

		try {
			ContextUtil.getDrugHelper().setIngredients(drug, generateOpenMRSIngredient(medication.getIngredient(), errors));
		}
		catch (FHIRException e) {
			errors.add(e.getMessage());
		}

		return drug;

	}

	public static Drug updateDrug(Drug newDrug, Drug drugToUpdate) {
		ContextUtil.getDrugHelper().updateDrug(newDrug, drugToUpdate);
		return drugToUpdate;
	}

	//region OpenMRS methods
	private static Collection<DrugIngredient> generateOpenMRSIngredient(
			List<Medication.MedicationIngredientComponent> ingredient, List<String> errors) throws FHIRException {
		Set<DrugIngredient> drugIngredients = new HashSet<>();

		for (Medication.MedicationIngredientComponent component : ingredient) {
			DrugIngredient drugIngredient = new DrugIngredient();
			Concept ingredientConcept = FHIRUtils
					.getConceptFromCode(component.getItemCodeableConcept(), errors);
			drugIngredient.setIngredient(ingredientConcept);
			drugIngredients.add(drugIngredient);
		}

		return drugIngredients;
	}
	//endregion

	//region FHIR methods
	private static List<Medication.MedicationIngredientComponent> generateIngredient(
			Collection<DrugIngredient> drugIngredients) {
		List<Medication.MedicationIngredientComponent> ingredientComponents = new ArrayList<>();

		for (DrugIngredient drugIngredient : drugIngredients) {

			for (ConceptMap map : drugIngredient.getIngredient().getConceptMappings()) {
				Medication.MedicationIngredientComponent ingredientComponent =
						new Medication.MedicationIngredientComponent(
								FHIRUtils.getCodeableConceptConceptMappings(map)
						);
				ingredientComponents.add(ingredientComponent);
			}
		}

		return ingredientComponents;
	}
	//endregion
}
