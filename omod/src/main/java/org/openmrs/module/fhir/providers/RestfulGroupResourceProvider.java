package org.openmrs.module.fhir.providers;

import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.hl7.fhir.dstu3.model.Group;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.openmrs.module.fhir.resources.FHIRGroupResource;

import java.util.List;

public class RestfulGroupResourceProvider implements IResourceProvider {

    private FHIRGroupResource groupResource;

    public RestfulGroupResourceProvider() {
        groupResource = new FHIRGroupResource();
    }

    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return Group.class;
    }

    @Read()
    public Group getResourceById(@IdParam IdType id) {
        return groupResource.getByUniqueId(id);
    }

    @Search()
    public List<Group> searchGroupsById(
            @RequiredParam(name = Group.SP_RES_ID)TokenParam id) {
        return groupResource.searchGroupById(id);
    }

    @Search()
    public List<Group> searchGroupsByName(
            @RequiredParam(name = "name") StringParam name) {
        return groupResource.searchGroupByName(name);
    }
}
