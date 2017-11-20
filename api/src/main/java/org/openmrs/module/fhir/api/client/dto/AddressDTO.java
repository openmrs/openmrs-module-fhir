package org.openmrs.module.fhir.api.client.dto;

import java.util.List;

public class AddressDTO {
    String use;
    String[] line;

    public AddressDTO() {}

    public String getUse() {
        return use;
    }

    public void setUse(String use) {
        this.use = use;
    }

    public String[] getLine() {
        return line;
    }

    public void setLine(String[] line) {
        this.line = line;
    }
}
