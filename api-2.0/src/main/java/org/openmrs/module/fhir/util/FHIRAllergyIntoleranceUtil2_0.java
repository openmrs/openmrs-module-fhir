/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.fhir.util;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hl7.fhir.dstu3.model.AllergyIntolerance;
import org.hl7.fhir.dstu3.model.Annotation;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Enumeration;
import org.openmrs.Allergen;
import org.openmrs.AllergenType;
import org.openmrs.Allergy;
import org.openmrs.AllergyReaction;
import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.BaseOpenMRSDataUtil;
import org.openmrs.module.fhir.api.util.FHIRUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hl7.fhir.dstu3.model.AllergyIntolerance.AllergyIntoleranceCategory;
import static org.hl7.fhir.dstu3.model.AllergyIntolerance.AllergyIntoleranceCriticality;
import static org.hl7.fhir.dstu3.model.AllergyIntolerance.AllergyIntoleranceReactionComponent;

public class FHIRAllergyIntoleranceUtil2_0 {

	public static AllergyIntolerance generateAllergyIntolerance(Allergy allergy) {
		AllergyIntolerance allergyIntolerance = new AllergyIntolerance();

		BaseOpenMRSDataUtil.setBaseExtensionFields(allergyIntolerance, allergy);

		allergyIntolerance.setId(allergy.getUuid());
		allergyIntolerance.setPatient(FHIRUtils.buildPatientOrPersonResourceReference(allergy.getPatient()));
		allergyIntolerance.setCriticality(buildCriticality(allergy));

		for (Enumeration<AllergyIntoleranceCategory> category : buildCategory(allergy)) {
			allergyIntolerance.addCategory(category.getValue());
		}

		for (AllergyIntoleranceReactionComponent reaction : buildReaction(allergy)) {
			allergyIntolerance.addReaction(reaction);
		}

		for (Annotation note : buildNote(allergy)) {
			allergyIntolerance.addNote(note);
		}

		allergyIntolerance.setCode(buildCode(allergy));
		return allergyIntolerance;
	}

	public static Allergy generateAllergy(AllergyIntolerance allergyIntolerance, List<String> errors) {
		Allergy allergy = new Allergy();
		allergy.setUuid(allergyIntolerance.getId());
		allergy.setPatient(buildPatient(allergyIntolerance));
		allergy.setSeverity(buildSeverity(allergyIntolerance));
		allergy.setAllergen(buildAllergen(allergyIntolerance));
		if (allergy.getAllergen() != null) {
			allergy.setAllergenType(buildAllergenType(allergyIntolerance));
		}
		for (AllergyReaction reaction : buildReactions(allergyIntolerance, errors)) {
			allergy.addReaction(reaction);
		}
		allergy.setComment(buildComment(allergyIntolerance));
		return allergy;
	}

	private static Patient buildPatient(AllergyIntolerance allergyIntolerance) {
		String patientId = FHIRUtils.getObjectUuidByReference(allergyIntolerance.getPatient());
		return Context.getPatientService().getPatientByUuid(patientId);
	}

	public static Allergy updateAllergyAttributes(Allergy allergy, Allergy newAllergy) {
		allergy.setPatient(newAllergy.getPatient());
		allergy.setSeverity(newAllergy.getSeverity());
		allergy.setAllergenType(newAllergy.getAllergenType());
		for (AllergyReaction reaction : newAllergy.getReactions()) {
			allergy.addReaction(reaction);
		}
		allergy.setAllergen(newAllergy.getAllergen());
		allergy.setComment(newAllergy.getComment());
		return allergy;
	}

	private static List<Annotation> buildNote(Allergy allergy) {
		if (StringUtils.isNotEmpty(allergy.getComment())) {
			Annotation annotation = new Annotation();
			annotation.setText(allergy.getComment());
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

	private static List<AllergyIntoleranceReactionComponent> buildReaction(Allergy allergy) {
		if (CollectionUtils.isNotEmpty(allergy.getReactions())) {
			AllergyIntoleranceReactionComponent entry = new AllergyIntoleranceReactionComponent();
			for (CodeableConcept codeableConcept : buildManifestations(allergy.getReactions())) {
				entry.addManifestation(codeableConcept);
			}
			return Collections.singletonList(entry);
		}
		return new ArrayList<>();
	}

	private static List<AllergyReaction> buildReactions(AllergyIntolerance allergyIntolerance, List<String> errors) {
		List<AllergyReaction> result = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(allergyIntolerance.getReaction())) {
			for (AllergyIntoleranceReactionComponent reaction : allergyIntolerance.getReaction()) {
				result.addAll(buildReactionsFromManifestation(reaction, errors));
			}
		} else {
			errors.add("The allergy intolerance reaction can't be empty.");
		}
		return result;
	}

	private static List<AllergyReaction> buildReactionsFromManifestation(
			AllergyIntoleranceReactionComponent reactionComponent, List<String> errors) {
		List<AllergyReaction> result = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(reactionComponent.getManifestation())) {
			for (CodeableConcept codeableConcept : reactionComponent.getManifestation()) {
				AllergyReaction allergyReaction = new AllergyReaction();
				if (codeableConcept.getCoding().isEmpty()) {
					allergyReaction.setReactionNonCoded(codeableConcept.getText());
				} else {
					Concept concept = FHIRUtils.getConceptByCodeableConcept(codeableConcept);
					if (concept == null) {
						errors.add(String.format("Couldn't find concept %s", codeableConcept.getText()));
					}
					allergyReaction.setReaction(concept);
				}
				result.add(allergyReaction);
			}
		} else {
			errors.add("The allergy intolerance reaction manifestation can't be empty.");
		}
		return result;
	}

	private static List<CodeableConcept> buildManifestations(List<AllergyReaction> reactions) {
		List<CodeableConcept> result = new ArrayList<>();
		for (AllergyReaction reaction : reactions) {
			result.add(buildManifestation(reaction));
		}
		return result;
	}

	private static CodeableConcept buildManifestation(AllergyReaction reaction) {
		CodeableConcept result = new CodeableConcept();
		if (StringUtils.isNotEmpty(reaction.getReactionNonCoded())) {
			result.setText(reaction.getReactionNonCoded());
		} else {
			result = FHIRUtils.createCodeableConcept(reaction.getReaction());
		}
		return result;
	}

	private static CodeableConcept buildCode(Allergy allergy) {
		if (allergy.getAllergen() != null) {
			if (StringUtils.isNotEmpty(allergy.getAllergen().getNonCodedAllergen())) {
				CodeableConcept codeableConcept = new CodeableConcept();
				codeableConcept.setText(allergy.getAllergen().getNonCodedAllergen());
				return codeableConcept;
			} else {
				return FHIRUtils.createCodeableConcept(allergy.getAllergen().getCodedAllergen());
			}
		}
		return null;
	}

	private static Allergen buildAllergen(AllergyIntolerance allergyIntolerance) {
		if (allergyIntolerance.getCode() != null) {
			Allergen result = new Allergen();
			if (allergyIntolerance.getCode().getCoding().isEmpty()) {
				result.setNonCodedAllergen(allergyIntolerance.getCode().getText());
			} else {
				result.setCodedAllergen(FHIRUtils.getConceptByCodeableConcept(allergyIntolerance.getCode()));
			}
			return result;
		}
		return null;
	}

	private static List<Enumeration<AllergyIntoleranceCategory>> buildCategory(Allergy allergy) {
		List<Enumeration<AllergyIntoleranceCategory>> categories = new ArrayList<>();
		if (allergy.getAllergen() != null && allergy.getAllergen().getAllergenType() != null) {
			Enumeration<AllergyIntoleranceCategory> category = new Enumeration<>(
					new AllergyIntolerance.AllergyIntoleranceCategoryEnumFactory());
			switch (allergy.getAllergen().getAllergenType()) {
				case DRUG:
					category.setValue(AllergyIntoleranceCategory.MEDICATION);
					break;
				case ENVIRONMENT:
					category.setValue(AllergyIntoleranceCategory.ENVIRONMENT);
					break;
				case FOOD:
					category.setValue(AllergyIntoleranceCategory.FOOD);
					break;
				default:
					category.setValue(AllergyIntoleranceCategory.ENVIRONMENT);
					break;
			}
			categories.add(category);
		}
		return categories;
	}

	private static AllergenType buildAllergenType(AllergyIntolerance allergyIntolerance) {
		if (CollectionUtils.isNotEmpty(allergyIntolerance.getCategory())) {
			Enumeration<AllergyIntoleranceCategory> cat = allergyIntolerance.getCategory().get(0);
			switch (cat.getValue()) {
				case MEDICATION:
					return AllergenType.DRUG;
				case ENVIRONMENT:
					return AllergenType.ENVIRONMENT;
				case FOOD:
					return AllergenType.FOOD;
				default:
					return null;
			}
		}
		return null;
	}

	private static AllergyIntoleranceCriticality buildCriticality(Allergy allergy) {
		if (allergy.getSeverity() != null) {
			if (allergy.getSeverity().equals(FHIRUtils.getMildSeverityConcept())) {
				return AllergyIntoleranceCriticality.LOW;
			} else if (allergy.getSeverity().equals(FHIRUtils.getModerateSeverityConcept())) {
				return AllergyIntoleranceCriticality.UNABLETOASSESS;
			} else if (allergy.getSeverity().equals(FHIRUtils.getSevereSeverityConcept())) {
				return AllergyIntoleranceCriticality.HIGH;
			} else {
				return AllergyIntoleranceCriticality.NULL;
			}
		}
		return null;
	}

	private static Concept buildSeverity(AllergyIntolerance allergyIntolerance) {
		AllergyIntoleranceCriticality criticality = allergyIntolerance.getCriticality();
		if (criticality != null) {
			switch (criticality) {
				case LOW:
					return FHIRUtils.getMildSeverityConcept();
				case UNABLETOASSESS:
					return FHIRUtils.getModerateSeverityConcept();
				case HIGH:
					return FHIRUtils.getSevereSeverityConcept();
				default:
					return null;
			}
		}
		return null;
	}
}
