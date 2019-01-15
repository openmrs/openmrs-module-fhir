package org.openmrs.module.fhir.api.client;

import org.springframework.http.HttpHeaders;

public class BasicHttpRequestInterceptor implements ClientHttpRequestInterceptor {

	private String headerName;

	private String headerValue;

	public BasicHttpRequestInterceptor(String headerName, String headerValue) {
		this.headerName = headerName;
		this.headerValue = headerValue;
	}

	@Override
	public void addToHeaders(HttpHeaders headers) {
		headers.add(headerName, headerValue);
	}
}

