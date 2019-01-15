package org.openmrs.module.fhir.api.helper;

import ca.uhn.fhir.context.FhirContext;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Person;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.junit.Test;
import org.openmrs.module.fhir.api.client.ClientHttpEntity;
import org.springframework.http.HttpMethod;

import java.net.URI;

import static org.junit.Assert.assertEquals;

public class FHIRClientHelperTest {

	private static final String TEST_URI = "https://test/uri";

	private static final String TEST_PATIENT_UUID = "test_uuid";


	@Test
	public void retrieveRequest() throws Exception {
		ClientHttpEntity expected = new ClientHttpEntity(HttpMethod.GET, URI.create(TEST_URI));

		FHIRClientHelper fhirClientHelper = new FHIRClientHelper();
		assertEquals(expected, fhirClientHelper.retrieveRequest(TEST_URI));
	}

	@Test
	public void createRequest() throws Exception {
		Patient patient = new Patient();
		patient.setId(TEST_PATIENT_UUID);
		String expectedBody = FhirContext.forDstu3().newJsonParser().encodeResourceToString(patient);
		ClientHttpEntity expected = new ClientHttpEntity<String>(expectedBody, HttpMethod.PUT,
				URI.create(TEST_URI + "/" + TEST_PATIENT_UUID));

		FHIRClientHelper fhirClientHelper = new FHIRClientHelper();
		assertEquals(expected, fhirClientHelper.createRequest(TEST_URI, patient));
	}

	@Test
	public void deleteRequest() throws Exception {
		Patient patient = new Patient();
		patient.setId(TEST_PATIENT_UUID);
		ClientHttpEntity expected = new ClientHttpEntity<String>(TEST_PATIENT_UUID, HttpMethod.DELETE,
				URI.create(TEST_URI + "/" + TEST_PATIENT_UUID));

		FHIRClientHelper fhirClientHelper = new FHIRClientHelper();
		assertEquals(expected, fhirClientHelper.deleteRequest(TEST_URI, TEST_PATIENT_UUID));
	}

	@Test
	public void updateRequest() throws Exception {
		Patient patient = new Patient();
		patient.setId(TEST_PATIENT_UUID);
		String expectedBody = FhirContext.forDstu3().newJsonParser().encodeResourceToString(patient);
		ClientHttpEntity expected = new ClientHttpEntity<String>(expectedBody, HttpMethod.PUT,
				URI.create(TEST_URI + "/" + TEST_PATIENT_UUID));

		FHIRClientHelper fhirClientHelper = new FHIRClientHelper();
		assertEquals(expected, fhirClientHelper.updateRequest(TEST_URI, patient));
	}

	@Test
	public void resolveClassByCategory() {
		FHIRClientHelper fhirClientHelper = new FHIRClientHelper();
		assertEquals(Patient.class, fhirClientHelper.resolveClassByCategory("patient"));
		assertEquals(Encounter.class, fhirClientHelper.resolveClassByCategory("visit"));
		assertEquals(Encounter.class, fhirClientHelper.resolveClassByCategory("encounter"));
		assertEquals(Observation.class, fhirClientHelper.resolveClassByCategory("observation"));
		assertEquals(Location.class, fhirClientHelper.resolveClassByCategory("location"));
		assertEquals(Practitioner.class, fhirClientHelper.resolveClassByCategory("practitioner"));
		assertEquals(Practitioner.class, fhirClientHelper.resolveClassByCategory("provider"));
		assertEquals(Person.class, fhirClientHelper.resolveClassByCategory("person"));
	}
}
