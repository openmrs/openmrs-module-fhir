package org.openmrs.module.fhir.api.util;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu.composite.*;
import ca.uhn.fhir.model.dstu.resource.Composition;
import ca.uhn.fhir.model.dstu.resource.Composition.Section;
import ca.uhn.fhir.model.dstu.resource.Encounter;
import ca.uhn.fhir.model.dstu.resource.Practitioner;
import ca.uhn.fhir.model.dstu.valueset.AdministrativeGenderCodesEnum;
import ca.uhn.fhir.model.dstu.valueset.NameUseEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.StringDt;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ValidationFailureException;
import org.openmrs.EncounterProvider;
import org.openmrs.PersonName;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;

import java.util.ArrayList;
import java.util.List;

public class FHIREncounterUtil {

    public static Composition generateEncounter(org.openmrs.Encounter openMRSEncounter) {
        Composition composition = new Composition();

        IdDt uuid = new IdDt();

        uuid.setValue(openMRSEncounter.getUuid());
        composition.setId(uuid);

        Section patientSection = composition.addSection();

        IdDt patientUuid = new IdDt();

        patientUuid.setValue(openMRSEncounter.getPatient().getUuid());
        patientSection.setId(patientUuid);

        ResourceReferenceDt patientReference = new ResourceReferenceDt();

        patientReference.setDisplay("Patient");
        String patientUri = Context.getAdministrationService().getGlobalProperty("fhir.uriPrefix")+ "/Patient/" + openMRSEncounter.getPatient().getUuid();

        IdDt patientRef = new IdDt();
        patientRef.setValue(patientUri);
        patientReference.setReference(patientRef);

        patientSection.setSubject(patientReference);

        for(EncounterProvider provider : openMRSEncounter.getEncounterProviders()){

            Section providerSection = composition.addSection();

            IdDt providerUuid = new IdDt();

            providerUuid.setValue(provider.getUuid());
            providerSection.setId(providerUuid);

            ResourceReferenceDt providerReference = new ResourceReferenceDt();

            providerReference.setDisplay("Provider");
            String providerUri = Context.getAdministrationService().getGlobalProperty("fhir.uriPrefix")+ "/Practitioner/" + provider.getUuid();

            IdDt providerRef = new IdDt();
            providerRef.setValue(providerUri);
            providerReference.setReference(providerRef);

            providerSection.setSubject(providerReference);






        }


        return composition;
    }

    public static void validate(org.openmrs.Encounter encounter){
        FhirContext ctx = new FhirContext();

        // Request a validator and apply it
        FhirValidator val = ctx.newValidator();
        try {


            System.out.println("Validation passed");

        } catch (ValidationFailureException e) {
            // We failed validation!

            System.out.println("Validation failed");

            String results = ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(e.getOperationOutcome());
            System.out.println(results);
        }

    }
}
