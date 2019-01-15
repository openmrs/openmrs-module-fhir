package org.openmrs.module.fhir.api.util;

import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.helper.AllergyHelper;
import org.openmrs.module.fhir.api.helper.DrugHelper;
import org.openmrs.module.fhir.api.helper.DrugOrderHelper;
import org.openmrs.module.fhir.api.helper.ObsHelper;
import org.openmrs.module.fhir.api.helper.OrderHelper;
import org.openmrs.module.fhir.api.helper.TestOrderHelper;

public class ContextUtil {

	public static ObsHelper getObsHelper() {
		return Context.getRegisteredComponent("fhir.ObsHelper", ObsHelper.class);
	}

	public static DrugHelper getDrugHelper() {
		return Context.getRegisteredComponent("fhir.DrugHelper", DrugHelper.class);
	}

	public static AllergyHelper getAllergyHelper() {
		return Context.getRegisteredComponent("fhir.AllergyHelper", AllergyHelper.class);
	}

	public static OrderHelper getOrderHelper() {
		return Context.getRegisteredComponent("fhir.OrderHelper", OrderHelper.class);
	}

	public static TestOrderHelper getTestOrderHelper() {
		return Context.getRegisteredComponent("fhir.TestOrderHelper", TestOrderHelper.class);
	}

	public static DrugOrderHelper getDrugOrderHelper() {
		return Context.getRegisteredComponent("fhir.DrugOrderHelper", DrugOrderHelper.class);
	}

	private ContextUtil() { }
}
