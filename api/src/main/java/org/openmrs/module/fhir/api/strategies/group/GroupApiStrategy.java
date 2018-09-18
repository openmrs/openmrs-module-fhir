package org.openmrs.module.fhir.api.strategies.group;

import org.hl7.fhir.dstu3.model.Group;

import java.util.List;

public class GroupApiStrategy implements GenericGroupStrategy {
    @Override
    public Group getGroupById(String uuid) {
        return null;
    }

    @Override
    public List<Group> searchGroupById(String uuid) {
        return null;
    }

    @Override
    public List<Group> searchGroupByName(String name) {
        return null;
    }
}
