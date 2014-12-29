package org.openmrs.module.fhir.resources;

import ca.uhn.fhir.model.dstu.resource.Practitioner;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.PractitionerService;
import org.openmrs.module.fhir.util.Parser;
import org.openmrs.module.fhir.api.util.FHIRPractitionerUtil;

public class FHIRPractitionerResource extends Resource {

	public Object retrieve(String uuid) throws Exception {

		Object delegate = getByUniqueId(uuid, null);
		System.out.println(delegate);
		if (delegate == null) {
			throw new Exception();
		}

		return delegate;
	}

	public String getByUniqueId(String uuid, String contentType) {

		PractitionerService practitionerService = Context.getService(PractitionerService.class);
		Practitioner fhirPractitioner = practitionerService.getPractitioner(uuid);

		return Parser.parse(fhirPractitioner, contentType);
	}

    public String searchById(String id, String contentType) {

        PractitionerService practitionerService = Context.getService(PractitionerService.class);
        ca.uhn.fhir.model.api.Bundle practitionerBundle = practitionerService.getPractitionersById(id);
        return FHIRPractitionerUtil.parseBundle(practitionerBundle);
    }

}
