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

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.FHIRObsUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by snkasthu on 9/9/14.
 */
/**
 * Created by snkasthu on 9/9/14.
 */
public class FHIRObservationResource extends Resource {

	org.openmrs.module.fhir.api.ObsService obsService = Context.getService(org.openmrs.module.fhir.api.ObsService.class);

	public Object retrieve(String uuid, HttpServletRequest request) throws Exception {

		if (!uuid.equals("_search")) {
			String resource = request.getParameter("Patient");
			String name = request.getParameter("name");

			String contentType = request.getContentType();
			Object delegate = getByUniqueId(uuid, contentType);
			if (delegate == null) {
				throw new Exception();
			}

			return delegate;
		} else {

			String patientUUid = request.getParameter("subject:Patient");
			String[] concepts = request.getParameter("name").split(",");

			Patient patient = Context.getPatientService().getPatientByUuid(patientUUid);

			List<Concept> conceptList = new ArrayList<Concept>();
			for (String s : concepts) {
				Concept concept = Context.getConceptService().getConceptByMapping(s, "LOINC");
				conceptList.add(concept);

			}

			List<Obs> totalObsList = new ArrayList<Obs>();

			for (Concept concept : conceptList) {
				List<Obs> obsList = Context.getObsService().getObservationsByPersonAndConcept(patient, concept);
				totalObsList.addAll(obsList);
			}

			String resultString = FHIRObsUtil.generateBundle(totalObsList);

			return resultString;

		}

	}

	public String getByUniqueId(String uniqueId, String contentType) {

		ca.uhn.fhir.model.dstu.resource.Observation fhirObservation = obsService.getObs(uniqueId);

		return FHIRObsUtil.parseObservation(fhirObservation, contentType);
	}

	public String doSearch(HttpServletRequest request) {

		String patientUUid = request.getParameter("subject:Patient");
		String[] concepts = request.getParameter("name").split(",");

		List<Obs> totalObsList = obsService.getObsByPatientandConcept(patientUUid, concepts);

		String resultString = FHIRObsUtil.generateBundle(totalObsList);

		return resultString;
	}

}


