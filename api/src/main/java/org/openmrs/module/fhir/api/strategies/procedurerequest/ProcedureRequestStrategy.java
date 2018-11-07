package org.openmrs.module.fhir.api.strategies.procedurerequest;

import org.hl7.fhir.dstu3.model.ProcedureRequest;
import org.openmrs.Order;
import org.openmrs.TestOrder;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.FHIRProcedureRequestUtil;
import org.springframework.stereotype.Component;

@Component("DefaultProcedureRequestStrategy")
public class ProcedureRequestStrategy implements GenericProcedureRequestStrategy {

	@Override
	public ProcedureRequest getProcedureRequestByUuid(String uuid) {
		Order order = getOrderService().getOrderByUuid(uuid);
		if (order == null || !(order instanceof TestOrder) || order.getVoided()) {
			return null;
		}

		return FHIRProcedureRequestUtil.generateProcedureRequest((TestOrder) order);
	}

	@Override
	public void delete(String uuid) {
		//TODO
	}

	@Override
	public ProcedureRequest createFHIRProcedureRequest(ProcedureRequest procedureRequest) {
		//TODO
		return new ProcedureRequest();
	}

	@Override
	public ProcedureRequest updateFHIRProcedureRequest(ProcedureRequest procedureRequest, String uuid) {
		//TODO
		return new ProcedureRequest();
	}

	private OrderService getOrderService() {
		return Context.getOrderService();
	}
}
