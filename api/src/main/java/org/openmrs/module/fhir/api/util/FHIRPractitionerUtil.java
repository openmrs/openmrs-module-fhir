package org.openmrs.module.fhir.api.util;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu.composite.ContactDt;
import ca.uhn.fhir.model.dstu.composite.HumanNameDt;
import ca.uhn.fhir.model.dstu.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu.composite.NarrativeDt;
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

public class FHIRPractitionerUtil {

	public static Practitioner generatePractitioner(Provider provider) {
		Practitioner practitioner = new Practitioner();

		IdDt uuid = new IdDt();

		uuid.setValue(provider.getUuid());
		practitioner.setId(uuid);

		IdentifierDt identifier = new IdentifierDt();
		identifier.setValue(provider.getIdentifier());
		List<IdentifierDt> identifiers = new ArrayList<IdentifierDt>();
		identifiers.add(identifier);

		practitioner.setIdentifier(identifiers);

		List<HumanNameDt> humanNameDts = new ArrayList<HumanNameDt>();

		for (PersonName name : provider.getPerson().getNames()) {

			if (name.isPreferred()) {

				HumanNameDt fhirName = new HumanNameDt();
				StringDt familyName = new StringDt();
				familyName.setValue(name.getFamilyName());
				List<StringDt> familyNames = new ArrayList<StringDt>();
				familyNames.add(familyName);

				fhirName.setFamily(familyNames);

				StringDt givenName = new StringDt();
				givenName.setValue(name.getGivenName());
				List<StringDt> givenNames = new ArrayList<StringDt>();
				givenNames.add(givenName);

				fhirName.setGiven(givenNames);

				if (name.getFamilyNameSuffix() != null) {
					StringDt suffix = fhirName.addSuffix();
					suffix.setValue(name.getFamilyNameSuffix());
				}

				if (name.getPrefix() != null) {
					StringDt prefix = fhirName.addPrefix();
					prefix.setValue(name.getPrefix());
				}

				if (name.isPreferred()) {
					fhirName.setUse(NameUseEnum.USUAL);
				} else {
					fhirName.setUse(NameUseEnum.NICKNAME);
				}

				humanNameDts.add(fhirName);
			}

		}

		if (provider.getPerson().getGender().equals("M")) {
			practitioner.setGender(AdministrativeGenderCodesEnum.M);

		}
		if (provider.getPerson().getGender().equals("M")) {
			practitioner.setGender(AdministrativeGenderCodesEnum.F);

		}

		NarrativeDt dt = new NarrativeDt();

		DateTimeDt fhirBirthDate = practitioner.getBirthDate();
		fhirBirthDate.setValue(provider.getPerson().getBirthdate());

		List<ContactDt> dts = new ArrayList<ContactDt>();

		validate(practitioner);

		return practitioner;
	}

	public static void validate(Practitioner practitioner) {
		FhirContext ctx = new FhirContext();

		// Request a validator and apply it
		FhirValidator val = ctx.newValidator();
		try {
			val.validate(practitioner);
		} catch (ValidationFailureException e) {
			// We failed validation!
			String results = ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(e.getOperationOutcome());
		}

	}
}
