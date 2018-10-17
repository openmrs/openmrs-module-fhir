package org.openmrs.module.fhir.api.helper;

import org.openmrs.module.fhir.api.client.BasicAuthInterceptor;
import org.openmrs.module.fhir.api.client.FHIRHttpMessageConverter;
import org.openmrs.module.fhir.api.client.HeaderClientHttpRequestInterceptor;
import org.springframework.http.RequestEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

public interface ClientHelper {

	RequestEntity retrieveRequest(String url) throws URISyntaxException;

	RequestEntity createRequest(String url, Object object) throws URISyntaxException;

	RequestEntity deleteRequest(String url, String uuid) throws URISyntaxException;

	RequestEntity updateRequest(String url, Object object) throws URISyntaxException;

	Class resolveClassByCategory(String category);

	List<ClientHttpRequestInterceptor> getCustomInterceptors(String username, String password);

	List<HttpMessageConverter<?>> getCustomMessageConverter();
}
