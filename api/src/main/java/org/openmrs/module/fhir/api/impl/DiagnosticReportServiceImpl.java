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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.DiagnosticReport;
import org.openmrs.Encounter;
import org.openmrs.Order;
import org.openmrs.api.APIException;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.fhir.api.DiagnosticReportService;
import org.openmrs.module.fhir.api.db.FHIRDAO;
import org.openmrs.module.fhir.api.diagnosticreport.DiagnosticReportHandler;
import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.openmrs.module.fhir.api.util.FHIRDiagnosticReportUtil;
import org.openmrs.util.OpenmrsClassLoader;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * It is a default implementation of {@link org.openmrs.module.fhir.api.DiagnosticReportService}.
 */
public class DiagnosticReportServiceImpl extends BaseOpenmrsService implements DiagnosticReportService {

	private static final String FHIR_DIAGNOSTICREPORT_ORDER_TYPE_TO_HANDLER_MAP = "fhir.diagnosticreport.orderTypeToHandlerMap";

	private static final String DEFAULT_HANDLER = "DEFAULT";

	private static Map<String, DiagnosticReportHandler> handlers = null;

	protected final Log log = LogFactory.getLog(this.getClass());

	private FHIRDAO dao;

	private Map<String, String> orderTypeToHandlerNameMap  = new HashMap<>();

	public DiagnosticReportServiceImpl() {
		orderTypeToHandlerNameMap.put("Test Order", "LAB");
		orderTypeToHandlerNameMap.put("Default", DEFAULT_HANDLER);
	}

	//TODO: find a better way to do this!!
	private void loadHandlerMap() {
		String orderTypeHandlerMapText = Context.getAdministrationService().getGlobalProperty(FHIR_DIAGNOSTICREPORT_ORDER_TYPE_TO_HANDLER_MAP);
		if (!StringUtils.isEmpty(orderTypeHandlerMapText)) {
			String[] parts = orderTypeHandlerMapText.trim().split(",");
			for (String part : parts) {
				if (!StringUtils.isEmpty(part)) {
					String[] handlerMap = part.trim().split("=");
					orderTypeToHandlerNameMap.put(handlerMap[0].trim(), handlerMap[1].trim());
				}
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
	public DiagnosticReport getDiagnosticReport(final String accessionNumber) {
		loadHandlerMap();
		List<Order> orders = dao.getOrdersByAccessionNumber(accessionNumber);
		if (!orders.isEmpty()) {
			Order order = orders.get(0);

			String handlerName = orderTypeToHandlerNameMap.get(order.getOrderType().getName());
			if (StringUtils.isEmpty(handlerName)) {
				handlerName = orderTypeToHandlerNameMap.get(DEFAULT_HANDLER);
			}
			return FHIRDiagnosticReportUtil.getFHIRDiagnosticReport(order.getUuid(), getHandler(handlerName));
		} else {
			throw new APIException("Can not identify order by accession number");
		}
	}


	@Override
	public DiagnosticReport createFHIRDiagnosticReport(DiagnosticReport diagnosticReport) {
		log.debug("DiagnosticReportServiceImpl : create FHIRDiagnostic Report");
		List<Coding> codingList = diagnosticReport.getCategory().getCoding();

		// If serviceCategory is not present in the DiagnosticReport, then use "DEFAULT"
		String handlerName = FHIRConstants.DEFAULT;
		if (!codingList.isEmpty()) {
			handlerName = codingList.get(0).getCode();
		}

		return FHIRDiagnosticReportUtil.saveDiagnosticReport(diagnosticReport, getHandler(handlerName));
	}

	@Override
	public DiagnosticReport updateFHIRDiagnosticReport(DiagnosticReport diagnosticReport, String theId) {
		log.debug("DiagnosticReportServiceImpl : updateFHIRDiagnosticReport with ID " + theId);
		// Find Diagnostic Report (Encounter) in OpenMRS database
		EncounterService encounterService = Context.getEncounterService();
		Encounter omrsDiagnosticReport = encounterService.getEncounterByUuid(theId);
		// Get corresponding Handler
		String handlerName = omrsDiagnosticReport.getEncounterType().getName();

		return FHIRDiagnosticReportUtil.updateDiagnosticReport(diagnosticReport, theId, getHandler(
				handlerName));
	}

	/**
	 * @see org.openmrs.module.fhir.api.DiagnosticReportService#retireDiagnosticReport(String)
	 */
	@Override
	public void retireDiagnosticReport(String id) {
		// Find Diagnostic Report (Encounter) in OpenMRS database
		EncounterService encounterService = Context.getEncounterService();
		Encounter omrsDiagnosticReport = encounterService.getEncounterByUuid(id);
		// Get corresponding Handler
		String handlerName = omrsDiagnosticReport.getEncounterType().getName();

		FHIRDiagnosticReportUtil.retireDiagnosticReport(id, getHandler(handlerName));
	}

	/**
	 * @see org.openmrs.module.fhir.api.DiagnosticReportService#getDiagnosticReportByPatientNameAndServiceCategory(String,
	 * String)
	 */
	@Override
	public List<DiagnosticReport> getDiagnosticReportByPatientNameAndServiceCategory(String patientName, String
			serviceCode) {
		if (serviceCode == null) {
			// Get DEFAULT Handler
			serviceCode = "DEFAULT";
		}

		return FHIRDiagnosticReportUtil.getFHIRDiagnosticReportBySubjectName(patientName, getHandler(serviceCode));
	}

	/****************************************************************
	 * Handler Implementation
	 ***************************************************************/
	@Override
	public DiagnosticReportHandler getHandler(String key) {
		try {
			return handlers.get(key);
		} catch (NullPointerException e) {
			throw new APIException("Could not find handler for order type " + key, e);
		}
	}

	@Override
	public Map<String, DiagnosticReportHandler> getHandlers() throws APIException {
		if (handlers == null) {
			handlers = new LinkedHashMap<>();
		}

		return handlers;
	}

	@Override
	public void setHandlers(Map<String, DiagnosticReportHandler> newHandlers) throws APIException {
		if (newHandlers == null) {
			DiagnosticReportServiceImpl.setStaticHandlers(null);
			return;
		}
		for (Map.Entry<String, DiagnosticReportHandler> entry : newHandlers.entrySet()) {
			registerHandler(entry.getValue().getServiceCategory(), entry.getValue());

		}
	}

	@Override
	public void registerHandler(String key, DiagnosticReportHandler handler) throws APIException {
		getHandlers().put(key, handler);
	}

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
