package org.openmrs.module.fhir.api.strategies.relatedperson;

import org.hl7.fhir.dstu3.model.RelatedPerson;

public interface GenericRelatedPersonStrategy {

    RelatedPerson getRelatedPerson(String uuid);

    void deleteRelatedPerson(String uuid);

    RelatedPerson updateRelatedPerson(String uuid, RelatedPerson relatedPerson);

    RelatedPerson createRelatedPerson(RelatedPerson relatedPerson);
}
