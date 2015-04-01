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

import ca.uhn.fhir.model.api.IValueSetEnumBinder;
import ca.uhn.fhir.model.dstu2.composite.BoundCodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import ca.uhn.fhir.model.dstu2.resource.AllergyIntolerance;
import ca.uhn.fhir.model.dstu2.resource.Substance;
import ca.uhn.fhir.model.dstu2.valueset.AllergyIntoleranceCategoryEnum;
import ca.uhn.fhir.model.dstu2.valueset.AllergyIntoleranceCriticalityEnum;
import ca.uhn.fhir.model.dstu2.valueset.SubstanceTypeEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import org.openmrs.ConceptMap;
import org.openmrs.Obs;
import org.openmrs.module.allergyapi.Allergy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FHIRAllergyIntoleranceUtil {

	public static AllergyIntolerance generateAllergyTolerance(Obs obs) {
		return null;
	}

	public static AllergyIntolerance generateAllergyTolerance(Allergy allergy) {
		return null;
	}

	public static AllergyIntolerance generateAllergyTolerance(org.openmrs.activelist.Allergy allergy) {
		AllergyIntolerance allergyIntolerance = new AllergyIntolerance();
		allergyIntolerance.setId(allergy.getUuid());
		//Build and set patient reference
		allergyIntolerance.setSubject(FHIRUtils.buildPatientOrPersonResourceReference(allergy.getPerson()));
		//Set record date
		DateTimeDt recordedDate = new DateTimeDt();
		recordedDate.setValue(allergy.getStartDate());
		allergyIntolerance.setRecordedDate(recordedDate);
		//Set critically
		switch (allergy.getSeverity()) {
			case INTOLERANCE:
				allergyIntolerance.setCriticality(AllergyIntoleranceCriticalityEnum.LOW_RISK);
				break;
			case MODERATE:
				allergyIntolerance.setCriticality(AllergyIntoleranceCriticalityEnum.LOW_RISK);
				break;
			case MILD:
				allergyIntolerance.setCriticality(AllergyIntoleranceCriticalityEnum.LOW_RISK);
				break;
			case SEVERE:
				allergyIntolerance.setCriticality(AllergyIntoleranceCriticalityEnum.HIGH_RISK);
				break;
			default:
				allergyIntolerance.setCriticality(AllergyIntoleranceCriticalityEnum.UNABLE_TO_DETERMINE);
				break;
		}

		//Set allergy category
		if (allergy.getAllergyType() != null) {
			switch (allergy.getAllergyType()) {
				case ANIMAL:
					allergyIntolerance.setCategory(AllergyIntoleranceCategoryEnum.ENVIRONMENT);
					break;
				case DRUG:
					allergyIntolerance.setCategory(AllergyIntoleranceCategoryEnum.MEDICATION);
					break;
				case ENVIRONMENT:
					allergyIntolerance.setCategory(AllergyIntoleranceCategoryEnum.ENVIRONMENT);
					break;
				case FOOD:
					allergyIntolerance.setCategory(AllergyIntoleranceCategoryEnum.FOOD);
					break;
				case PLANT:
					allergyIntolerance.setCategory(AllergyIntoleranceCategoryEnum.ENVIRONMENT);
					break;
				case POLLEN:
					allergyIntolerance.setCategory(AllergyIntoleranceCategoryEnum.ENVIRONMENT);
					break;
				default:
					allergyIntolerance.setCategory(AllergyIntoleranceCategoryEnum.ENVIRONMENT);
					break;
			}
		}
		//Set allergen
		Collection<ConceptMap> mappings = allergy.getAllergen().getConceptMappings();
		List<CodingDt> dts = allergyIntolerance.getSubstance().getCoding();

		for (ConceptMap map : mappings) {
			//Set concept name as the display value and set concept uuid if name is empty
			if (map.getConceptReferenceTerm() != null) {
				String display = map.getConceptReferenceTerm().getName();
				if (display == null || display.isEmpty()) {
					display = map.getConceptReferenceTerm().getUuid();
				}

				//Set concept mappings of concept
				if (FHIRConstants.CIEL.equalsIgnoreCase(map.getConceptReferenceTerm().getName())) {
					dts.add(new CodingDt().setCode(map.getConceptReferenceTerm().getCode()).setDisplay(display).setSystem(
							FHIRConstants.ciel));
				} else if (FHIRConstants.SNOMED.equalsIgnoreCase(map.getConceptReferenceTerm().getName())) {
					dts.add(new CodingDt().setCode(map.getConceptReferenceTerm().getCode()).setDisplay(display).setSystem(
							FHIRConstants.snomed));
				} else if (FHIRConstants.LOINC.equalsIgnoreCase(map.getConceptReferenceTerm().getName())) {
					dts.add(new CodingDt().setCode(map.getConceptReferenceTerm().getCode()).setDisplay(display).setSystem(
							FHIRConstants.loinc));
				} else {
					dts.add(new CodingDt().setCode(map.getConceptReferenceTerm().getCode()).setDisplay(display).setSystem(
							FHIRConstants.other));
				}
			}
		}
		//Setting default omrs concept
		dts.add(new CodingDt().setCode(allergy.getAllergen().getUuid()).setDisplay(allergy.getAllergen().getName().getName())
				.setSystem(FHIRConstants.openmrs));
		allergyIntolerance.getSubstance().setCoding(dts);
		return allergyIntolerance;
	}

	public static Obs generateAllergyObs(AllergyIntolerance allergy) {
		return null;
	}

	public static Allergy generateAllergyModuleAllergy(AllergyIntolerance allergy) {
		return null;
	}

	public static org.openmrs.activelist.Allergy generateActiveListAllergy(AllergyIntolerance allergy) {
		return null;
	}
}
