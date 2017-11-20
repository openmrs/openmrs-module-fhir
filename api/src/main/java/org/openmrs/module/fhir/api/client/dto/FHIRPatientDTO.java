package org.openmrs.module.fhir.api.client.dto;

import java.util.List;
import java.sql.Date;

public class FHIRPatientDTO {
    String resourceType;
    String id;
    IdentifierDTO[] identifier;
    boolean active;
    NameDTO[] name;
    TelcomDTO[] telcom;
    String gender;
    Date birthdate;
    boolean deceasedBoolean;
    AddressDTO[] address;

    public FHIRPatientDTO() {}

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public IdentifierDTO[] getIdentifier() {
        return identifier;
    }

    public void setIdentifier(IdentifierDTO[] identifier) {
        this.identifier = identifier;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public NameDTO[] getName() {
        return name;
    }

    public void setName(NameDTO[] name) {
        this.name = name;
    }

    public TelcomDTO[] getTelcom() {
        return telcom;
    }

    public void setTelcom(TelcomDTO[] telcom) {
        this.telcom = telcom;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public boolean isDeceasedBoolean() {
        return deceasedBoolean;
    }

    public void setDeceasedBoolean(boolean deceasedBoolean) {
        this.deceasedBoolean = deceasedBoolean;
    }

    public AddressDTO[] getAddress() {
        return address;
    }

    public void setAddress(AddressDTO[] address) {
        this.address = address;
    }
}
