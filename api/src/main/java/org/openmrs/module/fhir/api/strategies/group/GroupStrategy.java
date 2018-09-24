package org.openmrs.module.fhir.api.strategies.group;

import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import org.hl7.fhir.dstu3.model.Group;
import org.openmrs.Cohort;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.FHIRGroupCohortUtil;

import java.util.ArrayList;
import java.util.List;

public class GroupStrategy implements GenericGroupStrategy {
    @Override
    public Group getGroupById(String uuid) {
        Cohort cohort = Context.getCohortService().getCohortByUuid(uuid);

        return FHIRGroupCohortUtil.generateGroup(cohort);
    }

    @Override
    public List<Group> searchGroupById(String uuid) {
        List<Group> groups = new ArrayList<>();
        Cohort cohort = Context.getCohortService().getCohortByUuid(uuid);

        if (cohort != null) {
            groups.add(FHIRGroupCohortUtil.generateGroup(cohort));
        }

        return groups;
    }

    @Override
    public List<Group> searchGroupByName(String name) {
        List<Group> groups = new ArrayList<>();
        List<Cohort> cohorts = Context.getCohortService().getCohorts(name);

        for (Cohort cohort : cohorts) {
            groups.add(FHIRGroupCohortUtil.generateGroup(cohort));
        }

        return groups;
    }

    @Override
    public Group createGroup(Group group) {
        Cohort cohort = FHIRGroupCohortUtil.generateCohort(group);

        try {
            cohort = Context.getCohortService().saveCohort(cohort);
        } catch (APIException e) {
            throw new UnprocessableEntityException(
                    "The request cannot be processed due to the following issues \n" + e.getMessage());
        }

        return FHIRGroupCohortUtil.generateGroup(cohort);
    }
}
