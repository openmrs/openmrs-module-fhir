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
import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu2.resource.DiagnosticReport;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.rest.gclient.DateClientParam;
import ca.uhn.fhir.rest.gclient.ICriterion;
import ca.uhn.fhir.rest.gclient.ReferenceClientParam;
import ca.uhn.fhir.rest.gclient.StringClientParam;

import java.util.ArrayList;
import java.util.List;

public class FHIRRESTfulGenericClient {

	private static final FhirContext ctx = FhirContext.forDstu2();

	private static IGenericClient getGenericClient(String serverBase) {
		return ctx.newRestfulGenericClient(serverBase);
	}

	public static Bundle search(String serverBase,
	                            Class<DiagnosticReport> fhirResource,
	                            ICriterion<ReferenceClientParam> where) {
		/*Bundle results = getGenericClient(serverBase)
				.search()
				.forResource(fhirResource)
				.where(where)
				.execute();*/

		String serverBase1 = "http://fhir.hackathon.siim.org/fhir";
		String serverBase2 = "http://fhirtest.uhn.ca/baseDstu2";

		IGenericClient client = ctx.newRestfulGenericClient(serverBase1);

		// Perform a search
		Bundle results = client
				.search()
				.forResource(DiagnosticReport.class)
				.where(Patient.FAMILY.matches().value("SIIM"))
				.execute();

		System.out.println("Found " + results.size() + " patients named 'duck'");

		return results;
	}
}
