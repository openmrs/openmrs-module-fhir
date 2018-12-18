package org.openmrs.module.fhir.helper;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Duration;
import org.hl7.fhir.dstu3.model.MedicationRequest;
import org.hl7.fhir.dstu3.model.ProcedureRequest;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.SimpleQuantity;
import org.hl7.fhir.dstu3.model.Timing;
import org.openmrs.CareSetting;
import org.openmrs.Concept;
import org.openmrs.DosingInstructions;
import org.openmrs.DrugOrder;
import org.openmrs.Order;
import org.openmrs.OrderFrequency;
import org.openmrs.Patient;
import org.openmrs.PersonName;
import org.openmrs.Provider;
import org.openmrs.SimpleDosingInstructions;
import org.openmrs.TestOrder;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.helper.OrderHelper;
import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.openmrs.module.fhir.api.util.FHIRUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Component(value = "fhir.OrderHelper")
@OpenmrsProfile(openmrsPlatformVersion = "2.0.*")
public class OrderHelperImpl2_0 implements OrderHelper {

	private static final int INPATIENT = 2;

	@Override
	public Order saveOrder(Order order) {
		return Context.getOrderService().saveOrder(order, null);
	}

	@Override
	public void reviseOrder(Order order) {
		order.setAction(Order.Action.REVISE);
	}

	@Override
	public void setPreviousOrder(Order order, Order previousOrder) {
		order.setPreviousOrder(previousOrder);
	}

	@Override
	public String testOrderFrequencyToString(TestOrder order) {
		return order.getFrequency().getUuid();
	}

	@Override
	public void setTestOrderFrequencyByString(TestOrder testOrder, String orderFrequencyUuid) {
		OrderFrequency orderFrequency = Context.getOrderService().getOrderFrequencyByUuid(orderFrequencyUuid);
		testOrder.setFrequency(orderFrequency);
	}

	@Override
	public String careSettingToString(Order order) {
		return order.getCareSetting().getUuid();
	}

	@Override
	public void setCareSettingByString(Order order, String careSettingUuid) {
		CareSetting careSetting = Context.getOrderService().getCareSettingByUuid(careSettingUuid);
		if (careSetting == null) {
			careSetting = Context.getOrderService().getCareSetting(INPATIENT);
		}
		order.setCareSetting(careSetting);
	}

	@Override
	public void setTestOrderAction(TestOrder testOrder, ProcedureRequest.ProcedureRequestStatus status) {
		testOrder.setAction(buildTestOrderAction(status));
	}

	@Override
	public void setDrugOrderAction(DrugOrder drugOrder, MedicationRequest.MedicationRequestStatus status) {
		drugOrder.setAction(buildDrugOrderAction(status));
	}

	@Override
	public boolean isActive(Order order) {
		return order.isActive();
	}

	@Override
	public String getDrugOrderAsNeededCondition(DrugOrder drugOrder) {
		return drugOrder.getAsNeededCondition();
	}

	@Override
	public void setDrugOrderAsNeededCondition(DrugOrder drugOrder, String condition) {
		drugOrder.setAsNeededCondition(condition);
	}

	@Override
	public String getDrugOrderBrandName(DrugOrder drugOrder) {
		return drugOrder.getBrandName();
	}

	@Override
	public Boolean getDrugOrderDispenseAsWritten(DrugOrder drugOrder) {
		return drugOrder.getDispenseAsWritten();
	}

	@Override
	public void setDrugOrderDispenseAsWritten(DrugOrder drugOrder, Boolean dispenseAsWritten) {
		drugOrder.setDispenseAsWritten(dispenseAsWritten);
	}

	@Override
	public String getDrugOrderNonCoded(DrugOrder drugOrder) {
		return drugOrder.getDrugNonCoded();
	}

	@Override
	public void setDrugOrderAsNeeded(DrugOrder drugOrder, Boolean asNeeded) {
		drugOrder.setAsNeeded(asNeeded);
	}

	@Override
	public void setDrugOrderDosingInstructions(DrugOrder drugOrder, String dosingInstructions) {
		drugOrder.setDosingInstructions(dosingInstructions);
	}

	@Override
	public void setDrugOrderRoute(DrugOrder drugOrder, Concept route) {
		drugOrder.setRoute(route);
	}

	@Override
	public Concept getDrugOrderRoute(DrugOrder omrsDrugOrder) {
		return omrsDrugOrder.getRoute();
	}

	@Override
	public void setDrugOrderNumRefills(DrugOrder drugOrder, Integer numRefills) {
		drugOrder.setNumRefills(numRefills);
	}

	@Override
	public void setDrugOrderBrandName(DrugOrder drugOrder, String brandName) {
		drugOrder.setBrandName(brandName);
	}

	@Override
	public void setDrugOrderNonCoded(DrugOrder drugOrder, String drugNonCoded) {
		drugOrder.setDrugNonCoded(drugNonCoded);
	}

	@Override
	public List<Order> getAllOrdersByPatient(Patient patient) {
		return Context.getOrderService().getAllOrdersByPatient(patient);
	}

	@Override
	public boolean getDrugOrderAsNeeded(DrugOrder drugOrder) {
		return drugOrder.getAsNeeded();
	}

	@Override
	public String getDrugOrderDosingInstructions(DrugOrder omrsDrugOrder) {
		return omrsDrugOrder.getDosingInstructions();
	}

	@Override
	public String getDrugOrderDosingType(DrugOrder drugOrder) {
		String dosingType = null;
		if (drugOrder.getDosingType() != null) {
			dosingType = drugOrder.getDosingType().getCanonicalName();
		}
		return dosingType;
	}

	@Override
	public void setDrugOrderDosingType(DrugOrder drugOrder, String classCanonicalName) {
		try {
			drugOrder.setDosingType((Class<? extends DosingInstructions>) Class.forName(classCanonicalName));
		} catch (ClassNotFoundException e) {
			drugOrder.setDosingType(SimpleDosingInstructions.class);
		}
	}

	@Override
	public Integer getDrugOrderNumRefills(DrugOrder omrsDrugOrder) {
		return omrsDrugOrder.getNumRefills();
	}

	@Override
	public void setDrugOrderFrequency(DrugOrder drugOrder, Timing timing) {
		OrderFrequency orderFrequency = null;
		if (timing != null) {
			String orderFrequencyUuid = timing.getId();
			orderFrequency = Context.getOrderService().getOrderFrequencyByUuid(orderFrequencyUuid);
		}
		drugOrder.setFrequency(orderFrequency);
	}

	@Override
	public Timing getDrugOrderTiming(DrugOrder drugOrder) {
		Timing timing = new Timing();
		OrderFrequency orderFrequency = drugOrder.getFrequency();
		if (orderFrequency != null) {
			CodeableConcept timingCode = FHIRUtils.createCodeableConcept(orderFrequency.getConcept());
			timingCode.setText(orderFrequency.getName());
			timing.setCode(timingCode);
			timing.setId(orderFrequency.getUuid());
		}
		return timing;
	}

	@Override
	public SimpleQuantity getDrugOrderDose(DrugOrder omrsDrugOrder) {
		SimpleQuantity dose = new SimpleQuantity();
		Concept doseUnit = omrsDrugOrder.getDoseUnits();
		if (doseUnit != null) {
			dose.setUnit(doseUnit.getDisplayString());
			dose.setCode(doseUnit.getUuid());
		}
		if (omrsDrugOrder.getDose() != null) {
			dose.setValue(omrsDrugOrder.getDose());
		}
		return dose;
	}

	@Override
	public void setDrugOrderDose(DrugOrder drugOrder, SimpleQuantity dose) {
		if (dose != null) {
			drugOrder.setDose(dose.getValue().doubleValue());
			Concept unitConcept = Context.getConceptService().getConceptByUuid(dose.getCode());
			if (unitConcept == null) {
				unitConcept = Context.getConceptService().getConceptByName(dose.getUnit());
			}
			drugOrder.setDoseUnits(unitConcept);
		}
	}

	@Override
	public void setDrugOrderQuantity(DrugOrder drugOrder, SimpleQuantity quantity) {
		if (quantity != null) {
			if (quantity.getValue() != null) {
				drugOrder.setQuantity(quantity.getValue().doubleValue());
			}
			if (quantity.getCode() != null) {
				Concept unitConcept = Context.getConceptService().getConceptByUuid(quantity.getCode());
				if (unitConcept == null && quantity.getUnit() != null) {
					unitConcept = Context.getConceptService().getConceptByName(quantity.getUnit());
				}
				drugOrder.setQuantityUnits(unitConcept);
			}
		}
	}

	@Override
	public SimpleQuantity getDrugOrderQuantity(DrugOrder drugOrder) {
		SimpleQuantity quantity = new SimpleQuantity();
		Concept quantityUnit = drugOrder.getQuantityUnits();

		if (quantityUnit != null) {
			quantity.setUnit(quantityUnit.getDisplayString());
			quantity.setCode(quantityUnit.getUuid());
		}
		if (drugOrder.getQuantity() != null) {
			quantity.setValue(drugOrder.getQuantity());
		}

		return quantity;
	}

	@Override
	public Duration getDrugOrderDuration(DrugOrder omrsDrugOrder) {
		Concept durationUnit = omrsDrugOrder.getDurationUnits();
		Duration duration = new Duration();

		if (durationUnit != null) {
			duration.setUnit(durationUnit.getDisplayString());
			duration.setCode(durationUnit.getUuid());
		}
		if (omrsDrugOrder.getDuration() != null) {
			duration.setValue(omrsDrugOrder.getDuration());
		}
		return duration;
	}

	@Override
	public void setDrugOrderDuration(DrugOrder drugOrder, Duration duration) {
		if (duration != null) {
			if (duration.getValue() != null) {
				drugOrder.setDuration(duration.getValue().intValue());
			}

			if (duration.getCode() != null) {
				Concept unitConcept = Context.getConceptService().getConceptByUuid(duration.getCode());
				if (unitConcept == null && duration.getUnit() != null) {
					unitConcept = Context.getConceptService().getConceptByName(duration.getUnit());
				}
				drugOrder.setDurationUnits(unitConcept);
			}
		}
	}

	@Override
	public void setOrderer(Order order, String uuid) {
		Provider provider = Context.getProviderService().getProviderByUuid(uuid);
		order.setOrderer(provider);
	}

	@Override
	public Reference buildPartitionerReference(Order omrsOrder) {
		Reference practitionerRef = new Reference();
		Provider provider = omrsOrder.getOrderer();
		PersonName name = provider.getPerson().getPersonName();
		StringBuilder nameDisplay = new StringBuilder();
		nameDisplay.append(name.getGivenName());
		nameDisplay.append(" ");
		nameDisplay.append(name.getFamilyName());
		String patientUri;
		nameDisplay.append("(");
		nameDisplay.append(FHIRConstants.IDENTIFIER);
		nameDisplay.append(":");
		nameDisplay.append(provider.getIdentifier());
		nameDisplay.append(")");
		patientUri = FHIRConstants.PRACTITIONER + "/" + provider.getUuid();
		practitionerRef.setReference(patientUri);
		practitionerRef.setDisplay(nameDisplay.toString());
		practitionerRef.setId(provider.getUuid());
		return practitionerRef;
	}

	private static Order.Action buildTestOrderAction(ProcedureRequest.ProcedureRequestStatus status) {
		if (status != null) {
			if (ProcedureRequest.ProcedureRequestStatus.CANCELLED.toCode().
					equalsIgnoreCase(status.toCode())) {
				return Order.Action.DISCONTINUE;
			}
		}
		return Order.Action.NEW;
	}

	private static Order.Action buildDrugOrderAction(MedicationRequest.MedicationRequestStatus status) {
		//Cant set other status to order it check data for all orders
		if (status != null) {
			if (MedicationRequest.MedicationRequestStatus.STOPPED.toCode().
					equalsIgnoreCase(status.toCode())) {
				return Order.Action.DISCONTINUE;
			}
		}
		return Order.Action.NEW;
	}
}
