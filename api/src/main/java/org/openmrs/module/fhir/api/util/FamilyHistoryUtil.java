package org.openmrs.module.fhir.api.util;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.Bundle;
import ca.uhn.fhir.model.dstu.resource.FamilyHistory;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.InstantDt;
import ca.uhn.fhir.model.primitive.StringDt;
import ca.uhn.fhir.narrative.DefaultThymeleafNarrativeGenerator;
import ca.uhn.fhir.parser.IParser;
import org.openmrs.api.context.Context;

import java.util.Date;

/**
 * Created by snkasthu on 9/18/14.
 */
public class FamilyHistoryUtil {

	public static FamilyHistory generateFamilyHistory() {
		FamilyHistory familyHistory = new FamilyHistory();
		IdDt idDt = new IdDt();
		idDt.setValue("1");
		familyHistory.setId(idDt);
		familyHistory.setId("id");

		return familyHistory;
	}

	public static String parseFamilyHistory(FamilyHistory familyHistory, String contentType) {

		FhirContext ctx = new FhirContext();
		ctx.setNarrativeGenerator(new DefaultThymeleafNarrativeGenerator());

		IParser jsonParser = ctx.newJsonParser();
		IParser xmlParser = ctx.newXmlParser();

		String encoded = null;

		if (contentType != null) {
			if (contentType.equals("application/xml+fhir")) {

				xmlParser.setPrettyPrint(true);
				encoded = xmlParser.encodeResourceToString(familyHistory);
			} else {
				jsonParser.setPrettyPrint(true);
				encoded = jsonParser.encodeResourceToString(familyHistory);
			}
		} else {

			jsonParser.setPrettyPrint(true);
			encoded = jsonParser.encodeResourceToString(familyHistory);
		}

		return encoded;
	}

	public static String generateBundle() {

		FhirContext ctx = new FhirContext();
		IParser jsonParser = ctx.newJsonParser();

		Bundle bundle = new Bundle();
		StringDt title = bundle.getTitle();
		title.setValue("Search result");

		IdDt id = new IdDt();
		id.setValue("the request uri");

		bundle.setId(id);
		InstantDt instant = bundle.getUpdated();
		instant.setValue(new Date());

		IdDt entryId = new IdDt();
		entryId.setValue(Context.getAdministrationService().getGlobalProperty("webservices.rest.uriPrefix")
		                 + "/ws/fhir/Observation/" + "123");

		jsonParser.setPrettyPrint(true);
		String encoded = jsonParser.encodeBundleToString(bundle);

		return encoded;

	}
}
