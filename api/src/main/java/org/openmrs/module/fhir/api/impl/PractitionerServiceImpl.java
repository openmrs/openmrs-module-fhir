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
package org.openmrs.module.fhir.api.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.fhir.api.PractitionerService;
import org.openmrs.module.fhir.api.db.FHIRDAO;
import org.openmrs.module.fhir.api.util.FHIRPractitionerUtil;

import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.resource.Practitioner;
import ca.uhn.fhir.model.primitive.IdDt;

/**
 * It is a default implementation of {@link org.openmrs.module.fhir.api.PatientService}.
 */
public class PractitionerServiceImpl extends BaseOpenmrsService implements PractitionerService {

	protected final Log log = LogFactory.getLog(this.getClass());

	private FHIRDAO dao;

	/**
	 * @return the dao
	 */
	public FHIRDAO getDao() {
		return dao;
	}

	/**
	 * @param dao the dao to set
	 */
	public void setDao(FHIRDAO dao) {
		this.dao = dao;
	}

	/**
	 * @see org.openmrs.module.fhir.api.PractitionerService#getPractitioner(String)
	 */
	public Practitioner getPractitioner(String id) {
		Provider omrsProvider = Context.getProviderService().getProviderByUuid(id);
		if (omrsProvider == null || omrsProvider.isRetired()) {
			return null;
		}
		return FHIRPractitionerUtil.generatePractitioner(omrsProvider);
	}

	/**
	 * @see org.openmrs.module.fhir.api.PractitionerService#searchPractitionersById(String)
	 */
	public List<Practitioner> searchPractitionersById(String id) {
		Provider omrsProvider = Context.getProviderService().getProviderByUuid(id);
		List<Practitioner> practitioners = new ArrayList<Practitioner>();
		if (omrsProvider != null && !omrsProvider.isRetired()) {
			practitioners.add(FHIRPractitionerUtil.generatePractitioner(omrsProvider));
		}
		return practitioners;
	}

	/**
	 * @see org.openmrs.module.fhir.api.PractitionerService#searchPractitionersByName(String)
	 */
	public List<Practitioner> searchPractitionersByName(String name) {
		List<Provider> omrsProviders = searchProvidersByQuery(name);
		List<Practitioner> practitioners = new ArrayList<Practitioner>();
		for (Provider provider : omrsProviders) {
			practitioners.add(FHIRPractitionerUtil.generatePractitioner(provider));
		}
		return practitioners;
	}

	/**
	 * @see org.openmrs.module.fhir.api.PractitionerService#searchPractitionersByGivenName(String)
	 */
	public List<Practitioner> searchPractitionersByGivenName(String givenName) {
		List<Provider> omrsProviders = searchProvidersByQuery(givenName);
		List<Practitioner> practitioners = new ArrayList<Practitioner>();
		for (Provider provider : omrsProviders) {
			if (provider.getPerson() != null) {
				//Search through the provider given name for check whether given name exist in the returned provider
				// resource

				if (givenName.equalsIgnoreCase(provider.getPerson().getGivenName())) {
					practitioners.add(FHIRPractitionerUtil.generatePractitioner(provider));
				} else {
					for (PersonName personName : provider.getPerson().getNames()) {
						if (givenName.equalsIgnoreCase(personName.getGivenName())) {
							practitioners.add(FHIRPractitionerUtil.generatePractitioner(provider));
						}
					}
				}
			}
		}
		return practitioners;
	}

	/**
	 * @see org.openmrs.module.fhir.api.PractitionerService#searchPractitionersByFamilyName(String)
	 */
	public List<Practitioner> searchPractitionersByFamilyName(String familyName) {
		List<Provider> omrsProviders = searchProvidersByQuery(familyName);
		List<Practitioner> practitioners = new ArrayList<Practitioner>();
		for (Provider provider : omrsProviders) {
			//Search through the provider family name for check whether family name exist in the returned provider resource
			if (provider.getPerson() != null) {
				if (familyName.equalsIgnoreCase(provider.getPerson().getFamilyName())) {
					practitioners.add(FHIRPractitionerUtil.generatePractitioner(provider));
				} else {
					for (PersonName personName : provider.getPerson().getNames()) {
						if (familyName.equalsIgnoreCase(personName.getFamilyName())) {
							practitioners.add(FHIRPractitionerUtil.generatePractitioner(provider));
						}
					}
				}
			}
		}
		return practitioners;
	}

	/**
	 * @see org.openmrs.module.fhir.api.PractitionerService#searchPractitionersByIdentifier(String)
	 */
	public List<Practitioner> searchPractitionersByIdentifier(String identifier) {
		Provider omrsProvider = Context.getProviderService().getProviderByIdentifier(identifier);
		List<Practitioner> practitioners = new ArrayList<Practitioner>();
		if (omrsProvider != null) {
			practitioners.add(FHIRPractitionerUtil.generatePractitioner(omrsProvider));
		}
		return practitioners;
	}

	private List<Provider> searchProvidersByQuery(String query) {
		return Context.getProviderService().getProviders(query, null, null, null, false);
	}
	
	/**
	 * @see org.openmrs.module.fhir.api.PractitionerService#createFHIRPractitioner(String)
	 */
	public Practitioner createFHIRPractitioner(Practitioner practitioner) {
		Provider provider = new Provider();
		Person personFromRequest = FHIRPractitionerUtil.extractOpenMRSPerson(practitioner); // extracts openmrs person from the practitioner representation
		Person personToProvider = FHIRPractitionerUtil.generateOpenMRSPerson(personFromRequest); // either map to an existing person, or create a new person for the given representation
		
		List<IdentifierDt> identifiers = practitioner.getIdentifier();
		if (!identifiers.isEmpty()) {
			IdentifierDt idnt = identifiers.get(0);
			provider.setIdentifier(idnt.getValue());
		}
		provider.setPerson(personToProvider);
		
		Provider omrsProvider = Context.getProviderService().saveProvider(provider);
		return FHIRPractitionerUtil.generatePractitioner(omrsProvider);
	}
	
	/**
	 * @see org.openmrs.module.fhir.api.PractitionerService#updatePractitioner(Practitioner
	 *      practitioner, IdDt theId)
	 */
	public Practitioner updatePractitioner(Practitioner practitioner, IdDt theId) {
		org.openmrs.api.ProviderService providerService = Context.getProviderService();
		org.openmrs.Provider retrievedProvider = providerService.getProviderByUuid(theId.getIdPart());
		if (retrievedProvider != null) { // update existing practitioner
			retrievedProvider = FHIRPractitionerUtil.updatePractitionerAttributes(practitioner, retrievedProvider);
			Provider p=Context.getProviderService().saveProvider(retrievedProvider);
			return FHIRPractitionerUtil.generatePractitioner(p);
		} else { // no practitioner is associated with the given uuid. so create a new practitioner with the given uuid
			if (practitioner.getId() == null) { // since we need to PUT the Person to a specific URI, we need to set the uuid
				// here, if it is not
				// already set.
				IdDt uuid = new IdDt();
				uuid.setValue(theId.getIdPart());
				practitioner.setId(uuid);
			}
			return createFHIRPractitioner(practitioner);
		}
	}
}
