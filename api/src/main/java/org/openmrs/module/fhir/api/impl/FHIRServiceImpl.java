package org.openmrs.module.fhir.api.impl;

import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.fhir.api.FHIRService;
import org.openmrs.module.fhir.api.Strategy;
import org.openmrs.module.fhir.api.db.FHIRDAO;


public class FHIRServiceImpl  extends BaseOpenmrsService implements FHIRService {


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

    private Strategy strategy;

    public void setStrategy(Strategy strategy){
        this.strategy = strategy;
    }

    public Strategy getStrategy(){
        return this.strategy;
    }

}
