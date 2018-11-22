package org.openmrs.module.fhir.api.merge;

/**
 * <h1>MergeSuccess</h1>
 * Describes success of two entities.
 *
 * @see MergeBehaviour
 * @since 1.15.0
 */
public class MergeSuccess<T> extends MergeResult<T> {

	protected T merged;

	protected boolean updateLocal;

	protected boolean updateForeign;

	public MergeSuccess(MergeConflict<T> conflict, T merged, boolean updateLocal, boolean updateForeign) {
		super(conflict.getClazz(), conflict.getOrgLocal(), conflict.getOrgForeign(), null);
		this.merged = merged;
		this.updateLocal = updateLocal;
		this.updateForeign = updateForeign;
		resolveMessage();
	}

	public MergeSuccess(Class<? extends T> clazz, T orgLocal, T orgForeign, T merged,
			boolean updateLocal, boolean updateForeign) {
		super(clazz, orgLocal, orgForeign, null);
		this.merged = merged;
		this.updateLocal = updateLocal;
		this.updateForeign = updateForeign;
		resolveMessage();
	}

	public MergeSuccess(MergeConflict<T> conflict) {
		super(conflict.getClazz(), conflict.getOrgLocal(), conflict.getOrgForeign(),
				MergeMessageEnum.NO_SAVE_MESSAGE);
		this.merged = null;
		this.updateLocal = false;
		this.updateForeign = false;
	}

	public MergeSuccess(Class<? extends T> clazz, T orgLocal, T orgForeign) {
		super(clazz, orgLocal, orgForeign, MergeMessageEnum.NO_SAVE_MESSAGE);
		this.merged = null;
		this.updateLocal = false;
		this.updateForeign = false;
	}

	public boolean shouldUpdateForeign() {
		return updateForeign;
	}

	public boolean shouldUpdateLocal() {
		return updateLocal;
	}

	public T getMerged() {
		return merged;
	}

	public boolean shouldSave() {
		return (updateLocal || updateForeign) && merged != null;
	}

	private void resolveMessage() {
		if (updateLocal && updateForeign) {
			this.message = MergeMessageEnum.SAVE_BOTH_MESSAGES;
		} else if (updateLocal) {
			this.message = MergeMessageEnum.LOCAL_SAVE_MESSAGE;
		} else if (updateForeign) {
			this.message = MergeMessageEnum.FOREIGN_SAVE_MESSAGE;
		} else {
			this.message = MergeMessageEnum.NO_SAVE_MESSAGE;
		}
	}
}
