package org.openmrs.module.fhir.api.strategies.visit;

import ca.uhn.fhir.rest.server.exceptions.MethodNotAllowedException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.FamilyMemberHistory;
import org.hl7.fhir.dstu3.model.IdType;
import org.openmrs.Visit;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.EncounterService;
import org.openmrs.module.fhir.api.FamilyMemberHistoryService;
import org.openmrs.module.fhir.api.util.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.openmrs.module.fhir.api.util.FHIRUtils.extractUuid;

@Component("DefaultVisitStrategy")
public class VisitStrategy implements GenericVisitStrategy {

    @Override
    public Encounter getVisit(String uuid) {
        org.openmrs.Visit omrsVisit = Context.getVisitService().getVisitByUuid(uuid);

        if (omrsVisit == null || omrsVisit.isVoided()) {
            return null;
        }
        return OMRSFHIRVisitUtil.generateEncounter(omrsVisit);
    }

    @Override
    public List<Encounter> searchVisitsById(String uuid) {
        uuid = extractUuid(uuid);
        org.openmrs.Visit omrsVisit = Context.getVisitService().getVisitByUuid(uuid);

        List<Encounter> visitList = new ArrayList();
        if (omrsVisit != null && !omrsVisit.isVoided()) {
            visitList.add(OMRSFHIRVisitUtil.generateEncounter(omrsVisit));
        }
        return visitList;
    }



    @Override
    public List<Encounter> searchVisits(boolean active) {
        //TODO this method looks for all the patients which is inefficient. Reimplement after API revamp
        List<org.openmrs.Visit> visits = Context.getVisitService().getAllVisits();

        List<Encounter> fhirVisitList = new ArrayList();
        for (org.openmrs.Visit visit : visits) {
            if (active) {
                if (!visit.isVoided()) {
                    fhirVisitList.add(OMRSFHIRVisitUtil.generateEncounter(visit));
                }
            } else {
                if (visit.isVoided()) {
                    fhirVisitList.add(OMRSFHIRVisitUtil.generateEncounter(visit));
                }
            }
        }
        return fhirVisitList;
    }


    @Override
    public void deleteVisit(String uuid) {
        uuid = extractUuid(uuid);
        org.openmrs.Visit visit = Context.getVisitService().getVisitByUuid(uuid);

        // patient not found. return with 404
        if (visit == null) {
            //Jira related https://issues.openmrs.org/browse/FM-194
            throw new ResourceNotFoundException(new IdType(FHIRConstants.VISIT, uuid));
        }
        try {
            Context.getVisitService().voidVisit(visit, FHIRConstants.ENCOUNTER_DELETE_MESSAGE);
        } catch (APIException ex) {
            // refused to retire resource.  return with 405
            throw new MethodNotAllowedException("The OpenMRS API refused to retire the Patient via the FHIR request.");
        }
    }

    @Override
    public Encounter createFHIRVisit(Encounter visit) {
        List<String> errors = new ArrayList();
        org.openmrs.Visit omrsVisit = OMRSFHIRVisitUtil.generateOmrsVisit(visit, errors);

        FHIRUtils.checkGeneratorErrorList(errors);

        org.openmrs.api.VisitService visitService = Context.getVisitService();
        try {
            omrsVisit = visitService.saveVisit(omrsVisit);
        } catch (Exception e) {
            StringBuilder errorMessage = new StringBuilder("The request cannot be processed due to the following issues \n");
            errorMessage.append(e.getMessage());
            throw new UnprocessableEntityException(errorMessage.toString());
        }
        return OMRSFHIRVisitUtil.generateEncounter(omrsVisit);
    }

    @Override
    public Encounter updateVisit(Encounter visit, String uuid) {
        uuid = extractUuid(uuid);
        org.openmrs.api.VisitService visitService = Context.getVisitService();
        org.openmrs.Visit retrievedVisit = visitService.getVisitByUuid(uuid);

        return retrievedVisit != null ? updateRetrievedVisit(visit, retrievedVisit) : createVisit(visit, uuid);
    }

    private Encounter updateRetrievedVisit(Encounter visit, org.openmrs.Visit retrievedVisit) {
        List<String> errors = new ArrayList();
        org.openmrs.Visit omrsVisit = OMRSFHIRVisitUtil.generateOmrsVisit(visit, errors);
        FHIRUtils.checkGeneratorErrorList(errors);

        retrievedVisit = OMRSFHIRVisitUtil.updateVisitAttributes(omrsVisit, retrievedVisit);
        try {
            Context.getVisitService().saveVisit(retrievedVisit);
        } catch (Exception e) {
            StringBuilder errorMessage = new StringBuilder(
                    "The request cannot be processed due to the following issues \n");
            errorMessage.append(e.getMessage());
            throw new UnprocessableEntityException(errorMessage.toString());
        }
        return OMRSFHIRVisitUtil.generateEncounter(retrievedVisit);
    }

    private Encounter createVisit(Encounter visit, String uuid) {
        uuid = extractUuid(uuid);
        if (visit.getId() == null) { // since we need to PUT the patient to a specific URI, we need to set the uuid
            IdType uuidType = new IdType();
            uuidType.setValue(uuid);
            visit.setId(uuidType);
        }
        return createFHIRVisit(visit);
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
