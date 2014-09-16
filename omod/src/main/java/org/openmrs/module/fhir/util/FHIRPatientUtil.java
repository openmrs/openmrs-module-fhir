package org.openmrs.module.fhir.util;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.*;
import ca.uhn.fhir.model.dstu.composite.*;
import ca.uhn.fhir.model.dstu.resource.Observation;
import ca.uhn.fhir.model.dstu.resource.Patient;
import ca.uhn.fhir.model.dstu.valueset.*;
import ca.uhn.fhir.model.primitive.*;
import ca.uhn.fhir.narrative.DefaultThymeleafNarrativeGenerator;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ValidationFailureException;
import org.openmrs.*;
import org.openmrs.api.context.Context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class FHIRPatientUtil {


    public static Patient generatePatient(org.openmrs.Patient omrsPatient) {

            Patient patient = new Patient();
            IdDt uuid = new IdDt();

            uuid.setValue(omrsPatient.getUuid());
            patient.setId(uuid);


            for (PatientIdentifier identifier : omrsPatient.getIdentifiers()) {
                String uri = "http://" + Context.getAdministrationService().getSystemVariables().get("OPENMRS_HOSTNAME") + "/ws/rest/v1/patientidentifiertype/" + identifier.getIdentifierType().getUuid();
                if (identifier.isPreferred()) {

                    patient.addIdentifier().setUse(IdentifierUseEnum.USUAL).setSystem(uri).setValue(identifier.getIdentifier()).setLabel(identifier.getIdentifierType().getName());
                } else {
                    patient.addIdentifier().setUse(IdentifierUseEnum.SECONDARY).setSystem(uri).setValue(identifier.getIdentifier()).setLabel(identifier.getIdentifierType().getName());
                }
            }

            List<HumanNameDt> humanNameDts = new ArrayList<HumanNameDt>();

            for (PersonName name : omrsPatient.getNames()) {

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


                if (name.isPreferred())
                    fhirName.setUse(NameUseEnum.USUAL);
                else
                    fhirName.setUse(NameUseEnum.NICKNAME);

                humanNameDts.add(fhirName);

            }

            patient.setName(humanNameDts);


            if (omrsPatient.getGender().equals("M")) {
                patient.setGender(AdministrativeGenderCodesEnum.M);

            }
            if (omrsPatient.getGender().equals("F")) {
                patient.setGender(AdministrativeGenderCodesEnum.F);

            }

            List<AddressDt> fhirAddresses = patient.getAddress();

            for (PersonAddress address : omrsPatient.getAddresses()) {

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

                if (address.isPreferred())
                    fhirAddress.setUse(AddressUseEnum.HOME);
                else
                    fhirAddress.setUse(AddressUseEnum.OLD);

                fhirAddresses.add(fhirAddress);

            }

            NarrativeDt dt = new NarrativeDt();

            patient.setText(dt);
            patient.setAddress(fhirAddresses);

            DateTimeDt fhirBirthDate = patient.getBirthDate();
            fhirBirthDate.setValue(omrsPatient.getBirthdate());

            patient.setActive(!omrsPatient.isVoided());

            BooleanDt isDeceased = new BooleanDt();
            isDeceased.setValue(omrsPatient.getDead());
            patient.setDeceased(isDeceased);

            List<ContactDt> dts = new ArrayList<ContactDt>();

            ContactDt telecom = new ContactDt();
            if (omrsPatient.getAttribute("Telephone Number") != null)
                telecom.setValue(omrsPatient.getAttribute("Telephone Number").getValue());
            else
                telecom.setValue("None");
            dts.add(telecom);
            patient.setTelecom(dts);

        return patient;

    }

    public static String parsePatient(Patient patient, String contentType){

        FhirContext ctx = new FhirContext();
        ctx.setNarrativeGenerator(new DefaultThymeleafNarrativeGenerator());

        IParser jsonParser = ctx.newJsonParser();
        IParser xmlParser = ctx.newXmlParser();

        String encoded = null;

        if(contentType != null) {
            if (contentType.equals("application/xml+fhir")) {

                xmlParser.setPrettyPrint(true);
                encoded = xmlParser.encodeResourceToString(patient);
                System.out.println(encoded);
            }else{
                jsonParser.setPrettyPrint(true);
                encoded = jsonParser.encodeResourceToString(patient);
                System.out.println(encoded);

            }
        }

        else {

            jsonParser.setPrettyPrint(true);
            encoded = jsonParser.encodeResourceToString(patient);
            System.out.println(encoded);
        }

        return encoded;
    }


}
