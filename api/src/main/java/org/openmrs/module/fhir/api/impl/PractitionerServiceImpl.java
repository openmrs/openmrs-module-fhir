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

import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.hl7.fhir.dstu3.model.StringType;
import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.fhir.api.PractitionerService;
import org.openmrs.module.fhir.api.db.FHIRDAO;
import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.openmrs.module.fhir.api.util.FHIRPractitionerUtil;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.valueOf;

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
		List<String> errors = new ArrayList<String>();
		String practionerName = "";
		Person personFromRequest = FHIRPractitionerUtil.extractOpenMRSPerson(practitioner); // extracts openmrs person from the practitioner representation
		List<Identifier> identifiers = practitioner.getIdentifier();
		if (identifiers != null && !identifiers.isEmpty()) {
			Identifier idnt = identifiers.get(0);
			provider.setIdentifier(idnt.getValue());
		}// identifiers can be empty
		if (personFromRequest == null) { // if this is true, that means the request doesn't have enough attributes to create a person from it, or attach a person from existing ones
			List<HumanName> humanNames = practitioner.getName();
			if (humanNames != null) { // check whether atleast one name is exist. if so we can create a practitioner without attaching a person, just with a name.
				for(HumanName humanName : humanNames) {
					practionerName = humanName.getFamily();

					List<StringType> givenNames = humanName.getGiven();
					for(StringType givenName : humanName.getGiven()) {
							practionerName = practionerName + " " + valueOf(givenName.getValue()); // will create a name like "John David"
					}
					if ("".equals(practionerName)) { // there is no given name or family name. cannot proceed with the request
						errors.add("Practioner should contain atleast given name or family name");
					}
					//Take only the first name as no person can attached
					break;
				}
			} else {
				errors.add("Practitioner should contain atleast given name or family name");
			}
		}
		if (!errors.isEmpty()) {
			StringBuilder errorMessage = new StringBuilder(FHIRConstants.REQUEST_ISSUE_LIST);
			for (int i = 0; i < errors.size(); i++) {
				errorMessage.append((i + 1) + " : " + errors.get(i) + "\n");
			}
			throw new UnprocessableEntityException(errorMessage.toString());
		}
		if (personFromRequest != null) { // if this is not null, we can have a person resource along the practitioner resource
			Person personToProvider = FHIRPractitionerUtil.generateOpenMRSPerson(personFromRequest); // either map to an existing person, or create a new person for the given representation
			provider.setPerson(personToProvider);
		} else {
			provider.setName(practionerName); // else create the practitioner just with the name
		}
		Provider omrsProvider = Context.getProviderService().saveProvider(provider);
		if (personFromRequest == null) {
			omrsProvider.setPerson(null);
		}
		return FHIRPractitionerUtil.generatePractitioner(omrsProvider);
	}
	
	/**
	 * @see org.openmrs.module.fhir.api.PractitionerService#updatePractitioner(Practitioner
	 *      practitioner, IdType theId)
	 */
	public Practitioner updatePractitioner(Practitioner practitioner, String theId) {
		org.openmrs.api.ProviderService providerService = Context.getProviderService();
		org.openmrs.Provider retrievedProvider = providerService.getProviderByUuid(theId);
		if (retrievedProvider != null) { // update existing practitioner
			retrievedProvider = FHIRPractitionerUtil.updatePractitionerAttributes(practitioner, retrievedProvider);
			Provider p=Context.getProviderService().saveProvider(retrievedProvider);
			return FHIRPractitionerUtil.generatePractitioner(p);
		} else { // no practitioner is associated with the given uuid. so create a new practitioner with the given uuid
			if (practitioner.getId() == null) { // since we need to PUT the Person to a specific URI, we need to set the uuid
				// here, if it is not
				// already set.
				IdType uuid = new IdType();
				uuid.setValue(theId);
				practitioner.setId(uuid);
			}
			return createFHIRPractitioner(practitioner);
		}
	}
}
