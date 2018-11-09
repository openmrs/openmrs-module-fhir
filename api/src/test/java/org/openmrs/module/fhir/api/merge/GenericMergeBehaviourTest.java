package org.openmrs.module.fhir.api.merge;

import org.junit.Test;
import org.openmrs.Patient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class GenericMergeBehaviourTest {

	@Test
	public void shouldUpdateParent() {
		Patient patient1 = new Patient();
		Patient patient2 = new Patient();
		MergeBehaviour<Object> simpleMerge = new MockedMergeBehaviour();

		MergeResult<Object> result = simpleMerge.resolveDiff(patient1, patient2, Patient.class);
		MergeSuccess<Object> success = null;
		if (result instanceof MergeSuccess) {
			success = (MergeSuccess<Object>) result;
		}

		assertNotNull(success);
		assertEquals(result.getMessage(), MergeMessageEnum.FOREIGN_SAVE_MESSAGE);
		assertTrue(success.shouldUpdateForeign());
		assertNotNull(success.getMerged());
	}
}
