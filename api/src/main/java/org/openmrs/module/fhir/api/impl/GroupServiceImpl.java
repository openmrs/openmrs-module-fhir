package org.openmrs.module.fhir.api.impl;

import org.hl7.fhir.dstu3.model.Group;
import org.openmrs.module.fhir.api.GroupService;
import org.openmrs.module.fhir.api.strategies.group.GroupStrategyUtil;

import java.util.List;

public class GroupServiceImpl implements GroupService {
    @Override
    public Group getGroup(String id) {
        return GroupStrategyUtil.getGroupStrategy().getGroupById(id);
    }

    @Override
    public List<Group> searchGroupById(String id) {
        return GroupStrategyUtil.getGroupStrategy().searchGroupById(id);
    }

    @Override
    public List<Group> searchGroupByName(String name) {
        return GroupStrategyUtil.getGroupStrategy().searchGroupByName(name);
    }
}
