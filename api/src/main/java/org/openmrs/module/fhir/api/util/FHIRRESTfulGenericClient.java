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
import ca.uhn.fhir.model.dstu2.resource.DiagnosticReport;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.dstu2.resource.Practitioner;
import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.rest.gclient.ICriterion;
import ca.uhn.fhir.rest.gclient.ReferenceClientParam;
import ca.uhn.fhir.rest.gclient.TokenClientParam;

public class FHIRRESTfulGenericClient {

	private static final FhirContext ctx = FhirContext.forDstu2();

	public static Bundle searchDiagnosticReportByReference(String serverBase,
	                            ICriterion<ReferenceClientParam> where) {
		IGenericClient client = ctx.newRestfulGenericClient(serverBase);

		return client
				.search()
				.forResource(DiagnosticReport.class)
				.where(where)
				.execute();
	}

	public static Patient readPatientById(String serverBase,
	                                     String theID) {
		IGenericClient client = ctx.newRestfulGenericClient(serverBase);

		return client
				.read()
				.resource(Patient.class)
				.withId(theID)
				.execute();
	}

	public static Practitioner readPractitionerById(String serverBase,
	                                      String theID) {
		IGenericClient client = ctx.newRestfulGenericClient(serverBase);

		return client
				.read()
				.resource(Practitioner.class)
				.withId(theID)
				.execute();
	}

	public static Bundle search(String serverBase,
	                            Class<DiagnosticReport> fhirResource,
	                            ICriterion<ReferenceClientParam> where,
	                            ICriterion<TokenClientParam> and) {
		IGenericClient client = ctx.newRestfulGenericClient(serverBase);

		return client
				.search()
				.forResource(fhirResource)
				.where(where)
				.and(and)
				.execute();
	}
}
