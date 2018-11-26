package org.openmrs.module.fhir.api.util;

import org.hl7.fhir.dstu3.model.Identifier;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;

import java.util.List;

public class FHIRIdentifierUtil {

	public static PatientIdentifier generateOpenmrsIdentifier(Identifier fhirIdentifier, List<String> errors) {
		PatientIdentifier patientIdentifier = new PatientIdentifier();
		patientIdentifier.setIdentifier(fhirIdentifier.getValue());
		if (String.valueOf(Identifier.IdentifierUse.USUAL).equalsIgnoreCase(fhirIdentifier.getUse().getDefinition())) {
			patientIdentifier.setPreferred(true);
		} else {
			patientIdentifier.setPreferred(false);
		}
		String identifierTypeName = fhirIdentifier.getSystem();
		PatientIdentifierType type = Context.getPatientService().getPatientIdentifierTypeByName(identifierTypeName);
		if (type == null) {
			errors.add("No PatientIdentifierType exists for the given PatientIdentifierTypeName");
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

	private FHIRIdentifierUtil() { }
}
