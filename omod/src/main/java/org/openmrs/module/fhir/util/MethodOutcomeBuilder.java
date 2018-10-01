package org.openmrs.module.fhir.util;

import ca.uhn.fhir.rest.api.MethodOutcome;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.DomainResource;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.OperationOutcome;

public final class MethodOutcomeBuilder {

	private static final String UPDATE_PATTERN = "%s with id %s was successfully updated";

	private static final String CREATE_PATTERN = "%s was successfully created with id %s";

	private MethodOutcomeBuilder() {
	}

	public static MethodOutcome buildCustom(String message) {
		return buildWithResource(null, message);
	}

	public static MethodOutcome buildUpdate(DomainResource resource) {
		return buildWithPattern(resource, UPDATE_PATTERN);
	}

	public static MethodOutcome buildCreate(DomainResource resource) {
		return buildWithPattern(resource, CREATE_PATTERN);
	}

	private static MethodOutcome buildWithPattern(DomainResource resource, String messagePattern) {
		String message = String.format(messagePattern, resource.getClass().getSimpleName(), resource.getId());
		return buildWithResource(resource, message);
	}

	private static MethodOutcome buildWithResource(DomainResource resource, String message) {
		MethodOutcome retVal = new MethodOutcome();
		if (resource != null) {
			retVal.setId(new IdType(resource.getClass().getSimpleName(), resource.getId()));
		}

		CodeableConcept concept = new CodeableConcept();
		Coding coding = concept.addCoding();
		coding.setDisplay(message);

		OperationOutcome outcome = new OperationOutcome();
		outcome.addIssue().setDetails(concept);
		retVal.setOperationOutcome(outcome);
		return retVal;
	}
}
