package org.openmrs.module.fhir.api.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FHIRClient implements Client {

	private static final Map<String, Class> CATEGORY_MAP;

	private static final String ACCEPT_HEADER = "Accept";

	private static final String ACCEPT_MIME_TYPE = "application/json";

	static {
		CATEGORY_MAP = new HashMap<>();
		CATEGORY_MAP.put("patient", Patient.class);
		CATEGORY_MAP.put("visit", Encounter.class);
		CATEGORY_MAP.put("encounter", Encounter.class);
		CATEGORY_MAP.put("obs", Observation.class);
		CATEGORY_MAP.put("location", Location.class);
		CATEGORY_MAP.put("practitioner", Practitioner.class);
		CATEGORY_MAP.put("provider", Practitioner.class);
	}

	protected final Log log = LogFactory.getLog(this.getClass());

	private RestTemplate restTemplate = new RestTemplate();

	public FHIRClient() {
	}

	@Override
	public Object retrieveObject(String category, String url, String username, String password)
			throws RestClientException {
		prepareRestTemplate(username, password);
		return restTemplate.getForObject(url, resolveCategory(category));
	}

	@Override
	public ResponseEntity<String> createObject(String url, String username, String password, Object object)
			throws RestClientException {
		return createResponse(url, username, password, (IBaseResource) object);
	}

	@Override
	public ResponseEntity<String> deleteObject(String url, String username, String password, String uuid)
			throws RestClientException {
		prepareRestTemplate(username, password);
		url = url + "/" + uuid;
		return restTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<Object>(uuid), String.class);
	}

	@Override
	public ResponseEntity<String> updateObject(String url, String username, String password, Object object)
			throws RestClientException {
		return createResponse(url, username, password, (IBaseResource) object);
	}

	private void prepareRestTemplate(String username, String password) {
		setCustomInterceptors(username, password);
		setCustomFHIRMessageConverter();
	}

	private void setCustomInterceptors(String username, String password) {
		this.restTemplate.setInterceptors(Arrays.asList(new BasicAuthInterceptor(username, password),
				new HeaderClientHttpRequestInterceptor(ACCEPT_HEADER, ACCEPT_MIME_TYPE)));
	}

	private void setCustomFHIRMessageConverter() {
		this.restTemplate.setMessageConverters(Arrays.asList(new HttpMessageConverter<?>[]
				{ new FHIRHttpMessageConverter(), new StringHttpMessageConverter() }));
	}

	private Class resolveCategory(String category) {
		if (CATEGORY_MAP.containsKey(category)) {
			return CATEGORY_MAP.get(category);
		}
		log.warn(String.format("Category %s not recognized", category));
		return null;
	}

	private ResponseEntity<String> createResponse(String url, String username, String password,
			IBaseResource object) {
		prepareRestTemplate(username, password);
		url = url + "/" + object.getIdElement().getIdPart();
		return restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<Object>(object), String.class);
	}
}
