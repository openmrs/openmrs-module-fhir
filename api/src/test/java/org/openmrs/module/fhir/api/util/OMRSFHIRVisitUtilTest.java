package org.openmrs.module.fhir.api.util;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonName;
import org.openmrs.Visit;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class OMRSFHIRVisitUtilTest {
	
	@Test
	public void testGenerateEncounter4VisitWithNoEncounters() throws Exception {
		Visit v = generateVisit();
		Encounter fv = OMRSFHIRVisitUtil.generateEncounter(v);
		assertEquals(0, fv.getUndeclaredExtensionsByUrl(FHIRConstants.ENCOUNTER_EXTENSION_URI).size());
	}


	@Test
	public void testGenerateEncounterIncludesEncountersinResult() throws Exception {
		Visit v = generateVisit();
		Set<org.openmrs.Encounter> encounters = new HashSet<org.openmrs.Encounter>();
		org.openmrs.Encounter e1 = new org.openmrs.Encounter();
		encounters.add(e1);
		org.openmrs.Encounter e2 = new org.openmrs.Encounter();
		encounters.add(e2);
		v.setEncounters(encounters);

		Encounter fv = OMRSFHIRVisitUtil.generateEncounter(v);
		assertEquals(2, fv.getUndeclaredExtensionsByUrl(FHIRConstants.ENCOUNTER_EXTENSION_URI).size());
	}

	@Test
	public void testGenerateEncounterPassesWithEmptyEncountersCollection() throws Exception {
		Visit v = generateVisit();
		Set<org.openmrs.Encounter> encounters = new HashSet<org.openmrs.Encounter>();
		v.setEncounters(encounters);

		Encounter fv = OMRSFHIRVisitUtil.generateEncounter(v);
		assertEquals(0, fv.getUndeclaredExtensionsByUrl(FHIRConstants.ENCOUNTER_EXTENSION_URI).size());
	}

	static Visit generateVisit(){
		Visit v = new Visit();

		Patient p = new Patient();
		PersonName name = new PersonName("test", "test", "test");
		p.addName(name);
		Set<PatientIdentifier> pi = new HashSet<PatientIdentifier>();
		pi.add(new PatientIdentifier("0", new PatientIdentifierType(), new Location()));
		p.setIdentifiers(pi);

		v.setPatient(p);
		return v;
	}
}
