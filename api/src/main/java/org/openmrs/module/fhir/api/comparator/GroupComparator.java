package org.openmrs.module.fhir.api.comparator;

import org.hl7.fhir.dstu3.model.Group;

import java.util.ArrayList;
import java.util.List;

public class GroupComparator extends AbstractPropertyComparator<Group> {

	@Override
	public boolean areCustomPropsEquals(Group g1, Group g2) {
		List<Boolean> result = new ArrayList<>();

		result.add(g1.getName().equals(g2.getName()));
		result.add(g1.getType().equals(g2.getType()));
		result.add(g1.getQuantity() == g2.getQuantity());
		result.add(areEquals(g1.getMember(), g2.getMember()));

		return areValuesOnlyTrue(result);
	}
}
