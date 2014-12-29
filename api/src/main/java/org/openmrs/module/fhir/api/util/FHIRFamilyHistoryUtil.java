/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
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

public class FHIRFamilyHistoryUtil {

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
