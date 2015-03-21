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
package org.openmrs.module.fhir.api.util;
import ca.uhn.fhir.model.dstu2.composite.AddressDt;
import ca.uhn.fhir.model.dstu2.composite.HumanNameDt;
import ca.uhn.fhir.model.dstu2.valueset.AddressUseEnum;
import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
import ca.uhn.fhir.model.dstu2.valueset.NameUseEnum;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.StringDt;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonName;

import java.util.List;

import static java.lang.String.valueOf;

public class OpenmrsPersonUtil {
    /**
     * @param personFHIR
     * @return OpenMRS person after giving a FHIR person
     * @should generate Oms Person
     */
     public static Person generateOpenMRSPerson(ca.uhn.fhir.model.dstu2.resource.Person personFHIR) {

             Person omrsPerson = new Person();
             IdDt uuid = new IdDt();
             uuid.setId(personFHIR.getId());
             omrsPerson.setUuid(valueOf(uuid));

             for(HumanNameDt humanNameDt : personFHIR.getName()) {
                    PersonName personName = new PersonName();
                    if (humanNameDt.getUse() != null) {
                        String getUse = humanNameDt.getUse();
                        if (getUse.equals(NameUseEnum.USUAL)) {
                            personName.setPreferred(true);
                        }
                        if (getUse.equals(NameUseEnum.OLD)) {
                            personName.setPreferred(false);
                        }
                    }
                if(humanNameDt.getSuffix() != null) {
                    List<StringDt> prefixes = humanNameDt.getSuffix();
                    if(prefixes.size()>0){
                        StringDt prefix = prefixes.get(0);
                        personName.setPrefix(valueOf(prefix));
                    }
                }
                if(humanNameDt.getSuffix() != null) {
                   List<StringDt> suffixes = humanNameDt.getSuffix();
                   if(suffixes.size()>0) {
                        StringDt suffix = suffixes.get(0);
                        personName.setFamilyNameSuffix(valueOf(suffix));
                   }
                }

                List<StringDt> givenNames = humanNameDt.getGiven();
                    if(givenNames!=null){
                        StringDt givenName = givenNames.get(0);
                        personName.setGivenName(valueOf(givenName));
                    }
                List<StringDt> familyNames = humanNameDt.getFamily();
                if(familyNames!=null) {
                    StringDt familyName = familyNames.get(0);
                    personName.setFamilyName(valueOf(familyName));
                }
             }


             PersonAddress address = new PersonAddress();
             for (AddressDt fhirAddress : personFHIR.getAddress()) {
                address.setCityVillage(fhirAddress.getCity());
                address.setCountry(fhirAddress.getCountry());
                address.setStateProvince(fhirAddress.getState());
                address.setPostalCode(fhirAddress.getPostalCode());
                List<StringDt> addressStrings = fhirAddress.getLine();

                if(addressStrings!=null){
                    address.setAddress1(valueOf(addressStrings.get(0)));
                    address.setAddress2(valueOf(addressStrings.get(1)));
                    address.setAddress3(valueOf(addressStrings.get(2)));
                    address.setAddress4(valueOf(addressStrings.get(3)));
                    address.setAddress5(valueOf(addressStrings.get(4)));
                }
                if (fhirAddress.getUse().equals(String.valueOf(AddressUseEnum.HOME))) {
                    address.setPreferred(true);
                }
                if (fhirAddress.getUse().equals(String.valueOf(AddressUseEnum.OLD))) {
                    address.setPreferred(false);
                }

             }

              if(personFHIR.getGender().equals(String.valueOf(AdministrativeGenderEnum.MALE))) {
                omrsPerson.setGender(FHIRConstants.MALE);
              } else if (personFHIR.getGender().equals(String.valueOf(AdministrativeGenderEnum.FEMALE))) {
                    omrsPerson.setGender(FHIRConstants.FEMALE);
              }

              omrsPerson.setBirthdate(personFHIR.getBirthDate());
                if (personFHIR.getActive()) {
                   omrsPerson.setVoided(false);
                } else {
                    omrsPerson.setVoided(true);
                }

    return omrsPerson;

    }

}

