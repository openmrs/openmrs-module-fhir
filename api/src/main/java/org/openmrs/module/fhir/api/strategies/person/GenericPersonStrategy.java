package org.openmrs.module.fhir.api.strategies.person;

import org.hl7.fhir.dstu3.model.Person;

import java.util.List;

public interface GenericPersonStrategy {

	Person getPerson(String uuid);

	List<Person> searchPersonByUuid(String uuid);

	List<Person> searchPersons(String name, Integer birthYear, String gender);

	List<Person> searchPersonsByName(String name);

	Person createFHIRPerson(Person person);

	Person updateFHIRPerson(Person person, String uuid);

	void retirePerson(String uuid);

}
