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

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.dstu3.model.BooleanType;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Dosage;
import org.hl7.fhir.dstu3.model.Duration;
import org.hl7.fhir.dstu3.model.Extension;
import org.hl7.fhir.dstu3.model.MedicationRequest;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.SimpleQuantity;
import org.hl7.fhir.dstu3.model.Timing;
import org.hl7.fhir.exceptions.FHIRException;
import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.DrugOrder;
import org.openmrs.Encounter;
import org.openmrs.Order;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.comparator.MedicationRequestComparator;
import org.openmrs.module.fhir.api.constants.ExtensionURL;

import java.util.Collections;
import java.util.List;

public class FHIRMedicationRequestUtil {

	private static final int FIRST = 0;

	public static boolean areMedicationRequestsEquals(Object o1, Object o2) {
		MedicationRequestComparator comparator = new MedicationRequestComparator();
		return comparator.areEquals((MedicationRequest) o1, (MedicationRequest) o2);
	}

	/**
	 * Generate medication request from drug order
	 *
	 * @param omrsDrugOrder openmrs drug order
	 * @return MedicationRequest object
	 */
	public static MedicationRequest generateMedicationRequest(DrugOrder omrsDrugOrder) {
		MedicationRequest medicationRequest = new MedicationRequest();

		BaseOpenMRSDataUtil.setBaseExtensionFields(medicationRequest, omrsDrugOrder);

		medicationRequest.setId(omrsDrugOrder.getUuid());
		medicationRequest.addIdentifier(FHIRUtils.createIdentifier(omrsDrugOrder.getUuid()));
		medicationRequest.setStatus(buildStatus(omrsDrugOrder));
		medicationRequest.setIntent(MedicationRequest.MedicationRequestIntent.ORDER);
		medicationRequest.setPriority(buildPriority(omrsDrugOrder));
		medicationRequest.setSubject(FHIRRequestUtil.buildSubject(omrsDrugOrder));
		medicationRequest.setContext(FHIRRequestUtil.buildContext(omrsDrugOrder));
		medicationRequest.setRequester(buildRequester(omrsDrugOrder));
		medicationRequest.setRecorder(FHIRRequestUtil.buildPractitionerReference(omrsDrugOrder));
		medicationRequest.setDosageInstruction(buildDosageInstructions(omrsDrugOrder));
		medicationRequest.setDispenseRequest(buildDispenseRequest(omrsDrugOrder));
		medicationRequest.setMedication(buildMedication(omrsDrugOrder));
		medicationRequest.addExtension(ExtensionsUtil.createAsNeededConditionExtension(
				ContextUtil.getDrugOrderHelper().getAsNeededCondition(omrsDrugOrder)));
		medicationRequest.addExtension(buildDosingType(omrsDrugOrder));
		medicationRequest.addExtension(buildNumRefills(omrsDrugOrder));
		medicationRequest.addExtension(ExtensionsUtil.createBrandNameExtension(
				ContextUtil.getDrugOrderHelper().getBrandName(omrsDrugOrder)));
		medicationRequest.addExtension(ExtensionsUtil.createDispenseAsWrittenExtension(
				ContextUtil.getDrugOrderHelper().getDispenseAsWritten(omrsDrugOrder)));
		medicationRequest.addExtension(ExtensionsUtil.createDrugNonCodedExtension(
				ContextUtil.getDrugOrderHelper().getNonCoded(omrsDrugOrder)));
		medicationRequest.addExtension(FHIRRequestUtil.buildCareSettingExtension(omrsDrugOrder));

		return medicationRequest;
	}

	/**
	 * Generate openmrs drug order from medication request
	 *
	 * @param fhirMedicationRequest medication request
	 * @param errors                error list
	 * @return openmrs drug order
	 */
	public static DrugOrder generateDrugOrder(MedicationRequest fhirMedicationRequest, List<String> errors) {
		DrugOrder drugOrder = new DrugOrder();

		BaseOpenMRSDataUtil.readBaseExtensionFields(drugOrder, fhirMedicationRequest);

		drugOrder.setUuid(FHIRUtils.getObjectUuidByIdentifier(fhirMedicationRequest.getIdentifierFirstRep()));
		ContextUtil.getDrugOrderHelper().setAction(drugOrder, fhirMedicationRequest.getStatus());
		drugOrder.setUrgency(buildUrgency(fhirMedicationRequest));
		drugOrder.setPatient(FHIRRequestUtil.buildPatient(fhirMedicationRequest, errors));
		drugOrder.setEncounter(buildEncounter(fhirMedicationRequest, errors));
		ContextUtil.getDrugOrderHelper().setOrderer(drugOrder, FHIRRequestUtil
				.getOrdererUuid(fhirMedicationRequest, errors));
		setDoseAndDoseUnit(drugOrder, fhirMedicationRequest);
		ContextUtil.getDrugOrderHelper().setFrequency(drugOrder, getFirstDosage(fhirMedicationRequest).getTiming());
		ContextUtil.getDrugOrderHelper().setAsNeeded(drugOrder, buildAsNeeded(fhirMedicationRequest));
		ContextUtil.getDrugOrderHelper().setDosingInstructions(drugOrder, buildDosingInstructions(fhirMedicationRequest));
		ContextUtil.getDrugOrderHelper().setRoute(drugOrder, buildRoute(fhirMedicationRequest, errors));
		setQuantityAndQuantityUnit(drugOrder, fhirMedicationRequest);
		setDurationAndDurationUnit(drugOrder, fhirMedicationRequest);
		drugOrder.setDrug(buildDrug(fhirMedicationRequest, errors));
		ContextUtil.getDrugOrderHelper().setAsNeededCondition(drugOrder, buildAsNeededCondition(fhirMedicationRequest));
		setDosingType(drugOrder, fhirMedicationRequest);
		ContextUtil.getDrugOrderHelper().setNumRefills(drugOrder, buildNumRefills(fhirMedicationRequest));
		ContextUtil.getDrugOrderHelper().setBrandName(drugOrder, buildBrandName(fhirMedicationRequest));
		ContextUtil.getDrugOrderHelper().setDispenseAsWritten(drugOrder, buildDispenseAsWritten(fhirMedicationRequest));
		ContextUtil.getDrugOrderHelper().setDrugNonCoded(drugOrder, buildDrugNonCoded(fhirMedicationRequest));
		ContextUtil.getDrugOrderHelper().setCareSettingByString(drugOrder, FHIRRequestUtil
				.getCareSetting(fhirMedicationRequest, errors));

		return drugOrder;
	}

	private static Dosage getFirstDosage(MedicationRequest fhirMedicationRequest) {
		List<Dosage> dosages = fhirMedicationRequest.getDosageInstruction();
		if (dosages.size() > 0) {
			return dosages.get(0);
		}
		return null;
	}

	private static String buildAsNeededCondition(MedicationRequest fhirMedicationRequest) {
		List<Extension> extensions = fhirMedicationRequest.getExtensionsByUrl(ExtensionURL.AS_NEEDED_CONDITION);
		if (extensions.size() > 0) {
			return ExtensionsUtil.getStringFromExtension(extensions.get(FIRST));
		}
		return null;
	}

	private static Extension buildDosingType(DrugOrder omrsDrugOrder) {
		String dosingType = ContextUtil.getDrugOrderHelper().getDosingType(omrsDrugOrder);
		if (StringUtils.isNotBlank(dosingType)) {
			return ExtensionsUtil.createDosingTypeExtension(dosingType);
		}
		return null;
	}

	private static void setDosingType(DrugOrder drugOrder, MedicationRequest fhirMedicationRequest) {
		List<Extension> extensions = fhirMedicationRequest.getExtensionsByUrl(ExtensionURL.DOSING_TYPE);
		if (extensions.size() > 0) {
			ContextUtil.getDrugOrderHelper().setDosingType(drugOrder, ExtensionsUtil.getStringFromExtension(extensions.get(FIRST)));
		}
	}

	private static Extension buildNumRefills(DrugOrder omrsDrugOrder) {
		Integer numRefills = ContextUtil.getDrugOrderHelper().getNumRefills(omrsDrugOrder);
		if (numRefills != null) {
			return ExtensionsUtil.createNumRefillsExtension(numRefills);
		}
		return null;
	}

	private static Integer buildNumRefills(MedicationRequest fhirMedicationRequest) {
		List<Extension> extensions = fhirMedicationRequest.getExtensionsByUrl(ExtensionURL.NUM_REFILLS);
		if (extensions.size() > 0) {
			return ExtensionsUtil.getIntegerFromExtension(extensions.get(FIRST));
		}
		return null;
	}

	private static String buildBrandName(MedicationRequest fhirMedicationRequest) {
		List<Extension> extensions = fhirMedicationRequest.getExtensionsByUrl(ExtensionURL.BRAND_NAME);
		if (extensions.size() > 0) {
			return ExtensionsUtil.getStringFromExtension(extensions.get(FIRST));
		}
		return null;
	}

	private static Boolean buildDispenseAsWritten(MedicationRequest fhirMedicationRequest) {
		List<Extension> extensions = fhirMedicationRequest.getExtensionsByUrl(ExtensionURL.DISPENSE_AS_WRITTEN);
		if (extensions.size() > 0) {
			return ExtensionsUtil.getBooleanFromExtension(extensions.get(FIRST));
		}
		return null;
	}

	private static String buildDrugNonCoded(MedicationRequest fhirMedicationRequest) {
		List<Extension> extensions = fhirMedicationRequest.getExtensionsByUrl(ExtensionURL.DRUG_NON_CODED);
		if (extensions.size() > 0) {
			return ExtensionsUtil.getStringFromExtension(extensions.get(FIRST));
		}
		return null;
	}

	private static MedicationRequest.MedicationRequestRequesterComponent buildRequester(DrugOrder drugOrder) {
		MedicationRequest.MedicationRequestRequesterComponent reqComponent =
				new MedicationRequest.MedicationRequestRequesterComponent();
		reqComponent.setAgent(FHIRRequestUtil.buildPractitionerReference(drugOrder));
		return reqComponent;
	}

	private static Encounter buildEncounter(MedicationRequest fhirMedicationRequest, List<String> errors) {
		Encounter encounter = null;
		Reference encounterRef = fhirMedicationRequest.getContext();
		if (encounterRef != null) {
			String encounterUuid = FHIRUtils.getObjectUuidByReference(encounterRef);
			encounter = Context.getEncounterService().getEncounterByUuid(encounterUuid);
			if (encounter == null) {
				errors.add("There is no encounter for the given uuid");
			}
		} else {
			errors.add("Context cannot be empty");
		}
		return encounter;
	}

	private static MedicationRequest.MedicationRequestStatus buildStatus(DrugOrder omrsDrugOrder) {
		if (ContextUtil.getOrderHelper().isActive(omrsDrugOrder)) {
			return MedicationRequest.MedicationRequestStatus.ACTIVE;
		} else if (omrsDrugOrder.isDiscontinuedRightNow()) {
			return MedicationRequest.MedicationRequestStatus.STOPPED;
		} else {
			return MedicationRequest.MedicationRequestStatus.COMPLETED;
		}
	}

	private static MedicationRequest.MedicationRequestPriority buildPriority(DrugOrder omrsDrugOrder) {
		if (Order.Urgency.ROUTINE.toString().equalsIgnoreCase(omrsDrugOrder.getUrgency().toString())) {
			return MedicationRequest.MedicationRequestPriority.ROUTINE;
		} else if (Order.Urgency.STAT.toString().equalsIgnoreCase(omrsDrugOrder.getUrgency().toString())) {
			return MedicationRequest.MedicationRequestPriority.STAT;
		} else {
			return MedicationRequest.MedicationRequestPriority.ROUTINE;
		}
	}

	private static Order.Urgency buildUrgency(MedicationRequest fhirMedicationRequest) {
		if (MedicationRequest.MedicationRequestPriority.ROUTINE.toCode().
				equalsIgnoreCase(fhirMedicationRequest.getPriority().toCode())) {
			return Order.Urgency.ROUTINE;
		} else if (MedicationRequest.MedicationRequestPriority.STAT.toCode().
				equalsIgnoreCase(fhirMedicationRequest.getPriority().toCode())) {
			return Order.Urgency.STAT;
		} else {
			return Order.Urgency.ROUTINE;
		}
	}

	private static Reference buildMedication(DrugOrder omrsDrugOrder) {
		Drug drug = omrsDrugOrder.getDrug();
		if (drug != null) {
			return FHIRUtils.buildMedicationReference(drug);
		}
		return null;
	}

	private static Drug buildDrug(MedicationRequest fhirMedicationRequest, List<String> errors) {
		Drug drug = null;
		try {
			Reference drugRef = fhirMedicationRequest.getMedicationReference();
			if (drugRef != null) {
				String drugUuid = FHIRUtils.getObjectUuidByReference(drugRef);
				drug = Context.getConceptService().getDrugByUuid(drugUuid);
				if (drug == null) {
					errors.add("There is no drug for the given uuid");
				}
			} else {
				errors.add("Medication cannot be empty");
			}
		}
		catch (FHIRException e) {
			errors.add(e.getMessage());
		}
		return drug;
	}

	private static List<Dosage> buildDosageInstructions(DrugOrder omrsDrugOrder) {
		Dosage dosage = new Dosage();
		dosage.setDose(buildDose(omrsDrugOrder));
		dosage.setTiming(buildTiming(omrsDrugOrder));
		dosage.setAsNeeded(new BooleanType(ContextUtil.getDrugOrderHelper().getAsNeeded(omrsDrugOrder)));
		dosage.setText(ContextUtil.getDrugOrderHelper().getDosingInstructions(omrsDrugOrder));
		dosage.setRoute(buildRoute(omrsDrugOrder));
		dosage.setSequence(1);
		return Collections.singletonList(dosage);
	}

	private static Boolean buildAsNeeded(MedicationRequest fhirMedicationRequest) {
		BooleanType asNeeded = (BooleanType) getFirstDosage(fhirMedicationRequest).getAsNeeded();
		return asNeeded != null && asNeeded.booleanValue();
	}

	private static String buildDosingInstructions(MedicationRequest fhirMedicationRequest) {
		return getFirstDosage(fhirMedicationRequest).getText();
	}

	private static SimpleQuantity buildDose(DrugOrder omrsDrugOrder) {
		return ContextUtil.getDrugOrderHelper().getDose(omrsDrugOrder);
	}

	private static void setDoseAndDoseUnit(DrugOrder order, MedicationRequest fhirMedicationRequest) {
		SimpleQuantity dose = (SimpleQuantity) getFirstDosage(fhirMedicationRequest).getDose();
		ContextUtil.getDrugOrderHelper().setDose(order, dose);
	}

	private static Timing buildTiming(DrugOrder omrsDrugOrder) {
		Timing timing = ContextUtil.getDrugOrderHelper().getTiming(omrsDrugOrder);
		return timing;
	}

	private static CodeableConcept buildRoute(DrugOrder omrsDrugOrder) {
		Concept routeConcept = ContextUtil.getDrugOrderHelper().getRoute(omrsDrugOrder);
		if (routeConcept != null) {
			CodeableConcept route = FHIRUtils.createCodeableConcept(routeConcept);
			route.setText(routeConcept.getDisplayString());
			route.setId(routeConcept.getUuid());
			return route;
		}
		return null;
	}

	private static Concept buildRoute(MedicationRequest fhirMedicationRequest, List<String> errors) {
		Concept omrsRouteConcept = null;
		CodeableConcept routeConcept = getFirstDosage(fhirMedicationRequest).getRoute();
		if (routeConcept != null) {
			omrsRouteConcept = FHIRUtils.getConceptFromCode(routeConcept, errors);
		}
		return omrsRouteConcept;
	}

	private static MedicationRequest.MedicationRequestDispenseRequestComponent buildDispenseRequest(DrugOrder omrsDrugOrder) {
		MedicationRequest.MedicationRequestDispenseRequestComponent component = new
				MedicationRequest.MedicationRequestDispenseRequestComponent();

		component.setQuantity(buildQuantity(omrsDrugOrder));
		component.setExpectedSupplyDuration(buildExpectedSupply(omrsDrugOrder));

		return component;
	}

	private static SimpleQuantity buildQuantity(DrugOrder omrsDrugOrder) {
		return ContextUtil.getDrugOrderHelper().getQuantity(omrsDrugOrder);
	}

	private static void setQuantityAndQuantityUnit(DrugOrder order, MedicationRequest fhirMedicationRequest) {
		MedicationRequest.MedicationRequestDispenseRequestComponent component
				= fhirMedicationRequest.getDispenseRequest();
		if (component != null) {
			SimpleQuantity quantity = component.getQuantity();
			ContextUtil.getDrugOrderHelper().setQuantity(order, quantity);
		}
	}

	private static Duration buildExpectedSupply(DrugOrder omrsDrugOrder) {
		return ContextUtil.getDrugOrderHelper().getDuration(omrsDrugOrder);
	}

	private static void setDurationAndDurationUnit(DrugOrder order, MedicationRequest fhirMedicationRequest) {
		MedicationRequest.MedicationRequestDispenseRequestComponent component
				= fhirMedicationRequest.getDispenseRequest();
		if (component != null) {
			Duration duration = component.getExpectedSupplyDuration();
			ContextUtil.getDrugOrderHelper().setDuration(order, duration);
		}
	}
}
