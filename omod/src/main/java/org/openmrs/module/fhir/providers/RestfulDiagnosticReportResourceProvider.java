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
package org.openmrs.module.fhir.providers;

import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu2.resource.DiagnosticReport;
import ca.uhn.fhir.model.dstu2.resource.OperationOutcome;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.Delete;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.server.IResourceProvider;

import org.openmrs.module.fhir.resources.FHIRDiagnosticReportResource;

public class RestfulDiagnosticReportResourceProvider implements IResourceProvider {

	private FHIRDiagnosticReportResource diagnosticReportResource;

	public RestfulDiagnosticReportResourceProvider() {
		diagnosticReportResource = new FHIRDiagnosticReportResource();
	}

	@Override
	public Class<? extends IResource> getResourceType() {
		return DiagnosticReport.class;
	}

	/**
	 * Create Diagnostic Report
	 *
	 * @param diagnosticReport FHIR Diagnostic Report object
	 * @return This method returns a list of Diagnostic Reports. This list may contain multiple
	 * matching resources, or it may also be empty.
	 */
	@Create
	public MethodOutcome createFHIRDiagnosticReport(@ResourceParam DiagnosticReport diagnosticReport) {
		System.out.println("DiagnosticReport: createFHIRDiagnosticReport");
		diagnosticReport = diagnosticReportResource.createFHIRDiagnosticReport(diagnosticReport);
		MethodOutcome retVal = new MethodOutcome();
		retVal.setId(new IdDt("DiagnosticReport", diagnosticReport.getId().getIdPart()));
		// retVal.setId(new IdDt("DiagnosticReport", "2889127246021897"));
		OperationOutcome outcome = new OperationOutcome();
		outcome.addIssue().setDetails("Diagnostic Report is successfully created");
		retVal.setOperationOutcome(outcome);
		return retVal;
	}

	/**
	 * The "@Read" annotation indicates that this method supports the read operation. Read
	 * operations should return a single resource instance.
	 *
	 * @param theId The read operation takes one parameter, which must be of type IdDt and must be
	 *              annotated with the "@Read.IdParam" annotation.
	 * @return Returns a resource matching this identifier, or null if none exists.
	 */
	@Read()
	public DiagnosticReport getResourceById(@IdParam IdDt theId) {
		System.out.println("DiagnosticReport : getResourceByID");
		DiagnosticReport result = null;
		result = diagnosticReportResource.getByUniqueId(theId);
		return result;
	}

	/**
	 * Delete person by unique id
	 *
	 * @param theId object containing the id
	 */
	@Delete()
	public void retireDiagnosticReport(@IdParam IdDt theId) {
		diagnosticReportResource.retireDiagnosticReport(theId);
	}
}
