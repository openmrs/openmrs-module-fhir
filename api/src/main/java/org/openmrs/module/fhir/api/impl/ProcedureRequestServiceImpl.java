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
package org.openmrs.module.fhir.api.impl;

import org.hl7.fhir.dstu3.model.ProcedureRequest;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.fhir.api.ProcedureRequestService;
import org.openmrs.module.fhir.api.strategies.procedurerequest.ProcedureRequestStrategyUtil;

public class ProcedureRequestServiceImpl extends BaseOpenmrsService implements ProcedureRequestService {

	@Override
	public ProcedureRequest getProcedureRequestByUuid(String uuid) {
		return ProcedureRequestStrategyUtil.getProcedureRequestStrategy().getProcedureRequestByUuid(uuid);
	}

	@Override
	public void deleteProcedureRequest(String uuid) {
		ProcedureRequestStrategyUtil.getProcedureRequestStrategy().deleteProcedureRequest(uuid);
	}

	@Override
	public ProcedureRequest createProcedureRequest(ProcedureRequest procedureRequest) {
		return ProcedureRequestStrategyUtil.getProcedureRequestStrategy().createProcedureRequest(procedureRequest);
	}

	@Override
	public ProcedureRequest updateProcedureRequest(ProcedureRequest procedureRequest, String uuid) {
		return ProcedureRequestStrategyUtil.getProcedureRequestStrategy()
				.updateProcedureRequest(procedureRequest, uuid);
	}
}
