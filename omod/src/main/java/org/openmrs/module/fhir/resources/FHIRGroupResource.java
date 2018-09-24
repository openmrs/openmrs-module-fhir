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
        Group group = getGroupService().getGroup(id.getIdPart());
        if (group == null) {
            throw new ResourceNotFoundException("Group is not found for given Id " + id.getIdPart());
        }
        return group;
    }

    public List<Group> searchGroupById(TokenParam id) {
        return getGroupService().searchGroupById(id.getValue());
    }

    public List<Group> searchGroupByName(StringParam name) {
        return getGroupService().searchGroupByName(name.getValue());
    }

    public Group createGroup(Group group) {
        return getGroupService().createGroup(group);
    }

    private GroupService getGroupService() {
        return Context.getService(GroupService.class);
    }
}
