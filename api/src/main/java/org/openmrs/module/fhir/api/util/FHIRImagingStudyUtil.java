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

import ca.uhn.fhir.model.dstu2.composite.AttachmentDt;
import ca.uhn.fhir.model.dstu2.resource.ImagingStudy;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.primitive.Base64BinaryDt;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptComplex;
import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.obs.ComplexData;
import org.openmrs.util.OpenmrsConstants;

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
		String patientId = imagingStudy.getPatient().getReference().getIdPart();
		org.openmrs.Patient omrsPatient = getOpenMRSPatient(patientId);
		// Create Obs for store `ImagingStudy`
		Obs omrsImagingStudy = new Obs(omrsPatient, conceptImagingStudy, started, null);

		// Set `uid` as uuid of Obs
		omrsImagingStudy.setUuid(imagingStudy.getId().getIdPart());
		// Set `accession` as AccessionNumber
		if (imagingStudy.getAccession() != null) {
			String value = imagingStudy.getAccession().getValue();
			omrsImagingStudy.setAccessionNumber(value);
		}
		// Set `numberOfSeries` = Number of Series Obs
		// Set `url` and `numberOfInstance` as Obs Value (Text)
		String value = "";
		if (imagingStudy.getUrl() != null) {
			value += "url:".concat(imagingStudy.getUrl() + ",");
		}
		value += "numberOfInstance:".concat(imagingStudy.getNumberOfInstances().toString());
		omrsImagingStudy.setValueText(value);
		// Set `clinicalInformation` and `description` as Obs comment
		String comment = "";
		if (imagingStudy.getClinicalInformation() != null) {
			comment += "clinicalInformation:".concat(imagingStudy.getClinicalInformation() + ",");
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
		for (ImagingStudy.Series series : imagingStudy.getSeries()) {
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
				StringBuilder errorMessage = new StringBuilder(
						"The request cannot be processed due to the following issues\n");
				for (int i = 0; i < errors.size(); i++) {
					errorMessage.append(i + 1).append(" : ").append(errors.get(i)).append("\n");
				}
				throw new UnprocessableEntityException(errorMessage.toString());
			}
		} else {
			return omrsPatient;
		}
	}

	public static Obs generateOpenMRSSeriesObs(ImagingStudy.Series series, org.openmrs.Patient omrsPatient) {
		Concept conceptSeries = FHIRUtils.getImagingStudySeriesConcept();
		// Set `dateTime` as Obs DateTime
		Date dateTime = new Date();
		if (series.getDateTime() != null) {
			dateTime = series.getDateTime();// Will not be null
		}
		// Create a Obs for store Series
		Obs omrsSeries = new Obs(omrsPatient, conceptSeries, dateTime, null);

		String value = "";
		// Set `number` as Obs Value (Text)
		if (series.getNumber() != null) {
			value += "number:".concat(series.getNumber().toString() + ",");
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
		if (series.getUrl() != null) {
			value += "url:".concat(series.getUrl() + ",");
		}

		// Set All values
		if (!"".equals(value)) {
			omrsSeries.setValueText(value);
		}
		// Set `instance` as Obs group
		ObsService obsService = Context.getObsService();
		for (ImagingStudy.SeriesInstance instance : series.getInstance()) {
			Obs omrsInstance = generateOpenMRSInstanceObs(instance, omrsPatient, dateTime);
			omrsInstance = obsService.saveObs(omrsInstance, null);
			omrsSeries.addGroupMember(omrsInstance);
		}
		return omrsSeries;
	}

	public static Obs generateOpenMRSInstanceObs(ImagingStudy.SeriesInstance instance, org.openmrs.Patient omrsPatient,
	                                             Date dateTime) {
		Concept conceptInstance = FHIRUtils.getImagingStudySeriesInstanceConcept();
		Obs omrsInstance = new Obs(omrsPatient, conceptInstance, dateTime, null);
		String value = "";
		// Set `number` as Obs Value (Text)
		if (instance.getNumber() != null) {
			value += "number:".concat(instance.getNumber().toString() + ",");
		}
		// Set `uid` as Obs uuid
		omrsInstance.setUuid(instance.getUid());

		// Set All values
		if (!"".equals(value)) {
			omrsInstance.setValueText(value);
		}
		// Set `content` as Complex Obs
		for (AttachmentDt attachment : instance.getContent()) {
			int conceptId = FHIRUtils.getImagingStudySeriesInstanceContentConcept().getConceptId();
			if (attachment.getCreation() == null) {
				attachment.setCreation(new DateTimeDt(new Date()));
			}
			Obs complexObs = saveComplexData(conceptId, omrsPatient, attachment);
			omrsInstance.addGroupMember(complexObs);
		}
		return omrsInstance;
	}

	public static Obs saveComplexData(int complexConceptId, org.openmrs.Patient patient, AttachmentDt attachment) {
		Person person = Context.getPersonService().getPersonByUuid(patient.getUuid());
		ConceptComplex conceptComplex = Context.getConceptService().getConceptComplex(complexConceptId);

		Obs complexObs = new Obs(person, conceptComplex, attachment.getCreation(), null);
		// If data is not given, set some sample data
		if (attachment.getData() == null) {
			byte[] bytes = "Test Complex Data".getBytes();
			Base64BinaryDt base64BinaryDt = new Base64BinaryDt(bytes);
			attachment.setData(base64BinaryDt);
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
		return Context.getObsService().getComplexObs(obsId, OpenmrsConstants.RAW_VIEW);
	}

}
