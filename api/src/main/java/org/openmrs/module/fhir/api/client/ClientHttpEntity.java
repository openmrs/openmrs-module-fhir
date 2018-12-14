package org.openmrs.module.fhir.api.client;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.util.MultiValueMap;

import java.net.URI;
import java.util.Objects;

public class ClientHttpEntity<T> extends HttpEntity<T> {

	private HttpMethod method;

	private URI url;

	public ClientHttpEntity(HttpMethod method, URI url) {
		this(null, null, method, url);
	}

	public ClientHttpEntity(T body, HttpMethod method, URI url) {
		this(body,null, method, url);
	}

	public ClientHttpEntity(MultiValueMap<String, String> headers, HttpMethod method, URI url) {
		this(null, headers, method, url);
	}

	public ClientHttpEntity(T body, MultiValueMap<String, String> headers, HttpMethod method, URI url) {
		super(body, headers);
		this.method = method;
		this.url = url;
	}

	public HttpMethod getMethod() {
		return method;
	}

	public void setMethod(HttpMethod method) {
		this.method = method;
	}

	public URI getUrl() {
		return url;
	}

	public void setUrl(URI url) {
		this.url = url;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		ClientHttpEntity that = (ClientHttpEntity) o;
		return method == that.method &&
				Objects.equals(url, that.url);
	}

	@Override
	public int hashCode() {
		return Objects.hash(method, url);
	}
}
