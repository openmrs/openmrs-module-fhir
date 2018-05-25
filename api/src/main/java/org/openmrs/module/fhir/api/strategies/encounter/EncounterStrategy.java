package org.openmrs.module.fhir.api.strategies.encounter;

import ca.uhn.fhir.rest.server.exceptions.MethodNotAllowedException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.IdType;
//import org.openmrs.Encounter;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.openmrs.module.fhir.api.util.FHIREncounterUtil;
import org.openmrs.module.fhir.api.util.FHIRUtils;
import org.openmrs.module.fhir.api.util.OMRSFHIRVisitUtil;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.openmrs.module.fhir.api.util.FHIRUtils.extractUuid;

@Component("DefaultEncounterStrategy")
public class EncounterStrategy implements GenericEncounterStrategy {

    @Override
    public org.hl7.fhir.dstu3.model.Encounter getEncounter(String uuid) {
        org.openmrs.Encounter omrsEncounter = Context.getEncounterService().getEncounterByUuid(uuid);

        if (omrsEncounter == null || omrsEncounter.isVoided()) {
            return null;
        }
        return FHIREncounterUtil.generateEncounter(omrsEncounter);
    }

    @Override
    public List<Encounter> searchEncountersById(String uuid) {
        uuid = extractUuid(uuid);
        org.openmrs.Encounter omrsEncounter = Context.getEncounterService().getEncounterByUuid(uuid);

        List<Encounter> encounterList = new ArrayList();
        if (omrsEncounter != null && !omrsEncounter.isVoided()) {
            encounterList.add(FHIREncounterUtil.generateEncounter(omrsEncounter));
        }
        return encounterList;
    }



    @Override
    public List<Encounter> searchEncounters(boolean active) {
        //TODO this method looks for all the patients which is inefficient. Reimplement after API revamp
        Map<Integer, List<org.openmrs.Encounter>> encounters = Context.getEncounterService().getAllEncounters(null);

        List<Encounter> fhirEncounterList = new ArrayList();
        for(Integer i : encounters.keySet()) {
            for (org.openmrs.Encounter encounter : encounters.get(i)) {
                if (active) {
                    if (!encounter.isVoided()) {
                        fhirEncounterList.add(FHIREncounterUtil.generateEncounter(encounter));
                    }
                } else {
                    if (encounter.isVoided()) {
                        fhirEncounterList.add(FHIREncounterUtil.generateEncounter(encounter));
                    }
                }
            }
        }
        return fhirEncounterList;
    }


    @Override
    public void deleteEncounter(String uuid) {
        uuid = extractUuid(uuid);
        org.openmrs.Encounter encounter = Context.getEncounterService().getEncounterByUuid(uuid);

        // patient not found. return with 404
        if (encounter == null) {
            //Jira related https://issues.openmrs.org/browse/FM-194
            throw new ResourceNotFoundException(new IdType(FHIRConstants.ENCOUNTER, uuid));
        }
        try {
            Context.getEncounterService().voidEncounter(encounter, FHIRConstants.ENCOUNTER_DELETE_MESSAGE);
        } catch (APIException ex) {
            // refused to retire resource.  return with 405
            throw new MethodNotAllowedException("The OpenMRS API refused to retire the Encounter via the FHIR request.");
        }
    }

    @Override
    public Encounter createFHIREncounter(Encounter encounter) {
        List<String> errors = new ArrayList();
        org.openmrs.Encounter omrsEncounter = FHIREncounterUtil.generateOMRSEncounter(encounter, errors);

        FHIRUtils.checkGeneratorErrorList(errors);

        org.openmrs.api.EncounterService encounterService = Context.getEncounterService();
        try {
            omrsEncounter = encounterService.saveEncounter(omrsEncounter);
        } catch (Exception e) {
            StringBuilder errorMessage = new StringBuilder("The request cannot be processed due to the following issues \n");
            errorMessage.append(e.getMessage());
            throw new UnprocessableEntityException(errorMessage.toString());
        }
        return FHIREncounterUtil.generateEncounter(omrsEncounter);
    }

    @Override
    public Encounter updateEncounter(Encounter encounter, String uuid) {
        uuid = extractUuid(uuid);
        org.openmrs.api.EncounterService encounterService = Context.getEncounterService();
        org.openmrs.Encounter retrievedEncounter = encounterService.getEncounterByUuid(uuid);

        return retrievedEncounter != null ? updateRetrievedEncounter(encounter, retrievedEncounter) : createEncounter(encounter, uuid);
    }

    private Encounter updateRetrievedEncounter(Encounter encounter, org.openmrs.Encounter retrievedEncounter) {
        List<String> errors = new ArrayList();
        org.openmrs.Encounter omrsEncounter = FHIREncounterUtil.generateOMRSEncounter(encounter, errors);
        FHIRUtils.checkGeneratorErrorList(errors);

//        retrievedEncounter = FHIREncounterUtil.updateEncounterAttributes(omrsEncounter, retrievedEncounter);
        try {
            Context.getEncounterService().saveEncounter(retrievedEncounter);
        } catch (Exception e) {
            StringBuilder errorMessage = new StringBuilder(
                    "The request cannot be processed due to the following issues \n");
            errorMessage.append(e.getMessage());
            throw new UnprocessableEntityException(errorMessage.toString());
        }
        return FHIREncounterUtil.generateEncounter(retrievedEncounter);
    }

    private Encounter createEncounter(Encounter encounter, String uuid) {
        uuid = extractUuid(uuid);
        if (encounter.getId() == null) { // since we need to PUT the patient to a specific URI, we need to set the uuid
            IdType uuidType = new IdType();
            uuidType.setValue(uuid);
            encounter.setId(uuidType);
        }
        return createFHIREncounter(encounter);
    }

    private List<Bundle.BundleEntryComponent> rejectResourceDuplicates(Bundle bundle) {
        List<Bundle.BundleEntryComponent> result = new ArrayList();

        for (Bundle.BundleEntryComponent temp : bundle.getEntry()) {
            boolean contains = false;
            for (Bundle.BundleEntryComponent filtered : result) {
                if (filtered.getResource().getId().equals(temp.getResource().getId())) {
                    contains = true;
                }
            }
            if (!contains) {
                result.add(temp);
            }
        }

        return result;
    }
}
