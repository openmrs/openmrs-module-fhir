package org.openmrs.module.fhir.api.strategies.practitioner;

import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.hl7.fhir.dstu3.model.StringType;
import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.Provider;
import org.openmrs.api.ProviderService;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.ErrorUtil;
import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.openmrs.module.fhir.api.util.FHIRPractitionerUtil;
import org.openmrs.module.fhir.api.util.StrategyUtil;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.valueOf;
import static org.openmrs.module.fhir.api.util.FHIRUtils.extractUuid;

@Component("DefaultPractitionerStrategy")
public class PractitionerStrategy implements GenericPractitionerStrategy {

    @Override
    public Practitioner getPractitioner(String uuid) {
        Provider omrsProvider = Context.getProviderService().getProviderByUuid(uuid);
        if (omrsProvider == null || omrsProvider.isRetired()) {
            return null;
        }
        return FHIRPractitionerUtil.generatePractitioner(omrsProvider);
    }

    @Override
    public List<Practitioner> searchPractitionersByUuid(String uuid) {
        Provider omrsProvider = Context.getProviderService().getProviderByUuid(uuid);
        List<Practitioner> practitioners = new ArrayList<>();
        if (omrsProvider != null && !omrsProvider.isRetired()) {
            practitioners.add(FHIRPractitionerUtil.generatePractitioner(omrsProvider));
        }
        return practitioners;
    }

    @Override
    public List<Practitioner> searchPractitionersByName(String name) {
        List<Provider> omrsProviders = searchProvidersByQuery(name);
        List<Practitioner> practitioners = new ArrayList<>();
        for (Provider provider : omrsProviders) {
            practitioners.add(FHIRPractitionerUtil.generatePractitioner(provider));
        }
        return practitioners;
    }

    @Override
    public List<Practitioner> searchPractitionersByGivenName(String givenName) {
        List<Provider> omrsProviders = searchProvidersByQuery(givenName);
        List<Practitioner> practitioners = new ArrayList<>();
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

    @Override
    public List<Practitioner> searchPractitionersByFamilyName(String familyName) {
        List<Provider> omrsProviders = searchProvidersByQuery(familyName);
        List<Practitioner> practitioners = new ArrayList<>();
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

    @Override
    public List<Practitioner> searchPractitionersByIdentifier(String identifier) {
        Provider omrsProvider = Context.getProviderService().getProviderByIdentifier(identifier);
        List<Practitioner> practitioners = new ArrayList<>();
        if (omrsProvider != null) {
            practitioners.add(FHIRPractitionerUtil.generatePractitioner(omrsProvider));
        }
        return practitioners;
    }

    @Override
    public Practitioner createFHIRPractitioner(Practitioner practitioner) {
        Provider provider = new Provider();
        List<String> errors = new ArrayList<>();
        String practionerName = "";
        Person personFromRequest = FHIRPractitionerUtil.extractOpenMRSPerson(practitioner); // extracts openmrs person from the practitioner representation
        List<Identifier> identifiers = practitioner.getIdentifier();
        if (identifiers != null && !identifiers.isEmpty()) {
            Identifier idnt = identifiers.get(0);
            provider.setIdentifier(idnt.getValue());
        }// identifiers can be empty

        if (practitioner.getId() != null) {
            provider.setUuid(extractUuid(practitioner.getId()));
        }

        if (personFromRequest == null) { // if this is true, that means the request doesn't have enough attributes to create a person from it, or attach a person from existing ones
            List<HumanName> humanNames = practitioner.getName();
            if (humanNames != null) { // check whether atleast one name is exist. if so we can create a practitioner without attaching a person, just with a name.
                for(HumanName humanName : humanNames) {
                    practionerName = humanName.getFamily();

                    List<StringType> givenNames = humanName.getGiven();
                    for(StringType givenName : givenNames) {
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
            String errorMessage = ErrorUtil.generateErrorMessage(errors, FHIRConstants.REQUEST_ISSUE_LIST);
            throw new UnprocessableEntityException(errorMessage);
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

    @Override
    public Practitioner updatePractitioner(Practitioner practitioner, String theId) {
        ProviderService service = Context.getProviderService();
        org.openmrs.Provider retrievedProvider = service.getProviderByUuid(theId);
        if (retrievedProvider != null) { // update existing practitioner
            retrievedProvider = FHIRPractitionerUtil.updatePractitionerAttributes(practitioner, retrievedProvider);
            Provider p = service.saveProvider(retrievedProvider);
            return FHIRPractitionerUtil.generatePractitioner(p);
        } else { // no practitioner is associated with the given uuid. so create a new practitioner with the given uuid
            StrategyUtil.setIdIfNeeded(practitioner, theId);
            return createFHIRPractitioner(practitioner);
        }
    }

    private List<Provider> searchProvidersByQuery(String query) {
        return Context.getProviderService().getProviders(query, null, null, null, false);
    }
}
