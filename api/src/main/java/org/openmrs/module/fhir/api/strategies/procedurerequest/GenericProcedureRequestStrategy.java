package org.openmrs.module.fhir.api.strategies.procedurerequest;

import org.hl7.fhir.dstu3.model.ProcedureRequest;

public interface GenericProcedureRequestStrategy {

	ProcedureRequest getById(String uuid);

	void delete(String uuid);

	ProcedureRequest createFHIRProcedureRequest(ProcedureRequest procedureRequest);

	ProcedureRequest updateFHIRProcedureRequest(ProcedureRequest procedureRequest, String uuid);
}
