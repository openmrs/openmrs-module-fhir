package org.openmrs.module.fhir.util;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hl7.fhir.dstu3.model.AllergyIntolerance;
import org.hl7.fhir.dstu3.model.Annotation;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Enumeration;
import org.openmrs.Concept;
import org.openmrs.Person;
import org.openmrs.activelist.Allergy;
import org.openmrs.activelist.AllergySeverity;
import org.openmrs.activelist.AllergyType;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.BaseOpenMRSDataUtil;
import org.openmrs.module.fhir.api.util.FHIRUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FHIRAllergyIntoleranceUtil1_11 {

	public static final int FIRST = 0;

	public static AllergyIntolerance generateAllergyIntolerance(Allergy allergy) {
		AllergyIntolerance allergyIntolerance = new AllergyIntolerance();

		BaseOpenMRSDataUtil.setBaseExtensionFields(allergyIntolerance, allergy);

		allergyIntolerance.setId(allergy.getUuid());
		allergyIntolerance.setPatient(FHIRUtils.buildPatientOrPersonResourceReference(allergy.getPerson()));
		allergyIntolerance.setCriticality(buildCriticality(allergy));

		for (Enumeration<AllergyIntolerance.AllergyIntoleranceCategory> category : buildCategory(allergy)) {
			allergyIntolerance.addCategory(category.getValue());
		}

		allergyIntolerance.addReaction(buildReaction(allergy));

		for (Annotation note : buildNote(allergy)) {
			allergyIntolerance.addNote(note);
		}

		allergyIntolerance.setCode(buildCode(allergy));
		return allergyIntolerance;
	}

	public static Allergy generateAllergy(AllergyIntolerance allergyIntolerance, List<String> errors) {
		Allergy allergy = new Allergy();
		allergy.setUuid(allergyIntolerance.getId());
		allergy.setPerson(buildPerson(allergyIntolerance));
		allergy.setSeverity(buildSeverity(allergyIntolerance));
		allergy.setAllergen(buildAllergen(allergyIntolerance));
		if (allergy.getAllergen() != null) {
			allergy.setAllergyType(buildAllergenType(allergyIntolerance));
		}
		allergy.setReaction(buildReaction(allergyIntolerance, errors));
		allergy.setComments(buildComment(allergyIntolerance));
		return allergy;
	}

	private static Person buildPerson(AllergyIntolerance allergyIntolerance) {
		String personUuid = FHIRUtils.getObjectUuidByReference(allergyIntolerance.getPatient());
		return Context.getPersonService().getPersonByUuid(personUuid);
	}

	public static Allergy updateAllergyAttributes(Allergy allergy, Allergy newAllergy) {
		allergy.setPerson(newAllergy.getPerson());
		allergy.setSeverity(newAllergy.getSeverity());
		allergy.setAllergyType(newAllergy.getAllergyType());
		allergy.setReaction(newAllergy.getReaction());
		allergy.setAllergen(newAllergy.getAllergen());
		allergy.setComments(newAllergy.getComments());
		return allergy;
	}

	private static List<Annotation> buildNote(Allergy allergy) {
		if (StringUtils.isNotEmpty(allergy.getComments())) {
			Annotation annotation = new Annotation();
			annotation.setText(allergy.getComments());
			return Collections.singletonList(annotation);
		}
		return new ArrayList<>();
	}

	private static String buildComment(AllergyIntolerance allergyIntolerance) {
		if (CollectionUtils.isNotEmpty(allergyIntolerance.getNote())) {
			return allergyIntolerance.getNoteFirstRep().getText();
		}
		return null;
	}

	private static AllergyIntolerance.AllergyIntoleranceReactionComponent buildReaction(Allergy allergy) {
		Concept reaction = allergy.getReaction();
		if (reaction != null) {
			AllergyIntolerance.AllergyIntoleranceReactionComponent entry =
					new AllergyIntolerance.AllergyIntoleranceReactionComponent();
			entry.addManifestation(buildManifestation(reaction));
			return entry;
		}
		return null;
	}

	private static Concept buildReaction(AllergyIntolerance allergyIntolerance, List<String> errors) {
		if (CollectionUtils.isNotEmpty(allergyIntolerance.getReaction())) {
			AllergyIntolerance.AllergyIntoleranceReactionComponent reaction = allergyIntolerance.getReaction().get(FIRST);
			return buildReactionsFromManifestation(reaction, errors);
		} else {
			errors.add("The allergy intolerance reaction can't be empty.");
		}
		return null;
	}

	private static Concept buildReactionsFromManifestation(
			AllergyIntolerance.AllergyIntoleranceReactionComponent reactionComponent, List<String> errors) {
		if (CollectionUtils.isNotEmpty(reactionComponent.getManifestation())) {
			CodeableConcept codeableConcept = reactionComponent.getManifestation().get(FIRST);
			Concept concept = FHIRUtils.getConceptByCodeableConcept(codeableConcept);
			if (concept == null) {
				errors.add(String.format("Couldn't find concept %s", codeableConcept.getText()));
			}
			return concept;
		} else {
			errors.add("The allergy intolerance reaction manifestation can't be empty.");
		}
		return null;
	}

	private static CodeableConcept buildManifestation(Concept reaction) {
		return FHIRUtils.createCodeableConcept(reaction);
	}

	private static CodeableConcept buildCode(Allergy allergy) {
		if (allergy.getAllergen() != null) {
			return FHIRUtils.createCodeableConcept(allergy.getAllergen());
		}
		return null;
	}

	private static Concept buildAllergen(AllergyIntolerance allergyIntolerance) {
		if (allergyIntolerance.getCode() != null) {
			return FHIRUtils.getConceptByCodeableConcept(allergyIntolerance.getCode());
		}
		return null;
	}

	private static List<Enumeration<AllergyIntolerance.AllergyIntoleranceCategory>> buildCategory(Allergy allergy) {
		List<Enumeration<AllergyIntolerance.AllergyIntoleranceCategory>> categories = new ArrayList<>();
		if (allergy.getAllergyType() != null) {
			Enumeration<AllergyIntolerance.AllergyIntoleranceCategory> category = new Enumeration<>(
					new AllergyIntolerance.AllergyIntoleranceCategoryEnumFactory());
			switch (allergy.getAllergyType()) {
				case DRUG:
					category.setValue(AllergyIntolerance.AllergyIntoleranceCategory.MEDICATION);
					break;
				case ENVIRONMENT:
					category.setValue(AllergyIntolerance.AllergyIntoleranceCategory.ENVIRONMENT);
					break;
				case FOOD:
					category.setValue(AllergyIntolerance.AllergyIntoleranceCategory.FOOD);
					break;
				case ANIMAL:
					category.setValue(AllergyIntolerance.AllergyIntoleranceCategory.BIOLOGIC);
					break;
				case PLANT:
				case POLLEN:
				default:
					category.setValue(AllergyIntolerance.AllergyIntoleranceCategory.NULL);
					break;
			}
			categories.add(category);
		}
		return categories;
	}

	private static AllergyType buildAllergenType(AllergyIntolerance allergyIntolerance) {
		if (CollectionUtils.isNotEmpty(allergyIntolerance.getCategory())) {
			Enumeration<AllergyIntolerance.AllergyIntoleranceCategory> cat = allergyIntolerance.getCategory().get(0);
			switch (cat.getValue()) {
				case MEDICATION:
					return AllergyType.DRUG;
				case ENVIRONMENT:
					return AllergyType.ENVIRONMENT;
				case FOOD:
					return AllergyType.FOOD;
				case BIOLOGIC:
					return AllergyType.ANIMAL;
				default:
					return AllergyType.OTHER;
			}
		}
		return null;
	}

	private static AllergyIntolerance.AllergyIntoleranceCriticality buildCriticality(Allergy allergy) {
		if (allergy.getSeverity() != null) {
			if (allergy.getSeverity().equals(AllergySeverity.MILD)) {
				return AllergyIntolerance.AllergyIntoleranceCriticality.LOW;
			} else if (allergy.getSeverity().equals(AllergySeverity.MODERATE)) {
				return AllergyIntolerance.AllergyIntoleranceCriticality.UNABLETOASSESS;
			} else if (allergy.getSeverity().equals(AllergySeverity.SEVERE)) {
				return AllergyIntolerance.AllergyIntoleranceCriticality.HIGH;
			} else {
				return AllergyIntolerance.AllergyIntoleranceCriticality.NULL;
			}
		}
		return null;
	}

	private static AllergySeverity buildSeverity(AllergyIntolerance allergyIntolerance) {
		AllergyIntolerance.AllergyIntoleranceCriticality criticality = allergyIntolerance.getCriticality();
		if (criticality != null) {
			switch (criticality) {
				case LOW:
					return AllergySeverity.MILD;
				case UNABLETOASSESS:
					return AllergySeverity.MODERATE;
				case HIGH:
					return AllergySeverity.SEVERE;
				default:
					return AllergySeverity.UNKNOWN;
			}
		}
		return AllergySeverity.UNKNOWN;
	}
}
