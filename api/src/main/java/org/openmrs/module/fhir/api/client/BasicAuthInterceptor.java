package org.openmrs.module.fhir.api.client;

import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpHeaders;

import java.nio.charset.Charset;

public class BasicAuthInterceptor implements ClientHttpRequestInterceptor {

	private String username;

	private String password;

	public BasicAuthInterceptor(String username, String password) {
		this.username = username;
		this.password = password;
	}

	@Override
	public void addToHeaders(HttpHeaders headers) {

		Charset charset = Charset.forName("US-ASCII");
		String auth = username + ":" + password;
		byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(charset));
		String authHeader = "Basic " + new String(encodedAuth, charset);

		headers.add("Authorization", authHeader);
	}
}
