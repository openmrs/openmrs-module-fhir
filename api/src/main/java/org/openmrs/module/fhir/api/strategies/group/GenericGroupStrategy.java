package org.openmrs.module.fhir.api.strategies.group;

import org.hl7.fhir.dstu3.model.Group;

import java.util.List;

public interface GenericGroupStrategy {

    Group getGroupById(String uuid);

    List<Group> searchGroupById(String uuid);

    List<Group> searchGroupByName(String name);

    Group createGroup(Group group);

    Group updateGroup(Group group, String uuid);

    void deleteGroup(String uuid);
}
