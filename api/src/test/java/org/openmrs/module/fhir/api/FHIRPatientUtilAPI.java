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
package org.openmrs.module.fhir.api;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.Bundle;
import ca.uhn.fhir.model.api.BundleEntry;
import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu.composite.AddressDt;
import ca.uhn.fhir.model.dstu.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu.composite.CodingDt;
import ca.uhn.fhir.model.dstu.composite.HumanNameDt;
import ca.uhn.fhir.model.dstu.composite.NarrativeDt;
import ca.uhn.fhir.model.dstu.composite.QuantityDt;
import ca.uhn.fhir.model.dstu.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu.resource.Observation;
import ca.uhn.fhir.model.dstu.resource.Patient;
import ca.uhn.fhir.model.dstu.valueset.AdministrativeGenderCodesEnum;
import ca.uhn.fhir.model.dstu.valueset.IdentifierUseEnum;
import ca.uhn.fhir.model.dstu.valueset.ObservationReliabilityEnum;
import ca.uhn.fhir.model.dstu.valueset.ObservationStatusEnum;
import ca.uhn.fhir.model.primitive.BooleanDt;
import ca.uhn.fhir.model.primitive.DateDt;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.InstantDt;
import ca.uhn.fhir.model.primitive.StringDt;
import ca.uhn.fhir.narrative.DefaultThymeleafNarrativeGenerator;
import ca.uhn.fhir.parser.IParser;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptNumeric;
import org.openmrs.EncounterProvider;
import org.openmrs.Obs;
import org.openmrs.PatientIdentifier;
import org.openmrs.PersonAddress;
import org.openmrs.PersonName;
import org.openmrs.api.context.Context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class FHIRPatientUtilAPI {

    /*public static void generatePatient(DelegatingResourceDescription description){
        Map<String, DelegatingResourceDescription.Property> map = description.getProperties();

        DelegatingResourceDescription.Property value = map.get("person");


    }*/

	public String generatePatient(org.openmrs.Patient omrsPatient) {
		Patient patient = new Patient();
		patient.setId(omrsPatient.getUuid());

		for (PatientIdentifier identifier : omrsPatient.getIdentifiers()) {
			String uri = Context.getAdministrationService().getSystemVariables().get("OPENMRS_HOSTNAME")
			             + "/ws/rest/v1/patientidentifiertype/" + identifier.getIdentifierType().getUuid();
			if (identifier.isPreferred()) {

				patient.addIdentifier().setUse(IdentifierUseEnum.USUAL).setSystem(uri).setValue(identifier.getIdentifier())
						.setLabel(identifier.getIdentifierType().getName());
			} else {
				patient.addIdentifier().setUse(IdentifierUseEnum.OFFICIAL).setSystem(uri).setValue(
						identifier.getIdentifier()).setLabel(identifier.getIdentifierType().getName());
			}
		}

		List<HumanNameDt> humanNameDts = new ArrayList<HumanNameDt>();

		for (PersonName name : omrsPatient.getNames()) {

			HumanNameDt fhirName = new HumanNameDt();
			StringDt familyName = new StringDt();
			familyName.setValue(name.getFamilyName());
			List<StringDt> familyNames = new ArrayList<StringDt>();
			familyNames.add(familyName);

			fhirName.setFamily(familyNames);

			StringDt givenName = new StringDt();
			givenName.setValue(name.getGivenName());
			List<StringDt> givenNames = new ArrayList<StringDt>();
			givenNames.add(givenName);

			fhirName.setGiven(givenNames);

			if (name.getFamilyNameSuffix() != null) {
				StringDt suffix = fhirName.addSuffix();
				suffix.setValue(name.getFamilyNameSuffix());
			}

			if (name.getPrefix() != null) {
				StringDt prefix = fhirName.addPrefix();
				prefix.setValue(name.getPrefix());
			}

			humanNameDts.add(fhirName);

		}

		patient.setName(humanNameDts);

		if (omrsPatient.getGender().equals("M")) {
			patient.setGender(AdministrativeGenderCodesEnum.M);
		}
		if (omrsPatient.getGender().equals("F")) {
			patient.setGender(AdministrativeGenderCodesEnum.F);
		}

		List<AddressDt> fhirAddresses = patient.getAddress();

		for (PersonAddress address : omrsPatient.getAddresses()) {

			AddressDt fhirAddress = new AddressDt();
			fhirAddress.setCity(address.getCityVillage());
			fhirAddress.setCountry(address.getCountry());

			List<StringDt> addressStrings = new ArrayList<StringDt>();

			addressStrings.add(new StringDt(address.getAddress1()));
			addressStrings.add(new StringDt(address.getAddress2()));
			addressStrings.add(new StringDt(address.getAddress3()));
			addressStrings.add(new StringDt(address.getAddress4()));
			addressStrings.add(new StringDt(address.getAddress5()));

			fhirAddress.setLine(addressStrings);
			fhirAddresses.add(fhirAddress);

		}

		NarrativeDt dt = new NarrativeDt();

		patient.setText(dt);
		patient.setAddress(fhirAddresses);

		DateTimeDt fhirBirthDate = patient.getBirthDate();
		fhirBirthDate.setValue(omrsPatient.getBirthdate());

		patient.setActive(!omrsPatient.isVoided());

		FhirContext ctx = new FhirContext();
		// ctx.setNarrativeGenerator(new DefaultThymeleafNarrativeGenerator());

		IParser jsonParser = ctx.newJsonParser();
		jsonParser.setPrettyPrint(true);
		String encoded = jsonParser.encodeResourceToString(patient);
		return encoded;

	}

	public static String generateObs(Obs obs, String contentType) {

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
		String patientUri = "http://" + Context.getAdministrationService().getSystemVariables().get("OPENMRS_HOSTNAME")
		                    + "/ws/rest/v1/fhirpatient/" + obs.getPerson().getUuid();

		//patientReference.setReference("ref");
		IdDt patientRef = new IdDt();
		patientRef.setValue(patientUri);
		patientReference.setReference(patientRef);

		observation.setSubject(patientReference);

		List<ResourceReferenceDt> performers = new ArrayList<ResourceReferenceDt>();

		for (EncounterProvider provider : obs.getEncounter().getEncounterProviders()) {
			ResourceReferenceDt providerReference = new ResourceReferenceDt();
			providerReference.setDisplay(
					provider.getProvider().getName() + "(" + provider.getProvider().getProviderId() + ")");
			//patientReference.setReference("ref");
			IdDt providerRef = new IdDt();
			String providerUri = "http://" + Context.getAdministrationService().getSystemVariables().get("OPENMRS_HOSTNAME")
			                     + "/ws/rest/v1/fhirprovider/" + provider.getUuid();

			providerRef.setValue(providerUri);
			providerReference.setReference(providerRef);

			performers.add(providerReference);
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
						Constants.loinc));
			}
			if (map.getSource().getName().equals("SNOMED")) {
				dts.add(new CodingDt().setCode(map.getConceptReferenceTerm().getCode()).setDisplay(display).setSystem(
						Constants.snomed));
			}
			if (map.getSource().getName().equals("CIEL")) {
				dts.add(new CodingDt().setCode(map.getConceptReferenceTerm().getCode()).setDisplay(display).setSystem(
						Constants.ciel));
			} else {
				String uri = "http://" + Context.getAdministrationService().getSystemVariables().get("OPENMRS_HOSTNAME")
				             + "/ws/rest/v1/fhirconceptsource/" + map.getSource().getUuid();
				dts.add(new CodingDt().setCode(map.getConceptReferenceTerm().getCode()).setDisplay(
						map.getSource().toString()).setSystem(uri));

			}

		}
		dt.setCoding(dts);

		if (obs.getConcept().isNumeric()) {

			obs.getConcept().isNumeric();
			((ConceptNumeric) obs.getConcept()).getHiCritical();

			QuantityDt q = new QuantityDt();

			q.setValue(obs.getValueNumeric());

			q.setSystem("http://unitsofmeasure.org");

			// q.setCode(obs.get);

			observation.getReferenceRangeFirstRep().setLow(((ConceptNumeric) obs.getConcept()).getLowCritical());

			observation.getReferenceRangeFirstRep().setHigh(((ConceptNumeric) obs.getConcept()).getHiCritical());

			observation.setValue(q);

		}

		if (obs.getConcept().getDatatype().getHl7Abbreviation().equals("ST")) {
			StringDt value = new StringDt();
			value.setValue(obs.getValueAsString(Context.getLocale()));
			observation.setValue(value);

		}

		if (obs.getConcept().getDatatype().getHl7Abbreviation().equals("BIT")) {
			BooleanDt value = new BooleanDt();
			value.setValue(obs.getValueBoolean());
			observation.setValue(value);

		}

		if (obs.getConcept().getDatatype().getHl7Abbreviation().equals("TS")) {
			DateTimeDt datetime = new DateTimeDt();
			datetime.setValue(obs.getValueDatetime());
			observation.setValue(datetime);
		}

		if (obs.getConcept().getDatatype().getHl7Abbreviation().equals("DT")) {
			DateDt date = new DateDt();
			date.setValue(obs.getValueDate());
			observation.setValue(date);
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
									Constants.loinc));
				}
				if (map.getSource().getName().equals("SNOMED")) {
					values.add(new CodingDt().setCode(map.getConceptReferenceTerm().getCode()).setDisplay(display)
							.setSystem(
									Constants.snomed));
				}
				if (map.getSource().getName().equals("CIEL")) {
					values.add(new CodingDt().setCode(map.getConceptReferenceTerm().getCode()).setDisplay(display)
							.setSystem(
									Constants.ciel));
				} else {
					String uri = "http://" + Context.getAdministrationService().getSystemVariables().get("OPENMRS_HOSTNAME")
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

		FhirContext ctx = new FhirContext();
		ctx.setNarrativeGenerator(new DefaultThymeleafNarrativeGenerator());

		IParser jsonParser = ctx.newJsonParser();
		IParser xmlParser = ctx.newXmlParser();

		String encoded = null;

		if (contentType.equals("application/xml+fhir")) {

			xmlParser.setPrettyPrint(true);
			encoded = xmlParser.encodeResourceToString(observation);
		} else {

			jsonParser.setPrettyPrint(true);
			encoded = jsonParser.encodeResourceToString(observation);
		}

		return encoded;

	}

	public static Observation generateObsObject(Obs obs) {

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
		String patientUri = "http://" + Context.getAdministrationService().getSystemVariables().get("OPENMRS_HOSTNAME")
		                    + "/ws/rest/v1/fhirpatient/" + obs.getPerson().getUuid();

		//patientReference.setReference("ref");
		IdDt patientRef = new IdDt();
		patientRef.setValue(patientUri);
		patientReference.setReference(patientRef);

		observation.setSubject(patientReference);

		List<ResourceReferenceDt> performers = new ArrayList<ResourceReferenceDt>();

		for (EncounterProvider provider : obs.getEncounter().getEncounterProviders()) {
			ResourceReferenceDt providerReference = new ResourceReferenceDt();
			providerReference.setDisplay(
					provider.getProvider().getName() + "(" + provider.getProvider().getProviderId() + ")");
			//patientReference.setReference("ref");
			IdDt providerRef = new IdDt();
			String providerUri = "http://" + Context.getAdministrationService().getSystemVariables().get("OPENMRS_HOSTNAME")
			                     + "/ws/rest/v1/fhirprovider/" + provider.getUuid();

			providerRef.setValue(providerUri);
			providerReference.setReference(providerRef);

			performers.add(providerReference);
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
						Constants.loinc));
			}
			if (map.getSource().getName().equals("SNOMED")) {
				dts.add(new CodingDt().setCode(map.getConceptReferenceTerm().getCode()).setDisplay(display).setSystem(
						Constants.snomed));
			}
			if (map.getSource().getName().equals("CIEL")) {
				dts.add(new CodingDt().setCode(map.getConceptReferenceTerm().getCode()).setDisplay(display).setSystem(
						Constants.ciel));
			} else {
				String uri = "http://" + Context.getAdministrationService().getSystemVariables().get("OPENMRS_HOSTNAME")
				             + "/ws/rest/v1/fhirconceptsource/" + map.getSource().getUuid();
				dts.add(new CodingDt().setCode(map.getConceptReferenceTerm().getCode()).setDisplay(
						map.getSource().toString()).setSystem(uri));

			}

		}
		dt.setCoding(dts);

		if (obs.getConcept().isNumeric()) {

			obs.getConcept().isNumeric();
			((ConceptNumeric) obs.getConcept()).getHiCritical();

			QuantityDt q = new QuantityDt();

			q.setValue(obs.getValueNumeric());

			q.setSystem("http://unitsofmeasure.org");

			// q.setCode(obs.get);

			observation.getReferenceRangeFirstRep().setLow(((ConceptNumeric) obs.getConcept()).getLowCritical());

			observation.getReferenceRangeFirstRep().setHigh(((ConceptNumeric) obs.getConcept()).getHiCritical());

			observation.setValue(q);

		}

		if (obs.getConcept().getDatatype().getHl7Abbreviation().equals("ST")) {
			StringDt value = new StringDt();
			value.setValue(obs.getValueAsString(Context.getLocale()));
			observation.setValue(value);

		}

		if (obs.getConcept().getDatatype().getHl7Abbreviation().equals("BIT")) {
			BooleanDt value = new BooleanDt();
			value.setValue(obs.getValueBoolean());
			observation.setValue(value);

		}

		if (obs.getConcept().getDatatype().getHl7Abbreviation().equals("TS")) {
			DateTimeDt datetime = new DateTimeDt();
			datetime.setValue(obs.getValueDatetime());
			observation.setValue(datetime);
		}

		if (obs.getConcept().getDatatype().getHl7Abbreviation().equals("DT")) {
			DateDt date = new DateDt();
			date.setValue(obs.getValueDate());
			observation.setValue(date);
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
									Constants.loinc));
				}
				if (map.getSource().getName().equals("SNOMED")) {
					values.add(new CodingDt().setCode(map.getConceptReferenceTerm().getCode()).setDisplay(display)
							.setSystem(
									Constants.snomed));
				}
				if (map.getSource().getName().equals("CIEL")) {
					values.add(new CodingDt().setCode(map.getConceptReferenceTerm().getCode()).setDisplay(display)
							.setSystem(
									Constants.ciel));
				} else {
					String uri = "http://" + Context.getAdministrationService().getSystemVariables().get("OPENMRS_HOSTNAME")
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

		FhirContext ctx = new FhirContext();
		ctx.setNarrativeGenerator(new DefaultThymeleafNarrativeGenerator());

		IParser jsonParser = ctx.newJsonParser();
		IParser xmlParser = ctx.newXmlParser();

		String encoded = null;

		return observation;

	}

	public static String generateBundle(List<Obs> obsList, String contentType) {

		Bundle bundle = new Bundle();
		StringDt title = bundle.getTitle();
		title.setValue("Search result");

		IdDt id = new IdDt();
		id.setValue("the request uri");
		bundle.setId(id);

		InstantDt instant = bundle.getUpdated();
		instant.setValue(new Date());

		for (Obs obs : obsList) {
			BundleEntry bundleEntry = new BundleEntry();

			IResource resource = new Observation();
			resource = generateObsObject(obs);

			bundleEntry.setResource(resource);

			bundle.addEntry(bundleEntry);
		}
		FhirContext ctx = new FhirContext();
		IParser jsonParser = ctx.newJsonParser();

		jsonParser.setPrettyPrint(true);
		String encoded = jsonParser.encodeBundleToString(bundle);

		return encoded;

	}

}
