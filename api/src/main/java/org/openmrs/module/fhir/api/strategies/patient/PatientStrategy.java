package org.openmrs.module.fhir.api.strategies.patient;

import ca.uhn.fhir.rest.server.exceptions.MethodNotAllowedException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.FamilyMemberHistory;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Patient;
import org.openmrs.Encounter;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonName;
import org.openmrs.Visit;
import org.openmrs.api.APIException;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.EncounterService;
import org.openmrs.module.fhir.api.FamilyMemberHistoryService;
import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.openmrs.module.fhir.api.util.FHIRLocationUtil;
import org.openmrs.module.fhir.api.util.FHIRPatientUtil;
import org.openmrs.module.fhir.api.util.FHIRUtils;
import org.openmrs.module.fhir.api.util.FHIRVisitUtil;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.openmrs.module.fhir.api.util.FHIRUtils.extractUuid;

@Component("DefaultPatientStrategy")
public class PatientStrategy implements GenericPatientStrategy {

    @Override
    public Patient getPatient(String uuid) {
        org.openmrs.Patient omrsPatient = Context.getPatientService().getPatientByUuid(uuid);

        if (omrsPatient == null || omrsPatient.isVoided()) {
            return null;
        }
        return FHIRPatientUtil.generatePatient(omrsPatient);
    }

    @Override
    public List<Patient> searchPatientsById(String uuid) {
        uuid = extractUuid(uuid);
        org.openmrs.Patient omrsPatient = Context.getPatientService().getPatientByUuid(uuid);

        List<Patient> patientList = new ArrayList<>();
        if (omrsPatient != null && !omrsPatient.isVoided()) {
            patientList.add(FHIRPatientUtil.generatePatient(omrsPatient));
        }
        return patientList;
    }

    @Override
    public List<Patient> searchPatientsByIdentifier(String identifierValue, String identifierTypeName) {
        org.openmrs.api.PatientService patientService = Context.getPatientService();
        List<PatientIdentifierType> patientIdentifierTypes = new ArrayList<>();
        patientIdentifierTypes.add(patientService.getPatientIdentifierTypeByName(identifierTypeName));
        List<org.openmrs.Patient> patientList = patientService.getPatients(identifierValue, null,
                patientIdentifierTypes, true);

        List<Patient> fhirPatientList = new ArrayList<>();
        for (org.openmrs.Patient patient : patientList) {
            fhirPatientList.add(FHIRPatientUtil.generatePatient(patient));
        }
        return fhirPatientList;
    }

    @Override
    public List<Patient> searchPatientsByIdentifier(String identifier) {
        org.openmrs.api.PatientService patientService = Context.getPatientService();
        List<PatientIdentifierType> allPatientIdentifierTypes = patientService.getAllPatientIdentifierTypes();
        List<org.openmrs.Patient> patientList = patientService
                .getPatients(identifier, null, allPatientIdentifierTypes, true);

        List<Patient> fhirPatientList = new ArrayList<>();
        for (org.openmrs.Patient patient : patientList) {
            fhirPatientList.add(FHIRPatientUtil.generatePatient(patient));
        }
        return fhirPatientList;
    }

    @Override
    public List<Patient> searchPatients(boolean active) {
        //TODO this method looks for all the patients which is inefficient. Reimplement after API revamp
        List<org.openmrs.Patient> patients = Context.getPatientService().getAllPatients(true);

        List<Patient> fhirPatientList = new ArrayList<>();
        for (org.openmrs.Patient patient : patients) {
            if (active) {
                if (!patient.isVoided()) {
                    fhirPatientList.add(FHIRPatientUtil.generatePatient(patient));
                }
            } else {
                if (patient.isVoided()) {
                    fhirPatientList.add(FHIRPatientUtil.generatePatient(patient));
                }
            }
        }
        return fhirPatientList;
    }

    @Override
    public Bundle searchPatientsByGivenName(String givenName) {
        List<org.openmrs.Patient> patients = searchPatientByQuery(givenName);

        List<Patient> fhirPatientList = new ArrayList<>();
        //Go through the patients given by the openmrs core api and find them patient who has the givenName matching
        for (org.openmrs.Patient patient : patients) {
            if (givenName.toLowerCase().contains(patient.getGivenName().toLowerCase())) {
                fhirPatientList.add(FHIRPatientUtil.generatePatient(patient));
            } else {
                for (PersonName personName : patient.getNames()) {
                    if (givenName.toLowerCase().contains(personName.getGivenName().toLowerCase())) {
                        fhirPatientList.add(FHIRPatientUtil.generatePatient(patient));
                    }
                }
            }
        }

        Bundle bundle = new Bundle();
        List<Bundle.BundleEntryComponent> filteredList = new ArrayList<>();
        for (Patient fhirPatient : fhirPatientList) {
            Bundle.BundleEntryComponent entry = new Bundle.BundleEntryComponent();
            entry.setResource(fhirPatient);
            filteredList.add(entry);
        }
        bundle.setEntry(filteredList);

        return bundle;
    }

    @Override
    public Bundle searchPatientsByFamilyName(String familyName) {
        List<org.openmrs.Patient> patients = searchPatientByQuery(familyName);

        List<Patient> fhirPatientList = new ArrayList<>();
        //Go through the patients given by the openmrs core api and find them patient who has the familyName matching
        for (org.openmrs.Patient patient : patients) {
            if (familyName.toLowerCase().contains(patient.getFamilyName().toLowerCase())) {
                fhirPatientList.add(FHIRPatientUtil.generatePatient(patient));
            } else {
                for (PersonName personName : patient.getNames()) {
                    if (familyName.toLowerCase().contains(personName.getFamilyName().toLowerCase())) {
                        fhirPatientList.add(FHIRPatientUtil.generatePatient(patient));
                    }
                }
            }
        }

        Bundle bundle = new Bundle();
        List<Bundle.BundleEntryComponent> filteredList = new ArrayList<>();
        for (Patient fhirPatient : fhirPatientList) {
            Bundle.BundleEntryComponent entry = new Bundle.BundleEntryComponent();
            entry.setResource(fhirPatient);
            filteredList.add(entry);
        }
        bundle.setEntry(filteredList);

        return bundle;
    }

    @Override
    public Bundle searchPatientsByName(String name) {
        List<org.openmrs.Patient> patients = searchPatientByQuery(name);

        List<Patient> fhirPatientList = new ArrayList<>();
        for (org.openmrs.Patient patient : patients) {
            fhirPatientList.add(FHIRPatientUtil.generatePatient(patient));
        }

        Bundle bundle = new Bundle();
        List<Bundle.BundleEntryComponent> filteredList = new ArrayList<>();
        for (Patient fhirPatient : fhirPatientList) {
            Bundle.BundleEntryComponent entry = new Bundle.BundleEntryComponent();
            entry.setResource(fhirPatient);
            filteredList.add(entry);
        }
        bundle.setEntry(filteredList);

        return bundle;
    }

    @Override
    public Bundle getPatientOperationsById(String patientId) {
        EncounterService encounterService = Context.getService(EncounterService.class);
        FamilyMemberHistoryService familyHistoryService = Context.getService(FamilyMemberHistoryService.class);
        org.openmrs.Patient omsrPatient = Context.getPatientService().getPatientByUuid(patientId);

        Bundle bundle = new Bundle();
        if (omsrPatient != null) {
            Bundle.BundleEntryComponent patient = bundle.addEntry();
            patient.setResource(FHIRPatientUtil.generatePatient(omsrPatient));

            //Set Enconter resources using encounter everything operation
            for (Encounter enc : Context.getEncounterService().getEncountersByPatient(omsrPatient)) {
                encounterService.getEncounterOperationsById(enc.getUuid(), bundle, false);
            }

            //Set patients' relationships
            for (FamilyMemberHistory familyHistory : familyHistoryService.searchFamilyHistoryByPersonId(omsrPatient
                    .getUuid())) {
                bundle.addEntry().setResource(familyHistory);
            }

            //Set visits
            for (Visit visit : Context.getVisitService().getVisitsByPatient(omsrPatient)) {
                bundle.addEntry().setResource(FHIRVisitUtil.generateEncounter(visit));
                if (visit.getLocation() != null) {
                    bundle.addEntry().setResource(FHIRLocationUtil.generateLocation(visit.getLocation()));
                }
            }

            List<Bundle.BundleEntryComponent> filteredList = rejectResourceDuplicates(bundle);

            bundle.setEntry(filteredList);
        }
        return bundle;
    }

    @Override
    public void deletePatient(String uuid) {
        uuid = extractUuid(uuid);
        org.openmrs.Patient patient = Context.getPatientService().getPatientByUuid(uuid);

        // patient not found. return with 404
        if (patient == null) {
            //Jira related https://issues.openmrs.org/browse/FM-194
            throw new ResourceNotFoundException(new IdType(FHIRConstants.PATIENT, uuid));
        }
        try {
            Context.getPatientService().voidPatient(patient, FHIRConstants.PATIENT_DELETE_MESSAGE);
        } catch (APIException ex) {
            // refused to retire resource.  return with 405
            throw new MethodNotAllowedException("The OpenMRS API refused to retire the Patient via the FHIR request.");
        }
    }

    @Override
    public Patient createFHIRPatient(Patient patient) {
        List<String> errors = new ArrayList<>();
        org.openmrs.Patient omrsPatient = FHIRPatientUtil.generateOmrsPatient(patient, errors);

        FHIRUtils.checkGeneratorErrorList(errors);

        org.openmrs.api.PatientService patientService = Context.getPatientService();
        try {
            omrsPatient = patientService.savePatient(omrsPatient);
        } catch (Exception e) {
            throw new UnprocessableEntityException(
                    "The request cannot be processed due to the following issues \n" + e.getMessage());
        }
        return FHIRPatientUtil.generatePatient(omrsPatient);
    }

    @Override
    public Patient updatePatient(Patient patient, String uuid) {
        uuid = extractUuid(uuid);
        PatientService patientService = Context.getPatientService();
        org.openmrs.Patient retrievedPatient = patientService.getPatientByUuid(uuid);

        return retrievedPatient != null ? updateRetrievedPatient(patient, retrievedPatient) : createPatient(patient, uuid);
    }

    private Patient updateRetrievedPatient(Patient patient, org.openmrs.Patient retrievedPatient) {
        List<String> errors = new ArrayList<>();
        org.openmrs.Patient omrsPatient = FHIRPatientUtil.generateOmrsPatient(patient, errors);
        FHIRUtils.checkGeneratorErrorList(errors);

        retrievedPatient = FHIRPatientUtil.updatePatientAttributes(omrsPatient, retrievedPatient);
        try {
            Context.getPatientService().savePatient(retrievedPatient);
        } catch (Exception e) {
            throw new UnprocessableEntityException(
                    "The request cannot be processed due to the following issues \n" + e.getMessage());
        }
        return FHIRPatientUtil.generatePatient(retrievedPatient);
    }

    private Patient createPatient(Patient patient, String uuid) {
        uuid = extractUuid(uuid);
        if (patient.getId() == null) { // since we need to PUT the patient to a specific URI, we need to set the uuid
            IdType uuidType = new IdType();
            uuidType.setValue(uuid);
            patient.setId(uuidType);
        }
        return createFHIRPatient(patient);
    }

    private List<org.openmrs.Patient> searchPatientByQuery(String query) {
        return Context.getPatientService().getPatients(query);
    }

    private List<Bundle.BundleEntryComponent> rejectResourceDuplicates(Bundle bundle) {
        List<Bundle.BundleEntryComponent> result = new ArrayList<>();

        for (Bundle.BundleEntryComponent temp : bundle.getEntry()) {
            boolean contains = false;
            for (Bundle.BundleEntryComponent filtered : result) {
                if (filtered.getResource().getId().equals(temp.getResource().getId())) {
                    contains = true;
                }
            }
            if (!contains) {
                result.add(temp);
            }
        }

        return result;
    }
}
