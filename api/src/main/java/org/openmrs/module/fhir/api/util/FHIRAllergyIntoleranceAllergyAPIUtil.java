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
package org.openmrs.module.fhir.api.util;

import org.hl7.fhir.dstu3.model.AllergyIntolerance;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Enumeration;
import org.openmrs.Allergy;
import org.openmrs.AllergyReaction;
import org.openmrs.ConceptMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FHIRAllergyIntoleranceAllergyAPIUtil {

	public static AllergyIntolerance generateAllergyTolerance(Allergy allergy) {
		AllergyIntolerance allergyIntolerance = new AllergyIntolerance();
		allergyIntolerance.setId(allergy.getUuid());

		//Build and set patient reference
		allergyIntolerance.setPatient(FHIRUtils.buildPatientOrPersonResourceReference(allergy.getPatient()));

		//Set record date
		allergyIntolerance.setAssertedDate(allergy.getDateLastUpdated());

		//Set critically
		if (allergy.getSeverity() != null) {
			if (allergy.getSeverity().equals(FHIRUtils.getMildSeverityConcept())) {
				allergyIntolerance.setCriticality(AllergyIntolerance.AllergyIntoleranceCriticality.LOW);
			} else if (allergy.getSeverity().equals(FHIRUtils.getModerateSeverityConcept())) {
				allergyIntolerance.setCriticality(AllergyIntolerance.AllergyIntoleranceCriticality.LOW);
			} else if (allergy.getSeverity().equals(FHIRUtils.getSevereSeverityConcept())) {
				allergyIntolerance.setCriticality(AllergyIntolerance.AllergyIntoleranceCriticality.HIGH);
			} else {
				allergyIntolerance.setCriticality(AllergyIntolerance.AllergyIntoleranceCriticality.UNABLETOASSESS);
			}
		}

		//Set allergy category
		if (allergy.getAllergen().getAllergenType() != null) {
			List<Enumeration<AllergyIntolerance.AllergyIntoleranceCategory>> catagories = new ArrayList();
			Enumeration<AllergyIntolerance.AllergyIntoleranceCategory> enumeration = new Enumeration(
					new AllergyIntolerance.AllergyIntoleranceCategoryEnumFactory());
			switch (allergy.getAllergen().getAllergenType()) {
				case DRUG:
					enumeration.setValue(AllergyIntolerance.AllergyIntoleranceCategory.MEDICATION);
					break;
				case ENVIRONMENT:
					enumeration.setValue(AllergyIntolerance.AllergyIntoleranceCategory.ENVIRONMENT);
					break;
				case FOOD:
					enumeration.setValue(AllergyIntolerance.AllergyIntoleranceCategory.FOOD);
					break;
				default:
					enumeration.setValue(AllergyIntolerance.AllergyIntoleranceCategory.ENVIRONMENT);
					break;
			}
			catagories.add(enumeration);
			allergyIntolerance.setCategory(catagories);
		}

		//Set adverse reaction details
		for (AllergyReaction reaction : allergy.getReactions()) {
			AllergyIntolerance.AllergyIntoleranceReactionComponent event = allergyIntolerance.addReaction();
			List<CodeableConcept> manifest = event.getManifestation();

			//Set allergen
			if (allergy.getAllergen().getCodedAllergen() != null) {
				Collection<ConceptMap> mappings = allergy.getAllergen().getCodedAllergen().getConceptMappings();

				//Set concept codings
				if (mappings != null && !mappings.isEmpty()) {
					for (ConceptMap map : mappings) {
						if (map.getConceptReferenceTerm() != null) {
							allergyIntolerance.addReaction(FHIRUtils.getAllergyReactionComponent(map, event));
						}
					}
				}

				//Setting default omrs concept
				Coding code = new Coding();
				code.setSystem(FHIRConstants.OPENMRS_URI);
				code.setCode(allergy.getAllergen().getCodedAllergen().getUuid());
				CodeableConcept substance = new CodeableConcept();
				if (allergy.getAllergen().getCodedAllergen().getName() != null) {
					code.setDisplay(allergy.getAllergen().getCodedAllergen().getName().getName());
				}
				substance.addCoding(code);
				event.setSubstance(substance);
			}

			//Set concept codings reactions
			if (reaction.getReaction() != null) { //TODO need to think about how non coded reactions going to represent
				Collection<ConceptMap> conceptMappings = reaction.getReaction().getConceptMappings();
				if (conceptMappings != null && !conceptMappings.isEmpty()) {
					for (ConceptMap map : conceptMappings) {
						if (map.getConceptReferenceTerm() != null) {
							manifest.add(FHIRUtils.getCodeableConceptConceptMappings(map));
						}
					}
				}
				//Setting omrs concept
				if (reaction.getReaction().getName() != null) {
					manifest.add(new CodeableConcept().addCoding(new Coding().setCode(reaction.getReaction().getUuid()).setDisplay(
							reaction.getReaction().getName().getName()).setSystem(FHIRConstants.OPENMRS_URI)));
				} else {
					manifest.add(new CodeableConcept().addCoding(new Coding().setCode(reaction.getReaction().getUuid()).setSystem(
							FHIRConstants.OPENMRS_URI)));
				}
			}
		}
		return allergyIntolerance;
	}

	public static Allergy generateAllergyModuleAllergy(AllergyIntolerance allergy) {
		return null;
	}
}
