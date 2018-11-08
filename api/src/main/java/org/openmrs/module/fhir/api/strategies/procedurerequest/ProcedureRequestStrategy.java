package org.openmrs.module.fhir.api.strategies.procedurerequest;

import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import org.hl7.fhir.dstu3.model.ProcedureRequest;
import org.openmrs.Order;
import org.openmrs.TestOrder;
import org.openmrs.api.APIException;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.openmrs.module.fhir.api.util.FHIRProcedureRequestUtil;
import org.openmrs.module.fhir.api.util.FHIRUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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
	public void deleteProcedureRequest(String uuid) {
		Order testOrder = getOrderService().getOrderByUuid(uuid);
		getOrderService().voidOrder(testOrder, FHIRConstants.FHIR_VOIDED_MESSAGE);
	}

	@Override
	public ProcedureRequest createProcedureRequest(ProcedureRequest procedureRequest) {
		List<String> errors = new ArrayList<>();
		TestOrder testOrder = FHIRProcedureRequestUtil.generateTestOrder(procedureRequest, errors);
		FHIRUtils.checkGeneratorErrorList(errors);

		if (testOrder != null) {
			try {
				testOrder = (TestOrder) getOrderService().saveOrder(testOrder, null);
			} catch (APIException e) {
				throw new UnprocessableEntityException(
						"The request cannot be processed due to the following issues \n" + e.getMessage());
			}
		}

		return FHIRProcedureRequestUtil.generateProcedureRequest(testOrder);
	}

	@Override
	public ProcedureRequest updateProcedureRequest(ProcedureRequest procedureRequest, String uuid) {
		//TODO
		return new ProcedureRequest();
	}

	private OrderService getOrderService() {
		return Context.getOrderService();
	}
}
