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

import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hl7.fhir.dstu3.model.Attachment;
import org.hl7.fhir.dstu3.model.ImagingStudy;
import org.hl7.fhir.dstu3.model.Patient;
import org.openmrs.Concept;
import org.openmrs.ConceptComplex;
import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.obs.ComplexData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FHIRImagingStudyUtil {

	protected static final Log log = LogFactory.getLog(FHIRImagingStudyUtil.class);

	public static ImagingStudy generateImagingStudy(Obs imagingStudyObs) {
		return new ImagingStudy();
	}

	public static Obs generateOpenMRSImagingStudy(ImagingStudy imagingStudy, List<String> errors) {
		Concept conceptImagingStudy = FHIRUtils.getDiagnosticReportImagingStudyConcept();
		// Set `started` as Obs DateTime
		Date started = new Date();
		if (imagingStudy.getStarted() != null) {
			started = imagingStudy.getStarted();
		}
		// Set `patient` as Obs Person
		String patientId = imagingStudy.getPatient().getId();
		org.openmrs.Patient omrsPatient = getOpenMRSPatient(patientId);
		// Create Obs for store `ImagingStudy`
		Obs omrsImagingStudy = new Obs(omrsPatient, conceptImagingStudy, started, null);

		// Set `uid` as uuid of Obs
		omrsImagingStudy.setUuid(imagingStudy.getId());
		// Set `accession` as AccessionNumber
		if (imagingStudy.getAccession() != null) {
			String value = imagingStudy.getAccession().getValue();
			omrsImagingStudy.setAccessionNumber(value);
		}
		// Set `numberOfSeries` = Number of Series Obs
		// Set `url` and `numberOfInstance` as Obs Value (Text)
		String value = "";
		if (imagingStudy.getUid() != null) {
			value += "url:".concat(imagingStudy.getUid() + ",");
		}
		value += "numberOfInstance:".concat(Integer.toString(imagingStudy.getNumberOfInstances()));
		omrsImagingStudy.setValueText(value);
		// Set `clinicalInformation` and `description` as Obs comment
		String comment = "";
		if (imagingStudy.getDescriptionElement() != null) {
			comment += "clinicalInformation:".concat(imagingStudy.getDescriptionElement().toString() + ",");
		}
		if (imagingStudy.getDescription() != null) {
			comment += "description:".concat(imagingStudy.getDescription());
		}
		if (!"".equals(comment)) {
			omrsImagingStudy.setComment(comment);
		}

		// Set All values
		if (!"".equals(value)) {
			omrsImagingStudy.setValueText(value);
		}
		// Set `series` as Obs group
		ObsService obsService = Context.getObsService();
		for (ImagingStudy.ImagingStudySeriesComponent series : imagingStudy.getSeries()) {
			Obs omrsSeries = generateOpenMRSSeriesObs(series, omrsPatient);
			omrsSeries = obsService.saveObs(omrsSeries, null);
			omrsImagingStudy.addGroupMember(omrsSeries);
		}
		return omrsImagingStudy;
	}

	/**
	 * Check whether there is a Patient in the Database for given uuid. If it's existing, then retrieve it, otherwise
	 * retrieve from third party server, save as a new Patient and return it back.
	 *
	 * @param uuid Uuid or FHIR Patient Id
	 * @return Patient Resultant OpenMRS Patient
	 * TODO: Current implementation is using Patient ID. But that may change from System to System as mentioned in
	 * http://hl7.org/fhir/2015May/resource.html#identifiers. It's better to use Identifiers.
	 */
	private static org.openmrs.Patient getOpenMRSPatient(String uuid) {
		//Check whether Patient is already exist
		org.openmrs.Patient omrsPatient = Context.getPatientService().getPatientByUuid(uuid);
		if (omrsPatient == null) {
			// If Patient isn't in the Database, retrieve it
			String serverBase = FHIRUtils.getDiagnosticReportRadiologyBaseServerURL();
			Patient patient = FHIRRESTfulGenericClient.readPatientById(serverBase, uuid);
			List<String> errors = new ArrayList<String>();
			omrsPatient = FHIRPatientUtil.generateOmrsPatient(patient, errors);
			if (errors.isEmpty()) {
				// Save the retrieved Patient
				omrsPatient = Context.getPatientService().savePatient(omrsPatient);
				return omrsPatient;
			} else {
				String errorMessage = ErrorUtil.generateErrorMessage(errors, "The request cannot be processed due to the following issues\n");
				throw new UnprocessableEntityException(errorMessage);
			}
		} else {
			return omrsPatient;
		}
	}

	public static Obs generateOpenMRSSeriesObs(ImagingStudy.ImagingStudySeriesComponent series, org.openmrs.Patient omrsPatient) {
		Concept conceptSeries = FHIRUtils.getImagingStudySeriesConcept();
		// Set `dateTime` as Obs DateTime
		Date dateTime = new Date();
		if (series.getStartedElement() != null) {
			dateTime = series.getStartedElement().getValue();// Will not be null
		}
		// Create a Obs for store Series
		Obs omrsSeries = new Obs(omrsPatient, conceptSeries, dateTime, null);

		String value = "";
		// Set `number` as Obs Value (Text)
		if (series.getNumber() != 0) {
			value += "number:".concat(series.getNumber() + ",");
		}
		// Set `modality` as Obs Value (Text)
		value += "modality:".concat(series.getModality() + ",");
		// Set `uid` as Obs uuid
		omrsSeries.setUuid(series.getUid());
		// Set `description` as Obs Comment
		if (series.getDescription() != null) {
			omrsSeries.setComment(series.getDescription());
		}
		// `numberOfInstance` = Number of Instance Obs
		// Set `url` as Obs Value (Text)
		if (series.getUid() != null) {
			value += "url:".concat(series.getUid() + ",");
		}

		// Set All values
		if (!"".equals(value)) {
			omrsSeries.setValueText(value);
		}
		// Set `instance` as Obs group
		ObsService obsService = Context.getObsService();
		for (ImagingStudy.ImagingStudySeriesInstanceComponent instance : series.getInstance()) {
			Obs omrsInstance = generateOpenMRSInstanceObs(instance, omrsPatient, dateTime);
			omrsInstance = obsService.saveObs(omrsInstance, null);
			omrsSeries.addGroupMember(omrsInstance);
		}
		return omrsSeries;
	}

	public static Obs generateOpenMRSInstanceObs(ImagingStudy.ImagingStudySeriesInstanceComponent instance, org.openmrs.Patient omrsPatient,
	                                             Date dateTime) {
		Concept conceptInstance = FHIRUtils.getImagingStudySeriesInstanceConcept();
		Obs omrsInstance = new Obs(omrsPatient, conceptInstance, dateTime, null);
		String value = "";
		// Set `number` as Obs Value (Text)
		if (instance.getNumber() != 0) {
			value += "number:".concat(instance.getNumber() + ",");
		}
		// Set `uid` as Obs uuid
		omrsInstance.setUuid(instance.getUid());

		// Set All values
		if (!"".equals(value)) {
			omrsInstance.setValueText(value);
		}
		return omrsInstance;
	}

	public static Obs saveComplexData(int complexConceptId, org.openmrs.Patient patient, Attachment attachment) {
		Person person = Context.getPersonService().getPersonByUuid(patient.getUuid());
		ConceptComplex conceptComplex = Context.getConceptService().getConceptComplex(complexConceptId);

		Obs complexObs = new Obs(person, conceptComplex, attachment.getCreation(), null);
		// If data is not given, set some sample data
		if (attachment.getData() == null) {
			byte[] bytes = "Test Complex Data".getBytes();
			attachment.setData(bytes);
		}
		if(attachment.getTitle() == null) {
			attachment.setTitle("Title");
		}
		ComplexData complexData = new ComplexData(attachment.getTitle(), attachment.getData());
		/**
		 * TODO: Not available in OpenMRS 1.10.0 version
		 * complexData.setMimeType(attachment.getContentType());
		 * complexData.setLength(attachment.getSize().longValue());
		 */
		complexObs.setComplexData(complexData);
		if (attachment.getUrl() != null) {
			complexObs.setComment(attachment.getUrl());
		}
		Context.getObsService().saveObs(complexObs, null);

		Integer obsId = complexObs.getObsId();
		return Context.getObsService().getComplexObs(obsId, "RAW_VIEW");
	}

}
