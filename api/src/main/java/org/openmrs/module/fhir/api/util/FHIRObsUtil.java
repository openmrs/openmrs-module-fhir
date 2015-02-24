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

import ca.uhn.fhir.model.api.ExtensionDt;
import ca.uhn.fhir.model.dstu2.composite.AttachmentDt;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.composite.QuantityDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.dstu2.valueset.ObservationRelationshipTypeEnum;
import ca.uhn.fhir.model.dstu2.valueset.ObservationReliabilityEnum;
import ca.uhn.fhir.model.dstu2.valueset.ObservationStatusEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.InstantDt;
import ca.uhn.fhir.model.primitive.StringDt;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptNumeric;
import org.openmrs.EncounterProvider;
import org.openmrs.Obs;
import org.openmrs.PersonName;
import org.openmrs.api.context.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FHIRObsUtil {

	private static final Log log = LogFactory.getLog(FHIRObsUtil.class);

	public static Observation generateObs(Obs obs) {

		Observation observation = new Observation();
		//Set observation id
		observation.setId(obs.getUuid());

		//Set issued date
		InstantDt instant = new InstantDt();
		instant.setValue(obs.getDateCreated());
		observation.setIssued(instant);

		//Set fhir observation comment
		observation.setComments(obs.getComment());

		//Build and set patient reference
		ResourceReferenceDt patientReference = new ResourceReferenceDt();
		PersonName name = obs.getPerson().getPersonName();
		StringBuilder nameDisplay = new StringBuilder();
		nameDisplay.append(name.getGivenName());
		nameDisplay.append(" ");
		nameDisplay.append(name.getFamilyName());
		String uri;
		if (Context.getPatientService().getPatientByUuid(obs.getPerson().getUuid()) != null) {
			nameDisplay.append("(");
			nameDisplay.append(FHIRConstants.IDENTIFIER);
			nameDisplay.append(":");
			nameDisplay.append(Context.getPatientService().getPatientByUuid(obs.getPerson().getUuid())
					.getPatientIdentifier()
					.getIdentifier());
			nameDisplay.append(")");
			uri = FHIRConstants.PATIENT + "/" + obs.getPerson().getUuid();
		} else {
			uri = FHIRConstants.WEB_SERVICES_URI_PREFIX + "/" + FHIRConstants.PERSON + "/" + obs.getPerson().getUuid();
		}

		patientReference.setDisplay(nameDisplay.toString());
		IdDt patientRef = new IdDt();
		patientRef.setValue(uri);
		patientReference.setReference(patientRef);
		observation.setSubject(patientReference);

		//Set fhir performers from openmrs providers
		List<ResourceReferenceDt> performers = new ArrayList<ResourceReferenceDt>();
		if (obs.getEncounter() != null) {
			for (EncounterProvider provider : obs.getEncounter().getEncounterProviders()) {
				ResourceReferenceDt providerReference = new ResourceReferenceDt();
				StringBuilder providerNameDisplay = new StringBuilder();
				providerNameDisplay.append(provider.getProvider().getName());
				providerNameDisplay.append("(");
				providerNameDisplay.append(FHIRConstants.IDENTIFIER);
				providerNameDisplay.append(":");
				providerNameDisplay.append(provider.getProvider().getIdentifier());
				providerNameDisplay.append(")");
				providerReference.setDisplay(providerNameDisplay.toString());
				IdDt providerRef = new IdDt();
				String providerUri = FHIRConstants.PRACTITIONER + "/" + provider.getUuid();
				providerRef.setValue(providerUri);
				providerReference.setReference(providerRef);
				performers.add(providerReference);
			}
		}
		observation.setPerformer(performers);

		//Set concepts
		Collection<ConceptMap> mappings = obs.getConcept().getConceptMappings();
		CodeableConceptDt dt = observation.getName();
		List<CodingDt> dts = new ArrayList<CodingDt>();

		for (ConceptMap map : mappings) {
			//Set concept name as the display value and set concept uuid if name is empty
			if(map.getConceptReferenceTerm() != null) {
				String display = map.getConceptReferenceTerm().getName();
				if (display == null || display.isEmpty()) {
					display = map.getConceptReferenceTerm().getUuid();
				}

				//Set concept mappings of concept
				if (FHIRConstants.CIEL.equalsIgnoreCase(map.getConceptReferenceTerm().getName())) {
					dts.add(new CodingDt().setCode(map.getConceptReferenceTerm().getCode()).setDisplay(display).setSystem(
							FHIRConstants.ciel));
				} else if (FHIRConstants.SNOMED.equalsIgnoreCase(map.getConceptReferenceTerm().getName())) {
					dts.add(new CodingDt().setCode(map.getConceptReferenceTerm().getCode()).setDisplay(display).setSystem(
							FHIRConstants.snomed));
				} else if (FHIRConstants.LOINC.equalsIgnoreCase(map.getConceptReferenceTerm().getName())) {
					dts.add(new CodingDt().setCode(map.getConceptReferenceTerm().getCode()).setDisplay(display).setSystem(
							FHIRConstants.loinc));
				} else {
					dts.add(new CodingDt().setCode(map.getConceptReferenceTerm().getCode()).setDisplay(display).setSystem(
							FHIRConstants.other));
				}
			}
		}
		dt.setCoding(dts);

		if (obs.getConcept().isNumeric()) {
			ConceptNumeric cn = Context.getConceptService().getConceptNumeric(obs.getConcept().getId());
			QuantityDt quantity = new QuantityDt();
			quantity.setValue(obs.getValueNumeric());
			quantity.setSystem(FHIRConstants.NUMERIC_CONCEPT_MEASURE_URI);
			quantity.setUnits(cn.getUnits());
			quantity.setCode(cn.getUnits());
			observation.setValue(quantity);
			//Set high and low ranges
			List<Observation.ReferenceRange> referenceRanges = new ArrayList<Observation.ReferenceRange>();
			Observation.ReferenceRange referenceRange = new Observation.ReferenceRange();
			if (cn.getHiAbsolute() != null) {
				QuantityDt high = new QuantityDt();
				high.setUnits(cn.getUnits());
				high.setCode(cn.getUnits());
				high.setSystem(FHIRConstants.NUMERIC_CONCEPT_MEASURE_URI);
				high.setValue(cn.getHiAbsolute());
				referenceRange.setHigh(high);
			}
			if (cn.getLowAbsolute() != null) {
				QuantityDt low = new QuantityDt();
				low.setUnits(cn.getUnits());
				low.setCode(cn.getUnits());
				low.setSystem(FHIRConstants.NUMERIC_CONCEPT_MEASURE_URI);
				low.setValue(cn.getLowAbsolute());
				referenceRange.setHigh(low);
			}
			referenceRanges.add(referenceRange);
			observation.setReferenceRange(referenceRanges);

		} else if (FHIRConstants.ST_HL7_ABBREVATION.equalsIgnoreCase(obs.getConcept().getDatatype().getHl7Abbreviation())) {
			StringDt value = new StringDt();
			value.setValue(obs.getValueAsString(Context.getLocale()));
			observation.setValue(value);

		} else if (FHIRConstants.BIT_HL7_ABBREVATION.equalsIgnoreCase(obs.getConcept().getDatatype().getHl7Abbreviation()
		)) {
			CodeableConceptDt codeableConceptDt = new CodeableConceptDt();
			List<CodingDt> codingDts = new ArrayList<CodingDt>();
			CodingDt codingDt = new CodingDt();
			codingDt.setCode(obs.getValueCoded().getName().getName());
			codingDts.add(codingDt);
			codeableConceptDt.setCoding(codingDts);
			observation.setValue(codeableConceptDt);

		} else if (FHIRConstants.TS_HL7_ABBREVATION.equalsIgnoreCase(obs.getConcept().getDatatype().getHl7Abbreviation())) {
			PeriodDt datetime = new PeriodDt();
			DateTimeDt startDate = new DateTimeDt();
			startDate.setValue(obs.getValueDatetime());
			DateTimeDt endDate = new DateTimeDt();
			endDate.setValue(obs.getValueDatetime());
			datetime.setStart(startDate);
			datetime.setEnd(endDate);
			observation.setValue(datetime);

		} else if (FHIRConstants.DT_HL7_ABBREVATION.equalsIgnoreCase(obs.getConcept().getDatatype().getHl7Abbreviation())) {
			PeriodDt datetime = new PeriodDt();

			DateTimeDt startDate = new DateTimeDt();
			startDate.setValue(obs.getValueDate());
			DateTimeDt endDate = new DateTimeDt();
			endDate.setValue(obs.getValueDate());
			datetime.setStart(startDate);
			datetime.setEnd(endDate);
			observation.setValue(datetime);

		} else if (FHIRConstants.CWE_HL7_ABBREVATION.equalsIgnoreCase(obs.getConcept().getDatatype().getHl7Abbreviation())) {
			Collection<ConceptMap> valueMappings = obs.getValueCoded().getConceptMappings();
			List<CodingDt> values = new ArrayList<CodingDt>();
			for (ConceptMap map : valueMappings) {
				if(map.getConceptReferenceTerm() != null) {
					String display = map.getConceptReferenceTerm().getName();
					if (display == null || display.isEmpty()) {
						display = map.getConceptReferenceTerm().getUuid();
					}
					//Set concept mappings of concept
					if (FHIRConstants.CIEL.equalsIgnoreCase(map.getConceptReferenceTerm().getName())) {
						values.add(new CodingDt().setCode(map.getConceptReferenceTerm().getCode()).setDisplay(display)
								.setSystem(
										FHIRConstants.ciel));
					} else if (FHIRConstants.SNOMED.equalsIgnoreCase(map.getConceptReferenceTerm().getName())) {
						values.add(new CodingDt().setCode(map.getConceptReferenceTerm().getCode()).setDisplay(display)
								.setSystem(
										FHIRConstants.snomed));
					} else if (FHIRConstants.LOINC.equalsIgnoreCase(map.getConceptReferenceTerm().getName())) {
						values.add(new CodingDt().setCode(map.getConceptReferenceTerm().getCode()).setDisplay(display)
								.setSystem(
										FHIRConstants.loinc));
					} else {
						values.add(new CodingDt().setCode(map.getConceptReferenceTerm().getCode()).setDisplay(display)
								.setSystem(
										FHIRConstants.other));
					}
				}
			}
			CodeableConceptDt codeableConceptDt = new CodeableConceptDt();
			codeableConceptDt.setCoding(values);
			observation.setValue(codeableConceptDt);
		} else if (FHIRConstants.ED_HL7_ABBREVATION.equalsIgnoreCase(obs.getConcept().getDatatype().getHl7Abbreviation())) {
			AttachmentDt attachmentDt = new AttachmentDt();
			attachmentDt.setUrl(obs.getValueComplex());
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ObjectOutputStream os = null;
			try {
				os = new ObjectOutputStream(out);
				os.writeObject(obs.getComplexData().getData());
			} catch (IOException e) {
				log.error("Error while converting object data to stream");
				attachmentDt.setData(out.toByteArray());
			}
			observation.setValue(attachmentDt);
		} else {
			StringDt value = new StringDt();
			value.setValue(obs.getValueAsString(Context.getLocale()));
			observation.setValue(value);
		}

		observation.setStatus(ObservationStatusEnum.FINAL);
		observation.setReliability(ObservationReliabilityEnum.OK);

		DateTimeDt dateApplies = new DateTimeDt();
		dateApplies.setValue(obs.getObsDatetime());
		observation.setApplies(dateApplies);

		//Set reference observations
		if (obs.getGroupMembers() != null && !obs.getGroupMembers().isEmpty()) {
			List<Observation.Related> relatedObs = new ArrayList<Observation.Related>();
			ResourceReferenceDt resourceReferenceDt;
			Observation.Related related;
			for (Obs ob : obs.getGroupMembers()) {
				related = new Observation.Related();
				related.setType(ObservationRelationshipTypeEnum.HAS_COMPONENT);
				resourceReferenceDt = new ResourceReferenceDt();
				resourceReferenceDt.setDisplay(ob.getConcept().getName().getName());
				IdDt providerRef = new IdDt();
				String obsUri = FHIRConstants.OBSERVATION + "/" + obs.getUuid();
				providerRef.setValue(obsUri);
				resourceReferenceDt.setReference(providerRef);
				related.setTarget(resourceReferenceDt);
			}
			observation.setRelated(relatedObs);
		}

		if(obs.getLocation() != null) {
			StringDt location = new StringDt();
			location.setValue(FHIRConstants.LOCATION + "/" + obs.getLocation().getUuid());
			ExtensionDt locationExt = new ExtensionDt(false, FHIRConstants.LOCATION_EXTENTION_URI, location);
			observation.addUndeclaredExtension(locationExt);
		}

		if (obs.getEncounter() != null) {
			StringDt encounter = new StringDt();
			encounter.setValue(FHIRConstants.ENCOUNTER + "/" + obs.getLocation().getUuid());
			ExtensionDt encounterExt = new ExtensionDt(false, FHIRConstants.ENCOUNTER_EXTENTION_URI, encounter);
			observation.addUndeclaredExtension(encounterExt);
		}
		return observation;
	}
}
