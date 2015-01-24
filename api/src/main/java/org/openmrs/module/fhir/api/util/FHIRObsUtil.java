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

import ca.uhn.fhir.model.dstu.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu.composite.CodingDt;
import ca.uhn.fhir.model.dstu.composite.PeriodDt;
import ca.uhn.fhir.model.dstu.composite.QuantityDt;
import ca.uhn.fhir.model.dstu.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu.resource.Observation;
import ca.uhn.fhir.model.dstu.valueset.ObservationReliabilityEnum;
import ca.uhn.fhir.model.dstu.valueset.ObservationStatusEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.InstantDt;
import ca.uhn.fhir.model.primitive.StringDt;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptNumeric;
import org.openmrs.EncounterProvider;
import org.openmrs.Obs;
import org.openmrs.PersonName;
import org.openmrs.api.context.Context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FHIRObsUtil {

	public static Observation generateObs(Obs obs) {

		Observation observation = new Observation();
		observation.setId(obs.getUuid());

		InstantDt instant = new InstantDt();
		instant.setValue(obs.getDateCreated());

		observation.setIssued(instant);

		observation.setComments(obs.getComment());

		ResourceReferenceDt patientReference = new ResourceReferenceDt();

		PersonName name = Context.getPatientService().getPatientByUuid(obs.getPerson().getUuid()).getPersonName();
		String nameDisplay = name.getGivenName() + " " + name.getFamilyName();
		nameDisplay += "(" + Context.getPatientService().getPatientByUuid(obs.getPerson().getUuid()).getPatientIdentifier()
				.getIdentifier() + ")";

		patientReference.setDisplay(nameDisplay);
		String patientUri = Context.getAdministrationService().getGlobalProperty("webservices.rest.uriPrefix")
		                    + "/ws/rest/v1/fhirpatient/" + obs.getPerson().getUuid();

		IdDt patientRef = new IdDt();
		patientRef.setValue(patientUri);
		patientReference.setReference(patientRef);

		observation.setSubject(patientReference);

		List<ResourceReferenceDt> performers = new ArrayList<ResourceReferenceDt>();

		if (obs.getEncounter() != null) {

			for (EncounterProvider provider : obs.getEncounter().getEncounterProviders()) {
				ResourceReferenceDt providerReference = new ResourceReferenceDt();
				providerReference.setDisplay(
						provider.getProvider().getName() + "(" + provider.getProvider().getProviderId() + ")");
				IdDt providerRef = new IdDt();
				String providerUri = Context.getAdministrationService().getSystemVariables().get("OPENMRS_HOSTNAME")
				                     + "/ws/rest/v1/fhirprovider/" + provider.getUuid();

				providerRef.setValue(providerUri);
				providerReference.setReference(providerRef);

				performers.add(providerReference);
			}
		}

		observation.setPerformer(performers);

		Collection<ConceptMap> mappings = obs.getConcept().getConceptMappings();
		CodeableConceptDt dt = observation.getName();
		List<CodingDt> dts = new ArrayList<CodingDt>();

		for (ConceptMap map : mappings) {

			String display = map.getConceptReferenceTerm().getName();
			if (display == null) {
				display = map.getConceptReferenceTerm().getUuid();
			}

			if (map.getSource().getName().equals("LOINC")) {
				dts.add(new CodingDt().setCode(map.getConceptReferenceTerm().getCode()).setDisplay(display).setSystem(
						FHIRConstants.loinc));
			}
			if (map.getSource().getName().equals("SNOMED")) {
				dts.add(new CodingDt().setCode(map.getConceptReferenceTerm().getCode()).setDisplay(display).setSystem(
						FHIRConstants.snomed));
			}
			if (map.getSource().getName().equals("CIEL")) {
				dts.add(new CodingDt().setCode(map.getConceptReferenceTerm().getCode()).setDisplay(display).setSystem(
						FHIRConstants.ciel));
			}

			dt.setCoding(dts);
		}

		if (obs.getConcept().isNumeric()) {
			ConceptNumeric cn = Context.getConceptService().getConceptNumeric(obs.getConcept().getId());

			QuantityDt q = new QuantityDt();

			q.setValue(obs.getValueNumeric());
			q.setSystem("http://unitsofmeasure.org");
			q.setUnits(cn.getUnits());
			q.setCode(cn.getUnits());

			observation.setValue(q);

		}

		if (obs.getConcept().getDatatype().getHl7Abbreviation().equals("ST")) {
			StringDt value = new StringDt();
			value.setValue(obs.getValueAsString(Context.getLocale()));
			observation.setValue(value);

		}

		if (obs.getConcept().getDatatype().getHl7Abbreviation().equals("BIT")) {
			CodeableConceptDt codeableConceptDt = new CodeableConceptDt();

			List<CodingDt> codingDts = new ArrayList<CodingDt>();
			CodingDt codingDt = new CodingDt();

			codingDts.add(codingDt);

			codeableConceptDt.setCoding(codingDts);
			observation.setValue(codeableConceptDt);

		}

		if (obs.getConcept().getDatatype().getHl7Abbreviation().equals("TS")) {
			PeriodDt datetime = new PeriodDt();

			DateTimeDt startDate = new DateTimeDt();
			startDate.setValue(obs.getValueDatetime());
			DateTimeDt endDate = new DateTimeDt();
			endDate.setValue(obs.getValueDatetime());

			datetime.setStart(startDate);
			datetime.setEnd(endDate);
			observation.setValue(datetime);
		}

		if (obs.getConcept().getDatatype().getHl7Abbreviation().equals("DT")) {
			PeriodDt datetime = new PeriodDt();

			DateTimeDt startDate = new DateTimeDt();
			startDate.setValue(obs.getValueDate());
			DateTimeDt endDate = new DateTimeDt();
			endDate.setValue(obs.getValueDate());

			datetime.setStart(startDate);
			datetime.setEnd(endDate);
			observation.setValue(datetime);

		}

		if (obs.getConcept().getDatatype().getHl7Abbreviation().equals("CWE")) {

			Collection<ConceptMap> valueMappings = obs.getValueCoded().getConceptMappings();

			List<CodingDt> values = new ArrayList<CodingDt>();

			for (ConceptMap map : valueMappings) {

				String display = map.getConceptReferenceTerm().getName();
				if (display == null) {
					display = map.getConceptReferenceTerm().toString();
				}

				if (map.getSource().getName().equals("LOINC")) {
					values.add(new CodingDt().setCode(map.getConceptReferenceTerm().getCode()).setDisplay(display)
							.setSystem(
									FHIRConstants.loinc));
				}
				if (map.getSource().getName().equals("SNOMED")) {
					values.add(new CodingDt().setCode(map.getConceptReferenceTerm().getCode()).setDisplay(display)
							.setSystem(
									FHIRConstants.snomed));
				}
				if (map.getSource().getName().equals("CIEL")) {
					values.add(new CodingDt().setCode(map.getConceptReferenceTerm().getCode()).setDisplay(display)
							.setSystem(
									FHIRConstants.ciel));
				} else {
					String uri = Context.getAdministrationService().getGlobalProperty("webservices.rest.uriPrefix")
					             + "/ws/rest/v1/fhirconceptsource/" + map.getSource().getUuid();
					dts.add(new CodingDt().setCode(map.getConceptReferenceTerm().getCode()).setDisplay(display).setSystem(
							uri));

				}

			}

			CodeableConceptDt codeableConceptDt = new CodeableConceptDt();
			codeableConceptDt.setCoding(values);
			observation.setValue(codeableConceptDt);

		}

		observation.setStatus(ObservationStatusEnum.FINAL);
		observation.setReliability(ObservationReliabilityEnum.OK);

		DateTimeDt dateApplies = new DateTimeDt();
		dateApplies.setValue(obs.getObsDatetime());
		observation.setApplies(dateApplies);

		return observation;

	}
}
