package org.openmrs.module.fhir.api.util;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu.composite.ContactDt;
import ca.uhn.fhir.model.dstu.composite.HumanNameDt;
import ca.uhn.fhir.model.dstu.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu.composite.NarrativeDt;
import ca.uhn.fhir.model.dstu.resource.AllergyIntolerance;
import ca.uhn.fhir.model.dstu.resource.Practitioner;
import ca.uhn.fhir.model.dstu.valueset.AdministrativeGenderCodesEnum;
import ca.uhn.fhir.model.dstu.valueset.NameUseEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.StringDt;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ValidationFailureException;
import org.openmrs.PersonName;
import org.openmrs.Provider;

import java.util.ArrayList;
import java.util.List;

public class FHIRAllergyIntoleranceUtil {

	public static AllergyIntolerance generatePractitioner() {
        AllergyIntolerance allergyIntolerance = new AllergyIntolerance();

		validate(allergyIntolerance);

		return allergyIntolerance;
	}

	public static void validate(AllergyIntolerance allergyIntolerance) {
		FhirContext ctx = new FhirContext();

		// Request a validator and apply it
		FhirValidator val = ctx.newValidator();
		try {
			val.validate(allergyIntolerance);
		} catch (ValidationFailureException e) {
			// We failed validation!
			String results = ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(e.getOperationOutcome());
		}

	}
}
