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
package org.openmrs.module.fhir.api;

import java.util.List;

import org.openmrs.api.OpenmrsService;
import org.springframework.transaction.annotation.Transactional;

import ca.uhn.fhir.model.dstu2.resource.Practitioner;
import ca.uhn.fhir.model.primitive.IdDt;

/**
 * This service exposes module's core functionality. It is a Spring managed bean which is configured
 * in moduleApplicationContext.xml. It can be accessed only via Context:<br>
 * <code>
 * Context.getService(FHIRService.class).someMethod();
 * </code>
 *
 * @see org.openmrs.api.context.Context
 */

@Transactional
public interface PractitionerService extends OpenmrsService {

	/**
	 * Get practitioner by id
	 *
	 * @param id the practitioner identifier
	 * @return practitioner fhir resource object
	 */
	Practitioner getPractitioner(String id);

	/**
	 * Search practitioners by uuid
	 *
	 * @param id the uuid to be search
	 * @return fhir practitioner resource list
	 */
	List<Practitioner> searchPractitionersById(String id);

	/**
	 * Search practitioners by name
	 *
	 * @param name the name to be search
	 * @return fhir practitioner resource list
	 */
	List<Practitioner> searchPractitionersByName(String name);

	/**
	 * Search practitioners by givenName
	 *
	 * @param givenName the name to be search
	 * @return fhir practitioner resource list
	 */
	List<Practitioner> searchPractitionersByGivenName(String givenName);

	/**
	 * Search practitioners by familyName
	 *
	 * @param familyName the name to be search
	 * @return fhir practitioner resource list
	 */
	List<Practitioner> searchPractitionersByFamilyName(String familyName);

	/**
	 * Search practitioners by identifier
	 *
	 * @param identifier the identifier to be search
	 * @return fhir practitioner resource list
	 */
	List<Practitioner> searchPractitionersByIdentifier(String identifier);
	
	/**
	 * Create Practitioner
	 *
	 * @param practitioner the practitioner to be created
	 * @return fhir practitioner resource list
	 */
	Practitioner createFHIRPractitioner(Practitioner practitioner);
	
	/**
	 * Update Practitioner
	 *
	 * @param practitioner the practitioner to update
	 * @param  theId , the uuid of the practitioner to update
	 * @return fhir practitioner resource list
	 */
	Practitioner updatePractitioner(Practitioner practitioner, IdDt theId);
}
