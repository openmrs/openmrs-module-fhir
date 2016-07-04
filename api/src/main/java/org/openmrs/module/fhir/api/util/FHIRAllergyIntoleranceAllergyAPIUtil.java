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

import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import ca.uhn.fhir.model.dstu2.resource.AllergyIntolerance;
import ca.uhn.fhir.model.dstu2.valueset.AllergyIntoleranceCategoryEnum;
import ca.uhn.fhir.model.dstu2.valueset.AllergyIntoleranceCertaintyEnum;
import ca.uhn.fhir.model.dstu2.valueset.AllergyIntoleranceCriticalityEnum;
import ca.uhn.fhir.model.dstu2.valueset.AllergyIntoleranceStatusEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import org.openmrs.ConceptMap;
import org.openmrs.Allergy;
import org.openmrs.AllergyReaction;

import java.util.Collection;
import java.util.List;

public class FHIRAllergyIntoleranceAllergyAPIUtil {

	public static AllergyIntolerance generateAllergyTolerance(Allergy allergy) {
		AllergyIntolerance allergyIntolerance = new AllergyIntolerance();
		allergyIntolerance.setId(allergy.getUuid());
		//Build and set patient reference
		allergyIntolerance.setPatient(FHIRUtils.buildPatientOrPersonResourceReference(allergy.getPatient()));

		//Set record date
		DateTimeDt recordedDate = new DateTimeDt();
		recordedDate.setValue(allergy.getDateLastUpdated());
		allergyIntolerance.setRecordedDate(recordedDate);

		//Set critically
		if (allergy.getSeverity() != null) {
			if (allergy.getSeverity().equals(FHIRUtils.getMildSeverityConcept())) {
				allergyIntolerance.setCriticality(AllergyIntoleranceCriticalityEnum.LOW_RISK);
			} else if (allergy.getSeverity().equals(FHIRUtils.getModerateSeverityConcept())) {
				allergyIntolerance.setCriticality(AllergyIntoleranceCriticalityEnum.LOW_RISK);
			} else if (allergy.getSeverity().equals(FHIRUtils.getSevereSeverityConcept())) {
				allergyIntolerance.setCriticality(AllergyIntoleranceCriticalityEnum.HIGH_RISK);
			} else {
				allergyIntolerance.setCriticality(AllergyIntoleranceCriticalityEnum.UNABLE_TO_DETERMINE);
			}
		}

		//Set allergy category
		if (allergy.getAllergen().getAllergenType() != null) {
			switch (allergy.getAllergen().getAllergenType()) {
				case DRUG:
					allergyIntolerance.setCategory(AllergyIntoleranceCategoryEnum.MEDICATION);
					break;
				case ENVIRONMENT:
					allergyIntolerance.setCategory(AllergyIntoleranceCategoryEnum.ENVIRONMENT);
					break;
				case FOOD:
					allergyIntolerance.setCategory(AllergyIntoleranceCategoryEnum.FOOD);
					break;
				default:
					allergyIntolerance.setCategory(AllergyIntoleranceCategoryEnum.ENVIRONMENT);
					break;
			}
		}

		//Set allergen
		if (allergy.getAllergen().getCodedAllergen() != null) {
			Collection<ConceptMap> mappings = allergy.getAllergen().getCodedAllergen().getConceptMappings();
			List<CodingDt> dts = allergyIntolerance.getSubstance().getCoding();

			//Set concept codings
			if (mappings != null && !mappings.isEmpty()) {
				for (ConceptMap map : mappings) {
					if (map.getConceptReferenceTerm() != null) {
						dts.add(FHIRUtils.getCodingDtByConceptMappings(map));
					}
				}
			}

			//Setting default omrs concept
			if (allergy.getAllergen().getCodedAllergen().getName() != null) {
				dts.add(new CodingDt().setCode(allergy.getAllergen().getCodedAllergen().getUuid()).setDisplay(
						allergy.getAllergen().getCodedAllergen().getName().getName()).setSystem(FHIRConstants.OPENMRS_URI));
			} else {
				dts.add(new CodingDt().setCode(allergy.getAllergen().getCodedAllergen().getUuid()).setSystem(
						FHIRConstants.OPENMRS_URI));
			}
			allergyIntolerance.getSubstance().setCoding(dts);
		}
		//Set status
		allergyIntolerance.setStatus(AllergyIntoleranceStatusEnum.CONFIRMED);

		//Set adverse reaction details
		if (allergy.getReactions().size() > 0) {
			List<CodingDt> dts = allergyIntolerance.getSubstance().getCoding();
			for (AllergyReaction reaction : allergy.getReactions()) {
				AllergyIntolerance.Reaction event = allergyIntolerance.addReaction();
				event.setCertainty(AllergyIntoleranceCertaintyEnum.LIKELY);
				CodeableConceptDt manifest = event.getManifestationFirstRep();
				List<CodingDt> manifestCodes = manifest.getCoding();

				//Set concept codings
				if (reaction.getReaction() != null) { //TODO need to think about how non coded reactions going to represent
					Collection<ConceptMap> conceptMappings = reaction.getReaction().getConceptMappings();
					if (conceptMappings != null && !conceptMappings.isEmpty()) {
						for (ConceptMap map : conceptMappings) {
							if (map.getConceptReferenceTerm() != null) {
								manifestCodes.add(FHIRUtils.getCodingDtByConceptMappings(map));
							}
						}
					}
					//Setting omrs concept
					if (reaction.getReaction().getName() != null) {
						dts.add(new CodingDt().setCode(reaction.getReaction().getUuid()).setDisplay(
								reaction.getReaction().getName().getName())
								.setSystem(FHIRConstants.OPENMRS_URI));
					} else {
						dts.add(new CodingDt().setCode(reaction.getReaction().getUuid()).setSystem(
								FHIRConstants.OPENMRS_URI));
					}
				}
				manifest.setCoding(manifestCodes);

			}
		}
		return allergyIntolerance;
	}

	public static Allergy generateAllergyModuleAllergy(AllergyIntolerance allergy) {
		return null;
	}
}
