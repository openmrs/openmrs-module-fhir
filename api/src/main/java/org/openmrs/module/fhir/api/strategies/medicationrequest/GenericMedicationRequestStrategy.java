package org.openmrs.module.fhir.api.strategies.medicationrequest;

import org.hl7.fhir.dstu3.model.MedicationRequest;

import java.util.List;

public interface GenericMedicationRequestStrategy {

	MedicationRequest getMedicationRequestById(String uuid);

	List<MedicationRequest> searchMedicationRequestById(String uuid);

	List<MedicationRequest> searchMedicationRequestByPatientId(String patientUuid);

	void deleteMedicationRequest(String uuid);

	MedicationRequest createFHIRMedicationRequest(MedicationRequest medicationRequest);

	MedicationRequest updateFHIRMedicationRequest(MedicationRequest medicationRequest, String uuid);
}
