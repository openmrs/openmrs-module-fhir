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

import ca.uhn.fhir.model.dstu.resource.Practitioner;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.PractitionerService;

public class FHIRPractitionerResource extends Resource {

	public Practitioner getByUniqueId(IdDt id) {
		PractitionerService practitionerService = Context.getService(PractitionerService.class);
		Practitioner fhirPractitioner = practitionerService.getPractitioner(id.getIdPart());
		if (fhirPractitioner == null) {
			throw new ResourceNotFoundException("Practitioner is not found for the given Id " + id.getIdPart());
		}
		return fhirPractitioner;
	}

}
