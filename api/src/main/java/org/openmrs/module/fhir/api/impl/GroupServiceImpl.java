package org.openmrs.module.fhir.api.impl;

import org.hl7.fhir.dstu3.model.Group;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.fhir.api.GroupService;
import org.openmrs.module.fhir.api.db.FHIRDAO;
import org.openmrs.module.fhir.api.strategies.group.GroupStrategyUtil;

import java.util.List;

public class GroupServiceImpl extends BaseOpenmrsService implements GroupService {

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

    @Override
    public Group createGroup(Group group) {
        return GroupStrategyUtil.getGroupStrategy().createGroup(group);
    }
}
