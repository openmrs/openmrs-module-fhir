package org.openmrs.module.fhir.api.client;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class FHIRClient implements Client {
    protected final Log log = LogFactory.getLog(this.getClass());

    private static final Map<String, Class> CATEGORY_MAP;
    private static final String ACCEPT_HEADER = "Accept";
    private static final String ACCEPT_MIME_TYPE = "application/json";

    static {
        CATEGORY_MAP = new HashMap<String, Class>();
        CATEGORY_MAP.put("patient", Patient.class);
        CATEGORY_MAP.put("location", Location.class);
    }

    private RestTemplate restTemplate = new RestTemplate();

    public FHIRClient(ClientHttpRequestFactory clientHttpRequestFactory) {
        restTemplate.setRequestFactory(clientHttpRequestFactory);
    }

    @Override
    public Object getObject(String category, String url, String username, String password)
            throws RestClientException {
        prepareRestTemplate(username, password);
        return restTemplate.getForObject(url, resolveCategory(category));
    }

    @Override
    public ResponseEntity<String> postObject(String url, String username, String password, Object object)
            throws RestClientException {
        prepareRestTemplate(username, password);
        IBaseResource baseResource = (IBaseResource) object;
        return restTemplate.postForEntity(url, baseResource, String.class);
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
}
