package org.openmrs.module.fhir.api.merge;

import org.junit.Test;
import org.openmrs.BaseOpenmrsData;
import org.openmrs.Patient;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class GenericMergeBehaviourTest {

	@Test
	public void shouldUpdateParent() {
		Patient local = new Patient();
		Patient foreign = new Patient();
		MergeBehaviour<Object> simpleMerge = new MockedMergeBehaviour();

		MergeResult<Object> result = simpleMerge.resolveDiff(Patient.class, local, foreign);
		MergeSuccess<Object> success = null;
		if (result instanceof MergeSuccess) {
			success = (MergeSuccess<Object>) result;
		}

		assertNotNull(success);
		assertEquals(result.getMessage(), MergeMessageEnum.FOREIGN_SAVE_MESSAGE);
		assertTrue(success.shouldUpdateForeign());
		assertNotNull(success.getMerged());
	}


	@Test
	public void shouldUpdateBoth() {
		Patient local = new Patient();
		local.setDateChanged(new Date(1540000000000L));
		local.setPatientId(1);

		Patient foreign = new Patient();
		foreign.setDateChanged(new Date(1230000000000L));
		foreign.setPatientId(2);

		MergeBehaviour<BaseOpenmrsData> simpleMerge = new TestMergeBehaviour();
		MergeResult<BaseOpenmrsData> result = simpleMerge.resolveDiff(Patient.class, local, foreign);
		MergeSuccess<BaseOpenmrsData> success = null;
		if (result instanceof MergeSuccess) {
			success = (MergeSuccess<BaseOpenmrsData>) result;
		}

		assertFalse(result instanceof MergeConflict);
		assertNotNull(success);
		assertEquals(result.getMessage(), MergeMessageEnum.LOCAL_SAVE_MESSAGE);
		assertFalse(success.shouldUpdateForeign());
		assertTrue(success.shouldUpdateLocal());
		assertNotNull(success.getMerged());
		assertEquals(foreign.getDateChanged(), success.getMerged().getDateChanged());
		assertEquals(foreign.getPatientId(), ((Patient) success.getMerged()).getPatientId());
	}
}
