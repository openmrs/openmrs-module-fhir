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

import ca.uhn.fhir.model.dstu2.resource.DiagnosticReport;
import org.openmrs.api.APIException;
import org.openmrs.module.fhir.api.diagnosticreport.DiagnosticReportHandler;

import java.util.List;
import java.util.Map;

public interface DiagnosticReportService {

	/**
	 * Get FHIR Diagnostic Report resource by uuid
	 *
	 * @param id uuid of the Diagnostic Report
	 * @return FHIR Diagnostic Report resource and will return null if patient not found for the
	 * given id
	 */
	DiagnosticReport getDiagnosticReport(String id);

	/**
	 * Creates OpenMRS objects from FHIR Diagnostic Report
	 *
	 * @param diagnosticReport FHIR Diagnostic Report
	 * @return Created FHIR Diagnostic Report
	 */
	DiagnosticReport createFHIRDiagnosticReport(DiagnosticReport diagnosticReport);

	/**
	 * Updates OpenMRS objects from FHIR Diagnostic Report
	 *
	 * @param diagnosticReport FHIR Diagnostic Report
	 * @param theId The ID of Stored Diagnostic Report to be updated
	 * @return Updated FHIR Diagnostic Report
	 */
	DiagnosticReport updateFHIRDiagnosticReport(DiagnosticReport diagnosticReport, String theId);

	/**
	 * Delete Diagnostic Report by id
	 *
	 * @param id uuid of the Diagnostic Report
	 * @return Diagnostic Report FHIR resource
	 */
	void retireDiagnosticReport(String id);

	/**
	 * Get FHIR Diagnostic Report resource by Patient Name and Service Category
	 *
	 * @param patientName Patient name of the Diagnostic Report
	 * @param service     Service Category of the Diagnostic Report
	 */
	List<DiagnosticReport> getDiagnosticReportByPatientNameAndServiceCategory(String patientName, String service);

	/**
	 * Get the DiagnosticReportHandler that has been registered with the given key
	 *
	 * @param key that has been registered with a handler class
	 * @return Object representing the handler for the given key
	 */
	DiagnosticReportHandler getHandler(String key) throws APIException;

	/**
	 * <u>Add</u> the given map to this service's handlers. This method registers each
	 * DiagnosticReportHandler to this service. If the given String key exists, that handler is
	 * overwritten with the given handler For most situations, this map is set via spring, see the
	 * moduleApplicationContext.xml file to add more handlers.
	 *
	 * @param handlers Map of class to handler object
	 * @throws APIException
	 */
	void setHandlers(Map<String, DiagnosticReportHandler> handlers) throws APIException;

	/**
	 * Gets the handlers map registered
	 *
	 * @return map of keys to handlers
	 * @throws APIException
	 * @should never return null
	 */
	Map<String, DiagnosticReportHandler> getHandlers() throws APIException;

	/**
	 * Registers the given handler with the given key If the given String key exists, that handler
	 * is overwritten with the given handler
	 *
	 * @param key     the key name to use for this handler
	 * @param handler the class to register with this key
	 * @throws APIException
	 */
	void registerHandler(String key, DiagnosticReportHandler handler) throws APIException;

	/**
	 * Convenience method for {@link #registerHandler(String, DiagnosticReportHandler)}
	 *
	 * @param key          the key name to use for this handler
	 * @param handlerClass the class to register with this key
	 * @throws APIException
	 * @should load handler and register key
	 */
	void registerHandler(String key, String handlerClass) throws APIException;

	/**
	 * Remove the handler associated with the key from list of available handlers
	 *
	 * @param key the key of the handler to unregister
	 * @should remove handler with matching key
	 * @should not fail with invalid key
	 */
	void removeHandler(String key) throws APIException;
}
