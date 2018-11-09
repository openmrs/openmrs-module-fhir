package org.openmrs.module.fhir.api.util;

import org.hl7.fhir.dstu3.model.Extension;
import org.hl7.fhir.dstu3.model.Group;
import org.hl7.fhir.dstu3.model.Reference;
import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.comparator.GroupComparator;
import org.openmrs.module.fhir.api.constants.ExtensionURL;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FHIRGroupUtil {

	private static final int FIRST = 0;

	public static boolean areGroupsEquals(Object ob1, Object ob2) {
		return new GroupComparator().areCustomPropsEquals((Group) ob1, (Group) ob2);
	}

	public static Group generateGroup(Cohort cohort) {
		if (cohort == null) {
			return null;
		}
		Group group = new Group();

		BaseOpenMRSDataUtil.setBaseExtensionFields(group, cohort);

		group.setId(cohort.getUuid());
		group.setName(cohort.getName());
		group.setType(Group.GroupType.PERSON);
		group.addExtension(ExtensionsUtil.createDescriptionExtension(cohort.getDescription()));

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

	public static Cohort generateCohort(Group group) {
		if (group == null) {
			return null;
		}

		List<Group.GroupMemberComponent> memberComponents = group.getMember();
		Integer[] ids = new Integer[memberComponents.size()];
		for (int i = 0; i < memberComponents.size(); i++) {
			Reference patientReference = memberComponents.get(i).getEntity();
			Patient patient = Context.getPatientService().getPatientByUuid(patientReference.getId());

			ids[i] = patient.getPatientId();
		}

		Cohort cohort = new Cohort(group.getName(), getDescription(group), ids);

		BaseOpenMRSDataUtil.readBaseExtensionFields(cohort, group);

		cohort.setUuid(FHIRUtils.extractUuid(group.getId()));

		return cohort;
	}

	/**
	 * This method updates name of cohort.
	 * Description can't be updated because FHIR Group doesn't have such field.
	 * Members aren't updated because REST also doesn't allow it.
	 *
	 * @param cohortToUpdate
	 * @param newCohort
	 * @return new updated Cohort
	 */
	public static Cohort updateCohort(Cohort cohortToUpdate, Cohort newCohort) {

		cohortToUpdate.setName(newCohort.getName());

		return cohortToUpdate;
	}

	public static String getDescription(Group group) {
		Extension extension = group.getExtensionsByUrl(ExtensionURL.DESCRIPTION_URL).get(FIRST);
		return extension.getValue().toString();
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
