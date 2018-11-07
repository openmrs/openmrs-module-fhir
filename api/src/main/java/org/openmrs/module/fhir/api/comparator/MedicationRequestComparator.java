package org.openmrs.module.fhir.api.comparator;

import org.hl7.fhir.dstu3.model.MedicationRequest;

import java.util.ArrayList;
import java.util.List;

public class MedicationRequestComparator  extends AbstractPropertyComparator<MedicationRequest> {

	@Override
	protected boolean areCustomPropsEquals(MedicationRequest b1, MedicationRequest b2) {
		List<Boolean> result = new ArrayList<>();

		result.add(b1.getId().equals(b2.getId()));
		result.add(b1.getStatus().equals(b2.getStatus()));
		result.add(b1.getPriority().equals(b2.getPriority()));
		result.add(areBasePropsEquals(b1.getSubject(), b2.getSubject()));
		result.add(areBasePropsEquals(b1.getContext(), b2.getContext()));
		result.add(areBasePropsEquals(b1.getRequester(), b2.getRequester()));
		result.add(areEquals(b1.getDosageInstruction(), b2.getDosageInstruction()));
		result.add(areBasePropsEquals(b1.getDispenseRequest(), b2.getDispenseRequest()));
		result.add(areBasePropsEquals(b1.getMedication(), b2.getMedication()));

		return areValuesOnlyTrue(result);
	}
}
