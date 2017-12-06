package org.openmrs.module.fhir.api.client;


import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import org.hl7.fhir.dstu3.model.Patient;
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
import java.util.HashSet;
import java.util.Set;

public class FHIRHttpMessageConverter extends AbstractHttpMessageConverter<Object> {

    private static final Set<Class<?>> SUPPORTED_CLASSES = new HashSet<Class<?>>(2);
    private static final String CHARSET = "UTF-8";
    private static final String TYPE = "application";
    private static final String SUBTYPE_1 = "fhir+json";
    private static final String SUBTYPE_2 = "json+fhir";

    private IParser parser = FhirContext.forDstu3().newJsonParser();

    static {
        SUPPORTED_CLASSES.add(Patient.class);
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
    protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        try {
            return convertStreamToString(inputMessage.getBody());
        } catch (IOException e) {
            throw new HttpMessageNotReadableException("Could not read JSON: " + e.getMessage(), e);
        }
    }

    @Override
    protected void writeInternal(Object o, HttpOutputMessage outputMessage) throws HttpMessageNotWritableException {
        try {
            String json = parser.encodeResourceToString((IBaseResource) o);
            outputMessage.getBody().write(json.getBytes());
        }
        catch (IOException e) {
            throw new HttpMessageNotWritableException("Could not serialize object. Msg: " + e.getMessage(), e);
        }
    }

    public String convertStreamToString(InputStream is) throws IOException {
        if (is != null) {
            Writer writer = new StringWriter();

            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
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
