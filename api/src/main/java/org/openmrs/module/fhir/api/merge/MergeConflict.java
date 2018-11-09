package org.openmrs.module.fhir.api.merge;

/**
 * <h1>MergeConflict</h1>
 * Describes conflict of two entities.
 *
 * @see MergeBehaviour
 * @since 1.15.0
 */
public class MergeConflict<T> extends MergeResult<T> {

	public MergeConflict(Class<?> clazz, T local, T foreign) {
		super(clazz, local, foreign, MergeMessageEnum.CONFLICT);
	}
}
