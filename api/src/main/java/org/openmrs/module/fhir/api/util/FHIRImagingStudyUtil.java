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
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import org.openmrs.Concept;
import org.openmrs.ConceptComplex;
import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.FHIR;
import org.openmrs.obs.ComplexData;
import org.openmrs.util.OpenmrsConstants;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FHIRImagingStudyUtil {

	public static ImagingStudy generateImagingStudy(Obs imagingStudyObs) {
		return new ImagingStudy();
	}

	public static Obs generateOpenMRSImagingStudy(ImagingStudy imagingStudy, List<String> errors) {
		Concept conceptImagingStudy = FHIRUtils.getDiagnosticReportImagingStudyConcept();
		// Set `started` as Obs DateTime
		Date started = imagingStudy.getStarted();// Since it'll not return null
		// Set `patient` as Obs Person
		String patientId = imagingStudy.getPatient().getReference().getIdPart();
		org.openmrs.Patient omrsPatient = getOpenMRSPatient(patientId);
		// Create Obs for store `ImagingStudy`
		Obs omrsImagingStudy = new Obs(omrsPatient, conceptImagingStudy, started, null);

		// Set `uid` as uuid of Obs
		omrsImagingStudy.setUuid(imagingStudy.getId().getIdPart());
		// Set `accession` as AccessionNumber
		if (!imagingStudy.getAccession().isEmpty()) {
			String value = imagingStudy.getAccession().getValue();
			omrsImagingStudy.setAccessionNumber(value);
		}
		// Set `numberOfSeries` = Number of Series Obs
		// Set `url` and `numberOfInstance` as Obs Value (Text)
		String value = "";
		value += "url:".concat(imagingStudy.getUrl() + ",");
		value += "numberOfInstance:".concat(imagingStudy.getNumberOfInstances().toString());
		omrsImagingStudy.setValueText(value);
		// Set `clinicalInformation` and `description` as Obs comment
		String comment = "";
		comment += "clinicalInformation:".concat(imagingStudy.getClinicalInformation() + ",");
		comment += "description:".concat(imagingStudy.getDescription());
		omrsImagingStudy.setComment(comment);

		// Set All values
		omrsImagingStudy.setValueText(value);
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
		Date dateTime = series.getDateTime();// Will not be null
		// Create a Obs for store Series
		Obs omrsSeries = new Obs(omrsPatient, conceptSeries, dateTime, null);

		String value = "";
		// Set `number` as Obs Value (Text)
		value += "number:".concat(series.getNumber().toString() + ",");
		// Set `modality` as Obs Value (Text)
		value += "modality:".concat(series.getModality() + ",");
		// Set `uid` as Obs uuid
		omrsSeries.setUuid(series.getUid());
		// Set `description` as Obs Comment
		omrsSeries.setComment(series.getDescription());
		// `numberOfInstance` = Number of Instance Obs
		// Set `url` as Obs Value (Text)
		value += "url:".concat(series.getUrl() + ",");

		// Set All values
		omrsSeries.setValueText(value);
		// Set `instance` as Obs group
		ObsService obsService = Context.getObsService();
		for (ImagingStudy.SeriesInstance instance : series.getInstance()) {
			Obs omrsInstance = generateOpenMRSInstanceObs(instance, omrsPatient);
			omrsInstance = obsService.saveObs(omrsInstance, null);
			omrsSeries.addGroupMember(omrsInstance);
		}
		return omrsSeries;
	}

	public static Obs generateOpenMRSInstanceObs(ImagingStudy.SeriesInstance instance, org.openmrs.Patient omrsPatient) {
		Concept conceptInstance = FHIRUtils.getImagingStudySeriesInstanceConcept();
		Obs omrsInstance = new Obs();
		omrsInstance.setPerson(omrsPatient);
		String value = "";
		// Set `number` as Obs Value (Text)
		value += "number:".concat(instance.getNumber().toString() + ",");
		// Set `uid` as Obs uuid
		omrsInstance.setUuid(instance.getUid());

		// Set All values
		omrsInstance.setValueText(value);
		// Set `content` as Complex Obs
		for (AttachmentDt attachment : instance.getContent()) {
			int conceptId = FHIRUtils.getImagingStudySeriesInstanceContentConcept().getConceptId();
			if (attachment.getCreation() == null) {
				attachment.setCreation(new DateTimeDt());
			}
			System.out.println("1111111111111 size " + attachment.getSize());
			System.out.println("1111111111111 data " + attachment.getData());
			System.out.println("1111111111111 data length " + attachment.getData().length);
			//Obs complexObs = saveComplexData(conceptId, omrsPatient, attachment);
			//omrsInstance.addGroupMember(complexObs);
		}
		return omrsInstance;
	}

	public static Obs saveComplexData(int complexConceptId, org.openmrs.Patient patient, AttachmentDt attachment) {
		Person person = Context.getPersonService().getPersonByUuid(patient.getUuid());
		ConceptComplex conceptComplex = Context.getConceptService().getConceptComplex(complexConceptId);

		Obs complexObs = new Obs(person, conceptComplex, attachment.getCreation(), null);
		ComplexData complexData = new ComplexData(attachment.getTitle(), attachment.getData());
		attachment.getSize();
		/**
		 * TODO: Not available in OpenMRS 1.10.0 version
		 * complexData.setMimeType(attachment.getContentType());
		 * complexData.setLength(attachment.getSize().longValue());
		 */
		complexObs.setComplexData(complexData);
		Context.getObsService().saveObs(complexObs, null);

		Integer obsId = complexObs.getObsId();
		return Context.getObsService().getComplexObs(obsId, OpenmrsConstants.RAW_VIEW);
	}

}