package org.openmrs.module.fhir.api.impl;

import ca.uhn.fhir.model.dstu.resource.FamilyHistory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.fhir.api.FamilyHistoryService;
import org.openmrs.module.fhir.api.db.FHIRDAO;
import org.openmrs.module.fhir.api.util.FamilyHistoryUtil;

public class FamilyHistoryServiceImpl extends BaseOpenmrsService implements FamilyHistoryService {

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

	public FamilyHistory getFamilyHistory(String id) {

		return FamilyHistoryUtil.generateFamilyHistory();

	}
}
