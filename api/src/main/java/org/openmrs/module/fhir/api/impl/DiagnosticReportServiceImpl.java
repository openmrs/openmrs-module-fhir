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

import ca.uhn.fhir.model.dstu2.resource.DiagnosticReport;
import ca.uhn.fhir.model.dstu2.resource.Person;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.server.exceptions.MethodNotAllowedException;
import ca.uhn.fhir.rest.server.exceptions.NotModifiedException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Obs;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.fhir.api.DiagnosticReportService;
import org.openmrs.module.fhir.api.ObsService;
import org.openmrs.module.fhir.api.PersonService;
import org.openmrs.module.fhir.api.db.FHIRDAO;
import org.openmrs.module.fhir.api.util.FHIRPersonUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * It is a default implementation of {@link org.openmrs.module.fhir.api.DiagnosticReportService}.
 */
public class DiagnosticReportServiceImpl extends BaseOpenmrsService implements DiagnosticReportService {
	
	protected final Log log = LogFactory.getLog(this.getClass());
	
	private FHIRDAO dao;
	
	/**
	 * @return the dao
	 */
	public FHIRDAO getDao() {
		return dao;
	}
	
	/**
	 * @param dao the dao to set
	 */
	public void setDao(FHIRDAO dao) {
		this.dao = dao;
	}
	
	@Override
	public DiagnosticReport getDiagnosticReport(String id) {
		return null;
	}
	
	@Override
	public DiagnosticReport createFHIRDiagReport(DiagnosticReport diagReport) {
		//org.openmrs.Person omrsPerson = FHIRPersonUtil.generateOpenMRSPerson(person);
		//org.openmrs.api.PersonService personService = Context.getPersonService();
		//omrsPerson = personService.savePerson(omrsPerson);
		//return FHIRPersonUtil.generatePerson(omrsPerson);
		return null;
	}
	
	/**
	 * @see org.openmrs.module.fhir.api.DiagnosticReportService#deleteDiagnosticReport(String)
	 */
	@Override
	public void deleteDiagnosticReport(String id) {
	}
}
