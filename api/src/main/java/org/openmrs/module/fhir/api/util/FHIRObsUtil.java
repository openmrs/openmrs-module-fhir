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

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptNumeric;
import org.openmrs.EncounterProvider;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.obs.ComplexData;

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
		observation.setSubject(FHIRUtils.buildPatientOrPersonResourceReference(obs.getPerson()));
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
		CodeableConceptDt dt = observation.getCode();
		List<CodingDt> dts = new ArrayList<CodingDt>();

		//Set codings from openmrs concept mappings
		for (ConceptMap map : mappings) {
			dts.add(FHIRUtils.getCodingDtByConceptMappings(map));
		}
		//Set openmrs concept
		dts.add(FHIRUtils.getCodingDtByOpenMRSConcept(obs.getConcept()));
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
			
		} else if (FHIRConstants.BIT_HL7_ABBREVATION.equalsIgnoreCase(obs.getConcept().getDatatype().getHl7Abbreviation())) {
			CodeableConceptDt codeableConceptDt = new CodeableConceptDt();
			List<CodingDt> codingDts = new ArrayList<CodingDt>();
			CodingDt codingDt = new CodingDt();
			codingDt.setCode(obs.getValueAsBoolean().toString()); // fixed by sashrika
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
			if (obs.getValueCoded() != null) {
				Collection<ConceptMap> valueMappings = obs.getValueCoded().getConceptMappings();
				List<CodingDt> values = new ArrayList<CodingDt>();
				//Set codings from openmrs concept mappings
				for (ConceptMap map : valueMappings) {
					if (map.getConceptReferenceTerm() != null) {
						values.add(FHIRUtils.getCodingDtByConceptMappings(map));
					}
				}
				//Set openmrs concept
				values.add(FHIRUtils.getCodingDtByOpenMRSConcept(obs.getValueCoded()));
				CodeableConceptDt codeableConceptDt = new CodeableConceptDt();
				codeableConceptDt.setCoding(values);
				observation.setValue(codeableConceptDt);
			}
		} else if (FHIRConstants.ED_HL7_ABBREVATION.equalsIgnoreCase(obs.getConcept().getDatatype().getHl7Abbreviation())) {
			AttachmentDt attachmentDt = new AttachmentDt();
			attachmentDt.setUrl(FHIRConstants.COMPLEX_DATA_URL + obs.getId());
			//ByteArrayOutputStream out = new ByteArrayOutputStream();
			/*ObjectOutputStream os = null;
			try {
				os = new ObjectOutputStream(out);
				os.writeObject(obs.getComplexData().getData());
			}
			catch (IOException e) {
				log.error("Error while converting object data to stream");
				attachmentDt.setData(out.toByteArray());
			}*/
			attachmentDt.setData(obs.getValueComplex().getBytes());
			observation.setValue(attachmentDt);
		} else {
			StringDt value = new StringDt();
			value.setValue(obs.getValueAsString(Context.getLocale()));
			observation.setValue(value);
		}

		observation.setStatus(ObservationStatusEnum.FINAL);
		observation.setReliability(ObservationReliabilityEnum.OK);

		DateTimeDt dateIssued = new DateTimeDt();
        	dateIssued.setValue(obs.getObsDatetime());
		observation.setApplies(dateIssued);

		//Set reference observations
		List<Observation.Related> relatedObs = null;
		if (obs.getGroupMembers() != null && !obs.getGroupMembers().isEmpty()) {
			relatedObs = new ArrayList<Observation.Related>();
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
				relatedObs.add(related);
			}
		}
		//Set old Obs
		if (obs.getPreviousVersion() != null) {
			if (relatedObs == null) {
				relatedObs = new ArrayList<Observation.Related>();
			}

			ResourceReferenceDt resourceReferenceDt = new ResourceReferenceDt();
			Observation.Related related = new Observation.Related();
			related.setType(ObservationRelationshipTypeEnum.REPLACES);
			resourceReferenceDt.setDisplay("Old Obs which replaced by the new Obs");
			IdDt providerRef = new IdDt();
			String obsUri = FHIRConstants.OBSERVATION + "/" + obs.getPreviousVersion().getUuid();
			providerRef.setValue(obsUri);
			resourceReferenceDt.setReference(providerRef);
			related.setTarget(resourceReferenceDt);
			relatedObs.add(related);
			//throw new NullPointerException();
		}
		observation.setRelated(relatedObs);

		//As per discussions, obs location will be deprecated from openmrs. So it will no need of setting it
		/*if (obs.getLocation() != null) {
			StringDt location = new StringDt();
			location.setValue(FHIRConstants.LOCATION + "/" + obs.getLocation().getUuid());
			ExtensionDt locationExt = new ExtensionDt(false, FHIRConstants.LOCATION_EXTENTION_URI, location);
			observation.addUndeclaredExtension(locationExt);
		}*/

		if (obs.getEncounter() != null) {
			ResourceReferenceDt encounter = new ResourceReferenceDt();
			encounter.setReference(FHIRConstants.ENCOUNTER + "/" + obs.getEncounter().getUuid());
			observation.setEncounter(encounter);
		}

		return observation;
	}
	
	public static Obs generateOpenMRSObs(Observation observation, List<String> errors) {
		Obs obs = new Obs();
		
		obs.setComment(observation.getComments());
		if (observation.getSubject() != null) {
			ResourceReferenceDt subjectref = observation.getSubject();
			IdDt id = subjectref.getReference();
			String patientUuid = id.getIdPart();
			obs.setPerson(Context.getPersonService().getPersonByUuid(patientUuid));
		} else {
			errors.add("Subject cannot be null");
		}
		
		DateTimeDt dateApplies = (DateTimeDt) observation.getApplies();
		obs.setObsDatetime(dateApplies.getValue());
		
		Date instant = observation.getIssued();
		obs.setDateCreated(instant);
		
		String conceptCode = null;
		String system = null;
		Concept concept = null;
		List<CodingDt> dts = null;
		try {
			CodeableConceptDt dt = observation.getCode();
			dts = dt.getCoding();
		}
		catch (NullPointerException e) {
			errors.add("Code cannot be empty");
			log.error("Code cannot be empty " + e.getMessage());
		}
		
		for (CodingDt cding : dts) {
			conceptCode = cding.getCode();
			system = cding.getSystem();
			if (FHIRConstants.OPENMRS_URI.equals(system)) {
				concept = Context.getConceptService().getConceptByUuid(conceptCode);
			} else {
				String systemName = FHIRConstants.conceptSourceURINameMap.get(system);
				if (systemName != null && !systemName.isEmpty()) {
					concept = Context.getConceptService().getConceptByMapping(conceptCode, systemName);
				}
			}
			if (concept != null) {
				break;
			}
		}
		if (concept == null) {
			errors.add("No matching concept found for the given codings");
		} else {
			obs.setConcept(concept);
		}

		if (concept != null) {
			if (concept.isNumeric()) {
				QuantityDt quantity = (QuantityDt) observation.getValue();
				BigDecimal bd = quantity.getValue();
				double doubleValue = bd.doubleValue();
				obs.setValueNumeric(doubleValue);
			} else if (FHIRConstants.ST_HL7_ABBREVATION.equalsIgnoreCase(concept.getDatatype().getHl7Abbreviation())) {
				StringDt value = (StringDt) observation.getValue();
				try {
					obs.setValueAsString(value.getValue());
				}
				catch (ParseException e) {
					errors.add("Obs set value failed");
					log.error("Obs set value failed " + e.getMessage());
				}
			} else if (FHIRConstants.BIT_HL7_ABBREVATION.equalsIgnoreCase(concept.getDatatype().getHl7Abbreviation())) {
				CodeableConceptDt codeableConceptDt = (CodeableConceptDt) observation.getValue();
				try {
					List<CodingDt> codingDts = codeableConceptDt.getCoding();
					CodingDt codingDt2 = codingDts.get(0);
					boolean booleanValue = Boolean.parseBoolean(codingDt2.getCode());
					obs.setValueBoolean(booleanValue);
				}
				catch (NullPointerException e) {
					errors.add("Setting valueBoolean failed");
					log.error("Setting valueBoolean failed " + e.getMessage());
				}
			} else if (FHIRConstants.TS_HL7_ABBREVATION.equalsIgnoreCase(concept.getDatatype().getHl7Abbreviation())) {
				PeriodDt datetime = (PeriodDt) observation.getValue();
				obs.setValueDatetime(datetime.getStart());

			} else if (FHIRConstants.DT_HL7_ABBREVATION.equalsIgnoreCase(concept.getDatatype().getHl7Abbreviation())) {
				PeriodDt datetime = (PeriodDt) observation.getValue();
				obs.setValueDate(datetime.getStart());
			} else if (FHIRConstants.ED_HL7_ABBREVATION.equalsIgnoreCase(concept.getDatatype().getHl7Abbreviation())) {
				AttachmentDt attachmentDt = (AttachmentDt) observation.getValue();
				byte[] byteStream = attachmentDt.getData();
				ComplexData data = new ComplexData("images.JPEG", byteStream);
				obs.setValueComplex(byteStream.toString());
				obs.setComplexData(data);
			}
			
		}
		
		if (observation.getEncounter() != null) {
			ResourceReferenceDt encounter = observation.getEncounter();
			IdDt ref = encounter.getReference();
			String encounterUuid = ref.getIdPart();
			obs.setEncounter(Context.getEncounterService().getEncounterByUuid(encounterUuid));
		}
		return obs;
	}
	
	public static Obs copyObsAttributes(Obs requestObs, Obs retrievedObs, List<String> errors) {
		retrievedObs.setPerson(requestObs.getPerson());
		retrievedObs.setObsDatetime(requestObs.getObsDatetime());
		retrievedObs.setConcept(requestObs.getConcept());
		Concept concept=requestObs.getConcept();
		if (concept != null) {
			if (requestObs.getConcept().isNumeric()) {
				retrievedObs.setValueNumeric(requestObs.getValueNumeric());
			} else if (FHIRConstants.ST_HL7_ABBREVATION.equalsIgnoreCase(concept.getDatatype().getHl7Abbreviation())) {
				try {
					retrievedObs.setValueAsString(requestObs.getValueAsString(Context.getLocale()));
				}
				catch (ParseException e) {
					errors.add("Couldn't set value as String to the Observation. Caused " + e.getMessage());
				}
			} else if (FHIRConstants.BIT_HL7_ABBREVATION.equalsIgnoreCase(concept.getDatatype().getHl7Abbreviation())) {
				retrievedObs.setValueCoded(requestObs.getValueCoded());
			} else if (FHIRConstants.TS_HL7_ABBREVATION.equalsIgnoreCase(concept.getDatatype().getHl7Abbreviation())) {
					retrievedObs.setValueDatetime(requestObs.getValueDatetime());
			} else if (FHIRConstants.DT_HL7_ABBREVATION.equalsIgnoreCase(concept.getDatatype().getHl7Abbreviation())) {
				retrievedObs.setValueDate(requestObs.getValueDate());
			} else if (FHIRConstants.ED_HL7_ABBREVATION.equalsIgnoreCase(concept.getDatatype().getHl7Abbreviation())) {
				//TBD
			}
	    }
		retrievedObs.setComment(requestObs.getComment());
		return retrievedObs;
	}
	
}
