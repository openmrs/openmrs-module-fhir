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
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.annotation.Update;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.OperationOutcome;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.hl7.fhir.dstu3.model.Resource;
import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.openmrs.module.fhir.resources.FHIRPractitionerResource;

import java.util.List;

public class RestfulPractitionerResourceProvider implements IResourceProvider {

	;

	private FHIRPractitionerResource practitionerResource;

	public RestfulPractitionerResourceProvider() {
		this.practitionerResource = new FHIRPractitionerResource();
	}

	@Override
	public Class<? extends Resource> getResourceType() {
		return Practitioner.class;
	}

	/**
	 * The "@Read" annotation indicates that this method supports the read operation. Read
	 * operations should return a single resource instance.
	 *
	 * @param theId The read operation takes one parameter, which must be of type IdType and must be
	 *            annotated with the "@Read.IdParam" annotation.
	 * @return Returns a resource matching this identifier, or null if none exists.
	 */
	@Read()
	public Practitioner getResourceById(@IdParam IdType theId) {
		Practitioner result = null;
		result = practitionerResource.getByUniqueId(theId);
		return result;
	}

	/**
	 * Search Practitioner by unique id
	 *
	 * @param id object contaning the requested family name
	 */
	@Search()
	public List<Practitioner> searchPractitionerByUniqueId(@RequiredParam(name = Practitioner.SP_RES_ID) TokenParam id) {
		return practitionerResource.searchByUniqueId(id);
	}

	/**
	 * Get Practitioner by family name
	 *
	 * @param theFamilyName object contaning the requested family name
	 */
	@Search()
	public List<Practitioner> findPractitionersByFamilyName(@RequiredParam(name = Practitioner.SP_FAMILY) StringParam theFamilyName) {
		return practitionerResource.searchByFamilyName(theFamilyName);
	}

	/**
	 * Get Practitioner by name
	 *
	 * @param name name of the Practitioner
	 * @return This method returns a list of Practitioners. This list may contain multiple matching
	 *         resources, or it may also be empty.
	 */
	@Search()
	public List<Practitioner> findPractitionersByName(@RequiredParam(name = Practitioner.SP_NAME) StringParam name) {
		return practitionerResource.searchByName(name);
	}

	/**
	 * Get Practitioner by identifier
	 *
	 * @param identifier
	 * @return This method returns a list of Practitioners. This list may contain multiple matching
	 *         resources, or it may also be empty.
	 */
	@Search()
	public List<Practitioner> searchPractitionersByIdentifier(@RequiredParam(name = Practitioner.SP_IDENTIFIER) TokenParam identifier) {
		return practitionerResource.searchByIdentifier(identifier);
	}

	/**
	 * Find Practitioner by given name
	 *
	 * @param givenName given name of the Practitioner
	 * @return This method returns a list of Practitioners. This list may contain multiple matching
	 *         resources, or it may also be empty.
	 */
	@Search()
	public List<Practitioner> findPractitionersByGivenName(@RequiredParam(name = Practitioner.SP_GIVEN) StringParam givenName) {
		return practitionerResource.searchByGivenName(givenName);
	}
	
	/**
	 * Create Practitioner
	 *
	 * @param practitioner fhir practitioner object
	 */
	@Create
	public MethodOutcome createFHIRPractitioner(@ResourceParam Practitioner practitioner) {
		practitioner = practitionerResource.createFHIRPractitioner(practitioner);
		MethodOutcome retVal = new MethodOutcome();
		retVal.setId(new IdType(FHIRConstants.PERSON, practitioner.getId()));
		OperationOutcome outcome = new OperationOutcome();
		CodeableConcept concept = new CodeableConcept();
		Coding coding = concept.addCoding();
		coding.setDisplay("Practitioner is successfully created with id " + practitioner.getId());
		outcome.addIssue().setDetails(concept);
		retVal.setOperationOutcome(outcome);
		return retVal;
	}
	
	@Update
	public MethodOutcome updatePractitioner(@ResourceParam Practitioner practitioner, @IdParam IdType theId) {
		MethodOutcome retVal = new MethodOutcome();
		OperationOutcome outcome = new OperationOutcome();
		try {
			practitioner = practitionerResource.updatePractitioner(practitioner, practitioner.getId());
		} catch (Exception e) {
			retVal.setOperationOutcome(outcome);
			CodeableConcept concept = new CodeableConcept();
			Coding coding = concept.addCoding();
			coding.setDisplay(
					"No Person is associated with the given UUID to update. Please"
							+ " make sure you have set at lease one non-delete name, Gender and birthday to create a new "
							+ "Person with the given Id" + practitioner.getId());
			outcome.addIssue().setDetails(concept);
			retVal.setOperationOutcome(outcome);
			return retVal;
		}
		CodeableConcept concept = new CodeableConcept();
		Coding coding = concept.addCoding();
		coding.setDisplay("Person is successfully updated " + practitioner.getId());
		outcome.addIssue().setDetails(concept);
		retVal.setOperationOutcome(outcome);
		return retVal;
	}
}
