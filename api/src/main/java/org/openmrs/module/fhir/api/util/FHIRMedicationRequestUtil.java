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
import org.openmrs.CareSetting;
import org.openmrs.Concept;
import org.openmrs.DosingInstructions;
import org.openmrs.Drug;
import org.openmrs.DrugOrder;
import org.openmrs.Encounter;
import org.openmrs.Order;
import org.openmrs.OrderFrequency;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.comparator.MedicationRequestComparator;

import java.util.Collections;
import java.util.List;

import static org.openmrs.module.fhir.api.util.ExtensionsUtil.AS_NEEDED_CONDITION;
import static org.openmrs.module.fhir.api.util.ExtensionsUtil.BRAND_NAME;
import static org.openmrs.module.fhir.api.util.ExtensionsUtil.CARE_SETTING;
import static org.openmrs.module.fhir.api.util.ExtensionsUtil.DISPENSE_AS_WRITTEN;
import static org.openmrs.module.fhir.api.util.ExtensionsUtil.DOSING_TYPE;
import static org.openmrs.module.fhir.api.util.ExtensionsUtil.DRUG_NON_CODED;
import static org.openmrs.module.fhir.api.util.ExtensionsUtil.NUM_REFILLS;
import static org.openmrs.module.fhir.api.util.ExtensionsUtil.createAsNeededConditionExtension;
import static org.openmrs.module.fhir.api.util.ExtensionsUtil.createBrandNameExtension;
import static org.openmrs.module.fhir.api.util.ExtensionsUtil.createCareSettingExtension;
import static org.openmrs.module.fhir.api.util.ExtensionsUtil.createDispenseAsWrittenExtension;
import static org.openmrs.module.fhir.api.util.ExtensionsUtil.createDosingTypeExtension;
import static org.openmrs.module.fhir.api.util.ExtensionsUtil.createDrugNonCodedExtension;
import static org.openmrs.module.fhir.api.util.ExtensionsUtil.createNumRefillsExtension;
import static org.openmrs.module.fhir.api.util.ExtensionsUtil.getBooleanFromExtension;
import static org.openmrs.module.fhir.api.util.ExtensionsUtil.getIntegerFromExtension;
import static org.openmrs.module.fhir.api.util.ExtensionsUtil.getStringFromExtension;
import static org.openmrs.module.fhir.api.util.FHIRUtils.createIdentifier;
import static org.openmrs.module.fhir.api.util.FHIRUtils.getObjectUuidByIdentifier;

public class FHIRMedicationRequestUtil {

	private static final int INPATIENT = 2;

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
		medicationRequest.addIdentifier(createIdentifier(omrsDrugOrder.getUuid()));
		medicationRequest.setStatus(buildStatus(omrsDrugOrder));
		medicationRequest.setIntent(MedicationRequest.MedicationRequestIntent.ORDER);
		medicationRequest.setPriority(buildPriority(omrsDrugOrder));
		medicationRequest.setSubject(buildSubject(omrsDrugOrder));
		medicationRequest.setContext(buildContext(omrsDrugOrder));
		medicationRequest.setRequester(buildRequester(omrsDrugOrder));
		medicationRequest.setRecorder(buildPractitionerReference(omrsDrugOrder));
		medicationRequest.setDosageInstruction(buildDosageInstructions(omrsDrugOrder));
		medicationRequest.setDispenseRequest(buildDispenseRequest(omrsDrugOrder));
		medicationRequest.setMedication(buildMedication(omrsDrugOrder));
		medicationRequest.addExtension(createAsNeededConditionExtension(omrsDrugOrder.getAsNeededCondition()));
		medicationRequest.addExtension(buildDosingType(omrsDrugOrder));
		medicationRequest.addExtension(buildNumRefills(omrsDrugOrder));
		medicationRequest.addExtension(createBrandNameExtension(omrsDrugOrder.getBrandName()));
		medicationRequest.addExtension(createDispenseAsWrittenExtension(omrsDrugOrder.getDispenseAsWritten()));
		medicationRequest.addExtension(createDrugNonCodedExtension(omrsDrugOrder.getDrugNonCoded()));
		medicationRequest.addExtension(buildCareSetting(omrsDrugOrder));

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

		drugOrder.setUuid(getObjectUuidByIdentifier(fhirMedicationRequest.getIdentifierFirstRep()));
		drugOrder.setAction(buildAction(fhirMedicationRequest));
		drugOrder.setUrgency(buildUrgency(fhirMedicationRequest));
		drugOrder.setPatient(buildPatient(fhirMedicationRequest, errors));
		drugOrder.setEncounter(buildEncounter(fhirMedicationRequest, errors));
		drugOrder.setOrderer(buildOrderer(fhirMedicationRequest, errors));
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
		drugOrder.setCareSetting(buildCareSetting(fhirMedicationRequest));

		return drugOrder;
	}

	/**
	 * Update drug order
	 *
	 * @param requestOrder   drug order coming in request
	 * @param retrievedOrder drug order saved in database
	 * @return updated DrugOrder
	 */
	public static DrugOrder copyObsAttributes(DrugOrder requestOrder, DrugOrder retrievedOrder) {
		retrievedOrder.setDose(requestOrder.getDose());
		retrievedOrder.setDoseUnits(requestOrder.getDoseUnits());
		retrievedOrder.setQuantityUnits(requestOrder.getQuantityUnits());
		retrievedOrder.setQuantity(requestOrder.getQuantity());
		retrievedOrder.setDrug(requestOrder.getDrug());
		retrievedOrder.setDosingInstructions(requestOrder.getDosingInstructions());
		retrievedOrder.setDurationUnits(requestOrder.getDurationUnits());
		retrievedOrder.setDuration(requestOrder.getDuration());
		retrievedOrder.setRoute(requestOrder.getRoute());
		retrievedOrder.setPatient(requestOrder.getPatient());
		retrievedOrder.setEncounter(requestOrder.getEncounter());
		retrievedOrder.setOrderer(requestOrder.getOrderer());
		retrievedOrder.setUuid(requestOrder.getUuid());
		return retrievedOrder;
	}

	private static Dosage getFirstDosage(MedicationRequest fhirMedicationRequest) {
		List<Dosage> dosages = fhirMedicationRequest.getDosageInstruction();
		if (dosages.size() > 0) {
			return dosages.get(0);
		}
		return null;
	}

	private static String buildAsNeededCondition(MedicationRequest fhirMedicationRequest) {
		List<Extension> extensions = fhirMedicationRequest.getExtensionsByUrl(AS_NEEDED_CONDITION);
		if (extensions.size() > 0) {
			return getStringFromExtension(extensions.get(FIRST));
		}
		return null;
	}

	private static Extension buildDosingType(DrugOrder omrsDrugOrder) {
		if (omrsDrugOrder.getDosingType() != null) {
			return createDosingTypeExtension(omrsDrugOrder.getDosingType().getCanonicalName());
		}
		return null;
	}

	private static void setDosingType(DrugOrder drugOrder, MedicationRequest fhirMedicationRequest, List<String> errors) {
		List<Extension> extensions = fhirMedicationRequest.getExtensionsByUrl(DOSING_TYPE);
		try {
			if (extensions.size() > 0) {
				drugOrder.setDosingType(
						(Class<? extends DosingInstructions>) Class.forName(getStringFromExtension(extensions.get(FIRST))));
			}
		}
		catch (ClassNotFoundException e) {
			errors.add(e.getMessage());
		}
	}

	private static Extension buildNumRefills(DrugOrder omrsDrugOrder) {
		Integer numRefills = omrsDrugOrder.getNumRefills();
		if (numRefills != null) {
			return createNumRefillsExtension(omrsDrugOrder.getNumRefills());
		}
		return null;
	}

	private static Integer buildNumRefills(MedicationRequest fhirMedicationRequest) {
		List<Extension> extensions = fhirMedicationRequest.getExtensionsByUrl(NUM_REFILLS);
		if (extensions.size() > 0) {
			return getIntegerFromExtension(extensions.get(FIRST));
		}
		return null;
	}

	private static String buildBrandName(MedicationRequest fhirMedicationRequest) {
		List<Extension> extensions = fhirMedicationRequest.getExtensionsByUrl(BRAND_NAME);
		if (extensions.size() > 0) {
			return getStringFromExtension(extensions.get(FIRST));
		}
		return null;
	}

	private static Boolean buildDispenseAsWritten(MedicationRequest fhirMedicationRequest) {
		List<Extension> extensions = fhirMedicationRequest.getExtensionsByUrl(DISPENSE_AS_WRITTEN);
		if (extensions.size() > 0) {
			return getBooleanFromExtension(extensions.get(FIRST));
		}
		return null;
	}

	private static String buildDrugNonCoded(MedicationRequest fhirMedicationRequest) {
		List<Extension> extensions = fhirMedicationRequest.getExtensionsByUrl(DRUG_NON_CODED);
		if (extensions.size() > 0) {
			return getStringFromExtension(extensions.get(FIRST));
		}
		return null;
	}

	private static Extension buildCareSetting(DrugOrder omrsDrugOrder) {
		CareSetting careSetting = omrsDrugOrder.getCareSetting();
		if (careSetting != null) {
			return createCareSettingExtension(careSetting.getUuid());
		}
		return null;
	}

	private static CareSetting buildCareSetting(MedicationRequest fhirMedicationRequest) {
		CareSetting careSetting = Context.getOrderService().getCareSetting(INPATIENT);
		List<Extension> extensions = fhirMedicationRequest.getExtensionsByUrl(CARE_SETTING);
		if (extensions.size() > 0) {
			String careSettingUuid = getStringFromExtension(extensions.get(FIRST));
			careSetting = Context.getOrderService().getCareSettingByUuid(careSettingUuid);
		}
		return careSetting;
	}

	private static MedicationRequest.MedicationRequestRequesterComponent buildRequester(DrugOrder omrsDrugOrder) {
		MedicationRequest.MedicationRequestRequesterComponent reqComponent =
				new MedicationRequest.MedicationRequestRequesterComponent();
		reqComponent.setAgent(buildPractitionerReference(omrsDrugOrder));
		return reqComponent;
	}

	private static Provider buildOrderer(MedicationRequest fhirMedicationRequest, List<String> errors) {
		MedicationRequest.MedicationRequestRequesterComponent medicationRequestRequesterComponent
				= fhirMedicationRequest.getRequester();
		if (medicationRequestRequesterComponent != null) {
			Reference providerRef = medicationRequestRequesterComponent.getAgent();
			String providerUuid =  FHIRUtils.getObjectUuidByReference(providerRef);
			return Context.getProviderService().getProviderByUuid(providerUuid);
		} else {
			errors.add("Requester cannot be empty");
		}
		return null;
	}

	private static Reference buildPractitionerReference(DrugOrder omrsDrugOrder) {
		Provider provider = omrsDrugOrder.getOrderer();
		if (provider != null) {
			return FHIRPractitionerUtil.buildPractionaerReference(provider);
		}
		return null;
	}

	private static Reference buildContext(DrugOrder omrsDrugOrder) {
		Encounter encounter = omrsDrugOrder.getEncounter();
		if (encounter != null) {
			Reference encounterRef = FHIRObsUtil.getFHIREncounterReference(encounter);
			encounterRef.setId(encounter.getUuid());
			return encounterRef;
		}
		return null;
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

	private static Reference buildSubject(DrugOrder omrsDrugOrder) {
		Patient patient = omrsDrugOrder.getPatient();
		return FHIRPatientUtil.buildPatientReference(patient);
	}

	private static Patient buildPatient(MedicationRequest fhirMedicationRequest, List<String> errors) {
		Patient patient = null;
		Reference patientRef = fhirMedicationRequest.getSubject();
		if (patientRef != null) {
			String patientUuid = FHIRUtils.getObjectUuidByReference(patientRef);
			patient = Context.getPatientService().getPatientByUuid(patientUuid);
			if (patient == null) {
				errors.add("There is no patient for the given uuid");
			}
		} else {
			errors.add("Subject cannot be empty");
		}
		return patient;
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
