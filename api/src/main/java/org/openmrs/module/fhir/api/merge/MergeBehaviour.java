package org.openmrs.module.fhir.api.merge;

/**
 * <h1>MergeBehaviour</h1>
 * Reflects merging behaviour for type T.
 * <p><b>Note:</b> use Object.class as a type T for more generic merging behaviour.</p>
 *
 * @see <a href="https://issues.openmrs.org/browse/SYNCT-243">SYNCT-243</a>
 * @since 1.15.0
 */
public interface MergeBehaviour<T> {

	/**
	 * <p>Resolves merge conflicts for entities' synchronization.</p>
	 *
	 * @param local represents local version of an entity
	 * @param foreign represents foreign version of an entity
	 * @param clazz represents specific class which extends T
	 * @return returns a result which depends on implemented merging behaviour
	 */
	MergeResult<T> resolveDiff(Class<? extends T> clazz, T local, T foreign);
}
