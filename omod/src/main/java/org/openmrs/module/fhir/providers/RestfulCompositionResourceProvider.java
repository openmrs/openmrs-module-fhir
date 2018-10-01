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
package org.openmrs.module.fhir.providers;

import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.hl7.fhir.dstu3.model.Composition;
import org.hl7.fhir.dstu3.model.Resource;
import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.openmrs.module.fhir.resources.FHIRCompositionResource;

import java.util.List;

public class RestfulCompositionResourceProvider implements IResourceProvider {

	private FHIRCompositionResource compositionResource;

	public RestfulCompositionResourceProvider() {
		compositionResource = new FHIRCompositionResource();
	}

	@Override
	public Class<? extends Resource> getResourceType() {
		return Composition.class;
	}

	/**
	 * Search compositions by patient
	 *
	 * @param patient object containing the requested id
	 */
	@Search
	public List<Composition> findCompositionEncountersByPatient(
			@RequiredParam(name = Composition.SP_SUBJECT) ReferenceParam
					patient) {
		return compositionResource.searchEncounterCompostionsByPatient(patient);
	}

	/**
	 * Search compositions by patient
	 *
	 * @param encounter object containing the requested encounter id
	 */
	@Search
	public List<Composition> findCompositionEncountersByEncounterId(@RequiredParam(name = FHIRConstants.ENCOUNTER_ID)
			TokenParam encounter) {
		return compositionResource.searchEncounterCompostionsByEncounterId(encounter);
	}
}
