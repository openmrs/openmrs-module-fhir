package org.openmrs.module.fhir.api.comparator;

import org.hl7.fhir.dstu3.model.AllergyIntolerance;
import org.hl7.fhir.dstu3.model.Base;

import java.util.List;

public abstract class AbstractPropertyComparator<B extends Base> {

	protected abstract List<Boolean> compareProps(B b1, B b2);

	public boolean areEquals(B b1, B b2) {
		if (isOnlyOneNull(b1, b2)) {
			return false;
		} else if (areBothNull(b1, b2)) {
			return true;
		} else {
			for (boolean result : compareProps(b1, b2)) {
				if (!result) {
					return false;
				}
			}
			return true;
		}
	}

	protected boolean arePropsEquals(Base b1, Base b2) {
		if (b1 == null) {
			return b2 == null;
		} else {
			return b1.equalsDeep(b2);
		}
	}

	protected <T extends Base> boolean areEquals(List<T> list1, List<T> list2) {
		if (isOnlyOneNull(list1, list2)) {
			return false;
		} else if (areBothNull(list1, list2)) {
			return true;
		} else if (list1.size() != list2.size()) {
			return false;
		} else {
			return arePropsEquals(list1.get(0), list2.get(0));
		}
	}

	protected boolean isOnlyOneNull(Object o1, Object o2) {
		return (o1 == null && o2 != null) || (o1 != null && o2 == null);
	}

	protected boolean areBothNull(Object o1, Object o2) {
		return o1 == null && o2 == null;
	}
}
