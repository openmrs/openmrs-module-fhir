package org.openmrs.module.fhir.api.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;


public class FHIRClient implements Client {
    protected final Log log = LogFactory.getLog(this.getClass());

    private static final String PATIENT_CATEGORY = "patient";
    private static final String ACCEPT_HEADER = "Accept";
    private static final String ACCEPT_MIME_TYPE = "application/json";

    private RestTemplate restTemplate = new RestTemplate();

    public FHIRClient(ClientHttpRequestFactory clientHttpRequestFactory) {
        restTemplate.setRequestFactory(clientHttpRequestFactory);
    }

    @Override
    public Object getObject(String category, String url, String username, String password) {
        prepareRestTemplate(username, password);
        try {
            return restTemplate.getForObject(url, resolveCategory(category));
        } catch(HttpClientErrorException e) {
            log.error(String.format("Resource %s not found", category));
        }
        return null;
    }

    @Override
    public void postObject(String category, String url, String username, String password, Object object) {
        prepareRestTemplate(username, password);

        IBaseResource baseResource = (IBaseResource) object;

        restTemplate.postForObject(category, baseResource, Void.class);
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
        this.restTemplate.setMessageConverters(Collections.<HttpMessageConverter<?>>singletonList(new FHIRHttpMessageConverter()));
    }

    private Class resolveCategory(String category) {
        if (PATIENT_CATEGORY.equals(category)) {
            return Patient.class;
        } else {
            return null;
        }
    }
}
