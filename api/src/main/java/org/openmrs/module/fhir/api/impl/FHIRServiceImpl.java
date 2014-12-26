package org.openmrs.module.fhir.api.impl;

import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.fhir.api.FHIRService;
import org.openmrs.module.fhir.api.db.FHIRDAO;
import org.openmrs.module.fhir.strategy.AllergyStrategyInterface;

public class FHIRServiceImpl extends BaseOpenmrsService implements FHIRService {

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

	private AllergyStrategyInterface allergyStrategy;

	public AllergyStrategyInterface getAllergyStrategy() {
		return allergyStrategy;
	}

	public void setAllergyStrategy(AllergyStrategyInterface allergyStrategy) {
		this.allergyStrategy = allergyStrategy;
	}
}
