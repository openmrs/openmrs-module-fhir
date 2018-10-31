/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.fhir.api.util;

import org.hl7.fhir.dstu3.model.ProcedureRequest;
import org.openmrs.TestOrder;

import java.util.List;

public class FHIRProcedureRequestUtil {

	public static boolean areProcedureRequestsEquals(Object o1, Object o2) {
		//TODO
		return false;
	}

	public static ProcedureRequest generateProcedureRequest(TestOrder testOrder) {
		//TODO
		return new ProcedureRequest();
	}

	public static TestOrder generateTestOrder(ProcedureRequest fhirProcedureRequest, List<String> errors) {
		//TODO
		return new TestOrder();
	}
}
