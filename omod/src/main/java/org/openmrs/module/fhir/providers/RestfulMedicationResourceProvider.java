package org.openmrs.module.fhir.providers;

import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Medication;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.openmrs.module.fhir.resources.FHIRMedicationResource;

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

    @Read()
    public Medication getResourceById(@IdParam IdType id) {
        return medicationResource.getByUniqueId(id);
    }

    @Search()
    public List<Medication> searchMedicationById(
            @RequiredParam(name = Medication.SP_RES_ID)TokenParam id) {
        return medicationResource.searchMedicationById(id);
    }
}
