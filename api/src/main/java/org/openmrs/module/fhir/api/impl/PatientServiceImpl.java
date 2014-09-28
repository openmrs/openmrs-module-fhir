/**
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

import ca.uhn.fhir.model.dstu.resource.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.fhir.api.PatientService;
import org.openmrs.module.fhir.api.db.FHIRDAO;
import org.openmrs.module.fhir.api.util.FHIRPatientUtil;

/**
 * It is a default implementation of {@link org.openmrs.module.fhir.api.PatientService}.
 */
public class PatientServiceImpl extends BaseOpenmrsService implements PatientService {
	
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

    public Patient getPatient(String id){

        org.openmrs.Patient omrsPatient = Context.getPatientService().getPatientByUuid(id);
        return FHIRPatientUtil.generatePatient(omrsPatient);

    }
}