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
import org.hl7.fhir.dstu3.model.MedicationRequest;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.fhir.api.MedicationRequestService;
import org.openmrs.module.fhir.api.db.FHIRDAO;
import org.openmrs.module.fhir.api.strategies.medicationrequest.MedicationRequestStrategyUtil;

import java.util.List;

public class MedicationRequestServiceImpl extends BaseOpenmrsService implements MedicationRequestService {

	protected final Log log = LogFactory.getLog(this.getClass());

	private FHIRDAO dao;

	/**
	 * @return the dao
	 */
	public FHIRDAO getDao() {
		return dao;
	}

	/**
	 * @param dao the dao to set
	 */
	public void setDao(FHIRDAO dao) {
		this.dao = dao;
	}

	@Override
	public MedicationRequest getMedicationRequestById(String uuid) {
		return MedicationRequestStrategyUtil.getMedicationRequestStrategy().getMedicationRequestById(uuid);
	}

	@Override
	public List<MedicationRequest> searchMedicationRequestById(String uuid) {
		return MedicationRequestStrategyUtil.getMedicationRequestStrategy().searchMedicationRequestByUuid(uuid);
	}

	@Override
	public List<MedicationRequest> searchMedicationRequestByPatientId(String patientUuid) {
		return MedicationRequestStrategyUtil.getMedicationRequestStrategy().searchMedicationRequestByPatientUuid(patientUuid);
	}

	@Override
	public void deleteMedicationRequest(String uuid) {
		MedicationRequestStrategyUtil.getMedicationRequestStrategy().deleteMedicationRequest(uuid);
	}

	@Override
	public MedicationRequest createFHIRMedicationRequest(MedicationRequest medicationRequest) {
		return MedicationRequestStrategyUtil.getMedicationRequestStrategy().createFHIRMedicationRequest(medicationRequest);
	}

	@Override
	public MedicationRequest updateFHIRMedicationRequest(MedicationRequest medicationRequest, String uuid) {
		return MedicationRequestStrategyUtil.getMedicationRequestStrategy()
				.updateFHIRMedicationRequest(medicationRequest, uuid);
	}
}
