package org.openmrs.module.fhir.api.strategies.familymemberhistory;

import org.hl7.fhir.dstu3.model.FamilyMemberHistory;

import java.util.List;

public interface GenericFamilyMemberHistoryStrategy {

    List<FamilyMemberHistory> searchFamilyHistoryByPersonId(String personId);

    FamilyMemberHistory getRelationshipById(String id);

    List<FamilyMemberHistory> searchRelationshipsById(String id);
}
