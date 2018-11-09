package org.openmrs.module.fhir.api.strategies.procedurerequest;

import org.hl7.fhir.dstu3.model.ProcedureRequest;

public interface GenericProcedureRequestStrategy {

	ProcedureRequest getProcedureRequestByUuid(String uuid);

	void deleteProcedureRequest(String uuid);

	ProcedureRequest createProcedureRequest(ProcedureRequest procedureRequest);

	ProcedureRequest updateProcedureRequest(ProcedureRequest procedureRequest, String uuid);
}
