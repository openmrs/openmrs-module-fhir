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

import ca.uhn.fhir.model.dstu2.composite.AddressDt;
import ca.uhn.fhir.model.dstu2.composite.HumanNameDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.DiagnosticReport;
import ca.uhn.fhir.model.dstu2.resource.Person;
import ca.uhn.fhir.model.dstu2.valueset.AddressUseEnum;
import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
import ca.uhn.fhir.model.dstu2.valueset.NameUseEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.StringDt;

import org.openmrs.Patient;
import org.openmrs.PersonAddress;
import org.openmrs.PersonName;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.diagnosticreport.DiagnosticReportHandler;
import org.openmrs.module.fhir.api.diagnosticreport.DiagnosticReportTemplate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static java.lang.String.valueOf;

public class FHIRDiagnosticReportUtil {
	
	private static Map<String, DiagnosticReportHandler> handlers = null;
	
	public static DiagnosticReport generateFHIRDiagnosticReport(String handler, DiagnosticReportTemplate omrsDiagnosticReport) {
		return handlers.get(handler).generateFHIRDiagnosticReport(omrsDiagnosticReport);
	}
	
	public static DiagnosticReportTemplate generateOpenMRSDiagnosticReport(String handler, DiagnosticReport fhirDiagnosticReport) {
		return handlers.get(handler).generateOpenMRSDiagnosticReport(fhirDiagnosticReport);
	}
	
	public DiagnosticReportHandler getHandler(String key) {
		return handlers.get(key);
	}
	
	public Map<String, DiagnosticReportHandler> getHandlers() throws APIException {
		if (handlers == null) {
			handlers = new LinkedHashMap<String, DiagnosticReportHandler>();
		}
		
		return handlers;
	}
	
	public void registerHandler(String key, DiagnosticReportHandler handler) throws APIException {
		getHandlers().put(key, handler);
	}
	
	public void removeHandler(String key) {
		handlers.remove(key);
	}
}
