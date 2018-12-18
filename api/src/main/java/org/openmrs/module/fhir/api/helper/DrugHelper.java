package org.openmrs.module.fhir.api.helper;

import org.openmrs.Drug;
import org.openmrs.DrugIngredient;

import java.util.Collection;

public interface DrugHelper {

	void updateDrug(Drug newDrug, Drug drugToUpdate);

	void setIngredients(Drug drug, Collection<DrugIngredient> ingredients);

	Collection<DrugIngredient> getIngredients(Drug drug);
}
