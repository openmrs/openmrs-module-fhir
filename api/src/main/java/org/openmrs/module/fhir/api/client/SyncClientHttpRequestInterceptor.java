package org.openmrs.module.fhir.api.client;

import org.springframework.http.HttpHeaders;

public interface SyncClientHttpRequestInterceptor {

	void addToHeaders(HttpHeaders headers);
}
