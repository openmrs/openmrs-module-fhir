package org.openmrs.module.fhir.api.comparator;

import org.hl7.fhir.dstu3.model.Person;

import java.util.ArrayList;
import java.util.List;

public class PersonComparator extends AbstractPropertyComparator<Person> {

	@Override
	protected boolean areCustomPropsEquals(Person b1, Person b2) {
		List<Boolean> result = new ArrayList<>();

		result.add(b1.getNameFirstRep().equalsDeep(b2.getNameFirstRep()));
		result.add(b1.getTelecomFirstRep().equalsDeep(b2.getTelecomFirstRep()));
		result.add(b1.getGender().equals(b2.getGender()));
		result.add(b1.getAddressFirstRep().equalsDeep(b2.getAddressFirstRep()));
		result.add(b1.getBirthDate().equals(b2.getBirthDate()));
		result.add(b1.getLinkFirstRep().equalsDeep(b2.getLinkFirstRep()));
		result.add(b1.getActive() == b2.getActive());

		return areValuesOnlyTrue(result);
	}
}
