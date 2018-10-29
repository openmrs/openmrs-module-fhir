package org.openmrs.module.fhir.api.comparator;

import org.hl7.fhir.dstu3.model.AllergyIntolerance;

import java.util.ArrayList;
import java.util.List;

public class AllergyIntoleranceComparator extends AbstractPropertyComparator<AllergyIntolerance> {

	@Override
	public boolean areCustomPropsEquals(AllergyIntolerance a1, AllergyIntolerance a2) {
		List<Boolean> result = new ArrayList<>();

		result.add(areBasePropsEquals(a1.getPatient(), a2.getPatient()));
		result.add(a1.getCriticality().equals(a2.getCriticality()));
		result.add(areEquals(a1.getCategory(), a2.getCategory()));
		result.add(areEquals(a1.getReaction(), a2.getReaction()));
		result.add(areBasePropsEquals(a1.getCode(), a2.getCode()));
		result.add(areEquals(a1.getNote(), a2.getNote()));

		return areValuesOnlyTrue(result);
	}
}
