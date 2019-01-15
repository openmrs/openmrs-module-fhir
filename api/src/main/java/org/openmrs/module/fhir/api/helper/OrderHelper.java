package org.openmrs.module.fhir.api.helper;

import org.hl7.fhir.dstu3.model.Reference;
import org.openmrs.Order;
import org.openmrs.Patient;

import java.util.List;

public interface OrderHelper {

	Order saveOrder(Order order);

	void reviseOrder(Order order);

	void setPreviousOrder(Order order, Order previousOrder);

	String careSettingToString(Order order);

	void setCareSettingByString(Order order, String careSetting);

	boolean isActive(Order order);

	List<Order> getAllOrdersByPatient(Patient patient);

	void setOrderer(Order order, String uuid);

	Reference buildPartitionerReference(Order omrsOrder);
}
