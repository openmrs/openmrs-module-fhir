package org.openmrs.module.fhir.api.client.dto;


public class IdentifierDTO {
    String use;
    String system;
    String value;

    public IdentifierDTO() {
    }

    public String getUse() {
        return use;
    }

    public void setUse(String use) {
        this.use = use;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
