package org.openmrs.module.fhir.api.util;

import org.hl7.fhir.dstu3.model.DomainResource;
import org.hl7.fhir.dstu3.model.IdType;

public final class StrategyUtil {

	private StrategyUtil() {

	}

	public static void setIdIfNeeded(DomainResource resource, String uuid) {
		if (resource.getId() == null) {
			IdType id = new IdType();
			id.setValue(uuid);
			resource.setId(uuid);
		}
	}
}
