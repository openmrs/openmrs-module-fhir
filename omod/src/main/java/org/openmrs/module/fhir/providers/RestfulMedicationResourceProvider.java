package org.openmrs.module.fhir.providers;

import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Medication;
import org.hl7.fhir.dstu3.model.OperationOutcome;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.openmrs.module.fhir.api.util.FHIRConstants;
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

    @Create()
    public MethodOutcome createFHIRMedication(@ResourceParam Medication medication) {
        Medication createdMedication = medicationResource.createMedication(medication);
        return createMethodOutcome(createdMedication.getId());
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

    private MethodOutcome createMethodOutcome(String resourceId) {
        MethodOutcome retVal = new MethodOutcome();
        if (resourceId != null) {
            retVal.setId(new IdType(Medication.class.getSimpleName(), resourceId));

            OperationOutcome outcome = new OperationOutcome();
            CodeableConcept concept = new CodeableConcept();
            Coding coding = concept.addCoding();
            coding.setDisplay("Medication successfully created with id " + resourceId);
            outcome.addIssue().setDetails(concept);
            retVal.setOperationOutcome(outcome);
        } else {
            OperationOutcome outcome = new OperationOutcome();
            CodeableConcept concept = new CodeableConcept();
            Coding coding = concept.addCoding();
            coding.setDisplay("Failed to create Medication");
            outcome.addIssue().setDetails(concept);
            retVal.setOperationOutcome(outcome);
        }
        return retVal;
    }
}
