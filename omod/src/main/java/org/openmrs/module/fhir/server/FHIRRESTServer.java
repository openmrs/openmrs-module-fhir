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
package org.openmrs.module.fhir.server;

import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.RestfulServer;
import org.openmrs.module.fhir.addressstrategy.OpenMRSFHIRRequestAddressStrategy;
import org.openmrs.module.fhir.providers.RestfulAllergyIntoleranceResourceProvider;
import org.openmrs.module.fhir.providers.RestfulEncounterResourceProvider;
import org.openmrs.module.fhir.providers.RestfulFamilyHistoryResourceProvider;
import org.openmrs.module.fhir.providers.RestfulLocationResourceProvider;
import org.openmrs.module.fhir.providers.RestfulObservationResourceProvider;
import org.openmrs.module.fhir.providers.RestfulPatientResourceProvider;
import org.openmrs.module.fhir.providers.RestfulPractitionerResourceProvider;

import javax.servlet.ServletException;
import java.util.ArrayList;
import java.util.List;

public class FHIRRESTServer extends RestfulServer {

	private static final long serialVersionUID = 1L;
	private static final String MODULE_SERVELET_PREFIX = "/fhir/fhirServelet";

	/**
	 * The initialize method is automatically called when the servlet is starting up, so it can
	 * be used to configure the servlet to define resource providers, or set up
	 * configuration, interceptors, etc.
	 */
	@Override
	protected void initialize() throws ServletException {
		this.setServerAddressStrategy(new OpenMRSFHIRRequestAddressStrategy());
		List<IResourceProvider> resourceProviders = new ArrayList<IResourceProvider>();
		resourceProviders.add(new RestfulPatientResourceProvider());
		resourceProviders.add(new RestfulAllergyIntoleranceResourceProvider());
		resourceProviders.add(new RestfulEncounterResourceProvider());
		resourceProviders.add(new RestfulFamilyHistoryResourceProvider());
		resourceProviders.add(new RestfulLocationResourceProvider());
		resourceProviders.add(new RestfulObservationResourceProvider());
		resourceProviders.add(new RestfulPractitionerResourceProvider());
		setResourceProviders(resourceProviders);
	}

	protected String getRequestPath(String requestFullPath, String servletContextPath, String servletPath) {
		return requestFullPath.substring(escapedLength(servletContextPath) + escapedLength(servletPath) + escapedLength(
				MODULE_SERVELET_PREFIX));
	}
}
