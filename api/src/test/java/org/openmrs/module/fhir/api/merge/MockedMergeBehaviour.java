package org.openmrs.module.fhir.api.merge;

import java.net.URI;

public class MockedMergeBehaviour implements MergeBehaviour<Object> {

	@Override
	public MergeResult<Object> resolveDiff(Class<?> clazz, Object local, Object foreign) {
		return new MergeSuccess<>(clazz, local, foreign,
				URI.create("www.example.com/api/object"), new Object(), false, true);
	}
}
