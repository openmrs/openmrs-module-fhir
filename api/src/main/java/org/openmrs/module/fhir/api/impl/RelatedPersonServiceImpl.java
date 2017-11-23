package org.openmrs.module.fhir.api.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hl7.fhir.dstu3.model.RelatedPerson;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.fhir.api.RelatedPersonService;
import org.openmrs.module.fhir.api.db.FHIRDAO;
import org.openmrs.module.fhir.api.strategies.relatedperson.RelatedPersonStrageryUtil;

public class RelatedPersonServiceImpl extends BaseOpenmrsService implements RelatedPersonService {

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
    public RelatedPerson getRelatedPerson(String uuid) {
        return RelatedPersonStrageryUtil.getPersonStrategy().getRelatedPerson(uuid);
    }

    @Override
    public void deleteRelatedPerson(String uuid) {
        RelatedPersonStrageryUtil.getPersonStrategy().deleteRelatedPerson(uuid);
    }

    @Override
    public RelatedPerson updateRelatedPerson(String uuid, RelatedPerson relatedPerson) {
        return RelatedPersonStrageryUtil.getPersonStrategy().updateRelatedPerson(uuid, relatedPerson);
    }

    @Override
    public RelatedPerson createRelatedPerson(RelatedPerson relatedPerson) {
        return RelatedPersonStrageryUtil.getPersonStrategy().createRelatedPerson(relatedPerson);
    }
}
