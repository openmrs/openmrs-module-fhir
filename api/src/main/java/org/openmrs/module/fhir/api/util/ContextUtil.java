package org.openmrs.module.fhir.api.util;

import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.helper.AllergyHelper;
import org.openmrs.module.fhir.api.helper.DrugHelper;
import org.openmrs.module.fhir.api.helper.ObsHelper;
import org.openmrs.module.fhir.api.helper.OrderHelper;

public class ContextUtil {

	public static ObsHelper getObsHelper() {
		return Context.getRegisteredComponent("", ObsHelper.class); //TODO-Arek missing implementation
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

	private ContextUtil() { }
}
