package org.openmrs.module.fhir.api.merge;

/**
 * <h1>MergeConflict</h1>
 * Describes conflict of two entities.
 *
 * @see MergeBehaviour
 * @since 1.15.0
 */
public class MergeConflict<T> extends MergeResult<T> {

	protected static final String CONFLICT = "Entities cannot be merged automatically!";

	public MergeConflict(Class<?> clazz, T local, T foreign) {
		super(clazz, local, foreign, CONFLICT);
	}

	public boolean isCompleted() {
		return false;
	}
}
