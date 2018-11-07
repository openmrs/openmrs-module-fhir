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
package org.openmrs.module.fhir.api.strategies.condition;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.hl7.fhir.dstu3.model.Annotation;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Condition;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Reference;
import org.openmrs.ConceptMap;
import org.openmrs.Obs;
import org.openmrs.PersonName;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.openmrs.module.fhir.api.util.FHIRUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ObsConditionStrategy implements GenericConditionStrategy {

	/**
	 * @param uuid the uuid of the Observation
	 * @return fhir condition resource and if condition is not found for the given id then return null
	 */
	@Override
	public Condition getConditionById(String uuid) {
		int[] conceptsAsConditions = FHIRUtils.getConceptIdsOfConditions();
		Obs obs = Context.getObsService().getObsByUuid(uuid);
		if (obs == null || obs.isVoided() || conceptsAsConditions == null || !ArrayUtils.contains
				(conceptsAsConditions, obs.getConcept().getId())) {
			return null;
		}

		return generateFHIRConditionForOpenMRSObs(obs);
	}

	@Override
	public List<Condition> searchConditionById(String uuid) {
		return null;
	}

	@Override
	public List<Condition> searchConditionByName(String name) {
		return null;
	}

	/**
	 * Returns the fhir Condition for the given OpenMrs Obs. Mapping is done as follows
	 * FhirCondition.Identifier: Obs.uuid
	 * FhirCondition.Patient: Link to FHIR Patient
	 * FhirCondition.Encounter: Link to FHIR Encounter resource
	 * FhirCondition.Asserter: Link to FHIR Provider
	 * FhirCondition.DateAsserted: Date created
	 * FhirCondition.Code: concept mappings for this concept
	 * FhirCondition.Note: Obs.comment
	 *
	 * @param openMrsObs Openmrs Obs object to be mapped to fhir Condition
	 * @return FHIR condition resource
	 */
	private Condition generateFHIRConditionForOpenMRSObs(Obs openMrsObs) {

		org.hl7.fhir.dstu3.model.Condition fhirCondition = new org.hl7.fhir.dstu3.model.Condition();
		IdType id = new IdType();
		id.setValue(openMrsObs.getUuid());
		fhirCondition.setId(id);

		if (openMrsObs.getPerson().isPatient()) {
			Reference patient = FHIRUtils.buildPatientOrPersonResourceReference(openMrsObs.getPerson());
			fhirCondition.setSubject(patient);
			//Set Encounter
			if (openMrsObs.getEncounter() != null) {
				fhirCondition.setContext(buildPatientReference(openMrsObs.getEncounter()));
			}
		}

		//Set Asserter
		fhirCondition.setAssertedDate(openMrsObs.getDateCreated());

		if (openMrsObs.getConcept() != null) {
			CodeableConcept conceptDt = fhirCondition.getCode();
			Collection<ConceptMap> mappings = openMrsObs.getConcept().getConceptMappings();
			List<Coding> dts = conceptDt.getCoding();
			if (mappings != null && !mappings.isEmpty()) {
				for (ConceptMap map : mappings) {
					if (map.getConceptReferenceTerm() != null) {
						dts.add(FHIRUtils.createCoding(map));
					}
				}
			}
			if (openMrsObs.getConcept().getName() != null) {
				dts.add(new Coding().setCode(openMrsObs.getConcept().getUuid()).setDisplay(
						openMrsObs.getConcept().getName().getName()).setSystem(FHIRConstants.OPENMRS_URI));
			} else {
				dts.add(new Coding().setCode(openMrsObs.getConcept().getUuid()).setSystem(
						FHIRConstants.OPENMRS_URI));
			}
			conceptDt.setCoding(dts);
			fhirCondition.setCode(conceptDt);
		}

		if (!StringUtils.isEmpty(openMrsObs.getComment())) {
			List<Annotation> annotations = new ArrayList<Annotation>();
			Annotation annotation = new Annotation();
			annotation.setText(openMrsObs.getComment());
			fhirCondition.setNote(annotations);
		}

		return fhirCondition;
	}

	private Reference buildPatientReference(org.openmrs.Encounter omrsEncounter) {
		//Build and set patient reference
		Reference patientReference = new Reference();
		PersonName name = omrsEncounter.getPatient().getPersonName();
		StringBuilder nameDisplay = new StringBuilder();
		nameDisplay.append(name.getGivenName());
		nameDisplay.append(" ");
		nameDisplay.append(name.getFamilyName());
		String patientUri;
		nameDisplay.append("(");
		nameDisplay.append(FHIRConstants.IDENTIFIER);
		nameDisplay.append(":");
		nameDisplay.append(omrsEncounter.getPatient().getPatientIdentifier().getIdentifier());
		nameDisplay.append(")");
		patientUri = FHIRConstants.PATIENT + "/" + omrsEncounter.getPatient().getUuid();
		patientReference.setReference(patientUri);
		patientReference.setDisplay(nameDisplay.toString());
		return patientReference;
	}
}
