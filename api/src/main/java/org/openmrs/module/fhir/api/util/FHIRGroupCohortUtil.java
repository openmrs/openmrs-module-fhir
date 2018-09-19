package org.openmrs.module.fhir.api.util;

import org.hl7.fhir.dstu3.model.Group;
import org.hl7.fhir.dstu3.model.Reference;
import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FHIRGroupCohortUtil {

    public static Group generateGroup(Cohort cohort) {
        if (cohort == null) {
            return null;
        }
        Group group = new Group();

        group.setId(cohort.getUuid());
        group.setName(cohort.getName());
        group.setType(Group.GroupType.PERSON);

        Set<Integer> memberIds = cohort.getMemberIds();

        group.setQuantity(memberIds.size());

        PatientService patientService = Context.getPatientService();
        List<Group.GroupMemberComponent> groupMemberComponents = new ArrayList<>();

        for (Integer patientId : memberIds) {
            Patient p = patientService.getPatient(patientId);
            groupMemberComponents.add(generateGroupMemberComponent(p));
        }

        group.setMember(groupMemberComponents);

        return group;
    }

    private static Group.GroupMemberComponent generateGroupMemberComponent(Patient patient) {
        if (patient == null) {
            return null;
        }
        Group.GroupMemberComponent gmc = new Group.GroupMemberComponent();

        Reference patientReference = FHIRPatientUtil.buildPatientReference(patient);
        gmc.setEntity(patientReference);

        return gmc;
    }
}
