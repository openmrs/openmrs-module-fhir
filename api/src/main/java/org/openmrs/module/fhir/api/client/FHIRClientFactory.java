package org.openmrs.module.fhir.api.client;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

public class FHIRClientFactory {

    private final static int SECOND = 1000;
    private final static int CONNECTION_REQUEST_TIMEOUT = SECOND;
    private final static int CONNECT_TIMEOUT = 15 * SECOND;
    private final static int READ_TIMEOUT = 30 * SECOND;

    private FHIRClientFactory() { }

    public static Client createClient() {
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();

        httpRequestFactory.setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT);
        httpRequestFactory.setConnectTimeout(CONNECT_TIMEOUT);
        httpRequestFactory.setReadTimeout(READ_TIMEOUT);

        return new FHIRClient(httpRequestFactory);
    }
}
