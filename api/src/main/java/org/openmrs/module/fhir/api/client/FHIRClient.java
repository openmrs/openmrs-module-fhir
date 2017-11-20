package org.openmrs.module.fhir.api.client;

import org.openmrs.module.fhir.api.client.dto.FHIRPatientDTO;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;


public class FHIRClient {
    private static final String PATIENT_CATEGORY = "patient";

    private RestTemplate restTemplate = new RestTemplate();

    public Object getObject(String category, String url, String username, String password) {
        this.restTemplate.setInterceptors(Arrays.asList(new BasicAuthInterceptor(username, password),
                new HeaderClientHttpRequestInterceptor("Accept", "application/json")));

        this.restTemplate.setMessageConverters(Collections.<HttpMessageConverter<?>>singletonList(new FHIRHttpMessageConverter()));

        return restTemplate.getForObject(url, resolveCategory(category));
    }

    private Class resolveCategory(String category) {
        if (PATIENT_CATEGORY.equals(category)) {
            return FHIRPatientDTO.class;
        } else {
            return null;
        }
    }
}