package org.openmrs.module.fhir.api.util;

import org.hl7.fhir.dstu3.model.Identifier;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;

import java.util.List;

public class FHIRIdentifierUtil {

	public static Identifier generateIdentifier(PatientIdentifier identifier) {
		Identifier patientIdentifier = new Identifier();
		if (identifier.isPreferred()) {
			patientIdentifier
					.setUse(Identifier.IdentifierUse.USUAL)
					.setSystem(identifier.getIdentifierType().getName())
					.setValue(identifier.getIdentifier())
					.setId(identifier.getUuid());
		} else {
			patientIdentifier
					.setUse(Identifier.IdentifierUse.SECONDARY)
					.setSystem(identifier.getIdentifierType()
							.getName()).setValue(identifier.getIdentifier())
					.setId(identifier.getUuid());
		}
		return patientIdentifier;
	}

	public static PatientIdentifier generateOpenmrsIdentifier(Identifier fhirIdentifier, List<String> errors) {
		PatientIdentifier patientIdentifier = new PatientIdentifier();
		patientIdentifier.setIdentifier(fhirIdentifier.getValue());
		if (String.valueOf(Identifier.IdentifierUse.USUAL).equalsIgnoreCase(fhirIdentifier.getUse().getDefinition())) {
			patientIdentifier.setPreferred(true);
		} else {
			patientIdentifier.setPreferred(false);
		}
		PatientIdentifierType type = getPatientIdentifierType(fhirIdentifier);
		if (type == null) {
			errors.add(String.format("Missing the PatientIdentifierType with the name '%s' and the UUID '%s'",
					fhirIdentifier.getSystem(), fhirIdentifier.getId()));
		}
		patientIdentifier.setIdentifierType(type);

		if (type != null) {
			PatientIdentifierType.LocationBehavior lb = type.getLocationBehavior();
			if (lb == null || lb == PatientIdentifierType.LocationBehavior.REQUIRED) {
				LocationService locationService = Context.getLocationService();
				patientIdentifier.setLocation(locationService.getLocation(1));
			}
		}
		patientIdentifier.setUuid(fhirIdentifier.getId());
		return patientIdentifier;
	}

	public static PatientIdentifier updatePatientIdentifier(PatientIdentifier oldIdentifier, PatientIdentifier newIdentifier) {
		oldIdentifier.setIdentifier(newIdentifier.getIdentifier());
		oldIdentifier.setPreferred(newIdentifier.getPreferred());
		oldIdentifier.setIdentifierType(newIdentifier.getIdentifierType());
		oldIdentifier.setLocation(newIdentifier.getLocation());
		return oldIdentifier;
	}

	private static PatientIdentifierType getPatientIdentifierType(Identifier fhirIdentifier) {
		String identifierTypeName = fhirIdentifier.getSystem();
		PatientIdentifierType patientIdentifierType =  Context.getPatientService().getPatientIdentifierTypeByName(identifierTypeName);
		if (patientIdentifierType == null) {
			String identifierTypeUuid = fhirIdentifier.getId();
			patientIdentifierType =  Context.getPatientService().getPatientIdentifierTypeByUuid(identifierTypeUuid);
		}
		return patientIdentifierType;
	}

	private FHIRIdentifierUtil() { }
}
