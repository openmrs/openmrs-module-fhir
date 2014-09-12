package org.openmrs.module.fhir.api;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu.composite.*;
import ca.uhn.fhir.model.dstu.resource.Observation;
import ca.uhn.fhir.model.dstu.resource.Patient;
import ca.uhn.fhir.model.dstu.valueset.AdministrativeGenderCodesEnum;
import ca.uhn.fhir.model.dstu.valueset.IdentifierUseEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.StringDt;
import ca.uhn.fhir.narrative.DefaultThymeleafNarrativeGenerator;
import ca.uhn.fhir.parser.IParser;
import org.openmrs.*;
import org.openmrs.api.context.Context;

import java.util.*;

/**
 * Created by snkasthu on 9/6/14.
 */
public class FHIRPatientUtil {

    /*public static void generatePatient(DelegatingResourceDescription description){
        Map<String, DelegatingResourceDescription.Property> map = description.getProperties();

        DelegatingResourceDescription.Property value = map.get("person");


    }*/

    public String generatePatient(org.openmrs.Patient omrsPatient){
        Patient patient = new Patient();
        patient.setId(omrsPatient.getUuid());

        for(PatientIdentifier identifier : omrsPatient.getIdentifiers()) {
            String uri = Context.getAdministrationService().getSystemVariables().get("OPENMRS_HOSTNAME") + "/ws/rest/v1/patientidentifiertype/" + identifier.getIdentifierType().getUuid();
            if(identifier.isPreferred()) {

                patient.addIdentifier().setUse(IdentifierUseEnum.USUAL).setSystem(uri).setValue(identifier.getIdentifier()).setLabel(identifier.getIdentifierType().getName());
            }else {
                patient.addIdentifier().setUse(IdentifierUseEnum.OFFICIAL).setSystem(uri).setValue(identifier.getIdentifier()).setLabel(identifier.getIdentifierType().getName());
            }
            }

        List<HumanNameDt> humanNameDts = new ArrayList<HumanNameDt>();

        System.out.print(omrsPatient.getNames().size());

        for(PersonName name : omrsPatient.getNames()){

            HumanNameDt fhirName = new HumanNameDt();
            StringDt familyName = new StringDt();
            familyName.setValue(name.getFamilyName());
            List<StringDt> familyNames = new ArrayList<StringDt>();
            familyNames.add(familyName);

            fhirName.setFamily(familyNames);


            StringDt givenName = new StringDt();
            givenName.setValue(name.getGivenName());
            List<StringDt> givenNames = new ArrayList<StringDt>();
            givenNames.add(givenName);

            fhirName.setGiven(givenNames);


            if (name.getFamilyNameSuffix() != null) {
                StringDt suffix = fhirName.addSuffix();
                suffix.setValue(name.getFamilyNameSuffix());
            }

            if (name.getPrefix() != null) {
                StringDt prefix = fhirName.addPrefix();
                prefix.setValue(name.getPrefix());
            }


            humanNameDts.add(fhirName);

        }

        patient.setName(humanNameDts);


        if(omrsPatient.getGender().equals("M"))
            patient.setGender(AdministrativeGenderCodesEnum.M);
        if(omrsPatient.getGender().equals("F"))
            patient.setGender(AdministrativeGenderCodesEnum.F);

        List<AddressDt> fhirAddresses = patient.getAddress();

        for(PersonAddress address : omrsPatient.getAddresses()){

            AddressDt fhirAddress = new AddressDt();
            fhirAddress.setCity(address.getCityVillage());
            fhirAddress.setCountry(address.getCountry());

            List<StringDt> addressStrings = new ArrayList<StringDt>();

            addressStrings.add(new StringDt(address.getAddress1()));
            addressStrings.add(new StringDt(address.getAddress2()));
            addressStrings.add(new StringDt(address.getAddress3()));
            addressStrings.add(new StringDt(address.getAddress4()));
            addressStrings.add(new StringDt(address.getAddress5()));


            fhirAddress.setLine(addressStrings);
            fhirAddresses.add(fhirAddress);

        }

        NarrativeDt dt = new NarrativeDt();

        patient.setText(dt);
        patient.setAddress(fhirAddresses);

        DateTimeDt fhirBirthDate = patient.getBirthDate();
        fhirBirthDate.setValue(omrsPatient.getBirthdate());

        patient.setActive(!omrsPatient.isVoided());

        FhirContext ctx = new FhirContext();
       // ctx.setNarrativeGenerator(new DefaultThymeleafNarrativeGenerator());

        IParser jsonParser = ctx.newJsonParser();
        jsonParser.setPrettyPrint(true);
        String encoded = jsonParser.encodeResourceToString(patient);
        System.out.println(encoded);

        return encoded;

    }


    public static void generateObs(Obs obs){

            Observation observation = new Observation();
            observation.setId(obs.getUuid());

            Collection<ConceptMap> mappings = obs.getConcept().getConceptMappings();
            CodeableConceptDt dt = observation.getName();
            List<CodingDt> dts = new ArrayList<CodingDt>();

            for(ConceptMap map : mappings){
                System.out.println(map.getSource().getName());
                if(map.getSource().getName().equals("LOINC"))
                    dts.add(new CodingDt().setCode("code").setDisplay("display").setSystem(Constants.loinc));
                if(map.getSource().getName().equals("SNOMED"))
                    dts.add(new CodingDt().setCode("code").setDisplay("display").setSystem(Constants.snomed));

            }
            dt.setCoding(dts);

           // Datatype dtype = observation.getValue();



            FhirContext ctx = new FhirContext();
            IParser jsonParser = ctx.newJsonParser();
            jsonParser.setPrettyPrint(true);
            String encoded = jsonParser.encodeResourceToString(observation);
            System.out.println(encoded);

        }

}
