package org.openmrs.module.fhir.helper;

import ca.uhn.fhir.rest.server.exceptions.MethodNotAllowedException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.dstu3.model.AllergyIntolerance;
import org.openmrs.Allergy;
import org.openmrs.Patient;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.APIException;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.helper.AllergyHelper;
import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.openmrs.module.fhir.util.FHIRAllergyIntoleranceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component(value = "fhir.AllergyHelper")
@OpenmrsProfile(openmrsPlatformVersion = "2.0.*")
public class AllergyHelperImpl2_0 implements AllergyHelper {

	@Autowired
	private PatientService patientService;

	@Override
	public AllergyIntolerance getAllergyIntolerance(String uuid) {
		org.openmrs.Allergy openMRSAllergy = patientService.getAllergyByUuid(uuid);

		return openMRSAllergy != null ?
				FHIRAllergyIntoleranceUtil.generateAllergyIntolerance(openMRSAllergy)
				: null;
	}

	@Override
	public Collection<AllergyIntolerance> getAllergyIntoleranceByPatient(Patient patient) {
		List<AllergyIntolerance> allergies = new ArrayList<>();
		PatientService allergyService = Context.getService(PatientService.class);
		for (org.openmrs.Allergy allergy : allergyService.getAllergies(patient)) {
			allergies.add(FHIRAllergyIntoleranceUtil.generateAllergyIntolerance(allergy));
		}
		return allergies;
	}

	@Override
	public AllergyIntolerance createAllergy(AllergyIntolerance allergyIntolerance) {
		Allergy allergy = FHIRAllergyIntoleranceUtil.generateAllergy(allergyIntolerance);
		return FHIRAllergyIntoleranceUtil.generateAllergyIntolerance(saveAllergy(allergy));
	}

	@Override
	public AllergyIntolerance updateAllergy(AllergyIntolerance allergyIntolerance, String uuid) {
		Allergy newAllergy = FHIRAllergyIntoleranceUtil.generateAllergy(allergyIntolerance);

		Allergy allergy = patientService.getAllergyByUuid(uuid);
		if (allergy != null) {
			allergy = FHIRAllergyIntoleranceUtil.updateAllergyAttributes(allergy, newAllergy);
			allergy = saveAllergy(allergy);
		} else {
			newAllergy.setUuid(uuid);
			allergy = saveAllergy(newAllergy);
		}

		return FHIRAllergyIntoleranceUtil.generateAllergyIntolerance(allergy);
	}

	@Override
	public void deleteAllergy(String uuid) {
		Allergy allergy = patientService.getAllergyByUuid(uuid);

		if (allergy == null) {
			throw new ResourceNotFoundException(String.format("Allergy with id '%s' not found", uuid));
		} else {
			try {
				patientService.removeAllergy(allergy, FHIRConstants.FHIR_RETIRED_MESSAGE);
			} catch (APIException apie) {
				throw new MethodNotAllowedException(String.format("OpenMRS has failed to retire allergy'%s': %s", uuid,
						apie.getMessage()));
			}
		}
	}

	@Override
	public Object generateAllergy(Object object) {
		return FHIRAllergyIntoleranceUtil.generateAllergy((AllergyIntolerance) object);
	}

	private Allergy saveAllergy(Allergy allergy) {
		patientService.saveAllergy(allergy);
		//retrieve is necessary as saveAllergy(...) returns no value
		return patientService.getAllergyByUuid(allergy.getUuid());
	}
}
