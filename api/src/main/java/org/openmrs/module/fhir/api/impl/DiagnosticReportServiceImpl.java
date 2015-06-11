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

import ca.uhn.fhir.model.dstu2.composite.CodingDt;
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
import org.openmrs.module.fhir.api.diagnosticreport.DiagnosticReportHandler;
import org.openmrs.module.fhir.api.diagnosticreport.DiagnosticReportTemplate;
import org.openmrs.module.fhir.api.util.FHIRDiagnosticReportUtil;
import org.openmrs.module.fhir.api.util.FHIRPersonUtil;
import org.openmrs.util.OpenmrsClassLoader;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.InvalidNameException;

/**
 * It is a default implementation of {@link org.openmrs.module.fhir.api.DiagnosticReportService}.
 */
public class DiagnosticReportServiceImpl extends BaseOpenmrsService implements DiagnosticReportService {
	
	protected final Log log = LogFactory.getLog(this.getClass());
	
	private FHIRDAO dao;
	
	private static Map<String, DiagnosticReportHandler> handlers = null;
	
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
	public DiagnosticReport createFHIRDiagnosticReport(DiagnosticReport diagnosticReport) {
		List<CodingDt> codingList = diagnosticReport.getServiceCategory().getCoding();
		DiagnosticReportTemplate omrsDiagnosticReport = null;
		// If serviceCategory is not present in the DiagnosticReport, then use "DEFAULT"
		String handler = "DEFAULT";
		if (!codingList.isEmpty()) {
			handler = codingList.get(0).getCode();
		}
		omrsDiagnosticReport = FHIRDiagnosticReportUtil.generateOpenMRSDiagnosticReport(getHandler(handler), diagnosticReport);
		// Create resource in OpenMRS Database
		
		return FHIRDiagnosticReportUtil.generateFHIRDiagnosticReport(getHandler(handler), omrsDiagnosticReport);
	}
	
	/**
	 * @see org.openmrs.module.fhir.api.DiagnosticReportService#deleteDiagnosticReport(String)
	 */
	@Override
	public void deleteDiagnosticReport(String id) {
	}
	
	/****************************************************************
	 * Handler Implementation
	 ***************************************************************/
	@Override
	public DiagnosticReportHandler getHandler(String key) {
		return handlers.get(key);
	}
	
	@Override
	public void setHandlers(Map<String, DiagnosticReportHandler> newHandlers) throws APIException {
		if (newHandlers == null) {
			DiagnosticReportServiceImpl.setStaticHandlers(null);
			return;
		}
		for (Map.Entry<String, DiagnosticReportHandler> entry : newHandlers.entrySet()) {
			try {
	            FHIRDiagnosticReportUtil.getServiceCode(entry.getKey());
	            registerHandler(entry.getKey(), entry.getValue());
            }
            catch (InvalidNameException e) {
	            log.error("Unable to register Handler.", e);
            }
		}
	}
	
	/**
	 * Sets handlers using static method
	 *
	 * @param currentHandlers
	 */
	private static void setStaticHandlers(Map<String, DiagnosticReportHandler> currentHandlers) {
		DiagnosticReportServiceImpl.handlers = currentHandlers;
	}
	
	@Override
	public Map<String, DiagnosticReportHandler> getHandlers() throws APIException {
		if (handlers == null) {
			handlers = new LinkedHashMap<String, DiagnosticReportHandler>();
		}
		
		return handlers;
	}
	
	@Override
	public void registerHandler(String key, DiagnosticReportHandler handler) throws APIException {
		getHandlers().put(key, handler);
	}
	
	/**
	 * @see org.openmrs.api.DiagnosticReport#registerHandler(String, String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void registerHandler(String key, String handlerClass) throws APIException {
		try {
			Class loadedClass = OpenmrsClassLoader.getInstance().loadClass(handlerClass);
			registerHandler(key, (DiagnosticReportHandler) loadedClass.newInstance());
			
		}
		catch (Exception e) {
			throw new APIException("Unable.load.and.instantiate.handler", e);
		}
	}
	
	@Override
	public void removeHandler(String key) {
		handlers.remove(key);
	}
}
