package org.openmrs.module.fhir.api.strategies.group;

import ca.uhn.fhir.rest.server.exceptions.MethodNotAllowedException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import org.hl7.fhir.dstu3.model.Group;
import org.hl7.fhir.dstu3.model.IdType;
import org.openmrs.Cohort;
import org.openmrs.api.APIException;
import org.openmrs.api.CohortService;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.openmrs.module.fhir.api.util.FHIRGroupUtil;
import org.openmrs.module.fhir.api.util.FHIRUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("DefaultGroupStrategy")
public class GroupStrategy implements GenericGroupStrategy {
    @Override
    public Group getGroupById(String uuid) {
        Cohort cohort = getCohortService().getCohortByUuid(uuid);

        return FHIRGroupUtil.generateGroup(cohort);
    }

    @Override
    public List<Group> searchGroupById(String uuid) {
        List<Group> groups = new ArrayList<>();
        Cohort cohort = getCohortService().getCohortByUuid(uuid);

        if (cohort != null) {
            groups.add(FHIRGroupUtil.generateGroup(cohort));
        }

        return groups;
    }

    @Override
    public List<Group> searchGroupByName(String name) {
        List<Group> groups = new ArrayList<>();
        List<Cohort> cohorts = getCohortService().getCohorts(name);

        for (Cohort cohort : cohorts) {
            groups.add(FHIRGroupUtil.generateGroup(cohort));
        }

        return groups;
    }

    @Override
    public Group createGroup(Group group) {
        Cohort cohort = FHIRGroupUtil.generateCohort(group);

        try {
            cohort = getCohortService().saveCohort(cohort);
        } catch (APIException e) {
            throw new UnprocessableEntityException(
                    "The request cannot be processed due to the following issues \n" + e.getMessage());
        }

        return FHIRGroupUtil.generateGroup(cohort);
    }

    @Override
    public Group updateGroup(Group group, String uuid) {
        Cohort cohort = getCohortService().getCohortByUuid(uuid);

        return cohort != null ? updateGroup(group, cohort) : createGroup(group, uuid);
    }

    @Override
    public void deleteGroup(String uuid) {
        Cohort cohort = getCohortService().getCohortByUuid(FHIRUtils.extractUuid(uuid));

        if (cohort == null) {
            throw new ResourceNotFoundException(new IdType(Group.class.getSimpleName(), uuid));
        } else {
            try {
                getCohortService().voidCohort(cohort, FHIRConstants.PERSON_VOIDED_MESSAGE);
            } catch (APIException e) {
                throw new MethodNotAllowedException(String.format("The OpenMRS API refused to remove Group via FHIR request. Group id: %s", uuid));
            }
        }
    }

    private Group createGroup(Group group, String uuid) {
        if (group.getId() == null) {
            IdType id = new IdType();
            id.setValue(uuid);
            group.setId(id);
        }

        return createGroup(group);
    }

    private Group updateGroup(Group group, Cohort cohortToUpdate) {
        Cohort newCohort = FHIRGroupUtil.generateCohort(group);

        cohortToUpdate = FHIRGroupUtil.updateCohort(cohortToUpdate, newCohort);

        try {
            cohortToUpdate = getCohortService().saveCohort(cohortToUpdate);
        } catch (APIException e) {
            throw new UnprocessableEntityException(
                    "The request cannot be processed due to the following issues \n" + e.getMessage());
        }
        return FHIRGroupUtil.generateGroup(cohortToUpdate);
    }

    private CohortService getCohortService() {
        return Context.getCohortService();
    }
}
