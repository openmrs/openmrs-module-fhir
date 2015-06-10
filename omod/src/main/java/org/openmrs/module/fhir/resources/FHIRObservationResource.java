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

import ca.uhn.fhir.model.base.composite.BaseCodingDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenOrListParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;

import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.ObsService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FHIRObservationResource extends Resource {

	public Observation getByUniqueId(IdDt id) {
		ObsService obsService = Context.getService(ObsService.class);
		Observation fhirObservation = obsService.getObs(id.getIdPart());
		if (fhirObservation == null) {
			throw new ResourceNotFoundException("Observation is not found for the given Id " + id.getValue());
		}
		return fhirObservation;
	}

	public List<Observation> searchObsById(TokenParam id) {
		ObsService obsService = Context.getService(ObsService.class);
		return obsService.searchObsById(id.getValue());
	}

	public List<Observation> searchObsByPatientAndConcept(ReferenceParam person, TokenOrListParam codes) {
		ObsService obsService = Context.getService(ObsService.class);
		Map<String, String> conceptNamesAndURIs = new HashMap<String, String>();
		for (BaseCodingDt baseCodingDt : codes.getListAsCodings()) {
			conceptNamesAndURIs.put(baseCodingDt.getValueAsQueryToken(), baseCodingDt.getSystemElement().getValue());
		}
		return obsService.searchObsByPatientAndConcept(person.getIdPart(), conceptNamesAndURIs);
	}

	public List<Observation> searchObsByCode(TokenOrListParam codes) {
		ObsService obsService = Context.getService(ObsService.class);
		Map<String, String> conceptNamesAndURIs = new HashMap<String, String>();
		for (BaseCodingDt baseCodingDt : codes.getListAsCodings()) {
			conceptNamesAndURIs.put(baseCodingDt.getValueAsQueryToken(), baseCodingDt.getSystemElement().getValue());
		}
		return obsService.searchObsByCode(conceptNamesAndURIs);
	}

	public List<Observation> searchObsByDate(DateParam date) {
		ObsService obsService = Context.getService(ObsService.class);
		return obsService.searchObsByDate(date.getValue());
	}

	public List<Observation> searchObsByPerson(ReferenceParam person) {
		ObsService obsService = Context.getService(ObsService.class);
		return obsService.searchObsByPerson(person.getIdPart());
	}

	public List<Observation> searchObsByValueConcept(TokenParam answerConceptName) {
		ObsService obsService = Context.getService(ObsService.class);
		return obsService.searchObsByValueConcept(answerConceptName.getValue());
	}

	public List<Observation> searchObsByPatientIdentifier(ReferenceParam identifier) {
		List<Observation> fhirEncounters = new ArrayList<Observation>();

		String chain = identifier.getChain();
		if (Patient.SP_IDENTIFIER.equals(chain)) {
			fhirEncounters = Context.getService(ObsService.class).searchObsByPatientIdentifier(identifier.getValue());
		}
		return fhirEncounters;
	}

	public void deleteObservation(IdDt id) {
		ObsService obsService = Context.getService(ObsService.class);
		obsService.deleteObs(id.getIdPart());
	}
	
	public Observation createFHIRObservation(Observation observation) {
		ObsService obsService = Context.getService(ObsService.class);
		return obsService.createFHIRObservation(observation);
	}

}
