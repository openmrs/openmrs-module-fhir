package org.openmrs.module.fhir.api.strategies.patient;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Patient;

import java.util.List;

public interface GenericPatientStrategy {

	Patient getPatient(String uuid);

	List<Patient> searchPatientsById(String id);

	List<Patient> searchPatientsByIdentifier(String identifierValue, String identifierTypeName);

	List<Patient> searchPatientsByIdentifier(String identifierValue);

	List<Patient> searchPatients(boolean active);

	Bundle searchPatientsByGivenName(String givenName);

	Bundle searchPatientsByFamilyName(String familyName);

	Bundle searchPatientsByName(String name);

	Bundle getPatientOperationsById(String patientId);

	void deletePatient(String uuid);

	Patient createFHIRPatient(Patient patient);

	Patient updatePatient(Patient patient, String theId);
}
