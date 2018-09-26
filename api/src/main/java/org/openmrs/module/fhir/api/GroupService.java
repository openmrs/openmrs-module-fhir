package org.openmrs.module.fhir.api;

import org.hl7.fhir.dstu3.model.Group;

import java.util.List;

public interface GroupService {

    Group getGroup(String id);

    List<Group> searchGroupById(String id);

    List<Group> searchGroupByName(String name);

    Group createGroup(Group group);

    Group updateGroup(Group group, String uuid);

    void deleteGroup(String uuid);
}
