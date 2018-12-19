package org.openmrs.module.fhir.api.helper;

import org.hl7.fhir.dstu3.model.Duration;
import org.hl7.fhir.dstu3.model.MedicationRequest;
import org.hl7.fhir.dstu3.model.SimpleQuantity;
import org.hl7.fhir.dstu3.model.Timing;
import org.openmrs.Concept;
import org.openmrs.DrugOrder;

public interface DrugOrderHelper extends OrderHelper {

	void setAction(DrugOrder drugOrder, MedicationRequest.MedicationRequestStatus status);

	String getAsNeededCondition(DrugOrder drugOrder);

	void setAsNeededCondition(DrugOrder drugOrder, String condition);

	String getBrandName(DrugOrder drugOrder);

	void setBrandName(DrugOrder drugOrder, String brandName);

	Boolean getDispenseAsWritten(DrugOrder drugOrder);

	void setDispenseAsWritten(DrugOrder drugOrder, Boolean dispenseAsWritten);

	String getDrugNonCoded(DrugOrder drugOrder);

	void setDrugNonCoded(DrugOrder drugOrder, String drugNonCoded);

	boolean getAsNeeded(DrugOrder drugOrder);

	void setAsNeeded(DrugOrder drugOrder, Boolean asNeeded);

	String getDosingInstructions(DrugOrder omrsDrugOrder);

	void setDosingInstructions(DrugOrder drugOrder, String dosingInstructions);

	Concept getRoute(DrugOrder omrsDrugOrder);

	void setRoute(DrugOrder drugOrder, Concept route);

	Integer getNumRefills(DrugOrder omrsDrugOrder);

	void setNumRefills(DrugOrder drugOrder, Integer numRefills);

	String getDosingType(DrugOrder drugOrder);

	void setDosingType(DrugOrder drugOrder, String classCanonicalName);

	void setFrequency(DrugOrder drugOrder, Timing timing);

	Timing getTiming(DrugOrder drugOrder);

	SimpleQuantity getDose(DrugOrder drugOrder);

	void setDose(DrugOrder drugOrder, SimpleQuantity dose);

	void setQuantity(DrugOrder drugOrder, SimpleQuantity quantity);

	SimpleQuantity getQuantity(DrugOrder drugOrder);

	Duration getDuration(DrugOrder omrsDrugOrder);

	void setDuration(DrugOrder drugOrder, Duration duration);
}
