package org.openmrs.module.fhir.web.controller;

import ca.uhn.fhir.model.dstu.resource.Practitioner;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.PractitionerService;
import org.openmrs.module.fhir.util.Parser;




public class FHIRPractitionerResource extends Resource {

    public Object retrieve(String uuid) throws Exception {

        Object delegate = getByUniqueId(uuid, null);
        System.out.println(delegate);
        if (delegate == null)
            throw new Exception();

        return delegate;
    }

    public String getByUniqueId(String uuid, String contentType) {

        PractitionerService practitionerService = Context.getService(PractitionerService.class);
        Practitioner fhirPractitioner = practitionerService.getPractitioner(uuid);

        return Parser.parse(fhirPractitioner, contentType);
    }

}
