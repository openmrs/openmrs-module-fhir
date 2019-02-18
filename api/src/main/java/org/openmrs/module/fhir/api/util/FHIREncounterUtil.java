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

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Composition;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Extension;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.exceptions.FHIRException;
import org.openmrs.Concept;
import org.openmrs.EncounterProvider;
import org.openmrs.EncounterRole;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PersonName;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.FHIRHelperService;
import org.openmrs.module.fhir.api.constants.ExtensionURL;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.openmrs.module.fhir.api.util.FHIRUtils.extractUuid;

public class FHIREncounterUtil {

	private static final int UNKNOWN_ROLE = 1;

	public static Composition generateComposition(org.openmrs.Encounter omrsEncounter) throws FHIRException {

		Composition composition = new Composition();

		BaseOpenMRSDataUtil.setBaseExtensionFields(composition, omrsEncounter);

		//Set id of the composition from omrs encounter id
		IdType uuid = new IdType();
		uuid.setValue(omrsEncounter.getUuid());
		composition.setId(uuid);

		//Set composition date
		composition.setDate(omrsEncounter.getEncounterDatetime());

		//Set composition status
		composition.setStatus(Composition.CompositionStatus.FINAL);

		//Set document confidentiality
		Composition.DocumentConfidentiality confidentialityCode = Composition.DocumentConfidentiality.
				fromCode(FHIRConstants.CONFIDENTIALITY_CODING_R);
		composition.setConfidentiality(confidentialityCode);

		//Set composition subject as patient resource
		composition.setSubject(buildPatientReference(omrsEncounter));

		//Setting composition author
		if (omrsEncounter.getEncounterProviders().size() > 0) {
			List<Reference> authors = new ArrayList<>();
			Reference author;
			for (EncounterProvider provider : omrsEncounter.getEncounterProviders()) {
				author = new Reference();
				StringBuilder providerNameDisplay = new StringBuilder();
				providerNameDisplay.append(provider.getProvider().getName());
				providerNameDisplay.append("(");
				providerNameDisplay.append(FHIRConstants.IDENTIFIER);
				providerNameDisplay.append(":");
				providerNameDisplay.append(provider.getProvider().getIdentifier());
				providerNameDisplay.append(")");
				author.setDisplay(providerNameDisplay.toString());
				IdType providerRef = new IdType();
				String providerUri = FHIRConstants.PRACTITIONER + "/" + provider.getProvider().getUuid();
				providerRef.setValue(providerUri);
				author.setReference(providerUri);
				authors.add(author);
			}
			composition.setAuthor(authors);
		}

		//Set encounter reference
		Reference encounterRef = new Reference();
		String encounterRefId = FHIRConstants.ENCOUNTER + "/" + omrsEncounter.getUuid();
		encounterRef.setReference(encounterRefId);
		composition.setEncounter(encounterRef);

		//Set location
		Composition.SectionComponent locationSection = composition.addSection();
		Reference locationRef = locationSection.addEntry();
		locationRef.setDisplay(FHIRConstants.LOCATION);
		String locationUri = FHIRConstants.LOCATION + "/" + omrsEncounter.getLocation().getUuid();
		locationRef.setReference(locationUri);

		//Set observation section
		if (omrsEncounter.getAllObs(false).size() > 0) {
			Composition.SectionComponent obsSection = composition.addSection();
			Reference obsRef;
			String obsUri;
			for (Obs obs : omrsEncounter.getAllObs(false)) {
				obsRef = obsSection.addEntry();
				obsUri = FHIRConstants.OBSERVATION + "/" + obs.getUuid();
				obsRef.setReference(obsUri);
			}
		}
		return composition;
	}

	public static Encounter generateEncounter(org.openmrs.Encounter omrsEncounter) {
		Encounter encounter = new Encounter();

		BaseOpenMRSDataUtil.setBaseExtensionFields(encounter, omrsEncounter);

		IdType uuid = new IdType();
		uuid.setValue(omrsEncounter.getUuid());
		encounter.setId(uuid);
		encounter.setStatus(Encounter.EncounterStatus.FINISHED);

		//Set patient reference
		encounter.setSubject(buildPatientReference(omrsEncounter));

		encounter.setParticipant(buildParticipant(omrsEncounter));
		//Set encounter period from omrs encounter
		Period period = encounter.getPeriod();
		period.setStart(omrsEncounter.getEncounterDatetime());
		period.setEnd(omrsEncounter.getEncounterDatetime());
		encounter.setPeriod(period);

		//Set encounter location from omrs location
		if (omrsEncounter.getLocation() != null) {
			List<Encounter.EncounterLocationComponent> locations = new ArrayList<>();
			Encounter.EncounterLocationComponent location = new Encounter.EncounterLocationComponent();
			//set encounter period
			location.setPeriod(period);
			Reference locationReference = new Reference();
			locationReference.setDisplay(omrsEncounter.getLocation().getName());
			String locationRefUri = FHIRConstants.LOCATION + "/" + omrsEncounter.getLocation().getUuid();
			locationReference.setReference(locationRefUri);
			location.setLocation(locationReference);
			locations.add(location);
			encounter.setLocation(locations);
		}

		if (omrsEncounter.getVisit() != null) {
			//Set visit resource as a part of a encounter
			Reference visitRef = new Reference();
			visitRef.setDisplay(omrsEncounter.getVisit().getVisitType().getName());
			String visitRefUri = FHIRConstants.ENCOUNTER + "/" + omrsEncounter.getVisit().getUuid();
			visitRef.setReference(visitRefUri);
			encounter.setPartOf(visitRef);
		}

		buildFormExtension(omrsEncounter, encounter);

		String encounterType = omrsEncounter.getEncounterType().getName();
		Coding dt = new Coding();
		dt.setDisplay(encounterType);
		markAsEncounterType(dt);
		encounter.getTypeFirstRep().getCoding().add(dt);

		//TODO uncomment the validation and check what's going wrong
		//FHIRUtils.validate(encounter);
		return encounter;
	}

	public static List<Encounter.EncounterParticipantComponent> buildParticipant(org.openmrs.Encounter omrsEncounter) {
		List<Encounter.EncounterParticipantComponent> participants = new ArrayList<>();
		if (omrsEncounter.getEncounterProviders().size() > 0) {
			for (EncounterProvider provider : omrsEncounter.getEncounterProviders()) {
				Encounter.EncounterParticipantComponent participant = new Encounter.EncounterParticipantComponent();
				if (provider.getProvider() != null) {
					BaseOpenMRSDataUtil.setBaseExtensionFields(participant, provider);
					participant.setIndividual(buildProviderReference(provider));
					participant.addExtension(buildEncounterProviderExtension(provider));
					participant.addExtension(buildEncounterRoleExtension(provider.getEncounterRole()));
					participants.add(participant);
				}
			}
		}
		return participants;
	}

	private static Extension buildEncounterProviderExtension(EncounterProvider encounterProvider) {
		if (encounterProvider != null) {
			return ExtensionsUtil.createEncounterProviderUuidExtension(encounterProvider);
		}
		return null;
	}

	private static Extension buildEncounterRoleExtension(EncounterRole encounterRole) {
		if (encounterRole != null) {
			return ExtensionsUtil.createEncounterRoleUuidExtension(encounterRole);
		}
		return null;
	}

	public static Reference buildProviderReference(EncounterProvider provider) {
		Reference providerReference = new Reference();
		StringBuilder providerNameDisplay = new StringBuilder();
		providerNameDisplay.append(provider.getProvider().getName());
		providerNameDisplay.append("(");
		providerNameDisplay.append(FHIRConstants.IDENTIFIER);
		providerNameDisplay.append(":");
		providerNameDisplay.append(provider.getProvider().getIdentifier());
		providerNameDisplay.append(")");
		providerReference.setDisplay(providerNameDisplay.toString());
		String providerUri = FHIRConstants.PRACTITIONER + "/" + provider.getProvider().getUuid();
		providerReference.setReference(providerUri);
		return providerReference;
	}

	private static Reference buildPatientReference(org.openmrs.Encounter omrsEncounter) {
		//Build and set patient reference
		Reference patientReference = new Reference();
		PersonName name = omrsEncounter.getPatient().getPersonName();
		StringBuilder nameDisplay = new StringBuilder();
		nameDisplay.append(name.getGivenName());
		nameDisplay.append(" ");
		nameDisplay.append(name.getFamilyName());
		String patientUri;
		nameDisplay.append("(");
		nameDisplay.append(FHIRConstants.IDENTIFIER);
		nameDisplay.append(":");
		nameDisplay.append(omrsEncounter.getPatient().getPatientIdentifier().getIdentifier());
		nameDisplay.append(")");
		patientUri = FHIRConstants.PATIENT + "/" + omrsEncounter.getPatient().getUuid();
		patientReference.setReference(patientUri);
		patientReference.setDisplay(nameDisplay.toString());
		patientReference.setId(omrsEncounter.getPatient().getUuid());
		return patientReference;
	}

	/**
	 * Filter which obs need to be added to the encounter everything operation. Because some openmrs
	 * installations store allergies as obs. In that case we need to omit obs getting included in
	 * everything operation and return them in patient everything operation
	 *
	 * @param encounter encounter containing obs
	 * @param bundle    bundle containg encounter everything contents
	 */
	public static void addFilteredObs(org.openmrs.Encounter encounter, Bundle bundle) {
		String strategy = FHIRUtils.getAllergyStrategy();
		Bundle.BundleEntryComponent observation;
		if (FHIRConstants.OBS_ALLERGY_STRATEGY.equals(strategy)) {
			String allergyCode = FHIRUtils.getObsAllergyStrategyConceptUuid();
			Concept concept = Context.getConceptService().getConceptByUuid(allergyCode);
			for (Obs obs : encounter.getAllObs(false)) {
				if (concept != null && !concept.equals(obs.getConcept())) {
					observation = bundle.addEntry();
					observation.setResource(FHIRObsUtil.generateObs(obs));
				}
			}
		} else {
			for (Obs obs : encounter.getAllObs(false)) {
				observation = bundle.addEntry();
				observation.setResource(FHIRObsUtil.generateObs(obs));
			}
		}
	}

	public static org.openmrs.Encounter generateOMRSEncounter(Encounter encounter, List<String> errors) {
		org.openmrs.Encounter omrsEncounter = new org.openmrs.Encounter();

		BaseOpenMRSDataUtil.readBaseExtensionFields(omrsEncounter, encounter);

		if (encounter.getId() != null) {
			omrsEncounter.setUuid(extractUuid(encounter.getId()));
		}

		if (encounter.getSubject() != null) {
			Reference patientref = encounter.getSubject();
			String patientUuid = patientref.getId();
			Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);
			if (patient == null) {
				errors.add("There is no patient for the given uuid " + patientUuid); // remove to constants
			} else {
				omrsEncounter.setPatient(patient);
			}
		}
		Period period = encounter.getPeriod();
		Date start = period.getStart();
		omrsEncounter.setEncounterDatetime(start);

		List<Encounter.EncounterLocationComponent> locationList = encounter.getLocation();
		if (locationList != null && !locationList.isEmpty()) {
			Encounter.EncounterLocationComponent location = locationList.get(0);
			Reference locationRef = location.getLocation();
			String locationUuid = FHIRUtils.getObjectUuidByReference(locationRef);

			Location omrsLocation = Context.getLocationService().getLocationByUuid(locationUuid);
			if (omrsLocation != null) {
				omrsEncounter.setLocation(omrsLocation);
			}
		}

		omrsEncounter.setForm(buildFormReferenceByExtension(encounter));

		String encounterTypeName = encounter.getTypeFirstRep().getCodingFirstRep().getDisplay();
		EncounterType encounterType = Context.getEncounterService().getEncounterType(encounterTypeName);
		omrsEncounter.setEncounterType(encounterType);
		buildProviders(encounter, omrsEncounter);

		return omrsEncounter;
	}

	private static void buildProviders(Encounter encounter, org.openmrs.Encounter omrsEncounter) {
		Set<EncounterProvider> encounterProviders = new HashSet<>();
		for (Encounter.EncounterParticipantComponent participant : encounter.getParticipant()) {
			EncounterProvider encounterProvider = buildEncounterProvider(omrsEncounter, participant);
			encounterProviders.add(encounterProvider);
		}
		omrsEncounter.setEncounterProviders(encounterProviders);
	}

	private static EncounterProvider getEncounterProviderByUuid(String uniqueId) {
		return Context.getService(FHIRHelperService.class).getObjectByUuid(EncounterProvider.class, uniqueId);
	}

	private static EncounterProvider buildEncounterProvider(org.openmrs.Encounter omrsEncounter,
			Encounter.EncounterParticipantComponent participant) {
		String encounterProviderUuid = getEncounterProviderUuidByExtension(participant);

		EncounterProvider encounterProvider = getEncounterProviderByUuid(encounterProviderUuid);
		if (encounterProvider == null) {
			encounterProvider = createEncounterProvider(omrsEncounter, participant, encounterProviderUuid);
		} else {
			encounterProvider = updateEncounterProviderAttributes(encounterProvider, omrsEncounter, participant);
		}

		return encounterProvider;
	}

	private static EncounterProvider createEncounterProvider(org.openmrs.Encounter omrsEncounter,
			Encounter.EncounterParticipantComponent participant, String encounterProviderUuid) {
		String participantUuid = FHIRUtils.getObjectUuidByReference(participant.getIndividual());
		Provider provider = Context.getProviderService().getProviderByUuid(participantUuid);
		EncounterRole role = buildEncounterRole(participant);

		EncounterProvider encounterProvider = new EncounterProvider();
		BaseOpenMRSDataUtil.readBaseExtensionFields(encounterProvider, participant);
		encounterProvider.setEncounter(omrsEncounter);
		encounterProvider.setProvider(provider);
		encounterProvider.setEncounterRole(role);
		encounterProvider.setUuid(encounterProviderUuid);
		return encounterProvider;
	}

	private static EncounterProvider updateEncounterProviderAttributes(EncounterProvider encounterProvider,
			org.openmrs.Encounter omrsEncounter, Encounter.EncounterParticipantComponent participant) {
		String participantUuid = FHIRUtils.getObjectUuidByReference(participant.getIndividual());
		Provider provider = Context.getProviderService().getProviderByUuid(participantUuid);
		EncounterRole role = buildEncounterRole(participant);
		BaseOpenMRSDataUtil.readBaseExtensionFields(encounterProvider, participant);
		encounterProvider.setProvider(provider);
		encounterProvider.setEncounterRole(role);
		return encounterProvider;
	}

	private static EncounterRole buildEncounterRole(Encounter.EncounterParticipantComponent participant) {
		EncounterRole role = null;
		String encounterRoleUuid = getEncounterRoleUuidByExtension(participant);
		if (StringUtils.isNotBlank(encounterRoleUuid)) {
			role = Context.getEncounterService().getEncounterRoleByUuid(encounterRoleUuid);
		}
		if (role == null) {
			role = Context.getEncounterService().getEncounterRole(UNKNOWN_ROLE);
		}

		return role;
	}

	private static String getEncounterRoleUuidByExtension(Encounter.EncounterParticipantComponent participant) {
		List<Extension> extensions = participant.getExtensionsByUrl(ExtensionURL.ENCOUNTER_ROLE_UUID_URL);
		if (!CollectionUtils.isEmpty(extensions)) {
			return ExtensionsUtil.getStringFromExtension(extensions.get(FHIRConstants.FIRST));
		}
		return null;
	}

	private static String getEncounterProviderUuidByExtension(Encounter.EncounterParticipantComponent participant) {
		List<Extension> extensions = participant.getExtensionsByUrl(ExtensionURL.ENCOUNTER_PROVIDER_UUID_URL);
		if (!CollectionUtils.isEmpty(extensions)) {
			return ExtensionsUtil.getStringFromExtension(extensions.get(FHIRConstants.FIRST));
		}
		return null;
	}

	private static void buildFormExtension(org.openmrs.Encounter omrsEncounter, Encounter encounter) {
		if (omrsEncounter.getForm() != null) {
			encounter.addExtension(ExtensionsUtil.createFormUuidExtension(omrsEncounter.getForm()));
		}
	}

	private static Form buildFormReferenceByExtension(Encounter encounter) {
		String formUuid = getFormUuidFromExtension(encounter);
		if (StringUtils.isNotBlank(formUuid)) {
			return Context.getFormService().getFormByUuid(formUuid);
		}
		return null;
	}

	private static String getFormUuidFromExtension(Encounter encounter) {
		List<Extension> extensions = encounter.getExtensionsByUrl(ExtensionURL.FORM_UUID_URL);
		if (!CollectionUtils.isEmpty(extensions)) {
			return ExtensionsUtil.getStringFromExtension(extensions.get(FHIRConstants.FIRST));
		}
		return null;
	}

	public static org.openmrs.Encounter updateEncounterAttributes(org.openmrs.Encounter omrsEncounter,
			org.openmrs.Encounter retrievedEncounter) {
		retrievedEncounter.setPatient(omrsEncounter.getPatient());
		retrievedEncounter.setVisit(omrsEncounter.getVisit());
		retrievedEncounter.setEncounterDatetime(omrsEncounter.getEncounterDatetime());
		retrievedEncounter.setEncounterType(omrsEncounter.getEncounterType());
		retrievedEncounter.setLocation(omrsEncounter.getLocation());

		Set<EncounterProvider> encounterProviders = omrsEncounter.getEncounterProviders();
		for (EncounterProvider provider : encounterProviders) {
			retrievedEncounter.setProvider(provider.getEncounterRole(), provider.getProvider());
		}

		return retrievedEncounter;
	}

	public static boolean compareCurrentEncounters(Object encounter1, Object encounter2) {
		Encounter e1 = (Encounter) encounter1;
		Encounter e2 = (Encounter) encounter2;

		if (e1.getStatus() != null ? !e1.getStatus().equals(e2.getStatus()) : e2.getStatus() != null) {
			return false;
		}

		if (e1.getSubject() != null ? !e1.getSubject().equalsDeep(e2.getSubject()) : e2.getSubject() != null) {
			return false;
		}

		if (e1.getParticipant() != null ? !e1.getParticipant().equals(e2.getParticipant()) : e2.getParticipant() != null) {
			return false;
		}

		if (e1.getPeriod() != null ? !e1.getPeriod().equalsDeep(e2.getPeriod()) : e2.getPeriod() != null) {
			return false;
		}

		if (e1.getLocation() != null ? !e1.getLocation().equals(e2.getLocation()) : e2.getLocation() != null) {
			return false;
		}

		return e1.getPartOf() != null ? e1.getPartOf().equalsDeep(e2.getPartOf()) : e2.getPartOf() == null;
	}

	public static boolean shouldBeConsideredAsVisit(Encounter encounter) {
		return encounter.getTypeFirstRep().getCodingFirstRep().getUserSelected();
	}

	public static void markAsVisitType(Coding coding) {
		coding.setUserSelected(true);
	}

	public static void markAsEncounterType(Coding coding) {
		coding.setUserSelected(false);
	}
}
