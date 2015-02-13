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

import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu.resource.Observation;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.openmrs.module.fhir.resources.FHIRObservationResource;

import java.util.List;

public class RestfulObservationResourceProvider implements IResourceProvider {

	private FHIRObservationResource provider;

	@Override
	public Class<? extends IResource> getResourceType() {
		return Observation.class;
	}

	public RestfulObservationResourceProvider() {
		provider = new FHIRObservationResource();
	}

	/**
	 * The "@Read" annotation indicates that this method supports the
	 * read operation. Read operations should return a single resource
	 * instance.
	 *
	 * @param theId The read operation takes one parameter, which must be of type
	 *              IdDt and must be annotated with the "@Read.IdParam" annotation.
	 * @return Returns a resource matching this identifier, or null if none exists.
	 */
	@Read()
	public Observation getResourceById(@IdParam IdDt theId) {
		Observation result = null;
		result = provider.getByUniqueId(theId);
		return result;
	}

	/**
	 * Search obsservation by unique id
	 *
	 * @param id object containing the requested id
	 */
	@Search()
	public List<Observation> searchObservationById(@RequiredParam(name = Observation.SP_RES_ID) TokenParam id) {
		return provider.searchObsById(id);
	}

	/**
	 * Search obsservation by patient and concept name
	 *
	 * @param name object containing the requested name
	 */
	@Search()
	public List<Observation> searchObsByPatientAndConcept(@RequiredParam(name = Observation.SP_SUBJECT) ReferenceParam
			                                                      person,
	                                                      @RequiredParam(name = Observation.SP_NAME) TokenParam name) {
		return provider.searchObsByPatientAndConcept(person, name);
	}

	/**
	 * Search obsservation by observation name
	 *
	 * @param name object containing the requested name
	 */
	@Search()
	public List<Observation> searchObsByName(@RequiredParam(name = Observation.SP_NAME) TokenParam name) {
		return provider.searchObsByName(name);
	}

	/**
	 * Search obsservation by observation date
	 *
	 * @param date object containing the requested date
	 */
	@Search()
	public List<Observation> searchObsByDate(@RequiredParam(name = Observation.SP_DATE) DateParam date) {
		return provider.searchObsByDate(date);
	}

	/**
	 * Search obsservation by person
	 *
	 * @param person object containing the requested person id
	 */
	@Search()
	public List<Observation> searchObsByPerson(@RequiredParam(name = Observation.SP_SUBJECT) ReferenceParam person) {
		return provider.searchObsByPerson(person);
	}

	/**
	 * Search obsservation by answer concept
	 *
	 * @param answerConceptName object containing the value concept name which is the answer concept
	 */
	@Search()
	public List<Observation> searchObsByValueConcept(@RequiredParam(name = Observation.SP_VALUE_CONCEPT) TokenParam
			                                                 answerConceptName) {
		return provider.searchObsByValueConcept(answerConceptName);
	}
}
