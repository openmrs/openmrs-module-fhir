package org.openmrs.module.fhir.api.merge;

import java.net.URI;

public class MockedMergeBehaviour implements MergeBehaviour<Object> {

	@Override
	public MergeResult<Object> resolveDiff(Object local, Object foreign, Class<?> clazz) {
		return new MergeSuccess<>(clazz, local, foreign,
				URI.create("www.example.com/api/object"), new Object(), false, true);
	}
}
