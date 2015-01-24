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

import ca.uhn.fhir.model.dstu.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu.resource.Composition;
import ca.uhn.fhir.model.dstu.resource.Composition.Section;
import ca.uhn.fhir.model.primitive.IdDt;
import org.openmrs.EncounterProvider;
import org.openmrs.api.context.Context;

public class FHIREncounterUtil {

	public static Composition generateEncounter(org.openmrs.Encounter openMRSEncounter) {
		Composition composition = new Composition();

		IdDt uuid = new IdDt();

		uuid.setValue(openMRSEncounter.getUuid());
		composition.setId(uuid);

		Section patientSection = composition.addSection();

		IdDt patientUuid = new IdDt();

		patientUuid.setValue(openMRSEncounter.getPatient().getUuid());
		patientSection.setId(patientUuid);

		ResourceReferenceDt patientReference = new ResourceReferenceDt();

		patientReference.setDisplay("Patient");
		String patientUri = Context.getAdministrationService().getGlobalProperty("fhir.uriPrefix") + "/Patient/"
		                    + openMRSEncounter.getPatient().getUuid();

		IdDt patientRef = new IdDt();
		patientRef.setValue(patientUri);
		patientReference.setReference(patientRef);

		patientSection.setSubject(patientReference);

		for (EncounterProvider provider : openMRSEncounter.getEncounterProviders()) {

			Section providerSection = composition.addSection();

			IdDt providerUuid = new IdDt();

			providerUuid.setValue(provider.getUuid());
			providerSection.setId(providerUuid);

			ResourceReferenceDt providerReference = new ResourceReferenceDt();

			providerReference.setDisplay("Provider");
			String providerUri = Context.getAdministrationService().getGlobalProperty("fhir.uriPrefix") + "/Practitioner/"
			                     + provider.getUuid();

			IdDt providerRef = new IdDt();
			providerRef.setValue(providerUri);
			providerReference.setReference(providerRef);

			providerSection.setSubject(providerReference);

		}
		return composition;
	}
}
