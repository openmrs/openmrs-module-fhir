package org.openmrs.module.fhir.helper;

import org.openmrs.Drug;
import org.openmrs.DrugIngredient;
import org.openmrs.DrugReferenceMap;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.fhir.api.helper.DrugHelper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Component(value = "fhir.DrugHelper")
@OpenmrsProfile(openmrsPlatformVersion = "2.0.* - 2.1.*")
public class DrugHelperImpl2_0 implements DrugHelper {

	@Override
	public void updateDrug(Drug newDrug, Drug drugToUpdate) {
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
	}

	@Override
	public void setIngredients(Drug drug, Collection<DrugIngredient> ingredients) {
		drug.setIngredients(ingredients);
	}

	@Override
	public Collection<DrugIngredient> getIngredients(Drug drug) {
		return drug.getIngredients();
	}
}
