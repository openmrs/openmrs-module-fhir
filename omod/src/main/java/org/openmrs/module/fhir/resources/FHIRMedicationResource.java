package org.openmrs.module.fhir.resources;

import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Medication;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.MedicationService;

import java.util.List;

public class FHIRMedicationResource {

	public Medication getByUniqueId(IdType id) {
		Medication medication = getMedicationService().getMedicationById(id.getIdPart());
		if (medication == null) {
			throw new ResourceNotFoundException("Medication is not found for the given id " + id.getIdPart());
		}
		return medication;
	}

	public List<Medication> searchMedicationById(TokenParam id) {
		return getMedicationService().searchMedicationById(id.getValue());
	}

	public Medication createMedication(Medication medication) {
		return getMedicationService().createMedication(medication);
	}

	public Medication updateMedication(Medication medication, String id) {
		return getMedicationService().updateMedication(medication, id);
	}

	public void deleteMedication(IdType id) {
		getMedicationService().deleteMedication(id.getIdPart());
	}

	private MedicationService getMedicationService() {
		return Context.getService(MedicationService.class);
	}
}
