package org.openmrs.module.fhir.api.client;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.DataFormatException;
import ca.uhn.fhir.parser.IParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hl7.fhir.dstu3.model.Patient;
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
    IParser parser = FhirContext.forDstu3().newJsonParser();

    public Object getObject(String category, String url, String username, String password) throws HttpClientErrorException {
        prepareRestTemplate(username, password);
        String stringObject = "";
        try {
            stringObject = restTemplate.getForObject(url, resolveCategory(category)).toString();
        } catch(HttpClientErrorException e) {
            log.error(String.format("Resource %s not found", category));
        }
        return convertStringToFHIRObject(resolveCategory(category), stringObject);
    }

    private Object convertStringToFHIRObject(Class classType, String stringObject) {
        Object result = "";
        try {
            result = parser.parseResource(classType, stringObject);
        } catch(DataFormatException e) {
            log.error(String.format("Could not parse String to Object: %s", stringObject));
        }
        return result;
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