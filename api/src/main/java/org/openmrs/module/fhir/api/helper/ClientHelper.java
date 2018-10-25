package org.openmrs.module.fhir.api.helper;

import org.springframework.http.RequestEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.HttpMessageConverter;

import java.net.URISyntaxException;
import java.util.List;

public interface ClientHelper {

	RequestEntity retrieveRequest(String url) throws URISyntaxException;

	RequestEntity createRequest(String url, Object object) throws URISyntaxException;

	RequestEntity deleteRequest(String url, String uuid) throws URISyntaxException;

	RequestEntity updateRequest(String url, Object object) throws URISyntaxException;

	Class resolveCategoryByCategory(String category);

	List<ClientHttpRequestInterceptor> getCustomInterceptors(String username, String password);

	List<HttpMessageConverter<?>> getCustomFHIRMessageConverter();

	boolean compareResourceObjects(String category, Object from, Object dest);
}
