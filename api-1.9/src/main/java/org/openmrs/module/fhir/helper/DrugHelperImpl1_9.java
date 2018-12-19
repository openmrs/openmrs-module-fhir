package org.openmrs.module.fhir.helper;

import org.openmrs.Drug;
import org.openmrs.DrugIngredient;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.fhir.api.helper.DrugHelper;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.LinkedHashSet;

@Component(value = "fhir.DrugHelper")
@OpenmrsProfile(openmrsPlatformVersion = "1.9.*")
public class DrugHelperImpl1_9 implements DrugHelper {

	@Override
	public void updateDrug(Drug newDrug, Drug drugToUpdate) {
		//The drug reference map was introduce in the OpenMRS 1.10
		//The drug ingredients was introduced in the OpenMRS 1.10
		drugToUpdate.setConcept(newDrug.getConcept());
		drugToUpdate.setDosageForm(newDrug.getDosageForm());
		drugToUpdate.setMaximumDailyDose(newDrug.getMaximumDailyDose());
		drugToUpdate.setMinimumDailyDose(newDrug.getMinimumDailyDose());
		drugToUpdate.setCombination(newDrug.getCombination());
		//The drug strength was introduced in the OpenMRS 1.10
		drugToUpdate.setName(newDrug.getName());
	}

	@Override
	public void setIngredients(Drug drug, Collection<DrugIngredient> ingredients) {
		//The drug ingredients was introduced in the OpenMRS 1.10
	}

	@Override
	public Collection<DrugIngredient> getIngredients(Drug drug) {
		//The drug ingredients was introduced in the OpenMRS 1.10
		return new LinkedHashSet<DrugIngredient>();
	}
}
