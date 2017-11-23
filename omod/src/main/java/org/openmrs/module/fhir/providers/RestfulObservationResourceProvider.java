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

import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.Delete;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.annotation.Update;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenOrListParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.OperationOutcome;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Resource;
import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.openmrs.module.fhir.resources.FHIRObservationResource;

import java.util.List;

public class RestfulObservationResourceProvider implements IResourceProvider {

	private FHIRObservationResource provider;

	public RestfulObservationResourceProvider() {
		provider = new FHIRObservationResource();
	}

	@Override
	public Class<? extends Resource> getResourceType() {
		return Observation.class;
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
	public Observation getResourceById(@IdParam IdType theId) {
		return provider.getByUniqueId(theId);
	}

	/**
	 * Search observation by unique id
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
	 * @param codes object containing the requested name
	 */
	@Search()
	public List<Observation> searchObsByPatientAndConcept(@RequiredParam(name = Observation.SP_SUBJECT) ReferenceParam
			                                                      person,
	                                                      @RequiredParam(name = Observation.SP_CODE) TokenOrListParam
			                                                      codes) {
		return provider.searchObsByPatientAndConcept(person, codes);
	}

	/**
	 * Search obsservation by observation code
	 *
	 * @param theCodings object containing the requested code
	 */
	@Search()
	public List<Observation> searchObsByCode(@RequiredParam(name = Observation.SP_CODE) TokenOrListParam theCodings) {
		return provider.searchObsByCode(theCodings);
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

	/**
	 * Search observations by patient identifier
	 *
	 * @param identifier object containing the patient identifier
	 */
	@Search()
	public List<Observation> searchEncountersByPatientIdentifier(
			@RequiredParam(name = Observation.SP_PATIENT, chainWhitelist = {
					Patient.SP_IDENTIFIER }) ReferenceParam identifier
	) {
		return provider.searchObsByPatientIdentifier(identifier);
	}

	/**
	 * Delete observation by unique id
	 *
	 * @param theId object containing the id
	 */
	@Delete()
	public void deleteObservation(@IdParam IdType theId) {
		provider.deleteObservation(theId);
	}
	
	/**
	 * Create Observation
	 *
	 * @param observation fhir observation object
	 */
	@Create
	public MethodOutcome createFHIRObservation(@ResourceParam Observation observation) {
		observation = provider.createFHIRObservation(observation);
		MethodOutcome retVal = new MethodOutcome();
		retVal.setId(new IdType(FHIRConstants.OBSERVATION, observation.getId()));
		OperationOutcome outcome = new OperationOutcome();
		CodeableConcept concept = new CodeableConcept();
		Coding coding = concept.addCoding();
		coding.setDisplay("Observation is successfully created" + observation.getId());
		outcome.addIssue().setDetails(concept);
		retVal.setOperationOutcome(outcome);
		return retVal;
	}
	
	@Update
	public MethodOutcome updateFHIRObservation(@ResourceParam Observation observation, @IdParam IdType theId) {
		MethodOutcome retVal = new MethodOutcome();
		OperationOutcome outcome = new OperationOutcome();
		try {
			observation = provider.updateFHIRObservation(observation, theId.getIdPart());
		}
		catch (Exception e) {
			CodeableConcept concept = new CodeableConcept();
			Coding coding = concept.addCoding();
			coding.setDisplay("Following exception occured " + e.getMessage());
			outcome.addIssue()
			.setDetails(concept);
			retVal.setOperationOutcome(outcome);
			return retVal;
		}
		CodeableConcept concept = new CodeableConcept();
		Coding coding = concept.addCoding();
		coding.setDisplay("Observation is successfully updated. New Observation UUID is " + observation.getId());
		outcome.addIssue().setDetails(concept);
		retVal.setOperationOutcome(outcome);
		return retVal;
	}

}
