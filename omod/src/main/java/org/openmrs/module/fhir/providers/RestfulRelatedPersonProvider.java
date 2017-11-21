package org.openmrs.module.fhir.providers;

import ca.uhn.fhir.rest.annotation.Delete;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.RelatedPerson;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.openmrs.module.fhir.resources.FHIRRelatedPersonResource;

public class RestfulRelatedPersonProvider implements IResourceProvider {

    private FHIRRelatedPersonResource relatedPersonResource;

    public RestfulRelatedPersonProvider() {
        relatedPersonResource = new FHIRRelatedPersonResource();
    }

    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return RelatedPerson.class;
    }

    @Read()
    public RelatedPerson getResourceById(@IdParam IdType theId) {
        return relatedPersonResource.getByUniqueId(theId);
    }

    /**
     * Delete related person by unique id
     *
     * @param theId object containing the id
     */
    @Delete()
    public void deletePerson(@IdParam IdType theId) {
        relatedPersonResource.deleteRelatedPerson(theId);
    }
}
