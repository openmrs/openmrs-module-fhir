package org.openmrs.module.fhir.api.impl;

import ca.uhn.fhir.rest.server.exceptions.MethodNotAllowedException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.RelatedPerson;
import org.openmrs.Relationship;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.fhir.api.RelatedPersonService;
import org.openmrs.module.fhir.api.db.FHIRDAO;
import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.openmrs.module.fhir.api.util.FHIRRelationshipUtil;

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

        Relationship omrsRelationship = Context.getPersonService().getRelationshipByUuid(uuid);
        if (omrsRelationship == null || omrsRelationship.isVoided()) {
            return null;
        }
        return FHIRRelationshipUtil.GenerateRelationshipObject(omrsRelationship);
    }

    @Override
    public void deleteRelatedPerson(String uuid) {
        Relationship omrsRelationship = Context.getPersonService().getRelationshipByUuid(uuid);

        // patient not found. return with 404
        if (omrsRelationship == null) {
            throw new ResourceNotFoundException(new IdType(FHIRConstants.PATIENT, uuid));
        }
        try {
            Context.getPersonService().voidRelationship(omrsRelationship, FHIRConstants.PATIENT_DELETE_MESSAGE);
        } catch (APIException ex) {
            // refused to retire resource.  return with 405
            throw new MethodNotAllowedException("The OpenMRS API refused to retire the Related Person via the FHIR request.");
        }
    }
}
