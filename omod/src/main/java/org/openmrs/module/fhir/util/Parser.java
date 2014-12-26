package org.openmrs.module.fhir.util;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu.resource.Observation;
import ca.uhn.fhir.narrative.DefaultThymeleafNarrativeGenerator;
import ca.uhn.fhir.parser.IParser;

public class Parser {

	public static String parse(IResource resource, String contentType) {

		FhirContext ctx = new FhirContext();
		ctx.setNarrativeGenerator(new DefaultThymeleafNarrativeGenerator());
		IParser jsonParser = ctx.newJsonParser();
		IParser xmlParser = ctx.newXmlParser();
		String encoded = null;
		if (contentType != null) {
			if (contentType.equals("application/xml+fhir")) {
				xmlParser.setPrettyPrint(true);
				encoded = xmlParser.encodeResourceToString(resource);
			} else {
				jsonParser.setPrettyPrint(true);
				encoded = jsonParser.encodeResourceToString(resource);
			}
		} else {
			jsonParser.setPrettyPrint(true);
			encoded = jsonParser.encodeResourceToString(resource);
		}

		return encoded;
	}

	public static String parseObs(Observation observation, String contentType) {

		FhirContext ctx = new FhirContext();
		ctx.setNarrativeGenerator(new DefaultThymeleafNarrativeGenerator()
		);

		IParser jsonParser = ctx.newJsonParser();
		IParser xmlParser = ctx.newXmlParser();
		String encoded = null;
		if (contentType != null)
		{
			if (contentType.equals("application/xml+fhir")) {
				xmlParser.setPrettyPrint(true);
				encoded = xmlParser.encodeResourceToString(observation);
			} else {
				jsonParser.setPrettyPrint(true);
				encoded = jsonParser.encodeResourceToString(observation);
			}
		} else {
			jsonParser.setPrettyPrint(true);
			encoded = jsonParser.encodeResourceToString(observation);
		}
		return encoded;
	}

}
