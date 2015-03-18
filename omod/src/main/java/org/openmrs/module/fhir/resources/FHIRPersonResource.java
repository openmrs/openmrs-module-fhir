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

import ca.uhn.fhir.model.dstu2.resource.Person;
import ca.uhn.fhir.model.dstu2.resource.Practitioner;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.PersonService;
import org.openmrs.module.fhir.api.PractitionerService;

import java.util.List;

public class FHIRPersonResource extends Resource {

	public Person getByUniqueId(IdDt id) {
		PersonService personService = Context.getService(PersonService.class);
		Person fhirPerson = personService.getPerson(id.getIdPart());
		if (fhirPerson == null) {
			throw new ResourceNotFoundException("Practitioner is not found for the given Id " + id.getIdPart());
		}
		return fhirPerson;
	}

	public List<Person> searchByUniqueId(TokenParam id) {
		PersonService personService = Context.getService(PersonService.class);
		return personService.searchPersonById(id.getValue());
	}

	public List<Person> searchByName(StringParam name) {
		PersonService personService = Context.getService(PersonService.class);
		return personService.searchPersonsByName(name.getValue());
	}
}
