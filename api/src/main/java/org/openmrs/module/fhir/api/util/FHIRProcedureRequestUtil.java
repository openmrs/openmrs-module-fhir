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

import org.hl7.fhir.dstu3.model.ProcedureRequest;
import org.hl7.fhir.dstu3.model.Reference;
import org.openmrs.Concept;
import org.openmrs.Order;
import org.openmrs.TestOrder;

import java.util.Collections;
import java.util.List;

public class FHIRProcedureRequestUtil {

	public static boolean areProcedureRequestsEquals(Object o1, Object o2) {
		//TODO
		return false;
	}

	public static ProcedureRequest generateProcedureRequest(TestOrder testOrder) {
		ProcedureRequest procedureRequest = new ProcedureRequest();

		BaseOpenMRSDataUtil.setBaseExtensionFields(procedureRequest, testOrder);

		procedureRequest.setId(testOrder.getUuid());
		procedureRequest.setSpecimen(getSpecimenReference(testOrder.getSpecimenSource()));
		procedureRequest.setSubject(FHIRRequestUtil.buildSubject(testOrder));
		procedureRequest.setRequester(buildRequester(testOrder));
		procedureRequest.addIdentifier(FHIRUtils.createIdentifier(testOrder.getUuid()));
		procedureRequest.setStatus(buildStatus(testOrder));
		procedureRequest.setIntent(ProcedureRequest.ProcedureRequestIntent.ORDER);
		procedureRequest.setPriority(buildPriority(testOrder));
		procedureRequest.setContext(FHIRRequestUtil.buildContext(testOrder));

		procedureRequest.addExtension(FHIRRequestUtil.buildCareSettingExtension(testOrder));
		procedureRequest.addExtension(ExtensionsUtil.createOrderConceptExtension(testOrder.getConcept()));
		procedureRequest.addExtension(ExtensionsUtil.createLateralityExtension(testOrder.getLaterality()));
		procedureRequest.addExtension(ExtensionsUtil.createClinicalHistoryExtension(testOrder.getClinicalHistory()));
		procedureRequest.addExtension(ExtensionsUtil.createOrderFrequencyExtension(testOrder.getFrequency()));

		return procedureRequest;
	}

	public static TestOrder generateTestOrder(ProcedureRequest fhirProcedureRequest, List<String> errors) {
		//TODO
		return new TestOrder();
	}

	private static List<Reference> getSpecimenReference(Concept specimenSource) {
		Reference specimenReference = new Reference();
		specimenReference.setReference(FHIRConstants.CONCEPT + "/" + specimenSource.getUuid());
		specimenReference.setDisplay(specimenSource.getDisplayString());
		specimenReference.setId(specimenSource.getUuid());

		return Collections.singletonList(specimenReference);
	}

	private static ProcedureRequest.ProcedureRequestPriority buildPriority(TestOrder testOrder) {
		if (Order.Urgency.ROUTINE.toString().equalsIgnoreCase(testOrder.getUrgency().toString())) {
			return ProcedureRequest.ProcedureRequestPriority.ROUTINE;
		} else if (Order.Urgency.STAT.toString().equalsIgnoreCase(testOrder.getUrgency().toString())) {
			return ProcedureRequest.ProcedureRequestPriority.STAT;
		} else {
			return ProcedureRequest.ProcedureRequestPriority.ROUTINE;
		}
	}

	private static ProcedureRequest.ProcedureRequestStatus buildStatus(TestOrder testOrder) {
		if (testOrder.isActive()) {
			return ProcedureRequest.ProcedureRequestStatus.ACTIVE;
		} else if (testOrder.isDiscontinuedRightNow()) {
			return ProcedureRequest.ProcedureRequestStatus.CANCELLED;
		} else {
			return ProcedureRequest.ProcedureRequestStatus.COMPLETED;
		}
	}

	private static ProcedureRequest.ProcedureRequestRequesterComponent buildRequester(TestOrder testOrder) {
		ProcedureRequest.ProcedureRequestRequesterComponent reqComponent =
				new ProcedureRequest.ProcedureRequestRequesterComponent();
		reqComponent.setAgent(FHIRRequestUtil.buildPractitionerReference(testOrder));
		return reqComponent;
	}
}
