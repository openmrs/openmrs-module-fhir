package org.openmrs.module.fhir.helper;

import org.hl7.fhir.dstu3.model.ProcedureRequest;
import org.openmrs.Order;
import org.openmrs.OrderFrequency;
import org.openmrs.TestOrder;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.helper.TestOrderHelper;
import org.springframework.stereotype.Component;

@Component(value = "fhir.TestOrderHelper")
@OpenmrsProfile(openmrsPlatformVersion = "2.0.* - 2.1.*")
public class TestOrderHelperImpl2_0 extends OrderHelperImpl2_0 implements TestOrderHelper {

	@Override
	public String frequencyToString(TestOrder order) {
		return order.getFrequency().getUuid();
	}

	@Override
	public void setFrequencyByString(TestOrder testOrder, String orderFrequencyUuid) {
		OrderFrequency orderFrequency = Context.getOrderService().getOrderFrequencyByUuid(orderFrequencyUuid);
		testOrder.setFrequency(orderFrequency);
	}

	@Override
	public void setAction(TestOrder testOrder, ProcedureRequest.ProcedureRequestStatus status) {
		testOrder.setAction(buildTestOrderAction(status));
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
}
