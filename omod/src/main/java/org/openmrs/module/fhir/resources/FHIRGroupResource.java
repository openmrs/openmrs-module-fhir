package org.openmrs.module.fhir.resources;

import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.dstu3.model.Group;
import org.hl7.fhir.dstu3.model.IdType;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.GroupService;

import java.util.List;

public class FHIRGroupResource extends Resource {

    public Group getByUniqueId(IdType id) {
        GroupService groupService = Context.getService(GroupService.class);
        Group group = groupService.getGroup(id.getIdPart());
        if (group == null) {
            throw new ResourceNotFoundException("Group is not found for given Id " + id.getIdPart());
        }
        return group;
    }

    public List<Group> searchGroupById(TokenParam id) {
        return Context.getService(GroupService.class).searchGroupById(id.getValue());
    }

    public List<Group> searchGroupByName(StringParam name) {
        return Context.getService(GroupService.class).searchGroupByName(name.getValue());
    }
}
