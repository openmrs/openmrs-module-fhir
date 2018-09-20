package org.openmrs.module.fhir.api.impl;

import org.hl7.fhir.dstu3.model.Medication;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.fhir.api.MedicationService;
import org.openmrs.module.fhir.api.db.FHIRDAO;

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
        return null;
    }

    @Override
    public List<Medication> searchMedicationById(String uuid) {
        return null;
    }
}
