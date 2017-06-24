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

import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenParam;
import org.hl7.fhir.dstu3.model.Composition;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.EncounterService;

import java.util.List;

public class FHIRCompositionResource {

	public List<Composition> searchEncounterCompostionsByPatient(ReferenceParam patient) {
		EncounterService encounterService = Context.getService(EncounterService.class);
		return encounterService.searchEncounterCompositionByPatientId(patient.getIdPart());
	}

	public List<Composition> searchEncounterCompostionsByEncounterId(TokenParam encounterId) {
		EncounterService encounterService = Context.getService(EncounterService.class);
		return encounterService.searchEncounterCompositionByEncounterId(encounterId.getValue());
	}
}
