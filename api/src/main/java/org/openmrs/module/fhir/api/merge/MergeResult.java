package org.openmrs.module.fhir.api.merge;

/**
 * <h1>MergeResult</h1>
 * Represents result of merging behaviour
 *
 * @see MergeBehaviour
 * @since 1.15.0
 */
public abstract class MergeResult<T> {

	/**
	 * T type may describe generic class so we need to specify target class
	 */
	protected Class<? extends T> clazz;

	protected final T orgLocal;

	protected final T orgForeign;

	protected MergeMessageEnum message;

	public MergeResult(Class<? extends T> clazz, T orgLocal, T orgForeign, MergeMessageEnum message) {
		this.clazz = clazz;
		this.orgLocal = orgLocal;
		this.orgForeign = orgForeign;
		this.message = message;
	}

	public Class<? extends T> getClazz() {
		return clazz;
	}

	public T getOrgLocal() {
		return orgLocal;
	}

	public T getOrgForeign() {
		return orgForeign;
	}

	public MergeMessageEnum getMessage() {
		return message;
	}
}
