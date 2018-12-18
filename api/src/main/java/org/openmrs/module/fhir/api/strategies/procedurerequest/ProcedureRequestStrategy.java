package org.openmrs.module.fhir.api.strategies.procedurerequest;

import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import org.hl7.fhir.dstu3.model.ProcedureRequest;
import org.openmrs.Order;
import org.openmrs.TestOrder;
import org.openmrs.api.APIException;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.helper.OrderHelper;
import org.openmrs.module.fhir.api.util.ContextUtil;
import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.openmrs.module.fhir.api.util.FHIRProcedureRequestUtil;
import org.openmrs.module.fhir.api.util.FHIRUtils;
import org.openmrs.module.fhir.api.util.StrategyUtil;
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
				testOrder = (TestOrder) ContextUtil.getOrderHelper().saveOrder(testOrder);
			} catch (APIException e) {
				throw new UnprocessableEntityException(
						"The request cannot be processed due to the following issues \n" + e.getMessage());
			}
		}

		return FHIRProcedureRequestUtil.generateProcedureRequest(testOrder);
	}

	@Override
	public ProcedureRequest updateProcedureRequest(ProcedureRequest procedureRequest, String uuid) {
		List<String> errors = new ArrayList<>();
		TestOrder incomingTestOrder = FHIRProcedureRequestUtil.generateTestOrder(procedureRequest, errors);
		TestOrder existingTestOrder = (TestOrder) Context.getOrderService().getOrderByUuid(uuid);

		if (existingTestOrder != null) {
			incomingTestOrder.setUuid(null);
			ContextUtil.getOrderHelper().reviseOrder(incomingTestOrder);
			ContextUtil.getOrderHelper().setPreviousOrder(incomingTestOrder, existingTestOrder);
			incomingTestOrder = (TestOrder) ContextUtil.getOrderHelper().saveOrder(incomingTestOrder);
			return FHIRProcedureRequestUtil.generateProcedureRequest(incomingTestOrder);
		} else {
			StrategyUtil.setIdIfNeeded(procedureRequest, uuid);
			return createProcedureRequest(procedureRequest);
		}
	}

	private OrderService getOrderService() {
		return Context.getOrderService();
	}
}
