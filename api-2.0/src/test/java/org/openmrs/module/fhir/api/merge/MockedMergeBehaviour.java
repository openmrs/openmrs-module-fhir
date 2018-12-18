package org.openmrs.module.fhir.api.merge;

public class MockedMergeBehaviour implements MergeBehaviour<Object> {

	@Override
	public MergeResult<Object> resolveDiff(Class<?> clazz, Object local, Object foreign) {
		return new MergeSuccess<>(clazz, local, foreign, new Object(), false, true);
	}
}
