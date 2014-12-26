/**
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

import ca.uhn.fhir.model.dstu.resource.FamilyHistory;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.FHIRFamilyHistoryUtil;

import javax.servlet.http.HttpServletRequest;

public class FHIRFamilyHistoryResource extends Resource {

	public Object retrieve(String uuid, HttpServletRequest request) throws Exception {

		if (!uuid.equals("search")) {
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

			Patient patient = Context.getPatientService().getPatientByUuid(patientUUid);

			String resultString = FHIRFamilyHistoryUtil.generateBundle();

			return resultString;

		}
	}

	public String getByUniqueId(String uuid, String contentType) {
		Patient patient = Context.getPatientService().getPatientByUuid(uuid);
		FamilyHistory familyHistory = FHIRFamilyHistoryUtil.generateFamilyHistory();

		return FHIRFamilyHistoryUtil.parseFamilyHistory(familyHistory, contentType);
	}

}
