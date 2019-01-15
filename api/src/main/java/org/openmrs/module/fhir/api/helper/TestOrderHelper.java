package org.openmrs.module.fhir.api.helper;

import org.hl7.fhir.dstu3.model.ProcedureRequest;
import org.openmrs.TestOrder;

public interface TestOrderHelper extends OrderHelper {

	String frequencyToString(TestOrder order);

	void setFrequencyByString(TestOrder testOrder, String orderFrequencyUuid);

	void setAction(TestOrder testOrder, ProcedureRequest.ProcedureRequestStatus status);

}
