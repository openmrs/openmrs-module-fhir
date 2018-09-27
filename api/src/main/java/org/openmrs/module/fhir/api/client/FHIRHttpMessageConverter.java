package org.openmrs.module.fhir.api.client;


import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public class FHIRHttpMessageConverter extends AbstractHttpMessageConverter<IBaseResource> {

    private static final Set<Class<?>> SUPPORTED_CLASSES = new HashSet<>(2);
    private static final String CHARSET = "UTF-8";
    private static final String TYPE = "application";
    private static final String SUBTYPE_1 = "fhir+json";
    private static final String SUBTYPE_2 = "json+fhir";

    private IParser parser = FhirContext.forDstu3().newJsonParser();

    static {
        SUPPORTED_CLASSES.add(Patient.class);
        SUPPORTED_CLASSES.add(Encounter.class);
        SUPPORTED_CLASSES.add(Observation.class);
        SUPPORTED_CLASSES.add(Location.class);
        SUPPORTED_CLASSES.add(Practitioner.class);
    }

    public FHIRHttpMessageConverter() {
        super(new MediaType(TYPE, SUBTYPE_1, Charset.forName(CHARSET)),
                new MediaType(TYPE, SUBTYPE_2, Charset.forName(CHARSET)));
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return SUPPORTED_CLASSES.contains(clazz);
    }

    @Override
    protected IBaseResource readInternal(Class<? extends IBaseResource> clazz, HttpInputMessage inputMessage) throws
            HttpMessageNotReadableException {
        try {
            String json = convertStreamToString(inputMessage.getBody());
            return parser.parseResource(json);
        } catch (IOException e) {
            throw new HttpMessageNotReadableException("Could not read JSON: " + e.getMessage(), e);
        }
    }

    @Override
    protected void writeInternal(IBaseResource o, HttpOutputMessage outputMessage) throws HttpMessageNotWritableException {
        try {
            String json = parser.encodeResourceToString(o);
            outputMessage.getBody().write(json.getBytes());
        }
        catch (IOException e) {
            throw new HttpMessageNotWritableException("Could not serialize object. Msg: " + e.getMessage(), e);
        }
    }

    private String convertStreamToString(InputStream is) throws IOException {
        if (is != null) {
            Writer writer = new StringWriter();

            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }

                reader.close();
            } finally {
                is.close();
            }
            return writer.toString();
        } else {
            return "";
        }
    }

}
