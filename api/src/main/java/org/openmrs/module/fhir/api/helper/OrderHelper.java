package org.openmrs.module.fhir.api.helper;

import org.hl7.fhir.dstu3.model.Duration;
import org.hl7.fhir.dstu3.model.MedicationRequest;
import org.hl7.fhir.dstu3.model.ProcedureRequest;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.SimpleQuantity;
import org.hl7.fhir.dstu3.model.Timing;
import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.TestOrder;

import java.util.List;

public interface OrderHelper {

	Order saveOrder(Order order);

	void reviseOrder(Order order);

	void setPreviousOrder(Order order, Order previousOrder);

	String testOrderFrequencyToString(TestOrder order);

	void setTestOrderFrequencyByString(TestOrder testOrder, String orderFrequencyUuid);

	String careSettingToString(Order order);

	void setCareSettingByString(Order order, String careSetting);

	void setTestOrderAction(TestOrder testOrder, ProcedureRequest.ProcedureRequestStatus status);

	void setDrugOrderAction(DrugOrder drugOrder, MedicationRequest.MedicationRequestStatus status);

	boolean isActive(Order order);

	String getDrugOrderAsNeededCondition(DrugOrder drugOrder);

	void setDrugOrderAsNeededCondition(DrugOrder drugOrder, String condition);

	String getDrugOrderBrandName(DrugOrder drugOrder);

	Boolean getDrugOrderDispenseAsWritten(DrugOrder drugOrder);

	void setDrugOrderDispenseAsWritten(DrugOrder drugOrder, Boolean dispenseAsWritten);

	String getDrugOrderNonCoded(DrugOrder drugOrder);

	void setDrugOrderAsNeeded(DrugOrder drugOrder, Boolean asNeeded);

	void setDrugOrderDosingInstructions(DrugOrder drugOrder, String dosingInstructions);

	void setDrugOrderRoute(DrugOrder drugOrder, Concept route);

	Concept getDrugOrderRoute(DrugOrder omrsDrugOrder);

	void setDrugOrderNumRefills(DrugOrder drugOrder, Integer numRefills);

	void setDrugOrderBrandName(DrugOrder drugOrder, String brandName);

	void setDrugOrderNonCoded(DrugOrder drugOrder, String drugNonCoded);

	List<Order> getAllOrdersByPatient(Patient patient);

	boolean getDrugOrderAsNeeded(DrugOrder drugOrder);

	String getDrugOrderDosingInstructions(DrugOrder omrsDrugOrder);

	String getDrugOrderDosingType(DrugOrder drugOrder);

	void setDrugOrderDosingType(DrugOrder drugOrder, String classCanonicalName);

	Integer getDrugOrderNumRefills(DrugOrder omrsDrugOrder);

	void setDrugOrderFrequency(DrugOrder drugOrder, Timing timing);

	Timing getDrugOrderTiming(DrugOrder drugOrder);

	SimpleQuantity getDrugOrderDose(DrugOrder drugOrder);

	void setDrugOrderDose(DrugOrder drugOrder, SimpleQuantity dose);

	void setDrugOrderQuantity(DrugOrder drugOrder, SimpleQuantity quantity);

	SimpleQuantity getDrugOrderQuantity(DrugOrder drugOrder);

	Duration getDrugOrderDuration(DrugOrder omrsDrugOrder);

	void setDrugOrderDuration(DrugOrder drugOrder, Duration duration);

	void setOrderer(Order order, String uuid);

	Reference buildPartitionerReference(Order omrsOrder);
}
