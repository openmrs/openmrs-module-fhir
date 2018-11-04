package org.openmrs.module.fhir.api.strategies.procedurerequest;

import org.hl7.fhir.dstu3.model.ProcedureRequest;
import org.springframework.stereotype.Component;

@Component("DefaultProcedureRequestStrategy")
public class ProcedureRequestStrategy implements GenericProcedureRequestStrategy {

	@Override
	public ProcedureRequest getById(String uuid) {
		//TODO
		return new ProcedureRequest();
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
}
