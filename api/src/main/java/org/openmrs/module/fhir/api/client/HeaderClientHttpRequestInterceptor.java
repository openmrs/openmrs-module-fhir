package org.openmrs.module.fhir.api.client;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class HeaderClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

	private String headerName;

	private String headerValue;

	public HeaderClientHttpRequestInterceptor(String headerName, String headerValue) {
		this.headerName = headerName;
		this.headerValue = headerValue;
	}

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {
		request.getHeaders().set(headerName, headerValue);
		return execution.execute(request, body);
	}

}

