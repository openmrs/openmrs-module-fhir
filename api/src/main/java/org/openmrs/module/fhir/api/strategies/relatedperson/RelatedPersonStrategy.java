package org.openmrs.module.fhir.api.strategies.relatedperson;

import ca.uhn.fhir.rest.server.exceptions.MethodNotAllowedException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.RelatedPerson;
import org.openmrs.Patient;
import org.openmrs.Relationship;
import org.openmrs.api.APIException;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.openmrs.module.fhir.api.util.FHIRRelatedPersonUtil;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("DefaultRelatedPersonStrategy")
public class RelatedPersonStrategy implements GenericRelatedPersonStrategy {

	@Override
	public RelatedPerson getRelatedPerson(String uuid) {
		Relationship omrsRelationship = Context.getPersonService().getRelationshipByUuid(uuid);
		if (omrsRelationship == null || omrsRelationship.isVoided()) {
			return null;
		}
		return FHIRRelatedPersonUtil.generateRelationshipObject(omrsRelationship);
	}

	/**
	 * @see org.openmrs.module.fhir.api.strategies.relatedperson.GenericRelatedPersonStrategy#searchRelatedPersonByIdentifier(String)
	 */
	@Override
	public List<RelatedPerson> searchRelatedPersonByIdentifier(String identifier) {
		List<RelatedPerson> relatedPersonList = new ArrayList<>();
		List<Relationship> relationships = new ArrayList<>();
		PatientService patientService = Context.getPatientService();
		List<org.openmrs.Patient> patientList = patientService.getPatients(null,
				identifier, null, true);
		PersonService personService = Context.getPersonService();

		if (patientList != null && !patientList.isEmpty()) {
			for (Patient patient : patientList) {
				relationships.addAll(personService.getRelationshipsByPerson(patient));
			}
		}

		if (!relationships.isEmpty()) {
			for (Relationship relationship : relationships) {
				relatedPersonList.add(FHIRRelatedPersonUtil.generateRelationshipObject(relationship));
			}
		}

		return relatedPersonList;
	}

	@Override
	public void deleteRelatedPerson(String uuid) {
		Relationship omrsRelationship = Context.getPersonService().getRelationshipByUuid(uuid);

		// patient not found. return with 404
		if (omrsRelationship == null) {
			throw new ResourceNotFoundException(new IdType(FHIRConstants.PATIENT, uuid));
		}
		try {
			Context.getPersonService().voidRelationship(omrsRelationship, FHIRConstants.FHIR_VOIDED_MESSAGE);
		}
		catch (APIException ex) {
			// refused to retire resource.  return with 405
			throw new MethodNotAllowedException(
					"The OpenMRS API refused to retire the Related Person via the FHIR request.");
		}
	}

	@Override
	public RelatedPerson updateRelatedPerson(String uuid, RelatedPerson relatedPerson) {
		List<String> errors = new ArrayList<String>();
		org.openmrs.Relationship omrsRelationship = FHIRRelatedPersonUtil
				.generateOmrsRelationshipObject(relatedPerson, errors);
		handleErrorsIfAny(errors);

		org.openmrs.Relationship omrsRetrievedRelationship = Context.getPersonService().getRelationshipByUuid(uuid);
		if (omrsRetrievedRelationship != null) {
			omrsRelationship = FHIRRelatedPersonUtil
					.updateRelationshipAttributes(omrsRelationship, omrsRetrievedRelationship);
		}

		omrsRelationship = Context.getPersonService().saveRelationship(omrsRelationship);
		return FHIRRelatedPersonUtil.generateRelationshipObject(omrsRelationship);
	}

	@Override
	public RelatedPerson createRelatedPerson(RelatedPerson relatedPerson) {
		List<String> errors = new ArrayList<String>();
		org.openmrs.Relationship omrsRelationship = FHIRRelatedPersonUtil
				.generateOmrsRelationshipObject(relatedPerson, errors);
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
