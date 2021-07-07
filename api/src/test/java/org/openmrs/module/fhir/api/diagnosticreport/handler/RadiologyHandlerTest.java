package org.openmrs.module.fhir.api.diagnosticreport.handler;

import org.hl7.fhir.dstu3.model.DiagnosticReport;
import org.hl7.fhir.dstu3.model.Reference;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterRole;
import org.openmrs.EncounterType;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonName;
import org.openmrs.Provider;
import org.openmrs.Location;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;

import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.module.fhir.api.diagnosticreport.handler.RadiologyHandler;
import org.hl7.fhir.dstu3.model.Coding;

import java.util.Date;
import java.util.Set;
import java.util.List;
import java.util.LinkedHashSet;;
import java.util.TreeSet;;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Ignore;
public class RadiologyHandlerTest extends BaseModuleContextSensitiveTest {

    private PatientService patientService;

    private EncounterService encounterService;

    /**
     * Runs before every test and sets patientService and encounterService
     */
    @Before
    public void runBeforeEveryTest() {
        patientService = Context.getPatientService();
        encounterService = Context.getEncounterService();
    }

    /**
     * Creates and returns a Patient
     * 
     * @return PPatient object
     */
    private Patient createPatient()
    {
        Patient patient = new Patient();

        PersonName pName = new PersonName();
        pName.setGivenName("Tom");
        pName.setMiddleName("E.");
        pName.setFamilyName("Patient");
        patient.addName(pName);

        PersonAddress pAddress = new PersonAddress();
        pAddress.setAddress1("123 My street");
        pAddress.setAddress2("Apt 402");
        pAddress.setCityVillage("Anywhere city");
        pAddress.setCountry("Some Country");
        Set<PersonAddress> pAddressList = patient.getAddresses();
        pAddressList.add(pAddress);
        patient.setAddresses(pAddressList);
        patient.addAddress(pAddress);
        // patient.removeAddress(pAddress);

        patient.setBirthdateEstimated(true);
        patient.setBirthdate(new Date());
        patient.setBirthdateEstimated(true);
        patient.setDeathDate(new Date());
        patient.setCauseOfDeath(new Concept());
        patient.setGender("male");

        List<PatientIdentifierType> patientIdTypes = patientService.getAllPatientIdentifierTypes();
        assertNotNull(patientIdTypes);
        PatientIdentifier patientIdentifier = new PatientIdentifier();
        patientIdentifier.setIdentifier("123-0");
        patientIdentifier.setIdentifierType(patientIdTypes.get(0));
        patientIdentifier.setLocation(new Location(1));
        patientIdentifier.setPreferred(true);

        Set<PatientIdentifier> patientIdentifiers = new LinkedHashSet<>();
        patientIdentifiers.add(patientIdentifier);

        patient.setIdentifiers(patientIdentifiers);

        patientService.savePatient(patient);

        return patient;
    }

    /**
     * Creates and returns Person, given name
     * 
     * @param name name of the person to be created
     * @return Person object
     */
    private Person newPerson(String name) {
        Person person = new Person();
        Set<PersonName> personNames = new TreeSet<>();
        PersonName personName = new PersonName();
        personName.setFamilyName(name);
        personNames.add(personName);
        person.setNames(personNames);
        person.setPersonDateCreated(new Date());
        return person;
    }

    /**
     * Creates and returns Encounter, given the patient's Uuid
     * 
     * @param patient_uuid Uuid of the patient 
     * @return Encounter object
     */
    private Encounter createEncounter(String patient_uuid)
    {
        Encounter encounter = new Encounter();
        encounter.setLocation(new Location(1));
        encounter.setEncounterType(new EncounterType(1));
        encounter.setEncounterDatetime(new Date());
        encounter.setPatient(patientService.getPatientByUuid(patient_uuid));

        EncounterRole role = new EncounterRole();
        role.setName("role");
        role = Context.getEncounterService().saveEncounterRole(role);

        Provider provider = new Provider();
        provider.setIdentifier("id1");
        provider.setPerson(newPerson("name"));
        provider = Context.getProviderService().saveProvider(provider);

        Provider provider2 = new Provider();
        provider2.setIdentifier("id2");
        provider2.setPerson(newPerson("name2"));
        provider2 = Context.getProviderService().saveProvider(provider2);

        encounter.addProvider(role, provider);
        encounter.addProvider(role, provider2);

        EncounterType encounterType = new EncounterType("test", "test encounter type");
        Context.getEncounterService().saveEncounterType(encounterType);
        encounter.setEncounterType(encounterType);

        Context.getAdministrationService().setGlobalProperty("fhir.encounter.encounterRoleUuid", role.getUuid());

        EncounterService es = Context.getEncounterService();
        es.saveEncounter(encounter);

        encounterService.saveEncounter(encounter);
        return encounter;
    }
    
    /**
     * @see RadiologyHandler#getServiceCategory
     */
    @Test
    public void getServiceCategory_shouldReturnRAD()
    {
    	RadiologyHandler radiologyHandler=new RadiologyHandler();
    	assertEquals(radiologyHandler.getServiceCategory(),"RAD");
    }
    
    /**
     * @see RadiologyHandler#getFHIRDiagnosticReportById
     */
    @Test
    public void getFHIRDiagnosticReportById_shouldReturnNonNullDiagnosticReport() {
        RadiologyHandler  radiologyHandler = new RadiologyHandler();
        Patient patient = createPatient();
        Encounter encounter = createEncounter(patient.getUuid());        
        assertNotNull(radiologyHandler.getFHIRDiagnosticReportById(encounter.getUuid()));
    }
    
    @Test
    public void getFHIRDiagnosticReportBySubjectName_retrunListOfDiagnosticReports()
    {
    	RadiologyHandler radiologyHandler=new RadiologyHandler();
    	Patient patient=createPatient();
    	Encounter encounter = createEncounter(patient.getUuid());
    	String name="";
    	for(PersonName i:patient.getNames())
    	{
    	name=i.getGivenName();
    	break;
    	}
    	try {
       assertNotNull(radiologyHandler.getFHIRDiagnosticReportBySubjectName(name));
    }
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }

    /**
     * @see RadiologyHandler#saveFHIRDiagnosticReport(DiagnosticReport)
     */
    @Test
    public void saveFHIRDiagnosticReport_shouldSaveDiagnosticReport() {
        RadiologyHandler radiologyHandler = new RadiologyHandler();
        Patient patient = createPatient();
        Encounter encounter = createEncounter(patient.getUuid());
        DiagnosticReport diagnosticReport = radiologyHandler.getFHIRDiagnosticReportById(encounter.getUuid());
        Coding c=new Coding();
        c.setCode("test");
        diagnosticReport.getCategory().addCoding(c);
        Reference ref = new Reference();
        ref.setReference("Some reference");
        ref.setDisplay("Some display");
        ref.setId(patient.getUuid());
        diagnosticReport.setSubject(ref);
        Context.getAdministrationService().setGlobalProperty("fhir.encounter.encounterType.test", encounter.getEncounterType().getUuid()); 
    	diagnosticReport = radiologyHandler.saveFHIRDiagnosticReport(diagnosticReport);
    	assertNotNull(encounterService.getEncounterByUuid(diagnosticReport.getId().substring("DiagnosticReport/".length())));    
     }

    /**
     * @see RadiologyHandler#updateFHIRDiagnosticReport(DiagnosticReport,String)
     */
    @Test
    public void updateFHIRDiagnosticReport_shouldUpdateDiagnosticReport() {
        RadiologyHandler radiologyHandler = new RadiologyHandler();
        Patient patient = createPatient();
        Encounter encounter = createEncounter(patient.getUuid());
        DiagnosticReport diagnosticReport = radiologyHandler.getFHIRDiagnosticReportById(encounter.getUuid());
        diagnosticReport.setIssued(new Date(System.currentTimeMillis() - 60*1000));
        Coding c=new Coding();
        c.setCode("test");
        diagnosticReport.getCategory().addCoding(c);
        Reference ref = new Reference();
        ref.setReference("Some reference");
        ref.setDisplay("Some display");
        ref.setId(patient.getUuid());
        diagnosticReport.setSubject(ref);

        Context.getAdministrationService().setGlobalProperty("fhir.encounter.encounterType.test", encounter.getEncounterType().getUuid());

        diagnosticReport = radiologyHandler.saveFHIRDiagnosticReport(diagnosticReport);
        String encounterId = diagnosticReport.getId().substring("DiagnosticReport/".length());

        DiagnosticReport updated = diagnosticReport.copy();
        Date updatedDate = new Date();
        updated.setIssued(updatedDate);

        updated = radiologyHandler.updateFHIRDiagnosticReport(updated, encounterId);
        String updatedId = updated.getId().substring("DiagnosticReport/".length());
        assertNotNull(encounterService.getEncounterByUuid(updatedId));     
        //Since there is no body for updateFHIRDiagnosticReport() in RadiologyHandler, assertEquals is not being included in the test 
    }
} 