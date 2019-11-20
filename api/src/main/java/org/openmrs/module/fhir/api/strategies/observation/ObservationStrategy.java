package org.openmrs.module.fhir.api.strategies.observation;

import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import org.hl7.fhir.dstu3.model.Observation;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.openmrs.module.fhir.api.util.FHIRObsUtil;
import org.openmrs.module.fhir.api.util.FHIRUtils;
import org.openmrs.module.fhir.api.util.StrategyUtil;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("DefaultObservationStrategy")
public class ObservationStrategy implements GenericObservationStrategy {

	@Override
	public Observation getObservation(String uuid) {
		Obs omrsObs = Context.getObsService().getObsByUuid(uuid);
		if (omrsObs == null || omrsObs.isVoided()) {
			return null;
		}
		return FHIRObsUtil.generateObs(omrsObs);
	}

	@Override
	public List<Observation> searchObservationByPatientAndConcept(String patientUuid,
			Map<String, String> conceptNamesAndURIs) {
		Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);
		Concept concept;
		List<Observation> obsList = new ArrayList<Observation>();
		String codingSystem = FHIRUtils.getConceptCodingSystem();
		String systemName;
		for (Map.Entry<String, String> entry : conceptNamesAndURIs.entrySet()) {
			if (entry.getValue() == null || entry.getValue().isEmpty()) {
				if (codingSystem == null || FHIRConstants.OPENMRS_CONCEPT_CODING_SYSTEM.equals(codingSystem)) {
					concept = Context.getConceptService().getConceptByUuid(entry.getKey());
				} else {
					systemName = FHIRConstants.conceptSourceURINameMap.get(entry.getValue());
					if (systemName == null || systemName.isEmpty()) {
						return obsList;
					}
					concept = Context.getConceptService().getConceptByMapping(entry.getKey(), systemName);
				}
			} else {
				systemName = FHIRConstants.conceptSourceURINameMap.get(entry.getValue());
				if (systemName == null || systemName.isEmpty()) {
					return obsList;
				}
				concept = Context.getConceptService().getConceptByMapping(entry.getKey(), systemName);
			}
			List<Obs> obs = Context.getObsService().getObservationsByPersonAndConcept(patient, concept);
			for (Obs ob : obs) {
				obsList.add(FHIRObsUtil.generateObs(ob));
			}
		}
		return obsList;
	}

	@Override
	public List<Observation> searchObservationByUuid(String uuid) {
		Obs omrsObs = Context.getObsService().getObsByUuid(uuid);
		List<Observation> obsList = new ArrayList<Observation>();
		if (omrsObs != null && !omrsObs.getVoided()) {
			obsList.add(FHIRObsUtil.generateObs(omrsObs));
		}
		return obsList;
	}

	@Override
	public List<Observation> searchObservationsByCode(Map<String, String> conceptNamesAndURIs) {
		String codingSystem = FHIRUtils.getConceptCodingSystem();
		List<Observation> obsList = new ArrayList<Observation>();
		List<Obs> omrsObs = new ArrayList<Obs>();
		Concept concept = null;
		String systemName;
		//Check system uri specified and if so find system name and query appropriate concept
		for (Map.Entry<String, String> entry : conceptNamesAndURIs.entrySet()) {
			if (entry.getValue() == null || entry.getValue().isEmpty()) {
				if (codingSystem == null || FHIRConstants.OPENMRS_CONCEPT_CODING_SYSTEM.equals(codingSystem)) {
					concept = Context.getConceptService().getConceptByUuid(entry.getKey());
				} else {
					systemName = FHIRConstants.conceptSourceURINameMap.get(entry.getValue());
					if (systemName == null || systemName.isEmpty()) {
						return obsList;
					}
					concept = Context.getConceptService().getConceptByMapping(entry.getKey(), systemName);
				}
			} else {
				systemName = FHIRConstants.conceptSourceURINameMap.get(entry.getValue());
				if (systemName == null || systemName.isEmpty()) {
					return obsList;
				}
				concept = Context.getConceptService().getConceptByMapping(entry.getKey(), systemName);
			}

			if (concept == null) {
				return obsList;
			}

			List<Concept> concepts = new ArrayList<Concept>();
			concepts.add(concept);
			omrsObs = Context.getObsService().getObservations(null, null, concepts, null, null, null, null, null,
					null, null, null, false);

			for (Obs obs : omrsObs) {
				obsList.add(FHIRObsUtil.generateObs(obs));
			}
		}
		return obsList;
	}

	@Override
	public List<Observation> searchObservationByDate(Date date) {
		List<Obs> omrsObs = Context.getObsService().getObservations(null, null, null, null, null, null, null, null,
				null, date, date, false);
		List<Observation> obsList = new ArrayList<Observation>();
		for (Obs obs : omrsObs) {
			obsList.add(FHIRObsUtil.generateObs(obs));
		}
		return obsList;
	}

	@Override
	public List<Observation> searchObservationByPerson(String personUuid) {
		Person person = Context.getPersonService().getPersonByUuid(personUuid);
		List<Obs> omrsObs = Context.getObsService().getObservationsByPerson(person);
		List<Observation> obsList = new ArrayList<Observation>();
		for (Obs obs : omrsObs) {
			obsList.add(FHIRObsUtil.generateObs(obs));
		}
		return obsList;
	}

	@Override
	public List<Observation> searchObservationByValueConcept(String conceptName) {
		Concept concept = Context.getConceptService().getConcept(conceptName);
		List<Concept> conceptsAnswers = new ArrayList<Concept>();
		conceptsAnswers.add(concept);
		List<Obs> omrsObs = Context.getObsService().getObservations(null, null, null, conceptsAnswers, null, null, null,
				null,
				null, null, null, false);
		List<Observation> obsList = new ArrayList<Observation>();
		for (Obs obs : omrsObs) {
			obsList.add(FHIRObsUtil.generateObs(obs));
		}
		return obsList;
	}

	@Override
	public List<Observation> searchObservationByPatientIdentifier(String identifier) {
		List<Observation> fhirObsList = new ArrayList<Observation>();

		List<Obs> ormsObs = Context.getObsService().getObservations(identifier);
		for (Obs obs : ormsObs) {
			fhirObsList.add(FHIRObsUtil.generateObs(obs));
		}
		return fhirObsList;
	}

	@Override
	public void deleteObservation(String uuid) {
		Obs obs = Context.getObsService().getObsByUuid(uuid);
		Context.getObsService().voidObs(obs, FHIRConstants.FHIR_VOIDED_MESSAGE);
	}

	@Override
	public Observation createFHIRObservation(Observation observation) {
		Obs obs = null;
		Encounter enc = null;
		List<String> errors = new ArrayList<String>();
		String encRef = observation.getContext().getReference();
		if (encRef != null) {
			String enc_uuid = FHIRUtils.extractUuid(encRef);
			enc = Context.getEncounterService().getEncounterByUuid(enc_uuid);
		}
		if (enc != null) {
			obs = FHIRObsUtil.generateOpenMRSObsWithEncounter(observation, enc, errors);
		} else {
			obs = FHIRObsUtil.generateOpenMRSObs(observation, errors);
		}
		FHIRUtils.checkGeneratorErrorList(errors);
		obs = Context.getObsService().saveObs(obs, FHIRConstants.FHIR_CREATE_MESSAGE);
		if (FHIRObsUtil.hasGroupMembers(observation)) {
			buildObsGroup(observation, obs);
		}
		return FHIRObsUtil.generateObs(obs);
	}

	public static void buildObsGroup(Observation observation, Obs obs) {
		for (Observation.ObservationRelatedComponent component : observation.getRelated()) {
			String memberUuid = FHIRUtils.getObjectUuidByReference(component.getTarget());
			Obs member = Context.getObsService().getObsByUuid(memberUuid);
			if (member != null) {
				member.setObsGroup(obs);
				Context.getObsService().saveObs(member, FHIRConstants.FHIR_UPDATE_MESSAGE);
			}
		}
	}

	@Override
	public Observation updateFHITObservation(Observation observation, String uuid) {
		uuid = FHIRUtils.extractUuid(uuid);
		ObsService observationService = Context.getObsService();
		Obs retrievedObs = observationService.getObsByUuid(uuid);
		return retrievedObs != null ? updateRetrievedObservation(observation, retrievedObs) :
				createObservation(observation, uuid);
	}

	/**
	 * @see org.openmrs.module.fhir.api.strategies.observation.GenericObservationStrategy#searchObservationByPatientAndCode(java.lang.String, java.util.Map)
	 */
	@Override
	public List<Observation> searchObservationByPatientAndCode(String patientUuid, Map<String, String> codeAndSystem) {
		String codingSystem = FHIRUtils.getConceptCodingSystem();
		Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);
		List<Observation> obsList = new ArrayList<>();
		Concept concept;

		if (codingSystem == null || FHIRConstants.OPENMRS_CONCEPT_CODING_SYSTEM.equals(codingSystem)) {
			concept = Context.getConceptService().getConcept(codeAndSystem.get(FHIRConstants.CODE));
		} else {
			String systemName = FHIRConstants.conceptSourceURINameMap.get(codeAndSystem.get(FHIRConstants.SYSTEM));
			concept = Context.getConceptService().getConceptByMapping(codeAndSystem.get(FHIRConstants.CODE), systemName);
		}
		if (concept != null) {
			List<Obs> obs = Context.getObsService().getObservationsByPersonAndConcept(patient, concept);
			for (Obs ob : obs) {
				obsList.add(FHIRObsUtil.generateObs(ob));
			}
		}

		return obsList;
	}

	private Observation createObservation(Observation observation, String uuid) {
		uuid = FHIRUtils.extractUuid(uuid);
		StrategyUtil.setIdIfNeeded(observation, uuid);
		return createFHIRObservation(observation);
	}

	private Observation updateRetrievedObservation(Observation observation, Obs retrievedObs) {
		List<String> errors = new ArrayList<String>();
		Obs omrsObs = FHIRObsUtil.generateOpenMRSObs(observation, errors);
		FHIRUtils.checkGeneratorErrorList(errors);
		FHIRObsUtil.copyObsAttributes(omrsObs, retrievedObs, errors);
		try {
			omrsObs = Context.getObsService().saveObs(retrievedObs, FHIRConstants.FHIR_UPDATE_MESSAGE);
			if (FHIRObsUtil.hasGroupMembers(observation)) {
				buildObsGroup(observation, retrievedObs);
			}
		}
		catch (Exception e) {
			throw new UnprocessableEntityException(
					"The request cannot be processed due to the following issues \n" + e.getMessage());
		}
		return FHIRObsUtil.generateObs(omrsObs);
	}
}
