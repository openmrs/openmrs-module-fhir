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

import org.apache.commons.lang.StringUtils;
import org.hl7.fhir.dstu3.model.ProcedureRequest;
import org.hl7.fhir.dstu3.model.Reference;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.TestOrder;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.comparator.ProcedureRequestComparator;
import org.openmrs.module.fhir.api.constants.ExtensionURL;

import java.util.Collections;
import java.util.List;

public class FHIRProcedureRequestUtil {

	private static final String EMPTY_UUID = "Uuid cannot be empty";
	private static final String SUBJECT_NOT_FOUND = "Subject with id: '%s' not found";
	private static final String REQUESTER_NOT_FOUND = "Requester with id: '%s' not found";
	private static final String CONTEXT_NOT_FOUND = "Context with id: '%s' not found";
	private static final String LATERALITY_EMPTY = "Laterality cannot be empty";
	private static final String LATERALITY_VALUE_INCORRECT = "Laterality value should be LEFT, RIGHT or BILATERAL.";
	private static final String CONCEPT_NOT_FOUND = "Concept with id: '%s' not found";
	private static final String ORDER_FREQUENCY_NOT_FOUND = "OrderFrequency with id: '%s' not found";

	public static boolean areProcedureRequestsEqual(Object o1, Object o2) {
		return o1 instanceof ProcedureRequest && o2 instanceof ProcedureRequest
				&& new ProcedureRequestComparator().areEquals((ProcedureRequest) o1, (ProcedureRequest) o2);
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
		procedureRequest.addExtension(ExtensionsUtil.createOrderFrequencyExtension(testOrder));

		return procedureRequest;
	}

	public static TestOrder generateTestOrder(ProcedureRequest procedureRequest, List<String> errors) {
		TestOrder testOrder = new TestOrder();

		BaseOpenMRSDataUtil.readBaseExtensionFields(testOrder, procedureRequest);

		if (procedureRequest.getId() != null) {
            testOrder.setUuid(FHIRUtils.extractUuid(procedureRequest.getId()));
        }
		testOrder.setSpecimenSource(getSpecimenSource(procedureRequest.getSpecimenFirstRep()));
		ContextUtil.getTestOrderHelper()
				.setFrequencyByString(testOrder, getOrderFrequencyFromExtension(procedureRequest, errors));
		testOrder.setPatient(getOpenMRSPatient(procedureRequest.getSubject(), errors));
		ContextUtil.getTestOrderHelper().setOrderer(testOrder, FHIRRequestUtil
				.getOrdererUuid(procedureRequest, errors));
		testOrder.setUrgency(getUrgency(procedureRequest));
		testOrder.setEncounter(getEncounter(procedureRequest.getContext(), errors));
		ContextUtil.getTestOrderHelper().setCareSettingByString(testOrder, FHIRRequestUtil
				.getCareSetting(procedureRequest, errors));
		testOrder.setConcept(getConceptFromExtension(procedureRequest, errors));
		testOrder.setLaterality(getLateralityFromExtension(procedureRequest, errors));
		testOrder.setClinicalHistory(getClinicalHistoryFromExtension(procedureRequest));
		ContextUtil.getTestOrderHelper().setAction(testOrder, procedureRequest.getStatus());

		return testOrder;
	}

	//region OpenMRS methods

	private static TestOrder.Laterality getLateralityFromExtension(ProcedureRequest procedureRequest, List<String> errors) {
		String lateralityString = ExtensionsUtil.getStringFromExtension(
				ExtensionsUtil.getExtension(ExtensionURL.LATERALITY_URL, procedureRequest));

		if (StringUtils.isEmpty(lateralityString)) {
			errors.add(LATERALITY_EMPTY);
			return null;
		}
		TestOrder.Laterality laterality;
		try {
			laterality = TestOrder.Laterality.valueOf(lateralityString.toUpperCase());
		} catch (IllegalArgumentException e) {
			errors.add(LATERALITY_VALUE_INCORRECT);
			return null;
		}
		return laterality;
	}

	private static Concept getConceptFromExtension(ProcedureRequest procedureRequest, List<String> errors) {
		String conceptUuid = ExtensionsUtil.getStringFromExtension(
				ExtensionsUtil.getExtension(ExtensionURL.ORDER_CONCEPT_URL, procedureRequest));

		if (StringUtils.isEmpty(conceptUuid)) {
			errors.add(EMPTY_UUID);
			return null;
		}

		Concept concept = Context.getConceptService().getConceptByUuid(conceptUuid);
		if (concept == null) {
			errors.add(String.format(CONCEPT_NOT_FOUND, conceptUuid));
		}
		return concept;
	}

	private static Encounter getEncounter(Reference context, List<String> errors) {
		String uuid = FHIRUtils.extractUuid(context.getId());
		if (StringUtils.isEmpty(uuid)) {
			errors.add(EMPTY_UUID);
			return null;
		}
		Encounter encounter = Context.getEncounterService().getEncounterByUuid(uuid);
		if (encounter == null) {
			errors.add(String.format(CONTEXT_NOT_FOUND, uuid));
		}
		return encounter;
	}

	private static Order.Urgency getUrgency(ProcedureRequest procedureRequest) {
		switch (procedureRequest.getPriority()) {
			case STAT:
				return Order.Urgency.STAT;
			case ROUTINE:
			default:
				return Order.Urgency.ROUTINE;
		}
	}

	private static Patient getOpenMRSPatient(Reference subject, List<String> errors) {
		String uuid = FHIRUtils.extractUuid(subject.getId());
		if (StringUtils.isEmpty(uuid)) {
			errors.add(EMPTY_UUID);
			return null;
		}
		Patient patient = Context.getPatientService().getPatientByUuid(uuid);
		if (patient == null) {
			errors.add(String.format(SUBJECT_NOT_FOUND, uuid));
		}
		return patient;
	}

	private static Concept getSpecimenSource(Reference specimenReference) {
		String uuid = specimenReference.getId();
		if (StringUtils.isEmpty(uuid)) {
			return null;
		}
		return Context.getConceptService().getConceptByUuid(uuid);
	}

	private static String getOrderFrequencyFromExtension(ProcedureRequest procedureRequest, List<String> errors) {
		String orderFrequencyUuid = ExtensionsUtil.getStringFromExtension(
				ExtensionsUtil
						.getExtension(ExtensionURL.ORDER_FREQUENCY_URL, procedureRequest));
		if (StringUtils.isEmpty(orderFrequencyUuid)) {
			errors.add(EMPTY_UUID);
			return null;
		}

		return orderFrequencyUuid;
	}

	private static String getClinicalHistoryFromExtension(ProcedureRequest procedureRequest) {
		return ExtensionsUtil.getStringFromExtension(
				ExtensionsUtil
						.getExtension(ExtensionURL.CLINICAL_HISTORY_URL, procedureRequest));
	}

	//endregion

	//region FHIR methods

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
		if (ContextUtil.getTestOrderHelper().isActive(testOrder)) {
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

	//endregion
}
