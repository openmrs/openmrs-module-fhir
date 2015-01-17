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
package org.openmrs.module.fhir.resources;

import ca.uhn.fhir.model.dstu.resource.Patient;
import ca.uhn.fhir.model.primitive.IdDt;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.FHIRPatientUtil;
import org.openmrs.module.fhir.exception.FHIRModuleOmodException;
import org.openmrs.module.fhir.exception.FHIRValidationException;

public class FHIRPatientResource extends Resource {

	public Patient getByUniqueId(IdDt theId) {

		org.openmrs.module.fhir.api.PatientService patientService = Context.getService(
				org.openmrs.module.fhir.api.PatientService.class);
		ca.uhn.fhir.model.dstu.resource.Patient fhirPatient = patientService.getPatient(theId.getIdPart());
		return fhirPatient;
	}

	//search by patient identifier. ex: GET [base-url]/Patient?identifier=http://acme.org/patient|2345
	//returns a bundle of patients
	public String searchByIdentifier(String identifier, String contentType)
			throws FHIRValidationException {

		org.openmrs.module.fhir.api.PatientService patientService = Context.getService(
				org.openmrs.module.fhir.api.PatientService.class);
		ca.uhn.fhir.model.api.Bundle patientBundle = patientService.getPatientsByIdentifier(identifier);
		return FHIRPatientUtil.parseBundle(patientBundle);
	}

}
