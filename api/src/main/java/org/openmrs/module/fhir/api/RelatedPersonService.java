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

import org.hl7.fhir.dstu3.model.RelatedPerson;
import org.openmrs.api.OpenmrsService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface RelatedPersonService extends OpenmrsService {

	/**
	 * Get related person by id
	 *
	 * @param uuid The uuid of related person
	 * @return RelatedPerson fhir resource
	 */
	RelatedPerson getRelatedPerson(String uuid);

	/**
	 * Delete related person by id
	 *
	 * @param uuid The uuid of related person
	 */
	void deleteRelatedPerson(String uuid);

	/**
	 * Update related person
	 *
	 * @param uuid The uuid of related person
	 * @param relatedPerson representation of related person fhir resource
	 */
	RelatedPerson updateRelatedPerson(String uuid, RelatedPerson relatedPerson);

	/**
	 * Create related person
	 *
	 * @param relatedPerson the related person to create
	 */
	RelatedPerson createRelatedPerson(RelatedPerson relatedPerson);
}
