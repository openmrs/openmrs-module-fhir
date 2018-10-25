package org.openmrs.module.fhir.api.helper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hl7.fhir.dstu3.model.AllergyIntolerance;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.openmrs.module.fhir.api.client.BasicAuthInterceptor;
import org.openmrs.module.fhir.api.client.FHIRHttpMessageConverter;
import org.openmrs.module.fhir.api.client.HeaderClientHttpRequestInterceptor;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FHIRClientHelper implements ClientHelper {

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
		CATEGORY_MAP.put("allergy", AllergyIntolerance.class);
	}

	protected final Log log = LogFactory.getLog(this.getClass());

	@Override
	public RequestEntity retrieveRequest(String url) throws URISyntaxException {
		return new RequestEntity(HttpMethod.GET, new URI(url));
	}

	@Override
	public RequestEntity createRequest(String url, Object object) throws URISyntaxException {
		url = createUrl(url, (IBaseResource) object);
		return new RequestEntity(object, HttpMethod.PUT, new URI(url));
	}

	@Override
	public RequestEntity deleteRequest(String url, String uuid) throws URISyntaxException {
		url = url + "/" + uuid;
		return new RequestEntity(uuid, HttpMethod.DELETE, new URI(url));
	}

	@Override
	public RequestEntity updateRequest(String url, Object object) throws URISyntaxException {
		url = createUrl(url, (IBaseResource) object);
		return new RequestEntity(object, HttpMethod.PUT, new URI(url));
	}

	@Override
	public Class resolveCategoryByCategory(String category) {
		if (CATEGORY_MAP.containsKey(category)) {
			return CATEGORY_MAP.get(category);
		}
		log.warn(String.format("Category %s not recognized", category));
		return null;
	}

	@Override
	public List<ClientHttpRequestInterceptor> getCustomInterceptors(String username, String password) {
		return Arrays.asList(new BasicAuthInterceptor(username, password),
				new HeaderClientHttpRequestInterceptor(ACCEPT_HEADER, ACCEPT_MIME_TYPE));
	}

	@Override
	public List<HttpMessageConverter<?>> getCustomFHIRMessageConverter() {
		return Arrays.asList(new HttpMessageConverter<?>[]
				{ new FHIRHttpMessageConverter(), new StringHttpMessageConverter() });
	}

	private String createUrl(String url, IBaseResource object) {
		return url + "/" + object.getIdElement().getIdPart();
	}
}
