package org.openmrs.module.fhir.util;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu.resource.Observation;
import ca.uhn.fhir.model.dstu.resource.Patient;
import ca.uhn.fhir.narrative.DefaultThymeleafNarrativeGenerator;
import ca.uhn.fhir.parser.IParser;


public class Parser {

    public static String parsePatient(Patient patient, String contentType) {

        FhirContext ctx = new FhirContext();
        ctx.setNarrativeGenerator(new DefaultThymeleafNarrativeGenerator());

        IParser jsonParser = ctx.newJsonParser();
        IParser xmlParser = ctx.newXmlParser();

        String encoded = null;

        if (contentType != null) {
            if (contentType.equals("application/xml+fhir")) {

                xmlParser.setPrettyPrint(true);
                encoded = xmlParser.encodeResourceToString(patient);
                System.out.println(encoded);
            } else {
                jsonParser.setPrettyPrint(true);
                encoded = jsonParser.encodeResourceToString(patient);
                System.out.println(encoded);

            }
        } else {

            jsonParser.setPrettyPrint(true);
            encoded = jsonParser.encodeResourceToString(patient);
            System.out.println(encoded);
        }

        return encoded;
    }

    public static String parseObs(Observation observation, String contentType) {

    FhirContext ctx = new FhirContext();
    ctx.setNarrativeGenerator(new

    DefaultThymeleafNarrativeGenerator()

    );

    IParser jsonParser = ctx.newJsonParser();
    IParser xmlParser = ctx.newXmlParser();

    String encoded = null;

    if(contentType!=null)

    {

        if (contentType.equals("application/xml+fhir")) {

            xmlParser.setPrettyPrint(true);
            encoded = xmlParser.encodeResourceToString(observation);
            System.out.println(encoded);
        } else {

            jsonParser.setPrettyPrint(true);
            encoded = jsonParser.encodeResourceToString(observation);
            System.out.println(encoded);
        }
    }

    else

    {

        jsonParser.setPrettyPrint(true);
        encoded = jsonParser.encodeResourceToString(observation);
        System.out.println(encoded);
    }

    return encoded;
}


}
