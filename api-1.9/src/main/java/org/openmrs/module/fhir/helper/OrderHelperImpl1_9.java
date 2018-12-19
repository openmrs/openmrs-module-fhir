package org.openmrs.module.fhir.helper;

import org.hl7.fhir.dstu3.model.Reference;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.PersonName;
import org.openmrs.User;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.helper.OrderHelper;
import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.springframework.stereotype.Component;

import java.util.List;

@Component(value = "fhir.OrderHelper")
@OpenmrsProfile(openmrsPlatformVersion = "1.9.*")
public class OrderHelperImpl1_9 implements OrderHelper {

	@Override
	public Order saveOrder(Order order) {
		return Context.getOrderService().saveOrder(order);
	}

	@Override
	public void reviseOrder(Order order) {
		//The order actions were introduced in the OpenMRS 1.10
	}

	@Override
	public void setPreviousOrder(Order order, Order previousOrder) {
		//The previous order was introduced in the OpenMRS 1.10
	}

	@Override
	public String careSettingToString(Order order) {
		//The care setting was introduced in the OpenMRS 1.10
		return null;
	}

	@Override
	public void setCareSettingByString(Order order, String careSetting) {
		//The care setting was introduced in the OpenMRS 1.10
	}

	@Override
	public boolean isActive(Order order) {
		return order.isCurrent();

	}

	@Override
	public List<Order> getAllOrdersByPatient(Patient patient) {
		return Context.getOrderService().getOrdersByPatient(patient);
	}

	@Override
	public void setOrderer(Order order, String uuid) {
		User provider = Context.getUserService().getUserByUuid(uuid);
		order.setOrderer(provider);
	}

	@Override
	public Reference buildPartitionerReference(Order omrsOrder) {
		Reference practitionerRef = new Reference();
		User user = omrsOrder.getOrderer();
		PersonName name = user.getPerson().getPersonName();
		StringBuilder nameDisplay = new StringBuilder();
		nameDisplay.append(name.getGivenName());
		nameDisplay.append(" ");
		nameDisplay.append(name.getFamilyName());
		String patientUri;
		nameDisplay.append("(");
		nameDisplay.append(FHIRConstants.IDENTIFIER);
		nameDisplay.append(":");
		nameDisplay.append(user.getSystemId());
		nameDisplay.append(")");
		patientUri = FHIRConstants.PRACTITIONER + "/" + user.getUuid();
		practitionerRef.setReference(patientUri);
		practitionerRef.setDisplay(nameDisplay.toString());
		practitionerRef.setId(user.getUuid());
		return practitionerRef;
	}
}
