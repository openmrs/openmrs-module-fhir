package org.openmrs.module.fhir.api.impl;

import ca.uhn.fhir.rest.server.exceptions.MethodNotAllowedException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
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
import org.openmrs.module.fhir.api.util.FHIRRelatedPersonUtil;

import java.util.ArrayList;
import java.util.List;

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
        return FHIRRelatedPersonUtil.generateRelationshipObject(omrsRelationship);
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

    @Override
    public RelatedPerson updateRelatedPerson(String uuid, RelatedPerson relatedPerson) {
        List<String> errors = new ArrayList<String>();
        org.openmrs.Relationship omrsRelationship = FHIRRelatedPersonUtil.generateOmrsRelationshipObject(relatedPerson, errors);
        handleErrorsIfAny(errors);
        org.openmrs.Relationship omrsRetrievedRelationship = Context.getPersonService().getRelationshipByUuid(omrsRelationship.getUuid());
        omrsRelationship = FHIRRelatedPersonUtil.updateRelationshipAttributes(omrsRelationship, omrsRetrievedRelationship);
        omrsRelationship = Context.getPersonService().saveRelationship(omrsRelationship);
        return FHIRRelatedPersonUtil.generateRelationshipObject(omrsRelationship);
    }

    @Override
    public RelatedPerson createRelatedPerson(RelatedPerson relatedPerson) {
        List<String> errors = new ArrayList<String>();
        org.openmrs.Relationship omrsRelationship = FHIRRelatedPersonUtil.generateOmrsRelationshipObject(relatedPerson, errors);
        handleErrorsIfAny(errors);
        omrsRelationship = Context.getPersonService().saveRelationship(omrsRelationship);
        return FHIRRelatedPersonUtil.generateRelationshipObject(omrsRelationship);

    }

    private void handleErrorsIfAny(List<String> errors) throws UnprocessableEntityException {
        if (!errors.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder("The request cannot be processed due to following issues \n");
            for (int i = 0; i < errors.size(); i++) {
                errorMessage.append((i + 1) + " : " + errors.get(i) + "\n");
            }
            throw new UnprocessableEntityException(errorMessage.toString());
        }
    }
}
