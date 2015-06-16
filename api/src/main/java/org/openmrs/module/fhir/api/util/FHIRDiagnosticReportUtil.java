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

import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.DiagnosticReport;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.module.fhir.api.diagnosticreport.DiagnosticReportHandler;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.naming.InvalidNameException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FHIRDiagnosticReportUtil {

	private static final Log log = LogFactory.getLog(FHIRDiagnosticReportUtil.class);

	/**
	 * Generate a FHIR Diagnostic Report for a given OpenMRS Diagnostic Report
	 * (Encounter)
	 * Note: Since this method is generate a FHIR Diagnostic Report, it'll
	 * only calling to GET method in given Handler
	 *
	 * @param omrsDiagnosticReport OpenMRS Diagnostic Report (Encounter)
	 * @param handler              An implementation of DiagnosticReportHandler
	 * @return An instance of ca.uhn.fhir.model.dstu2.resource.DiagnosticReport
	 */
	public static DiagnosticReport generateFHIRDiagnosticReport(Encounter omrsDiagnosticReport, DiagnosticReportHandler
			handler) {
		DiagnosticReport diagnosticReport = new DiagnosticReport();
		// Get Obs and set as `Name`
		// Get Obs and set as `Status`
		// Get EncounterDateTime and set as `Issued` date
		// Get Encounter Patient and set as `Subject`
		// Get Encounter Provider and set as `Performer`
		// Get EncounterType and Set `ServiceCategory`
		// Get valueDateTime in Obs and Set `Diagnosis[x]->DateTime`
		// Get valueDateTime in Obs and Set `Diagnosis[x]->Period`

		// ObsSet set as `Result`
		// Binary Obs Handler

		return diagnosticReport;
	}

	/**
	 * Generate a OpenMRS Diagnostic Report (Encounter) for a given FHIR
	 * Diagnostic Report
	 * Note: Since this method is generating a OpenMRS Diagnostic Report,
	 * it'll only calling to CREATE method in give Handler
	 *
	 * @param diagnosticReport FHIR Diagnostic Report
	 * @param handler          An implementation of DiagnosticReportHandler
	 * @return An instance of org.openmrs.Encounter
	 */
	public static Encounter generateOpenMRSDiagnosticReport(DiagnosticReport diagnosticReport, DiagnosticReportHandler
			handler) {
		Encounter omrsDiagnosticReport = new Encounter();

		//Set ID if available
		if (diagnosticReport.getId() != null) {
			omrsDiagnosticReport.setUuid(diagnosticReport.getId().getIdPart());
		}
		// Set `Name` as a Obs
		// Set `Status` as a Obs
		// Set `Issued` date as EncounterDateTime
		// Set `Subject` as Encounter Patient
		// Set `Performer` as Encounter Provider
		// Set `ServiceCategory` as EncounterType
		// Set `Diagnosis[x]->DateTime` as valueDateTime in an Obs
		// Set `Diagnosis[x]->Period` as valueDateTime in an Obs

		// Set parsed obsSet (`Result` as Set of Obs)
		// Set Binary Obs Handler which used to store `PresentedForm`

		return omrsDiagnosticReport;
	}

	/**
	 * Update a OpenMRS Diagnostic Report (Encounter) with a given FHIR
	 * Diagnostic Report
	 * Note: Since this method is updating a OpenMRS Diagnostic Report,
	 * it'll only calling to UPDATE method in given Handler
	 *
	 * @param diagnosticReport     FHIR Diagnostic Report
	 * @param omrsDiagnosticReport OpenMRS Diagnostic Report (Encounter)
	 * @param handler              An implementation of DiagnosticReportHandler
	 * @return An instance of org.openmrs.Encounter
	 */
	public static Encounter updateOpenMRSDiagnosticReport(DiagnosticReport diagnosticReport, Encounter omrsDiagnosticReport,
	                                                      DiagnosticReportHandler handler) {
		// Update `Name` as a Obs
		// Update `Status` as a Obs
		// Update `Issued` date as EncounterDateTime
		// Update `Subject` as Encounter Patient
		// Update `Performer` as Encounter Provider
		// Update `ServiceCategory` as EncounterType
		// Update `Diagnosis[x]->DateTime` as valueDateTime in an Obs
		// Update `Diagnosis[x]->Period` as valueDateTime in an Obs

		// Update parsed obsSet (`Result` as Set of Obs)
		// Update Binary Obs Handler which used to store `PresentedForm`

		return omrsDiagnosticReport;
	}

	/**
	 * Delete a OpenMRS Diagnostic Report (Encounter)
	 * Note: It'll only calling to DELETE method in give Handler
	 *
	 * @param omrsDiagnosticReport FHIR Diagnostic Report
	 * @param handler              An implementation of DiagnosticReportHandler
	 * @return An instance of org.openmrs.Encounter
	 */
	public static Encounter deleteOpenMRSDiagnosticReport(Encounter omrsDiagnosticReport, DiagnosticReportHandler
			handler) {
		// Delete `Name` Obs
		// Delete `Status` Obs

		// Delete Obs (`Result` as Set of Obs)
		// Delete Binary Obs Handler which used to store `PresentedForm`

		return omrsDiagnosticReport;
	}

	public static List<Obs> getOpenMRSObs(DiagnosticReport diagnosticReport) {
		List<Obs> obsSet = new ArrayList<Obs>();
		for (ResourceReferenceDt reference : diagnosticReport.getResult()) {
			if (reference.getReference().isLocal()) {
				Observation obs = (Observation) reference.getResource();
				// obsSet.add(FHIRObsUtil.generateOpenMRSObs(obs, new ArrayList<String>()));
			} else {
				getOpenMRSObs(reference);
			}
		}
		return obsSet;
	}

	private static Obs getOpenMRSObs(ResourceReferenceDt reference) {
		if (reference.getReference().isLocal()) {
			Observation obs = (Observation) reference.getResource();
			return FHIRObsUtil.generateOpenMRSObs(obs, new ArrayList<String>());
		} else {
			return getExternalObsResource(reference);
		}
	}

	private static Obs getExternalObsResource(ResourceReferenceDt reference) {
		log.warn("Doesn't implement fetch external resources.", new NotImplementedException());
		return new Obs();
	}

	public static String getServiceCode(String handlerName) throws InvalidNameException {
		HashMap<String, String> diagnosticServices = new HashMap<String, String>();
		diagnosticServices.put("LaboratoryHandler", "LAB");
		diagnosticServices.put("RadiologyHandler", "RAD");
		diagnosticServices.put("BloodBankHandler", "BLB");
		diagnosticServices.put("CATScanHandler", "CT");

		if (diagnosticServices.containsKey(handlerName)) {
			return diagnosticServices.get(handlerName);
		} else {
			throw new InvalidNameException(
					"<Handler Name should be one of 'Description' value in http://hl7.org/fhir/v2/0074/> + Handler");
		}
	}

}
