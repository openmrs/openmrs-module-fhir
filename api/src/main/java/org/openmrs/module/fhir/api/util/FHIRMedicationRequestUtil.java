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
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Dosage;
import org.hl7.fhir.dstu3.model.Duration;
import org.hl7.fhir.dstu3.model.MedicationRequest;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.SimpleQuantity;
import org.hl7.fhir.dstu3.model.Timing;
import org.openmrs.Concept;
import org.openmrs.ConceptMap;
import org.openmrs.Drug;
import org.openmrs.DrugOrder;
import org.openmrs.Encounter;
import org.openmrs.Order;
import org.openmrs.OrderFrequency;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class FHIRMedicationRequestUtil {

	/**
	 * Generate medication request from drug order
	 *
	 * @param omrsDrugOrder openmrs drug order
	 * @return MedicationRequest object
	 */
	public static MedicationRequest generateMedicationRequest(org.openmrs.DrugOrder omrsDrugOrder) {
		MedicationRequest request = new MedicationRequest();

		BaseOpenMRSDataUtil.setBaseExtensionFields(request, omrsDrugOrder);

		//Set id
		if (!StringUtils.isEmpty(omrsDrugOrder.getUuid())) {
			request.setId(omrsDrugOrder.getUuid());
		}

		//set Status
		if (omrsDrugOrder.isActive()) {
			request.setStatus(MedicationRequest.MedicationRequestStatus.ACTIVE);
		} else if (omrsDrugOrder.isDiscontinuedRightNow()) {
			request.setStatus(MedicationRequest.MedicationRequestStatus.STOPPED);
		} else {
			request.setStatus(MedicationRequest.MedicationRequestStatus.COMPLETED);
		}

		//Set intent
		request.setIntent(MedicationRequest.MedicationRequestIntent.ORDER);

		//Set priority
		if (Order.Urgency.ROUTINE.toString().equalsIgnoreCase(omrsDrugOrder.getUrgency().toString())) {
			request.setPriority(MedicationRequest.MedicationRequestPriority.ROUTINE);
		} else if (Order.Urgency.STAT.toString().equalsIgnoreCase(omrsDrugOrder.getUrgency().toString())) {
			request.setPriority(MedicationRequest.MedicationRequestPriority.STAT);
		} else {
			request.setPriority(MedicationRequest.MedicationRequestPriority.ROUTINE);
		}

		//Set medication
		CodeableConcept medication = new CodeableConcept();
		Concept medicationConcept = omrsDrugOrder.getDrug().getConcept();
		List<Coding> medicationDts = medication.getCoding();
		addCodings(medicationConcept, medicationDts);
		medication.setId(omrsDrugOrder.getDrug().getUuid());
		request.setMedication(medication);

		//Set patient
		Patient patient = omrsDrugOrder.getPatient();
		Reference patientRef = FHIRPatientUtil.buildPatientReference(patient);
		patientRef.setId(patient.getUuid());

		request.setSubject(patientRef);

		//Set Encounter
		Encounter encounter = omrsDrugOrder.getEncounter();
		if (encounter != null) {
			Reference encounterRef = FHIRObsUtil.getFHIREncounterReference(encounter);
			encounterRef.setId(encounter.getUuid());
			request.setContext(encounterRef);
		}

		//Set author on date
		request.setAuthoredOn(omrsDrugOrder.getDateCreated());

		//Set requester
		Provider provider = omrsDrugOrder.getOrderer();
		if (provider != null) {
			Reference providerRef = FHIRPractitionerUtil.buildPractionaerReference(provider);
			providerRef.setId(provider.getUuid());
			MedicationRequest.MedicationRequestRequesterComponent reqComponent =
					new MedicationRequest.MedicationRequestRequesterComponent();
			reqComponent.setAgent(providerRef);
			request.setRequester(reqComponent);
			request.setRecorder(providerRef);
		}

		//set route
		List<Dosage> dosages = new ArrayList<Dosage>();
		Dosage dosage = new Dosage();
		CodeableConcept route = new CodeableConcept();

		Concept routeConcept = omrsDrugOrder.getRoute();
		if (routeConcept != null) {
			route.setText(omrsDrugOrder.getRoute().getName().getName());
			route.setId(omrsDrugOrder.getRoute().getUuid());

			List<Coding> dts = route.getCoding();
			addCodings(routeConcept, dts);
			dosage.setRoute(route);
		}

		dosage.setText(omrsDrugOrder.getDosingInstructions());
		dosage.setSequence(1);

		//Set timing
		Timing timing = new Timing();
		OrderFrequency orderFrequency = omrsDrugOrder.getFrequency();
		if (orderFrequency != null) {
			CodeableConcept timingCode = new CodeableConcept();
			timingCode.setText(orderFrequency.getName());
			List<Coding> timingDts = timingCode.getCoding();
			addCodings(orderFrequency.getConcept(), timingDts);
			timing.setCode(timingCode);
		}
		dosage.setTiming(timing);

		SimpleQuantity dose = new SimpleQuantity();
		Concept doseUnit = omrsDrugOrder.getDoseUnits();
		if (doseUnit != null) {
			dose.setUnit(doseUnit.getName().getName());
		}

		if (omrsDrugOrder.getDose() != null) {
			dose.setValue(omrsDrugOrder.getDose());
			dosage.setDose(dose);
		}

		Drug drug = omrsDrugOrder.getDrug();
		if (drug != null) {
			SimpleQuantity maxDose = new SimpleQuantity();
			if (doseUnit != null) {
				maxDose.setUnit(doseUnit.getName().getName());
			}
			if (drug.getMaximumDailyDose() != null) {
				maxDose.setValue(drug.getMaximumDailyDose());
				dosage.setMaxDosePerAdministration(maxDose);
			}
		}
		dosages.add(dosage);
		request.setDosageInstruction(dosages);

		MedicationRequest.MedicationRequestDispenseRequestComponent component = new
				MedicationRequest.MedicationRequestDispenseRequestComponent();
		Duration duration = new Duration();
		Concept durationUnit = omrsDrugOrder.getDurationUnits();
		if (durationUnit != null) {
			duration.setUnit(durationUnit.getName().getName());
		}

		if (omrsDrugOrder.getDuration() != null) {
			duration.setValue(omrsDrugOrder.getDuration());
		}

		component.setExpectedSupplyDuration(duration);
		if (omrsDrugOrder.getQuantityUnits() != null) {
			SimpleQuantity quantity = new SimpleQuantity();
			quantity.setUnit(omrsDrugOrder.getQuantityUnits().getName().getName());
			quantity.setValue(omrsDrugOrder.getQuantity());
			component.setQuantity(quantity);
		}

		request.setDispenseRequest(component);

		return request;
	}

	/**
	 * Generate openmrs drug order from medication request
	 *
	 * @param fhirMedicationRequest medication request
	 * @param errors                error list
	 * @return openmrs drug order
	 */
	public static DrugOrder generateDrugOrder(MedicationRequest fhirMedicationRequest, List<String> errors) {
		DrugOrder order = new DrugOrder();
		Drug drug = new Drug();

		BaseOpenMRSDataUtil.readBaseExtensionFields(order, fhirMedicationRequest);

		//Set patient
		Reference patientRef = fhirMedicationRequest.getSubject();
		if (patientRef != null) {
			String patientUuid = patientRef.getId();
			org.openmrs.Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);
			if (patient == null) {
				String patientRefId = patientRef.getReference();
				if (!StringUtils.isEmpty(patientRefId)) {
					String[] patientSplits = patientRefId.split("/");
					if (patientSplits.length > 0) {
						patientUuid = patientSplits[0];
						patient = Context.getPatientService().getPatientByUuid(patientUuid);
						order.setPatient(patient);
					} else {
						errors.add("There is no patient for the given uuid");
					}
				}
			} else {
				order.setPatient(patient);
			}
		} else {
			errors.add("Subject cannot be empty");
		}

		//Cant set other status to order it check data for all orders
		MedicationRequest.MedicationRequestStatus medicationRequestStatus = fhirMedicationRequest.getStatus();
		if (medicationRequestStatus != null) {
			if (MedicationRequest.MedicationRequestStatus.STOPPED.toCode().
					equalsIgnoreCase(medicationRequestStatus.toCode())) {
				order.setAction(Order.Action.DISCONTINUE);
			}
		}

		//Set urgency can't map all priorities
		if (MedicationRequest.MedicationRequestPriority.ROUTINE.toCode().
				equalsIgnoreCase(fhirMedicationRequest.getPriority().toCode())) {
			order.setUrgency(Order.Urgency.ROUTINE);
		} else if (MedicationRequest.MedicationRequestPriority.STAT.toCode().
				equalsIgnoreCase(fhirMedicationRequest.getPriority().toCode())) {
			order.setUrgency(Order.Urgency.STAT);
		} else {
			order.setUrgency(Order.Urgency.ROUTINE);
		}

		CodeableConcept drugConcept = (CodeableConcept) fhirMedicationRequest.getMedication();
		String drugId = fhirMedicationRequest.getMedication().getId();
		Concept drugOmrsConcept;
		if (drugConcept == null) {
			errors.add("Medication cannot be empty");
		} else {
			drugOmrsConcept = FHIRUtils.getConceptFromCode(drugConcept, errors);
			drug.setConcept(drugOmrsConcept);
			drug.setUuid(drugId);
			order.setDrug(drug);
		}

		//Set encounter
		Reference encounterRef = fhirMedicationRequest.getContext();
		if (encounterRef != null) {
			String encounterUuid = encounterRef.getId();
			org.openmrs.Encounter encounter = Context.getEncounterService().getEncounterByUuid(encounterUuid);
			if (encounter != null) {
				order.setEncounter(encounter);
			} else {
				String encounterRefId = encounterRef.getReference();
				if (!StringUtils.isEmpty(encounterRefId)) {
					String[] encounterSplits = encounterRefId.split("/");
					if (encounterSplits.length > 0) {
						encounterUuid = encounterSplits[1];
						encounter = Context.getEncounterService().getEncounterByUuid(encounterUuid);
						order.setEncounter(encounter);
					} else {
						errors.add("There is no encounter for the given uuid");
					}
				}
			}
		}

		//Set created date
		if (fhirMedicationRequest.getAuthoredOn() != null) {
			order.setDateCreated(fhirMedicationRequest.getAuthoredOn());
		} else {
			order.setDateCreated(new Date());
		}

		//Set provider
		MedicationRequest.MedicationRequestRequesterComponent medicationRequestRequesterComponent
				= fhirMedicationRequest.getRequester();
		if (medicationRequestRequesterComponent != null) {
			Reference providerRef = medicationRequestRequesterComponent.getAgent();
			String providerUuid = providerRef.getId();
			org.openmrs.Provider provider = Context.getProviderService().getProviderByUuid(providerUuid);
			if (provider != null) {
				order.setOrderer(provider);
			}
		}

		List<Dosage> dosages = fhirMedicationRequest.getDosageInstruction();
		//Consider only first dosage
		if (dosages.size() > 0) {
			Dosage dosage = dosages.get(0);
			//Set route
			CodeableConcept routeConcept = (CodeableConcept) dosage.getRoute();
			if (routeConcept != null) {
				Concept omrsRouteConcept = FHIRUtils.getConceptFromCode(routeConcept, errors);
				order.setRoute(omrsRouteConcept);
			}

			//Set dosing instructions
			order.setDosingInstructions(dosage.getText());

			//set order frequency
			if (dosage.getTiming() != null) {
				CodeableConcept orderFrequencyConcept = dosage.getTiming().getCode();
				Concept omrsTimingConcept = FHIRUtils.getConceptFromCode(orderFrequencyConcept, errors);
				OrderFrequency orderFrequency = new OrderFrequency();
				orderFrequency.setConcept(omrsTimingConcept);
			}

			//Set dosage
			SimpleQuantity dose = (SimpleQuantity) dosage.getDose();
			if (dose != null) {
				order.setDose(dose.getValue().doubleValue());
				Concept unitConcept = Context.getConceptService().getConceptByName(dose.getUnit());
				order.setDoseUnits(unitConcept);
			}

			//Set max dose
			SimpleQuantity maxDose = dosage.getMaxDosePerAdministration();
			if (maxDose != null) {
				if (maxDose.getValue() != null) {
					drug.setMaximumDailyDose(maxDose.getValue().doubleValue());
				}
			}

			MedicationRequest.MedicationRequestDispenseRequestComponent component
					= fhirMedicationRequest.getDispenseRequest();

			if (component != null) {
				//Set duration
				Duration duration = component.getExpectedSupplyDuration();
				if (duration != null) {
					if (duration.getValue() != null) {
						order.setDuration(duration.getValue().intValue());
					}

					if (duration.getUnit() != null) {
						Concept unitConcept = Context.getConceptService().getConceptByName(duration.getUnit());
						order.setDurationUnits(unitConcept);
					}
				}

				//Set quantity
				SimpleQuantity quantity = component.getQuantity();
				if (quantity != null) {
					if (quantity.getValue() != null) {
						order.setQuantity(quantity.getValue().doubleValue());
					}
					if (quantity.getUnit() != null) {
						Concept unitConcept = Context.getConceptService().getConceptByName(quantity.getUnit());
						order.setQuantityUnits(unitConcept);
					}
				}

			}
		}

		return order;
	}

	/**
	 * Update drug order
	 *
	 * @param requestOrder   drug order coming in request
	 * @param retrievedOrder drug order saved in database
	 * @param errors         if errors occur
	 * @return updated DrugOrder
	 */
	public static DrugOrder copyObsAttributes(DrugOrder requestOrder, DrugOrder retrievedOrder, List<String> errors) {
		//set dose
		if (requestOrder.getDose() != null) {
			retrievedOrder.setDose(requestOrder.getDose());
		}

		//set dose units
		if (requestOrder.getDoseUnits() != null) {
			retrievedOrder.setDoseUnits(requestOrder.getDoseUnits());
		}

		//set quantity units
		if (requestOrder.getQuantityUnits() != null) {
			retrievedOrder.setQuantityUnits(requestOrder.getQuantityUnits());
		}

		//set quantity
		if (requestOrder.getQuantity() != null) {
			retrievedOrder.setQuantity(requestOrder.getQuantity());
		}

		//set drug
		if (requestOrder.getDrug() != null) {
			retrievedOrder.setDrug(requestOrder.getDrug());
		}

		//set dosing instructions
		if (requestOrder.getDosingInstructions() != null) {
			retrievedOrder.setDosingInstructions(requestOrder.getDosingInstructions());
		}

		//set duration units
		if (requestOrder.getDurationUnits() != null) {
			retrievedOrder.setDurationUnits(requestOrder.getDurationUnits());
		}

		//set duration
		if (requestOrder.getDuration() != null) {
			retrievedOrder.setDuration(requestOrder.getDuration());
		}

		//set route
		if (requestOrder.getRoute() != null) {
			retrievedOrder.setRoute(requestOrder.getRoute());
		}

		//set patient
		if (requestOrder.getPatient() != null) {
			retrievedOrder.setPatient(requestOrder.getPatient());
		}

		//set encounter
		if (requestOrder.getEncounter() != null) {
			retrievedOrder.setEncounter(requestOrder.getEncounter());
		}

		//set orderer
		if (requestOrder.getOrderer() != null) {
			retrievedOrder.setOrderer(requestOrder.getOrderer());
		}

		return retrievedOrder;
	}

	/**
	 * Add codings from OpenMRS mappings
	 */
	private static void addCodings(Concept concept, List<Coding> codings) {
		//Set concept coding
		Collection<ConceptMap> conceptMappings = concept.getConceptMappings();
		if (conceptMappings != null && !conceptMappings.isEmpty()) {
			for (ConceptMap map : conceptMappings) {
				if (map.getConceptReferenceTerm() != null) {
					codings.add(FHIRUtils.getCodingDtByConceptMappings(map));
				}
			}
		}

		//Setting default omrs concept
		if (concept.getName() != null) {
			codings.add(new Coding().setCode(concept.getUuid()).setDisplay(
					concept.getName().getName()).setSystem(FHIRConstants.OPENMRS_URI));
		} else {
			codings.add(new Coding().setCode(concept.getUuid()).setSystem(
					FHIRConstants.OPENMRS_URI));
		}
	}
}
