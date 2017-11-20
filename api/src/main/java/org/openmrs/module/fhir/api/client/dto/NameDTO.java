package org.openmrs.module.fhir.api.client.dto;

public class NameDTO {
    String use;
    String family;
    String[] given;

    public NameDTO() {}

    public String getUse() {
        return use;
    }

    public void setUse(String use) {
        this.use = use;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String[] getGiven() {
        return given;
    }

    public void setGiven(String[] given) {
        this.given = given;
    }

}
