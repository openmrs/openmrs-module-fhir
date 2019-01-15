package org.openmrs.module.fhir.api.client;

import org.springframework.http.HttpHeaders;

public interface ClientHttpRequestInterceptor {

	void addToHeaders(HttpHeaders headers);
}
