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
package org.openmrs.module.fhir.providers;

import ca.uhn.fhir.rest.annotation.Transaction;
import ca.uhn.fhir.rest.annotation.TransactionParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Person;
import org.hl7.fhir.dstu3.model.Resource;
import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.openmrs.module.fhir.resources.FHIRBundleResource;
import org.openmrs.module.fhir.resources.FHIREncounterResource;
import org.openmrs.module.fhir.resources.FHIRLocationResource;
import org.openmrs.module.fhir.resources.FHIRObservationResource;
import org.openmrs.module.fhir.resources.FHIRPatientResource;
import org.openmrs.module.fhir.resources.FHIRPersonResource;

import java.util.ArrayList;
import java.util.List;

public class RestfulBundleResourceProvider implements IResourceProvider {
	
	private FHIRBundleResource bundleResource;
	
	public RestfulBundleResourceProvider() {
		this.bundleResource = new FHIRBundleResource();
	}
	
	@Override
	public Class<? extends Resource> getResourceType() {
		return Bundle.class;
	}
	
	@Transaction
	public Bundle transaction(@TransactionParam Bundle theResources) {
		// theResources will contain a complete bundle of all resources to persist
		// in a single transaction
		List<Resource> postResources = new ArrayList<Resource>();
		List<Resource> putResources = new ArrayList<Resource>();
		List<String> deleteResources = new ArrayList<String>();
		for (Bundle.BundleEntryComponent entry : theResources.getEntry()) {
			if(FHIRConstants.POST.equals(entry.getRequest().getMethod())){
				postResources.add(entry.getResource());
			}else if(FHIRConstants.PUT.equals(entry.getRequest().getMethod())){
				putResources.add(entry.getResource());
			}else if(FHIRConstants.DELETE.equals(entry.getRequest().getMethod())){
				deleteResources.add(entry.getRequest().getUrl());
			}
        }
		
		for (Resource next : postResources) {
			if (next instanceof Encounter) {
				FHIREncounterResource encounterResource = new FHIREncounterResource();
				encounterResource.createFHIREncounter((Encounter) next);
			} else if (next instanceof Observation) {
				FHIRObservationResource observationResource = new FHIRObservationResource();
				observationResource.createFHIRObservation((Observation) next);
			} else if (next instanceof Patient) {
				FHIRPatientResource patientResource = new FHIRPatientResource();
				patientResource.createFHIRPatient((Patient) next);
			} else if (next instanceof Location) {
				FHIRLocationResource locationResource = new FHIRLocationResource();
				locationResource.createLocation((Location) next);
			} else if (next instanceof Person) {
				FHIRPersonResource personResource = new FHIRPersonResource();
				personResource.createFHIRPerson((Person) next);
			}
		}
		
		// According to the specification, a bundle must be returned. This bundle will contain
		// all of the created/updated/deleted resources, including their new/updated identities.
		//
		// The returned list must be the exact same size as the list of resources
		// passed in, and it is acceptable to return the same list instance that was
		// passed in.
		/*
		 * Populate each returned resource with the new ID for that resource,
		 * including the new version if the server supports versioning.
		 */
		/*Bundle retVal = new Bundle();//(theResources);
		for (IResource next : theResources) {
			
			IdType newId = new IdType("Patient", "1", "2");
			next.setId(newId);
		}*/
		
		// If wanted, you may optionally also return an OperationOutcome resource
		// If present, the OperationOutcome must come first in the returned list.
		return theResources;
	}


}
