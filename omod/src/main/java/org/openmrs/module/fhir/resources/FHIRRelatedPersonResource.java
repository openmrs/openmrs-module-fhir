package org.openmrs.module.fhir.resources;

import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.RelatedPerson;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.RelatedPersonService;

public class FHIRRelatedPersonResource extends Resource {

    public RelatedPerson getByUniqueId(IdType id) {
        RelatedPersonService relatedPersonService = Context.getService(RelatedPersonService.class);
        return relatedPersonService.getRelatedPerson(id.getIdPart());
    }
}
