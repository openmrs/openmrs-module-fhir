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
package org.openmrs.module.fhir.api.util;

import ca.uhn.fhir.model.dstu2.resource.DiagnosticReport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.fhir.api.diagnosticreport.DiagnosticReportHandler;

public class FHIRDiagnosticReportUtil {

	private static final Log log = LogFactory.getLog(FHIRDiagnosticReportUtil.class);

	/**
	 * Get matching FHIR Diagnostic Report
	 *
	 * @param id      ID of the Diagnostic Report to be search
	 * @param handler An implementation of DiagnosticReportHandler
	 * @return An instance of ca.uhn.fhir.model.dstu2.resource.DiagnosticReport
	 */
	public static DiagnosticReport getFHIRDiagnosticReport(String id, DiagnosticReportHandler
			handler) {
		return handler.getFHIRDiagnosticReportById(id);
	}

	/**
	 * Save FHIR Diagnostic Report
	 *
	 * @param diagnosticReport FHIR Diagnostic Report
	 * @param handler          An implementation of DiagnosticReportHandler
	 * @return An instance of ca.uhn.fhir.model.dstu2.resource.DiagnosticReport
	 */
	public static DiagnosticReport saveDiagnosticReport(DiagnosticReport diagnosticReport, DiagnosticReportHandler
			handler) {
		return handler.saveFHIRDiagnosticReport(diagnosticReport);
	}

	/**
	 * Delete given FHIR Diagnostic Report
	 *
	 * @param diagnosticReport FHIR Diagnostic Report
	 * @param handler          An implementation of DiagnosticReportHandler
	 * @return An instance of org.openmrs.Encounter
	 */
	public static DiagnosticReport purgeDiagnosticReport(DiagnosticReport diagnosticReport, DiagnosticReportHandler
			handler) {
		return handler.purgeFHIRDiagnosticReport(diagnosticReport);
	}

}
