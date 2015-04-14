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

import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.Composition;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import org.openmrs.api.OpenmrsService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * This service exposes module's core functionality. It is a Spring managed bean which is configured in
 * moduleApplicationContext.xml.
 * It can be accessed only via Context:<br>
 * <code>
 * Context.getService(FHIRService.class).someMethod();
 * </code>
 *
 * @see org.openmrs.api.context.Context
 */

@Transactional
public interface EncounterService extends OpenmrsService {

	/**
	 * Get encounter composition by id
	 * As OpenMRS visits and encounters both model as fhir encounter, this method will return OpenMRS visit or a encounter
	 * which is to the matching id provided in the method parameter
	 *
	 * @param id encounter id
	 * @return fhir composition of enconter
	 */
	Encounter getEncounter(String id);

	/**
	 * Search encounters by id
	 *
	 * @param id to be search
	 * @return fhir encounter resource list
	 */
	public List<Encounter> searchEncounterById(String id);

	/**
	* Search encounters by patient identifier
	* @param identifier to be search
	* @return fhir encounter resource list
	*/
	public List<Encounter> searchEncountersByPatientIdentifier(String identifier);

	/**
	 * Search encounters by id and returned composition
	 *
	 * @param id the encounter id to be search
	 * @return fhir composition list
	 */
	public List<Composition> searchEncounterComposition(String id);

	/**
	 * Search encounters by patient id and returned composition
	 *
	 * @param patientId the patient id to be search encounters
	 * @return fhir composition list
	 */
	public List<Composition> searchEncounterCompositionByPatient(String patientId);

	/**
	 * Search encounters by patient id and returned composition
	 *
	 * @param encounterId the encounter id to be search encounters
	 * @return fhir composition list
	 */
	public List<Composition> searchEncounterCompositionByEncounterId(String encounterId);

	/**
	 * Get Encounter operations bundle resource
	 *
	 * @param encounterId the encounter id to be search encounters
	 * @return encounter resource bundle for operations
	 */
	public Bundle getEncounterOperationsById(String encounterId);

	/**
	 * Get Encounter operations bundle resource with providing external bundle resource
	 *
	 * @param encounterId the encounter id to be search encounters
	 * @param bundle the provided bundle
	 * @return contents of encounter resource bundle for operations
	 */
	public Bundle getEncounterOperationsById(String encounterId, Bundle bundle,  boolean includePatient);
}
