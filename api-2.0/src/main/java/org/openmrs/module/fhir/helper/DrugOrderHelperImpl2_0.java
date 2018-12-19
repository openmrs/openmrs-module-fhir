package org.openmrs.module.fhir.helper;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Duration;
import org.hl7.fhir.dstu3.model.MedicationRequest;
import org.hl7.fhir.dstu3.model.SimpleQuantity;
import org.hl7.fhir.dstu3.model.Timing;
import org.openmrs.Concept;
import org.openmrs.DosingInstructions;
import org.openmrs.DrugOrder;
import org.openmrs.Order;
import org.openmrs.OrderFrequency;
import org.openmrs.SimpleDosingInstructions;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.helper.DrugOrderHelper;
import org.openmrs.module.fhir.api.util.FHIRUtils;
import org.springframework.stereotype.Component;

@Component(value = "fhir.DrugOrderHelper")
@OpenmrsProfile(openmrsPlatformVersion = "2.0.*")
public class DrugOrderHelperImpl2_0 extends OrderHelperImpl2_0 implements DrugOrderHelper {

	@Override
	public void setAction(DrugOrder drugOrder, MedicationRequest.MedicationRequestStatus status) {
		drugOrder.setAction(buildDrugOrderAction(status));
	}

	@Override
	public String getAsNeededCondition(DrugOrder drugOrder) {
		return drugOrder.getAsNeededCondition();
	}

	@Override
	public void setAsNeededCondition(DrugOrder drugOrder, String condition) {
		drugOrder.setAsNeededCondition(condition);
	}

	@Override
	public String getBrandName(DrugOrder drugOrder) {
		return drugOrder.getBrandName();
	}

	@Override
	public Boolean getDispenseAsWritten(DrugOrder drugOrder) {
		return drugOrder.getDispenseAsWritten();
	}

	@Override
	public void setDispenseAsWritten(DrugOrder drugOrder, Boolean dispenseAsWritten) {
		drugOrder.setDispenseAsWritten(dispenseAsWritten);
	}

	@Override
	public String getNonCoded(DrugOrder drugOrder) {
		return drugOrder.getDrugNonCoded();
	}

	@Override
	public void setAsNeeded(DrugOrder drugOrder, Boolean asNeeded) {
		drugOrder.setAsNeeded(asNeeded);
	}

	@Override
	public void setDosingInstructions(DrugOrder drugOrder, String dosingInstructions) {
		drugOrder.setDosingInstructions(dosingInstructions);
	}

	@Override
	public void setRoute(DrugOrder drugOrder, Concept route) {
		drugOrder.setRoute(route);
	}

	@Override
	public Concept getRoute(DrugOrder omrsDrugOrder) {
		return omrsDrugOrder.getRoute();
	}

	@Override
	public void setNumRefills(DrugOrder drugOrder, Integer numRefills) {
		drugOrder.setNumRefills(numRefills);
	}

	@Override
	public void setBrandName(DrugOrder drugOrder, String brandName) {
		drugOrder.setBrandName(brandName);
	}

	@Override
	public void setDrugNonCoded(DrugOrder drugOrder, String drugNonCoded) {
		drugOrder.setDrugNonCoded(drugNonCoded);
	}

	@Override
	public boolean getAsNeeded(DrugOrder drugOrder) {
		return drugOrder.getAsNeeded();
	}

	@Override
	public String getDosingInstructions(DrugOrder omrsDrugOrder) {
		return omrsDrugOrder.getDosingInstructions();
	}

	@Override
	public String getDosingType(DrugOrder drugOrder) {
		String dosingType = null;
		if (drugOrder.getDosingType() != null) {
			dosingType = drugOrder.getDosingType().getCanonicalName();
		}
		return dosingType;
	}

	@Override
	public void setDosingType(DrugOrder drugOrder, String classCanonicalName) {
		try {
			drugOrder.setDosingType((Class<? extends DosingInstructions>) Class.forName(classCanonicalName));
		} catch (ClassNotFoundException e) {
			drugOrder.setDosingType(SimpleDosingInstructions.class);
		}
	}

	@Override
	public Integer getNumRefills(DrugOrder omrsDrugOrder) {
		return omrsDrugOrder.getNumRefills();
	}

	@Override
	public void setFrequency(DrugOrder drugOrder, Timing timing) {
		OrderFrequency orderFrequency = null;
		if (timing != null) {
			String orderFrequencyUuid = timing.getId();
			orderFrequency = Context.getOrderService().getOrderFrequencyByUuid(orderFrequencyUuid);
		}
		drugOrder.setFrequency(orderFrequency);
	}

	@Override
	public Timing getTiming(DrugOrder drugOrder) {
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
	public SimpleQuantity getDose(DrugOrder omrsDrugOrder) {
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
	public void setDose(DrugOrder drugOrder, SimpleQuantity dose) {
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
	public void setQuantity(DrugOrder drugOrder, SimpleQuantity quantity) {
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
	public SimpleQuantity getQuantity(DrugOrder drugOrder) {
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
	public Duration getDuration(DrugOrder omrsDrugOrder) {
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
	public void setDuration(DrugOrder drugOrder, Duration duration) {
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
