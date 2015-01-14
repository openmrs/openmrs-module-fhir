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

import ca.uhn.fhir.model.api.Bundle;
import ca.uhn.fhir.model.dstu.resource.Practitioner;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.fhir.api.PractitionerService;
import org.openmrs.module.fhir.api.db.FHIRDAO;
import org.openmrs.module.fhir.api.util.FHIRPatientUtil;
import org.openmrs.module.fhir.api.util.FHIRPractitionerUtil;
import org.openmrs.module.fhir.exception.FHIRValidationException;

import java.util.ArrayList;
import java.util.List;

/**
 * It is a default implementation of {@link org.openmrs.module.fhir.api.PatientService}.
 */
public class PractitionerServiceImpl extends BaseOpenmrsService implements PractitionerService {

	protected final Log log = LogFactory.getLog(this.getClass());

	private FHIRDAO dao;

	/**
	 * @param dao the dao to set
	 */
	public void setDao(FHIRDAO dao) {
		this.dao = dao;
	}

	/**
	 * @return the dao
	 */
	public FHIRDAO getDao() {
		return dao;
	}

	public Practitioner getPractitioner(String id) throws FHIRValidationException {

		Provider omrsProvider = Context.getProviderService().getProviderByUuid(id);
		return FHIRPractitionerUtil.generatePractitioner(omrsProvider);
	}

    public Bundle getPractitionersById(String id) throws FHIRValidationException {

        Provider omrsProvider = Context.getProviderService().getProviderByUuid(id);
        List<Provider> providerList = new ArrayList<Provider>();
        providerList.add(omrsProvider);
        return FHIRPractitionerUtil.generateBundle(providerList);
    }

}
