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
package org.openmrs.module.fhir.web.controller;

import org.openmrs.module.fhir.resources.FHIRPatientResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = "/fhir/")
public class FHIRPatientController {

	@RequestMapping(value = "/patient", method = RequestMethod.GET)
	@ResponseBody
	public Object search(@RequestParam(value = "identifier", required = false) String identifier,
	                     HttpServletRequest request) throws Exception {
		String result;
		FHIRPatientResource patientResource = new FHIRPatientResource();
		result = patientResource.searchByIdentifier(identifier, request.getContentType());
		return result;
	}

	@RequestMapping(value = "/patient/{uuid}", method = RequestMethod.GET)
	@ResponseBody
	public Object retrieve(@PathVariable("uuid") String uuid, HttpServletRequest request)
			throws Exception {
		String result;
		FHIRPatientResource patientResource = new FHIRPatientResource();
		result = patientResource.getByUniqueId(uuid, request.getContentType());
		return result;
	}
}
