package org.openmrs.module.fhir.providers;

import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.Delete;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.annotation.Update;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Medication;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.openmrs.module.fhir.resources.FHIRMedicationResource;
import org.openmrs.module.fhir.util.MethodOutcomeBuilder;

import java.util.List;

public class RestfulMedicationResourceProvider implements IResourceProvider {

    private FHIRMedicationResource medicationResource;

    public RestfulMedicationResourceProvider() {
        medicationResource = new FHIRMedicationResource();
    }

    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return Medication.class;
    }

    @Create
    public MethodOutcome createFHIRMedication(@ResourceParam Medication medication) {
        return MethodOutcomeBuilder.buildCreate(medicationResource.createMedication(medication));
    }

    @Update
    public MethodOutcome updateFHIRMedication(@ResourceParam Medication medication, @IdParam IdType id) {
        return MethodOutcomeBuilder.buildUpdate(medicationResource.updateMedication(medication, id.getIdPart()));
    }

    @Delete
    public void deleteMedication(@IdParam IdType id) {
        medicationResource.deleteMedication(id);
    }

    @Read
    public Medication getResourceById(@IdParam IdType id) {
        return medicationResource.getByUniqueId(id);
    }

    @Search
    public List<Medication> findMedicationById(
            @RequiredParam(name = Medication.SP_RES_ID)TokenParam id) {
        return medicationResource.searchMedicationById(id);
    }
}
