package org.openmrs.module.fhir.api.client.dto;

public class TelcomDTO {
    String system;
    String value;

    public TelcomDTO() {}

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
