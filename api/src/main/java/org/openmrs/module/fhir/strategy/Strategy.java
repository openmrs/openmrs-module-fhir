package org.openmrs.module.fhir.strategy;


import java.util.List;

public interface Strategy {

    public List<String> getSupportedOpenMRSVersions();
}
