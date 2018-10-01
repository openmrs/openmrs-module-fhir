package org.openmrs.module.fhir.api.strategies.relatedperson;

import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.FHIRUtils;

public class RelatedPersonStrageryUtil {

	public static GenericRelatedPersonStrategy getPersonStrategy() {
		String strategy = FHIRUtils.getRelatedPersonStrategy();

		return strategy == null ? new RelatedPersonStrategy() :
				Context.getRegisteredComponent(strategy, GenericRelatedPersonStrategy.class);
	}
}
