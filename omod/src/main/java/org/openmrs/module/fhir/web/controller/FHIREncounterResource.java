package org.openmrs.module.fhir.web.controller;

import ca.uhn.fhir.model.dstu.resource.Composition;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.EncounterService;
import org.openmrs.module.fhir.util.Parser;


public class FHIREncounterResource extends Resource {

    public Object retrieve(String uuid) throws Exception {

        Object delegate = getByUniqueId(uuid, null);
        System.out.println(delegate);
        if (delegate == null)
            throw new Exception();

        return delegate;
    }

    public String getByUniqueId(String uuid, String contentType) {

        EncounterService encounterService = Context.getService(EncounterService.class);
        Composition fhirEncounter = encounterService.getEncounter(uuid);

        return Parser.parse(fhirEncounter, contentType);
    }

}
