package org.openmrs.module.fhir.helper;

import org.hl7.fhir.dstu3.model.Reference;
import org.openmrs.CareSetting;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.PersonName;
import org.openmrs.Provider;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.helper.OrderHelper;
import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.springframework.stereotype.Component;

import java.util.List;

@Component(value = "fhir.OrderHelper")
@OpenmrsProfile(openmrsPlatformVersion = "2.0.* - 2.1.*")
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
	public boolean isActive(Order order) {
		return order.isActive();
	}

	@Override
	public List<Order> getAllOrdersByPatient(Patient patient) {
		return Context.getOrderService().getAllOrdersByPatient(patient);
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
}
