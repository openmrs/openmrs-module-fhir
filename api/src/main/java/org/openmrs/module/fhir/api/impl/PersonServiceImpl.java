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
package org.openmrs.module.fhir.api.impl;

import ca.uhn.fhir.model.dstu2.resource.Person;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.PersonService;
import org.openmrs.module.fhir.api.db.FHIRDAO;
import org.openmrs.module.fhir.api.reverse.OpenmrsPersonUtil;
import org.openmrs.module.fhir.api.util.FHIRPersonUtil;
import java.util.ArrayList;
import java.util.List;

public class PersonServiceImpl implements PersonService{

	protected final Log log = LogFactory.getLog(this.getClass());

	private FHIRDAO dao;

	/**
	 * @param dao the dao to set
	 */
	public void setDao(FHIRDAO dao) {
		this.dao = dao;
	}

	/**
	 * @return the dao
	 */
	public FHIRDAO getDao() {
		return dao;
	}

	@Override
	public Person getPerson(String id) {
		org.openmrs.Person omrsPerson = Context.getPersonService().getPersonByUuid(id);
		if (omrsPerson == null) {
			return null;
		}
		return FHIRPersonUtil.generatePerson(omrsPerson);
	}

	@Override
	public List<Person> searchPersonById(String id) {
		org.openmrs.Person omrsPerson =  Context.getPersonService().getPersonByUuid(id);
		List<Person> personList = new ArrayList<Person>();
		if (omrsPerson != null) {
			personList.add(FHIRPersonUtil.generatePerson(omrsPerson));
		}
		return personList;
	}

	@Override
	public List<Person> searchPersonByName(String name) {
		return null;
	}

    @Override
    public org.openmrs.Person consumeFHIRPerson(Person person) {

        return OpenmrsPersonUtil.generateOpenMRSPerson();
    }
}
