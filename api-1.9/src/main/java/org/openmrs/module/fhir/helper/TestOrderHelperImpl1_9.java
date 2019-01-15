package org.openmrs.module.fhir.helper;

import org.hl7.fhir.dstu3.model.ProcedureRequest;
import org.openmrs.TestOrder;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.fhir.api.helper.TestOrderHelper;
import org.springframework.stereotype.Component;

@Component(value = "fhir.TestOrderHelper")
@OpenmrsProfile(openmrsPlatformVersion = "1.9.*")
public class TestOrderHelperImpl1_9 extends OrderHelperImpl1_9 implements TestOrderHelper {

	@Override
	public String frequencyToString(TestOrder order) {
		//The order frequency was introduced in the OpenMRS 1.10
		return null;
	}

	@Override
	public void setFrequencyByString(TestOrder testOrder, String orderFrequencyUuid) {
		//The order frequency was introduced in the OpenMRS 1.10
	}

	@Override
	public void setAction(TestOrder testOrder, ProcedureRequest.ProcedureRequestStatus status) {
		//The order action was introduced in the OpenMRS 1.10
	}
}
