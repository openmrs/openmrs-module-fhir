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
import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.rest.gclient.ICriterion;
import ca.uhn.fhir.rest.gclient.ReferenceClientParam;
import ca.uhn.fhir.rest.gclient.TokenClientParam;
import org.hl7.fhir.dstu3.model.DiagnosticReport;
import org.hl7.fhir.dstu3.model.ImagingStudy;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Practitioner;

public class FHIRRESTfulGenericClient {

	private static final FhirContext ctx = FhirContext.forDstu3();

	public static Patient readPatientById(String serverBase,
	                                      String theID) {
		IGenericClient client = ctx.newRestfulGenericClient(serverBase);
		return client.read()
				.resource(Patient.class)
				.withId(theID)
				.execute();
	}

	public static Practitioner readPractitionerById(String serverBase,
	                                                String theID) {
		IGenericClient client = ctx.newRestfulGenericClient(serverBase);
		return client.read()
				.resource(Practitioner.class)
				.withId(theID)
				.execute();
	}

	public static ImagingStudy readImagingStudyById(String serverBase,
	                                                String theID) {
		IGenericClient client = ctx.newRestfulGenericClient(serverBase);
		return client.read()
				.resource(ImagingStudy.class)
				.withId(theID)
				.execute();
	}

	public static Bundle searchWhereReferenceAndToken(String serverBase,
	                                                  Class<DiagnosticReport> fhirResource,
	                                                  ICriterion<ReferenceClientParam> where,
	                                                  ICriterion<TokenClientParam> and) {
		IGenericClient client = ctx.newRestfulGenericClient(serverBase);
		return client.search()
				.forResource(fhirResource)
				.where(where)
				.and(and)
				.execute();
	}

	public static Bundle searchWhereReference(String serverBase,
	                                          Class<DiagnosticReport> fhirResource,
	                                          ICriterion<ReferenceClientParam> where) {
		IGenericClient client = ctx.newRestfulGenericClient(serverBase);
		return client.search()
				.forResource(fhirResource)
				.where(where)
				.execute();
	}

}
