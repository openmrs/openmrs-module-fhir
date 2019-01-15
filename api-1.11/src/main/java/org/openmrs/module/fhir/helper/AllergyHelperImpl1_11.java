package org.openmrs.module.fhir.helper;

import ca.uhn.fhir.rest.server.exceptions.MethodNotAllowedException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.dstu3.model.AllergyIntolerance;
import org.openmrs.Patient;
import org.openmrs.activelist.Allergy;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.APIException;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.helper.AllergyHelper;
import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.openmrs.module.fhir.api.util.FHIRUtils;
import org.openmrs.module.fhir.util.FHIRAllergyIntoleranceUtil1_11;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component(value = "fhir.AllergyHelper")
@OpenmrsProfile(openmrsVersion = "1.11.*")
public class AllergyHelperImpl1_11 implements AllergyHelper {

	@Override
	public AllergyIntolerance getAllergyIntolerance(String uuid) {
		Allergy allergy = (Allergy) Context.getActiveListService().getActiveListItemByUuid(uuid);
		return allergy != null ?
				FHIRAllergyIntoleranceUtil1_11.generateAllergyIntolerance(allergy)
				: null;
	}

	@Override
	public Collection<AllergyIntolerance> getAllergyIntoleranceByPatient(Patient patient) {
		List<AllergyIntolerance> allergies = new ArrayList<>();
		PatientService allergyService = Context.getService(PatientService.class);
		for (Allergy allergy : allergyService.getAllergies(patient)) {
			allergies.add(FHIRAllergyIntoleranceUtil1_11.generateAllergyIntolerance(allergy));
		}
		return allergies;
	}

	@Override
	public AllergyIntolerance createAllergy(AllergyIntolerance allergyIntolerance) {
		List<String> errors = new ArrayList<String>();
		Allergy allergy = FHIRAllergyIntoleranceUtil1_11.generateAllergy(allergyIntolerance, errors);
		FHIRUtils.checkGeneratorErrorList(errors);
		return FHIRAllergyIntoleranceUtil1_11.generateAllergyIntolerance(saveAllergy(allergy));
	}

	@Override
	public AllergyIntolerance updateAllergy(AllergyIntolerance allergyIntolerance, String uuid) {
		List<String> errors = new ArrayList<String>();
		Allergy newAllergy = FHIRAllergyIntoleranceUtil1_11.generateAllergy(allergyIntolerance, errors);
		FHIRUtils.checkGeneratorErrorList(errors);

		Allergy allergy = (Allergy) Context.getActiveListService().getActiveListItemByUuid(uuid);
		if (allergy != null) {
			allergy = FHIRAllergyIntoleranceUtil1_11.updateAllergyAttributes(allergy, newAllergy);
			allergy = saveAllergy(allergy);
		} else {
			newAllergy.setUuid(uuid);
			allergy = saveAllergy(newAllergy);
		}

		return FHIRAllergyIntoleranceUtil1_11.generateAllergyIntolerance(allergy);
	}

	@Override
	public void deleteAllergy(String uuid) {
		Allergy allergy = (Allergy) Context.getActiveListService().getActiveListItemByUuid(uuid);

		if (allergy == null) {
			throw new ResourceNotFoundException(String.format("Allergy with id '%s' not found", uuid));
		} else {
			try {
				Context.getPatientService().removeAllergy(allergy, FHIRConstants.FHIR_RETIRED_MESSAGE);
			} catch (APIException apie) {
				throw new MethodNotAllowedException(String.format("OpenMRS has failed to retire allergy'%s': %s", uuid,
						apie.getMessage()));
			}
		}
	}

	@Override
	public Object generateAllergy(Object object) {
		List<String> errors = new ArrayList<String>();
		Object allergy = FHIRAllergyIntoleranceUtil1_11.generateAllergy((AllergyIntolerance) object, errors);
		FHIRUtils.checkGeneratorErrorList(errors);
		return allergy;
	}

	private Allergy saveAllergy(Allergy allergy) {
		Context.getPatientService().saveAllergy(allergy);
		//retrieve is necessary as saveAllergy(...) returns no value
		return (Allergy) Context.getActiveListService().getActiveListItemByUuid(allergy.getUuid());
	}
}
