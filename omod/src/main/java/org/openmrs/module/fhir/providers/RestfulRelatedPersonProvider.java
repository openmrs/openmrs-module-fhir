package org.openmrs.module.fhir.providers;

import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.Delete;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.annotation.Update;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.RelatedPerson;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.openmrs.module.fhir.resources.FHIRRelatedPersonResource;
import org.openmrs.module.fhir.util.MethodOutcomeBuilder;

import java.util.List;

public class RestfulRelatedPersonProvider implements IResourceProvider {

	private FHIRRelatedPersonResource relatedPersonResource;

	public RestfulRelatedPersonProvider() {
		relatedPersonResource = new FHIRRelatedPersonResource();
	}

	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return RelatedPerson.class;
	}

	/**
	 * Get related person by unique id
	 *
	 * @param theId object containing the id
	 */
	@Read
	public RelatedPerson getResourceById(@IdParam IdType theId) {
		return relatedPersonResource.getByUniqueId(theId);
	}

	/**
	 * @see org.openmrs.module.fhir.api.RelatedPersonService#searchRelatedPersonByIdentifier(String)
	 */
	@Search
	public List<RelatedPerson> searchRelatedPersonByIdentifier(
			@RequiredParam(name = RelatedPerson.SP_PATIENT, chainWhitelist = {
					Patient.SP_IDENTIFIER }) ReferenceParam identifier) {
		return relatedPersonResource.searchRelatedPersonByIdentifier(identifier);
	}

	/**
	 * Delete related person by unique id
	 *
	 * @param theId object containing the id
	 */
	@Delete
	public void deleteRelatedPerson(@IdParam IdType theId) {
		relatedPersonResource.deleteRelatedPerson(theId);
	}

	/**
	 * Update related person by unique id
	 *
	 * @param theId object containing the id
	 */
	@Update
	public MethodOutcome updateRelatedPerson(@ResourceParam RelatedPerson relatedPerson, @IdParam IdType theId) {
		return MethodOutcomeBuilder.buildUpdate(relatedPerson);
	}

	/**
	 * Create related person
	 *
	 * @param relatedPerson fhir related person object
	 */
	@Create
	public MethodOutcome createRelatedPerson(@ResourceParam RelatedPerson relatedPerson) {
		return MethodOutcomeBuilder.buildCreate(relatedPersonResource.createRelatedPerson(relatedPerson));
	}
}
