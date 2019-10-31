package org.openmrs.module.fhir.api.strategies.relatedperson;

import org.hl7.fhir.dstu3.model.RelatedPerson;

import java.util.List;

public interface GenericRelatedPersonStrategy {

	RelatedPerson getRelatedPerson(String uuid);

	/**
	 * search related person by identifier
	 *
	 * @param identifier The identifier of the patient
	 * @return List of RelatedPerson
	 * @since 1.20.0
	 */
	List<RelatedPerson> searchRelatedPersonByIdentifier(String identifier);

	void deleteRelatedPerson(String uuid);

	RelatedPerson updateRelatedPerson(String uuid, RelatedPerson relatedPerson);

	RelatedPerson createRelatedPerson(RelatedPerson relatedPerson);
}
