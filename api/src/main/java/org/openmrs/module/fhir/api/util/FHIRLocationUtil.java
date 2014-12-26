package org.openmrs.module.fhir.api.util;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu.resource.Location;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ValidationFailureException;

public class FHIRLocationUtil {

	public static Location generateLocation(org.openmrs.Location omrsLocation) {
		Location location = new Location();

		IdDt uuid = new IdDt();

		uuid.setValue(omrsLocation.getUuid());
        location.setId(uuid);
        
		validate(location);

		return location;
	}

	public static void validate(Location location) {
		FhirContext ctx = new FhirContext();

		// Request a validator and apply it
		FhirValidator val = ctx.newValidator();
		try {
			val.validate(location);
		} catch (ValidationFailureException e) {
			// We failed validation!
			String results = ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(e.getOperationOutcome());
		}

	}
}
