package org.openmrs.module.fhir.api.impl;

import org.hl7.fhir.dstu3.model.Medication;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.fhir.api.MedicationService;
import org.openmrs.module.fhir.api.db.FHIRDAO;
import org.openmrs.module.fhir.api.strategies.medication.MedicationStrategyUtil;

import java.util.List;

public class MedicationServiceImpl extends BaseOpenmrsService implements MedicationService {

	private FHIRDAO fhirdao;

	public FHIRDAO getDao() {
		return fhirdao;
	}

	public void setDao(FHIRDAO dao) {
		this.fhirdao = dao;
	}

	@Override
	public Medication getMedicationById(String uuid) {
		return MedicationStrategyUtil.getMedicationStrategy().getMedicationById(uuid);
	}

	@Override
	public List<Medication> searchMedicationById(String uuid) {
		return MedicationStrategyUtil.getMedicationStrategy().searchMedicationById(uuid);
	}

	@Override
	public Medication createMedication(Medication medication) {
		return MedicationStrategyUtil.getMedicationStrategy().createMedication(medication);
	}

	@Override
	public Medication updateMedication(Medication medication, String id) {
		return MedicationStrategyUtil.getMedicationStrategy().updateMedication(medication, id);
	}

	@Override
	public void deleteMedication(String id) {
		MedicationStrategyUtil.getMedicationStrategy().purgeMedication(id);
	}
}
