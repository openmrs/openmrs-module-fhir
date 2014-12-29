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
package org.openmrs.module.fhir.web.controller;

import org.openmrs.module.fhir.resources.FHIRLocationResource;
import org.openmrs.module.fhir.resources.FHIRPatientResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = "/fhir/")
public class FHIRLocationController {

    @RequestMapping(value = "/Location", method = RequestMethod.GET)
    @ResponseBody
    public Object search(@RequestParam(value = "_id", required = false) String _id,
                         HttpServletRequest request) throws Exception {
        String result = null;
        FHIRLocationResource locationResource = new FHIRLocationResource();

        if(_id != null)
            result = locationResource.searchById(_id, request.getContentType());
        return result;
    }

	@RequestMapping(value = "/Location/{uuid}", method = RequestMethod.GET)
	@ResponseBody
	public Object retrieve(@PathVariable("uuid") String uuid,
	                       HttpServletRequest request) throws Exception {
		String result;
		FHIRLocationResource locationResource = new FHIRLocationResource();
		result = locationResource.getByUniqueId(uuid, request.getContentType());
		return result;
	}
}
