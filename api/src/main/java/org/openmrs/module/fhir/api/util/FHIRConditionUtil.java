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

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Reference;
import org.openmrs.ConceptMap;
import org.openmrs.Condition;

import java.util.Collection;
import java.util.List;

public class FHIRConditionUtil {

	public static org.hl7.fhir.dstu3.model.Condition generateFHIRCondition(Condition condition) {
		org.hl7.fhir.dstu3.model.Condition fhirCondition = new org.hl7.fhir.dstu3.model.Condition();
		IdType id = new IdType();
		id.setValue(condition.getUuid());
		fhirCondition.setId(id);

		//Set patient reference
		Reference patient = FHIRUtils.buildPatientOrPersonResourceReference(condition.getPatient());
		fhirCondition.setSubject(patient);

		//Set on set date
		fhirCondition.setAssertedDate(condition.getDateChanged());

		//Set condtion concept
		if (condition.getConcept() != null) {
			CodeableConcept conceptDt = fhirCondition.getCode();
			//Set allergen
			Collection<ConceptMap> mappings = condition.getConcept().getConceptMappings();
			List<Coding> dts = conceptDt.getCoding();

			//Set concept codings
			if (mappings != null && !mappings.isEmpty()) {
				for (ConceptMap map : mappings) {
					if (map.getConceptReferenceTerm() != null) {
						dts.add(FHIRUtils.createCoding(map));
					}
				}
			}

			//Setting default omrs concept
			if (condition.getConcept().getName() != null) {
				dts.add(new Coding().setCode(condition.getConcept().getUuid()).setDisplay(
						condition.getConcept().getName().getName()).setSystem(FHIRConstants.OPENMRS_URI));
			} else {
				dts.add(new Coding().setCode(condition.getConcept().getUuid()).setSystem(
						FHIRConstants.OPENMRS_URI));
			}
			conceptDt.setCoding(dts);
			fhirCondition.setCode(conceptDt);
		}

		return fhirCondition;
	}

	public static org.openmrs.Condition generateOpenMRSCondition() {
		return null;
	}
}
