/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.fhir.api.reverse;
import ca.uhn.fhir.model.dstu2.composite.AddressDt;
import ca.uhn.fhir.model.dstu2.composite.HumanNameDt;
import ca.uhn.fhir.model.dstu2.valueset.AddressUseEnum;
import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
import ca.uhn.fhir.model.dstu2.valueset.NameUseEnum;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.StringDt;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonName;
import org.openmrs.api.context.Context;
import java.util.List;

import static java.lang.String.valueOf;

        public class OpenmrsPersonUtil {

         public static Person generateOpenMRSPerson(ca.uhn.fhir.model.dstu2.resource.Person personFHIR) {

                //Set Person ID
                Person omrsPerson = new Person();
                IdDt uuid = new IdDt();
                uuid.setId(personFHIR.getId());
                omrsPerson.setUuid(valueOf(uuid));

                for (HumanNameDt humanNameDt : personFHIR.getName()) {
                PersonName personName = new PersonName();
                if (humanNameDt.getUse() != null) {
                String getUse = humanNameDt.getUse();

                 if (valueOf(NameUseEnum.USUAL) == getUse) {
                 personName.setPreferred(true);
                 }
                 if (valueOf(NameUseEnum.OLD) == getUse) {
                 personName.setPreferred(false);
                 }
                 }
                 if (humanNameDt.getSuffix() != null) {
                 List<StringDt> prefixes = humanNameDt.getSuffix();
                 StringDt prefix = prefixes.get(0); //need to finalize on this value
                 personName.setPrefix(valueOf(prefix));
                 }

                 if (humanNameDt.getSuffix() != null) {
                      List<StringDt> suffixes = humanNameDt.getSuffix();
                      StringDt suffix = (StringDt) suffixes.get(0);//need to finalize on this value
                      personName.setFamilyNameSuffix(valueOf(suffix));
                 }

                    List<StringDt> givenNames = humanNameDt.getGiven();
                    StringDt givenName = givenNames.get(0);
                    personName.setGivenName(valueOf(givenName));
                    List<StringDt> familyNames = humanNameDt.getFamily();
                    StringDt familyName = familyNames.get(0);
                    personName.setFamilyName(valueOf(familyName));

                }


             //set address in OpenMRS Person
                 PersonAddress address = new PersonAddress();
                 for (AddressDt fhirAddress : personFHIR.getAddress()) {

                     address.setCityVillage(fhirAddress.getCity());
                     address.setCountry(fhirAddress.getCountry());
                     address.setStateProvince(fhirAddress.getState());
                     address.setPostalCode(fhirAddress.getPostalCode());
                     List<StringDt> addressStrings = fhirAddress.getLine();
                     address.setAddress1(valueOf(addressStrings.get(0)));
                     address.setAddress2(valueOf(addressStrings.get(1)));
                     address.setAddress3(valueOf(addressStrings.get(2)));
                     address.setAddress4(valueOf(addressStrings.get(3)));
                     address.setAddress5(valueOf(addressStrings.get(4)));
                     if (fhirAddress.getUse() == String.valueOf(AddressUseEnum.HOME)) {
                         address.setPreferred(true);
                     }
                     if (fhirAddress.getUse() == String.valueOf(AddressUseEnum.OLD)) {
                         address.setPreferred(false);
                     }

                     List<AddressDt> addressList = personFHIR.getAddress();
                 }

             //set gender in fhir person object
                  if (personFHIR.getGender() == String.valueOf(AdministrativeGenderEnum.MALE)) {
                      omrsPerson.setGender("M");
                  } else if (personFHIR.getGender() == String.valueOf(AdministrativeGenderEnum.FEMALE)) {
                      omrsPerson.setGender("F");
                  }

                  omrsPerson.setBirthdate(personFHIR.getBirthDate());
                  if (personFHIR.getActive()) {
                        omrsPerson.setVoided(false);
                   } else {
                        omrsPerson.setVoided(true);
                   }

                  Patient patient = Context.getPatientService().getPatientByUuid(String.valueOf(personFHIR.getId()));
                  if(patient!=null) {
                    //some code
                   }

    return omrsPerson;
}

}

