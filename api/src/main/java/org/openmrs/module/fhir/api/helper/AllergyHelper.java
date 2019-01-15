package org.openmrs.module.fhir.api.helper;

import org.hl7.fhir.dstu3.model.AllergyIntolerance;
import org.openmrs.Patient;

import java.util.Collection;

public interface AllergyHelper {

	AllergyIntolerance getAllergyIntolerance(String uuid);

	Collection<AllergyIntolerance> getAllergyIntoleranceByPatient(Patient patient);

	AllergyIntolerance createAllergy(AllergyIntolerance allergyIntolerance);

	AllergyIntolerance updateAllergy(AllergyIntolerance allergyIntolerance, String uuid);

	void deleteAllergy(String uuid);

	Object generateAllergy(Object object);
}
