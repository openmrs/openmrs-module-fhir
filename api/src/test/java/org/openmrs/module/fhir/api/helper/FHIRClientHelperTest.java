package org.openmrs.module.fhir.api.helper;

import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;

import java.net.URI;

import static org.junit.Assert.assertEquals;

public class FHIRClientHelperTest {

	private static final String TEST_URI = "https://test/uri";

	private static final String TEST_PATIENT_UUID = "test_uuid";


	@Test
	public void retrieveRequest() throws Exception {
		RequestEntity expected = new RequestEntity(HttpMethod.GET, URI.create(TEST_URI));

		FHIRClientHelper fhirClientHelper = new FHIRClientHelper();
		assertEquals(expected, fhirClientHelper.retrieveRequest(TEST_URI));
	}

	@Test
	public void createRequest() throws Exception {
		Patient patient = new Patient();
		patient.setId(TEST_PATIENT_UUID);
		RequestEntity expected = new RequestEntity(patient, HttpMethod.PUT, URI.create(TEST_URI + "/" + TEST_PATIENT_UUID));

		FHIRClientHelper fhirClientHelper = new FHIRClientHelper();
		assertEquals(expected, fhirClientHelper.createRequest(TEST_URI, patient));
	}

	@Test
	public void deleteRequest() throws Exception {
		Patient patient = new Patient();
		patient.setId(TEST_PATIENT_UUID);
		RequestEntity expected = new RequestEntity(TEST_PATIENT_UUID, HttpMethod.DELETE,
				URI.create(TEST_URI + "/" + TEST_PATIENT_UUID));

		FHIRClientHelper fhirClientHelper = new FHIRClientHelper();
		assertEquals(expected, fhirClientHelper.deleteRequest(TEST_URI, TEST_PATIENT_UUID));
	}

	@Test
	public void updateRequest() throws Exception {
		Patient patient = new Patient();
		patient.setId(TEST_PATIENT_UUID);
		RequestEntity expected = new RequestEntity(patient, HttpMethod.PUT, URI.create(TEST_URI + "/" + TEST_PATIENT_UUID));

		FHIRClientHelper fhirClientHelper = new FHIRClientHelper();
		assertEquals(expected, fhirClientHelper.updateRequest(TEST_URI, patient));
	}

	@Test
	public void resolveCategoryByCategory() {
		FHIRClientHelper fhirClientHelper = new FHIRClientHelper();
		assertEquals(Patient.class, fhirClientHelper.resolveCategoryByCategory("patient"));
		assertEquals(Encounter.class, fhirClientHelper.resolveCategoryByCategory("visit"));
		assertEquals(Encounter.class, fhirClientHelper.resolveCategoryByCategory("encounter"));
		assertEquals(Observation.class, fhirClientHelper.resolveCategoryByCategory("obs"));
		assertEquals(Location.class, fhirClientHelper.resolveCategoryByCategory("location"));
		assertEquals(Practitioner.class, fhirClientHelper.resolveCategoryByCategory("practitioner"));
		assertEquals(Practitioner.class, fhirClientHelper.resolveCategoryByCategory("provider"));
	}
}
