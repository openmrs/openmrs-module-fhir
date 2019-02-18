package org.openmrs.module.fhir.api;

import org.openmrs.ConceptMap;

/**
 * It is provided as a workaround for missing API methods to fetch {@link ConceptMap}, etc.
 */
public interface FHIRHelperService {
	<T> T getObjectByUuid(Class<? extends T> type, String uuid);
}
