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
import org.openmrs.DosingInstructions;
import org.openmrs.Drug;
import org.openmrs.DrugOrder;
import org.openmrs.Encounter;
import org.openmrs.Order;
import org.openmrs.OrderFrequency;
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
		medicationRequest.addExtension(ExtensionsUtil.createAsNeededConditionExtension(omrsDrugOrder.getAsNeededCondition()));
		medicationRequest.addExtension(buildDosingType(omrsDrugOrder));
		medicationRequest.addExtension(buildNumRefills(omrsDrugOrder));
		medicationRequest.addExtension(ExtensionsUtil.createBrandNameExtension(omrsDrugOrder.getBrandName()));
		medicationRequest.addExtension(ExtensionsUtil.createDispenseAsWrittenExtension(omrsDrugOrder.getDispenseAsWritten()));
		medicationRequest.addExtension(ExtensionsUtil.createDrugNonCodedExtension(omrsDrugOrder.getDrugNonCoded()));
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
		drugOrder.setAction(buildAction(fhirMedicationRequest));
		drugOrder.setUrgency(buildUrgency(fhirMedicationRequest));
		drugOrder.setPatient(FHIRRequestUtil.buildPatient(fhirMedicationRequest, errors));
		drugOrder.setEncounter(buildEncounter(fhirMedicationRequest, errors));
		drugOrder.setOrderer(FHIRRequestUtil.buildOrderer(fhirMedicationRequest, errors));
		setDoseAndDoseUnit(drugOrder, fhirMedicationRequest);
		drugOrder.setFrequency(buildFrequency(fhirMedicationRequest, errors));
		drugOrder.setAsNeeded(buildAsNeeded(fhirMedicationRequest));
		drugOrder.setDosingInstructions(buildDosingInstructions(fhirMedicationRequest));
		drugOrder.setRoute(buildRoute(fhirMedicationRequest, errors));
		setQuantityAndQuantityUnit(drugOrder, fhirMedicationRequest);
		setDurationAndDurationUnit(drugOrder, fhirMedicationRequest);
		drugOrder.setDrug(buildDrug(fhirMedicationRequest, errors));
		drugOrder.setAsNeededCondition(buildAsNeededCondition(fhirMedicationRequest));
		setDosingType(drugOrder, fhirMedicationRequest, errors);
		drugOrder.setNumRefills(buildNumRefills(fhirMedicationRequest));
		drugOrder.setBrandName(buildBrandName(fhirMedicationRequest));
		drugOrder.setDispenseAsWritten(buildDispenseAsWritten(fhirMedicationRequest));
		drugOrder.setDrugNonCoded(buildDrugNonCoded(fhirMedicationRequest));
		drugOrder.setCareSetting(FHIRRequestUtil.buildCareSetting(fhirMedicationRequest, errors));

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
		if (omrsDrugOrder.getDosingType() != null) {
			return ExtensionsUtil.createDosingTypeExtension(omrsDrugOrder.getDosingType().getCanonicalName());
		}
		return null;
	}

	private static void setDosingType(DrugOrder drugOrder, MedicationRequest fhirMedicationRequest, List<String> errors) {
		List<Extension> extensions = fhirMedicationRequest.getExtensionsByUrl(ExtensionURL.DOSING_TYPE);
		try {
			if (extensions.size() > 0) {
				drugOrder.setDosingType(
						(Class<? extends DosingInstructions>) Class.forName(
								ExtensionsUtil.getStringFromExtension(extensions.get(FIRST))));
			}
		}
		catch (ClassNotFoundException e) {
			errors.add(e.getMessage());
		}
	}

	private static Extension buildNumRefills(DrugOrder omrsDrugOrder) {
		Integer numRefills = omrsDrugOrder.getNumRefills();
		if (numRefills != null) {
			return ExtensionsUtil.createNumRefillsExtension(omrsDrugOrder.getNumRefills());
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
		if (omrsDrugOrder.isActive()) {
			return MedicationRequest.MedicationRequestStatus.ACTIVE;
		} else if (omrsDrugOrder.isDiscontinuedRightNow()) {
			return MedicationRequest.MedicationRequestStatus.STOPPED;
		} else {
			return MedicationRequest.MedicationRequestStatus.COMPLETED;
		}
	}

	private static Order.Action buildAction(MedicationRequest fhirMedicationRequest) {
		//Cant set other status to order it check data for all orders
		MedicationRequest.MedicationRequestStatus medicationRequestStatus = fhirMedicationRequest.getStatus();
		if (medicationRequestStatus != null) {
			if (MedicationRequest.MedicationRequestStatus.STOPPED.toCode().
					equalsIgnoreCase(medicationRequestStatus.toCode())) {
				return Order.Action.DISCONTINUE;
			}
		}
		return Order.Action.NEW;
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
		dosage.setAsNeeded(new BooleanType(omrsDrugOrder.getAsNeeded()));
		dosage.setText(omrsDrugOrder.getDosingInstructions());
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
		SimpleQuantity dose = new SimpleQuantity();
		Concept doseUnit = omrsDrugOrder.getDoseUnits();
		if (doseUnit != null) {
			dose.setUnit(doseUnit.getDisplayString());
			dose.setCode(doseUnit.getUuid());
		}
		if (omrsDrugOrder.getDose() != null) {
			dose.setValue(omrsDrugOrder.getDose());
			return dose;
		}
		return null;
	}

	private static void setDoseAndDoseUnit(DrugOrder order, MedicationRequest fhirMedicationRequest) {
		SimpleQuantity dose = (SimpleQuantity) getFirstDosage(fhirMedicationRequest).getDose();
		if (dose != null) {
			order.setDose(dose.getValue().doubleValue());
			Concept unitConcept = Context.getConceptService().getConceptByUuid(dose.getCode());
			if (unitConcept == null) {
				unitConcept = Context.getConceptService().getConceptByName(dose.getUnit());
			}
			order.setDoseUnits(unitConcept);
		}
	}

	private static Timing buildTiming(DrugOrder omrsDrugOrder) {
		Timing timing = new Timing();
		OrderFrequency orderFrequency = omrsDrugOrder.getFrequency();
		if (orderFrequency != null) {
			CodeableConcept timingCode = FHIRUtils.createCodeableConcept(orderFrequency.getConcept());
			timingCode.setText(orderFrequency.getName());
			timing.setCode(timingCode);
			timing.setId(orderFrequency.getUuid());
		}
		return timing;
	}

	private static OrderFrequency buildFrequency(MedicationRequest fhirMedicationRequest, List<String> errors) {
		OrderFrequency orderFrequency = null;
		Timing timing = getFirstDosage(fhirMedicationRequest).getTiming();
		if (timing != null) {
			String orderFrequencyUuid = timing.getId();
			orderFrequency = Context.getOrderService().getOrderFrequencyByUuid(orderFrequencyUuid);
			if (orderFrequency == null) {
				errors.add(String.format("Missing OrderFrequency with uuid: %s", orderFrequencyUuid));
			}
		}
		return orderFrequency;
	}

	private static CodeableConcept buildRoute(DrugOrder omrsDrugOrder) {
		Concept routeConcept = omrsDrugOrder.getRoute();
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
		SimpleQuantity quantity = new SimpleQuantity();
		Concept quantityUnit = omrsDrugOrder.getQuantityUnits();

		if (quantityUnit != null) {
			quantity.setUnit(quantityUnit.getDisplayString());
			quantity.setCode(quantityUnit.getUuid());
		}
		if (omrsDrugOrder.getQuantity() != null) {
			quantity.setValue(omrsDrugOrder.getQuantity());
			return quantity;
		}

		return null;
	}

	private static void setQuantityAndQuantityUnit(DrugOrder order, MedicationRequest fhirMedicationRequest) {
		MedicationRequest.MedicationRequestDispenseRequestComponent component
				= fhirMedicationRequest.getDispenseRequest();
		if (component != null) {
			SimpleQuantity quantity = component.getQuantity();
			if (quantity != null) {
				if (quantity.getValue() != null) {
					order.setQuantity(quantity.getValue().doubleValue());
				}
				if (quantity.getCode() != null) {
					Concept unitConcept = Context.getConceptService().getConceptByUuid(quantity.getCode());
					if (unitConcept == null && quantity.getUnit() != null) {
						unitConcept = Context.getConceptService().getConceptByName(quantity.getUnit());
					}
					order.setQuantityUnits(unitConcept);
				}
			}
		}
	}

	private static Duration buildExpectedSupply(DrugOrder omrsDrugOrder) {
		Concept durationUnit = omrsDrugOrder.getDurationUnits();
		Duration duration = new Duration();

		if (durationUnit != null) {
			duration.setUnit(durationUnit.getDisplayString());
			duration.setCode(durationUnit.getUuid());
		}
		if (omrsDrugOrder.getDuration() != null) {
			duration.setValue(omrsDrugOrder.getDuration());
			return duration;
		}

		return null;
	}

	private static void setDurationAndDurationUnit(DrugOrder order, MedicationRequest fhirMedicationRequest) {
		MedicationRequest.MedicationRequestDispenseRequestComponent component
				= fhirMedicationRequest.getDispenseRequest();
		if (component != null) {
			Duration duration = component.getExpectedSupplyDuration();
			if (duration != null) {
				if (duration.getValue() != null) {
					order.setDuration(duration.getValue().intValue());
				}

				if (duration.getCode() != null) {
					Concept unitConcept = Context.getConceptService().getConceptByUuid(duration.getCode());
					if (unitConcept == null && duration.getUnit() != null) {
						unitConcept = Context.getConceptService().getConceptByName(duration.getUnit());
					}
					order.setDurationUnits(unitConcept);
				}
			}
		}
	}
}
