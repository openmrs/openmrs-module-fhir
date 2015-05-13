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
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.primitive.DateDt;
import ca.uhn.fhir.model.primitive.IdDt;
import org.openmrs.ConceptMap;
import org.openmrs.Condition;

import java.util.Collection;
import java.util.List;

public class FHIRConditionUtil {

	public static ca.uhn.fhir.model.dstu2.resource.Condition generateFHIRCondition(Condition condition) {
		ca.uhn.fhir.model.dstu2.resource.Condition fhirCondition = new ca.uhn.fhir.model.dstu2.resource.Condition();
		IdDt id = new IdDt();
		id.setValue(condition.getUuid());
		fhirCondition.setId(id);

		//Set patient reference
		ResourceReferenceDt patient = FHIRUtils.buildPatientOrPersonResourceReference(condition.getPatient());
		fhirCondition.setPatient(patient);

		//Set on set date
		DateDt dateDt = new DateDt();
		dateDt.setValue(condition.getDateChanged());
		fhirCondition.setDateAsserted(dateDt);

		//Set condtion concept
		if(condition.getConcept() != null) {
			CodeableConceptDt conceptDt = fhirCondition.getCode();
			//Set allergen
			Collection<ConceptMap> mappings = condition.getConcept().getConceptMappings();
			List<CodingDt> dts = conceptDt.getCoding();

			//Set concept codings
			if (mappings != null && !mappings.isEmpty()) {
				for (ConceptMap map : mappings) {
					if (map.getConceptReferenceTerm() != null) {
						dts.add(FHIRUtils.getCodingDtByConceptMappings(map));
					}
				}
			}

			//Setting default omrs concept
			if (condition.getConcept().getName() != null) {
				dts.add(new CodingDt().setCode(condition.getConcept().getUuid()).setDisplay(
						condition.getConcept().getName().getName()).setSystem(FHIRConstants.OPENMRS_URI));
			} else {
				dts.add(new CodingDt().setCode(condition.getConcept().getUuid()).setSystem(
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
