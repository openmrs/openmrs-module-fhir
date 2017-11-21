package org.openmrs.module.fhir.api.strategies.familymemberhistory;

import org.hl7.fhir.dstu3.model.FamilyMemberHistory;
import org.openmrs.Person;
import org.openmrs.Relationship;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.FHIRFamilyMemberHistoryUtil;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("DefaultFamilyMemberHistoryStrategy")
public class FamilyMemberHistoryHistoryStrategy implements GenericFamilyMemberHistoryStrategy {

    @Override
    public List<FamilyMemberHistory> searchFamilyHistoryByPersonId(String personId) {
        Person person = Context.getPersonService().getPersonByUuid(personId);
        List<FamilyMemberHistory> fhirFamilyHistory = new ArrayList<FamilyMemberHistory>();
        List<Relationship> relationships = null;
        if (person != null && !person.isVoided()) {
            relationships = Context.getPersonService().getRelationshipsByPerson(person);
        }
        if (relationships != null && relationships.size() > 0) {
            for (Relationship relationship : relationships) {
                fhirFamilyHistory.add(FHIRFamilyMemberHistoryUtil.generateFamilyHistory(relationship, person));
            }
        }
        return fhirFamilyHistory;
    }

    @Override
    public FamilyMemberHistory getRelationshipById(String id) {
        Person person = Context.getPersonService().getPersonByUuid(id);
        List<Relationship> relationships = Context.getPersonService().getRelationshipsByPerson(person);
        return FHIRFamilyMemberHistoryUtil.generateFamilyHistory(relationships.get(0), person);
    }

    @Override
    public List<FamilyMemberHistory> searchRelationshipsById(String id) {
        Person person = Context.getPersonService().getPersonByUuid(id);
        List<FamilyMemberHistory> familyHistories = new ArrayList<FamilyMemberHistory>();
        List<Relationship> relationships = Context.getPersonService().getRelationshipsByPerson(person);
        if (relationships != null) {
            for (Relationship relationship : relationships) {
                familyHistories.add(FHIRFamilyMemberHistoryUtil.generateFamilyHistory(relationship, person));
            }
        }
        return familyHistories;
    }
}
