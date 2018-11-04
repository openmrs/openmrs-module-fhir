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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hl7.fhir.dstu3.model.ProcedureRequest;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.fhir.api.ProcedureRequestService;
import org.openmrs.module.fhir.api.db.FHIRDAO;
import org.openmrs.module.fhir.api.strategies.procedurerequest.ProcedureRequestStrategyUtil;

public class ProcedureRequestServiceImpl extends BaseOpenmrsService implements ProcedureRequestService {

	protected final Log log = LogFactory.getLog(this.getClass());

	private FHIRDAO dao;

	public FHIRDAO getDao() {
		return dao;
	}

	public void setDao(FHIRDAO dao) {
		this.dao = dao;
	}

	@Override
	public ProcedureRequest getById(String uuid) {
		return ProcedureRequestStrategyUtil.getProcedureRequestStrategy().getById(uuid);
	}

	@Override
	public void delete(String uuid) {
		ProcedureRequestStrategyUtil.getProcedureRequestStrategy().delete(uuid);
	}

	@Override
	public ProcedureRequest createFHIRProcedureRequest(ProcedureRequest procedureRequest) {
		return ProcedureRequestStrategyUtil.getProcedureRequestStrategy().createFHIRProcedureRequest(procedureRequest);
	}

	@Override
	public ProcedureRequest updateFHIRProcedureRequest(ProcedureRequest procedureRequest, String uuid) {
		return ProcedureRequestStrategyUtil.getProcedureRequestStrategy()
				.updateFHIRProcedureRequest(procedureRequest, uuid);
	}
}
