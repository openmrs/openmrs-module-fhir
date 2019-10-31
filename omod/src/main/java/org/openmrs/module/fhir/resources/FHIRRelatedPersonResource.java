package org.openmrs.module.fhir.resources;

import ca.uhn.fhir.rest.param.ReferenceParam;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.RelatedPerson;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.RelatedPersonService;

import java.util.List;

public class FHIRRelatedPersonResource extends Resource {

	public RelatedPerson getByUniqueId(IdType id) {
		RelatedPersonService relatedPersonService = Context.getService(RelatedPersonService.class);
		return relatedPersonService.getRelatedPerson(id.getIdPart());
	}

	/**
	 * @see org.openmrs.module.fhir.api.RelatedPersonService#searchRelatedPersonByIdentifier(String)
	 */
	public List<RelatedPerson> searchRelatedPersonByIdentifier(ReferenceParam identifier) {
		return Context.getService(RelatedPersonService.class).searchRelatedPersonByIdentifier(
				identifier.getValue());

	}

	public void deleteRelatedPerson(IdType id) {
		RelatedPersonService relatedPersonService = Context.getService(RelatedPersonService.class);
		relatedPersonService.deleteRelatedPerson(id.getIdPart());
	}

	public RelatedPerson updateRelatedPerson(String id, RelatedPerson relatedPerson) {
		return Context.getService(RelatedPersonService.class).updateRelatedPerson(id, relatedPerson);
	}

	public RelatedPerson createRelatedPerson(RelatedPerson relatedPerson) {
		return Context.getService(RelatedPersonService.class).createRelatedPerson(relatedPerson);
	}
}
