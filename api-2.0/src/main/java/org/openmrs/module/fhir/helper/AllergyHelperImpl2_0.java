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
import org.openmrs.module.fhir.api.util.FHIRUtils;
import org.openmrs.module.fhir.util.FHIRAllergyIntoleranceUtil2_0;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component(value = "fhir.AllergyHelper")
@OpenmrsProfile(openmrsPlatformVersion = "2.0.* - 2.1.*")
public class AllergyHelperImpl2_0 implements AllergyHelper {

	@Override
	public AllergyIntolerance getAllergyIntolerance(String uuid) {
		Allergy openMRSAllergy = Context.getPatientService().getAllergyByUuid(uuid);

		return openMRSAllergy != null ?
				FHIRAllergyIntoleranceUtil2_0.generateAllergyIntolerance(openMRSAllergy)
				: null;
	}

	@Override
	public Collection<AllergyIntolerance> getAllergyIntoleranceByPatient(Patient patient) {
		List<AllergyIntolerance> allergies = new ArrayList<>();
		PatientService allergyService = Context.getService(PatientService.class);
		for (Allergy allergy : allergyService.getAllergies(patient)) {
			allergies.add(FHIRAllergyIntoleranceUtil2_0.generateAllergyIntolerance(allergy));
		}
		return allergies;
	}

	@Override
	public AllergyIntolerance createAllergy(AllergyIntolerance allergyIntolerance) {
		List<String> errors = new ArrayList<String>();
		Allergy allergy = FHIRAllergyIntoleranceUtil2_0.generateAllergy(allergyIntolerance, errors);
		FHIRUtils.checkGeneratorErrorList(errors);
		return FHIRAllergyIntoleranceUtil2_0.generateAllergyIntolerance(saveAllergy(allergy));
	}

	@Override
	public AllergyIntolerance updateAllergy(AllergyIntolerance allergyIntolerance, String uuid) {
		List<String> errors = new ArrayList<String>();
		Allergy newAllergy = FHIRAllergyIntoleranceUtil2_0.generateAllergy(allergyIntolerance, errors);
		FHIRUtils.checkGeneratorErrorList(errors);


		Allergy allergy = Context.getPatientService().getAllergyByUuid(uuid);
		if (allergy != null) {
			allergy = FHIRAllergyIntoleranceUtil2_0.updateAllergyAttributes(allergy, newAllergy);
			allergy = saveAllergy(allergy);
		} else {
			newAllergy.setUuid(uuid);
			allergy = saveAllergy(newAllergy);
		}

		return FHIRAllergyIntoleranceUtil2_0.generateAllergyIntolerance(allergy);
	}

	@Override
	public void deleteAllergy(String uuid) {
		Allergy allergy = Context.getPatientService().getAllergyByUuid(uuid);

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
		Object allergy = FHIRAllergyIntoleranceUtil2_0.generateAllergy((AllergyIntolerance) object, errors);
		FHIRUtils.checkGeneratorErrorList(errors);
		return allergy;
	}

	private Allergy saveAllergy(Allergy allergy) {
		Context.getPatientService().saveAllergy(allergy);
		//retrieve is necessary as saveAllergy(...) returns no value
		return Context.getPatientService().getAllergyByUuid(allergy.getUuid());
	}
}
