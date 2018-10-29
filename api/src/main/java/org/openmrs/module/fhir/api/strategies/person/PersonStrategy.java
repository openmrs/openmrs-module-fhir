package org.openmrs.module.fhir.api.strategies.person;

import ca.uhn.fhir.rest.server.exceptions.MethodNotAllowedException;
import ca.uhn.fhir.rest.server.exceptions.NotModifiedException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.dstu3.model.Person;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.openmrs.module.fhir.api.util.FHIRPersonUtil;
import org.openmrs.module.fhir.api.util.FHIRUtils;
import org.openmrs.module.fhir.api.util.StrategyUtil;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.openmrs.module.fhir.api.util.FHIRUtils.extractUuid;

@Component("DefaultPersonStrategy")
public class PersonStrategy implements GenericPersonStrategy {

	@Override
	public Person getPerson(String uuid) {
		org.openmrs.Person omrsPerson = Context.getPersonService().getPersonByUuid(uuid);

		if (omrsPerson == null || omrsPerson.getPersonVoided()) {
			return null;
		}
		return FHIRPersonUtil.generatePerson(omrsPerson);
	}

	@Override
	public List<Person> searchPersonByUuid(String uuid) {
		org.openmrs.Person omrsPerson = Context.getPersonService().getPersonByUuid(uuid);
		List<Person> personList = new ArrayList<>();

		if (omrsPerson != null && !omrsPerson.getPersonVoided()) {
			personList.add(FHIRPersonUtil.generatePerson(omrsPerson));
		}
		return personList;
	}

	@Override
	public List<Person> searchPersons(String name, Integer birthYear, String gender) {
		Set<org.openmrs.Person> persons = Context.getPersonService().getSimilarPeople(name, birthYear, gender);
		List<Person> fhirPersonsList = new ArrayList<>();

		for (org.openmrs.Person person : persons) {
			fhirPersonsList.add(FHIRPersonUtil.generatePerson(person));
		}
		return fhirPersonsList;
	}

	@Override
	public List<Person> searchPersonsByName(String name) {
		List<org.openmrs.Person> persons = Context.getPersonService().getPeople(name, null);
		List<Person> fhirPersonsList = new ArrayList<>();

		for (org.openmrs.Person person : persons) {
			fhirPersonsList.add(FHIRPersonUtil.generatePerson(person));
		}
		return fhirPersonsList;
	}

	@Override
	public Person createFHIRPerson(Person person) {
		List<String> errors = new ArrayList<>();
		org.openmrs.Person omrsPerson = FHIRPersonUtil.generateOpenMRSPerson(person, errors);

		FHIRUtils.checkGeneratorErrorList(errors);

		org.openmrs.api.PersonService personService = Context.getPersonService();
		omrsPerson = personService.savePerson(omrsPerson);
		return FHIRPersonUtil.generatePerson(omrsPerson);
	}

	@Override
	public Person updateFHIRPerson(Person person, String uuid) {
		List<String> errors = new ArrayList<>();
		org.openmrs.api.PersonService personService = Context.getPersonService();
		org.openmrs.Person retrievedPerson = personService.getPersonByUuid(uuid);

		if (retrievedPerson != null) {
			org.openmrs.Person omrsPerson = FHIRPersonUtil.generateOpenMRSPerson(person, errors);
			retrievedPerson = FHIRPersonUtil.updatePersonAttributes(omrsPerson, retrievedPerson);
			Context.getPersonService().savePerson(retrievedPerson);
			return FHIRPersonUtil.generatePerson(retrievedPerson);
		} else {
			StrategyUtil.setIdIfNeeded(person, uuid);
			return createFHIRPerson(person);
		}
	}

	@Override
	public void retirePerson(String uuid) throws ResourceNotFoundException, NotModifiedException {
		org.openmrs.Person person = Context.getPersonService().getPersonByUuid(uuid);
		if (person == null) {
			throw new ResourceNotFoundException(String.format("Person with id '%s' not found", uuid));
		}
		if (person.getPersonVoided()) {
			return;
		}
		try {
			Context.getPersonService().voidPerson(person, FHIRConstants.FHIR_VOIDED_MESSAGE);
		}
		catch (APIException apie) {
			throw new MethodNotAllowedException(String.format("OpenMRS has failed to retire person '%s': %s", uuid,
					apie.getMessage()));
		}
	}
}
