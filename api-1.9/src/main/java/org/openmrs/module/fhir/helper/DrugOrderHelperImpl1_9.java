package org.openmrs.module.fhir.helper;

import org.apache.commons.lang.StringUtils;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Duration;
import org.hl7.fhir.dstu3.model.MedicationRequest;
import org.hl7.fhir.dstu3.model.SimpleQuantity;
import org.hl7.fhir.dstu3.model.Timing;
import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.fhir.api.helper.DrugOrderHelper;
import org.springframework.stereotype.Component;

@Component(value = "fhir.DrugOrderHelper")
@OpenmrsProfile(openmrsPlatformVersion = "1.9.*")
public class DrugOrderHelperImpl1_9 extends OrderHelperImpl1_9 implements DrugOrderHelper {

	@Override
	public void setAction(DrugOrder drugOrder, MedicationRequest.MedicationRequestStatus status) {
		//The drug action was introduce in the OpenMRS 1.10
	}

	@Override
	public String getAsNeededCondition(DrugOrder drugOrder) {
		//The drug as needed condition was introduce in the OpenMRS 1.10
		return null;
	}

	@Override
	public void setAsNeededCondition(DrugOrder drugOrder, String condition) {
		//The drug as needed condition was introduce in the OpenMRS 1.10
	}

	@Override
	public String getBrandName(DrugOrder drugOrder) {
		//The brand name was introduced in the OpenMRS 1.10
		return null;
	}

	@Override
	public void setBrandName(DrugOrder drugOrder, String brandName) {
		//The brand name was introduced in the OpenMRS 1.10
	}

	@Override
	public Boolean getDispenseAsWritten(DrugOrder drugOrder) {
		//The dispense as written was introduced in the OpenMRS 1.10
		return null;
	}

	@Override
	public void setDispenseAsWritten(DrugOrder drugOrder, Boolean dispenseAsWritten) {
		//The dispense as written was introduced in the OpenMRS 1.10
	}

	@Override
	public String getDrugNonCoded(DrugOrder drugOrder) {
		//The drug non coded was introduced in the OpenMRS 1.12
		return null;
	}

	@Override
	public void setDrugNonCoded(DrugOrder drugOrder, String drugNonCoded) {
		//The drug non coded was introduced in the OpenMRS 1.12
	}

	@Override
	public boolean getAsNeeded(DrugOrder drugOrder) {
		//The as needed was introduced in the OpenMRS 1.10
		return false;
	}

	@Override
	public void setAsNeeded(DrugOrder drugOrder, Boolean asNeeded) {
		//The as needed was introduced in the OpenMRS 1.10
	}

	@Override
	public String getDosingInstructions(DrugOrder omrsDrugOrder) {
		return omrsDrugOrder.getInstructions();
	}

	@Override
	public void setDosingInstructions(DrugOrder drugOrder, String dosingInstructions) {
		drugOrder.setInstructions(dosingInstructions);
	}

	@Override
	public Concept getRoute(DrugOrder omrsDrugOrder) {
		//The route was introduced in the OpenMRS 1.10
		return null;
	}

	@Override
	public void setRoute(DrugOrder drugOrder, Concept route) {
		//The route was introduced in the OpenMRS 1.10
	}

	@Override
	public Integer getNumRefills(DrugOrder omrsDrugOrder) {
		//The num refills was introduced in the OpenMRS 1.10
		return null;
	}

	@Override
	public void setNumRefills(DrugOrder drugOrder, Integer numRefills) {
		//The num refills was introduced in the OpenMRS 1.10
	}

	@Override
	public String getDosingType(DrugOrder drugOrder) {
		//The dosing type was introduced in the OpenMRS 1.10
		return null;
	}

	@Override
	public void setDosingType(DrugOrder drugOrder, String classCanonicalName) {
		//The dosing type was introduced in the OpenMRS 1.10
	}

	@Override
	public void setFrequency(DrugOrder drugOrder, Timing timing) {
		if (timing != null) {
			CodeableConcept timingCode = timing.getCode();
			if (timingCode != null) {
				drugOrder.setFrequency(timingCode.getText());
			}
		}
	}

	@Override
	public Timing getTiming(DrugOrder drugOrder) {
		Timing timing = new Timing();
		String frequency = drugOrder.getFrequency();
		if (StringUtils.isNotBlank(frequency)) {
			CodeableConcept timingCode = new CodeableConcept();
			timingCode.setText(frequency);
			timing.setCode(timingCode);
		}
		return timing;
	}

	@Override
	public SimpleQuantity getDose(DrugOrder drugOrder) {
		SimpleQuantity dose = new SimpleQuantity();
		String doseUnit = drugOrder.getUnits();
		dose.setUnit(doseUnit);
		dose.setValue(drugOrder.getDose());
		return dose;
	}

	@Override
	public void setDose(DrugOrder drugOrder, SimpleQuantity dose) {
		if (dose != null) {
			drugOrder.setDose(dose.getValue().doubleValue());
			drugOrder.setUnits(dose.getUnit());
		}
	}

	@Override
	public void setQuantity(DrugOrder drugOrder, SimpleQuantity quantity) {
		drugOrder.setQuantity(quantity.getValue().toBigInteger().intValue());
	}

	@Override
	public SimpleQuantity getQuantity(DrugOrder drugOrder) {
		SimpleQuantity quantity = new SimpleQuantity();
		quantity.setValue(drugOrder.getQuantity());
		return quantity;
	}

	@Override
	public Duration getDuration(DrugOrder omrsDrugOrder) {
		//The duration was introduced in the OpenMRS 1.10
		return null;
	}

	@Override
	public void setDuration(DrugOrder drugOrder, Duration duration) {
		//The duration was introduced in the OpenMRS 1.10
	}
}
