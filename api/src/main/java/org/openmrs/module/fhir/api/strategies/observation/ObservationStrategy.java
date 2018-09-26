package org.openmrs.module.fhir.api.strategies.observation;

import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Observation;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Encounter;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.openmrs.module.fhir.api.util.FHIRObsUtil;
import org.openmrs.module.fhir.api.util.FHIRUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
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
    public List<Observation> searchObservationByPatientAndConcept(String patientUuid, Map<String, String> conceptNamesAndURIs) {
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
        if(encRef != null) {
                String enc_uuid = FHIRUtils.extractUuid(encRef);
                enc = Context.getEncounterService().getEncounterByUuid(enc_uuid);
        }
        if(enc != null) {
            obs = FHIRObsUtil.generateOpenMRSObsWithEncounter(observation, enc, errors);
        } else {
            obs = FHIRObsUtil.generateOpenMRSObs(observation, errors);
        }
        if (!errors.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder("The request cannot be processed due to the following issues \n");
            for (int i = 0; i < errors.size(); i++) {
                errorMessage.append((i + 1) + " : " + errors.get(i) + "\n");
            }
            throw new UnprocessableEntityException(errorMessage.toString());
        }
        obs = Context.getObsService().saveObs(obs, FHIRConstants.FHIR_CREATE_MESSAGE);
        return FHIRObsUtil.generateObs(obs);
    }

    @Override
    public Observation updateFHITObservation(Observation observation, String uuid) {
        List<String> errors = new ArrayList<String>();
        org.openmrs.api.ObsService observationService = Context.getObsService();
        org.openmrs.Obs retrievedObs = observationService.getObsByUuid(uuid);
        org.openmrs.Obs omrsObs = FHIRObsUtil.generateOpenMRSObs(observation, errors);
        FHIRObsUtil.copyObsAttributes(omrsObs, retrievedObs, errors);
        if (retrievedObs != null) { // update observation
            if (!errors.isEmpty()) {
                StringBuilder errorMessage = new StringBuilder(
                        "The request cannot be processed due to the following issues \n");
                for (int i = 0; i < errors.size(); i++) {
                    errorMessage.append((i + 1) + " : " + errors.get(i) + "\n");
                }
                throw new UnprocessableEntityException(errorMessage.toString());
            }
            omrsObs = Context.getObsService().saveObs(retrievedObs, FHIRConstants.FHIR_UPDATE_MESSAGE);
            return FHIRObsUtil.generateObs(omrsObs);
        } else { // no observation is associated with the given uuid. so create a new observation with the given uuid
            if (observation.getId() == null) { // since we need to PUT the observation to a specific URI, we need to set the uuid
                // here, if it is not
                // already set.
                IdType id = new IdType();
                id.setValue(uuid);
                observation.setId(uuid);
            }
            return createFHIRObservation(observation);
        }
    }
}
