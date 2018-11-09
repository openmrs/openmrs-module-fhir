package org.openmrs.module.fhir.api.merge;

import java.net.URI;

/**
 * <h1>MergeSuccess</h1>
 * Describes success of two entities.
 *
 * @see MergeBehaviour
 * @since 1.15.0
 */
public class MergeSuccess<T> extends MergeResult<T> {

	protected static final String NO_SAVE_MESSAGE = "Entities are equal";

	protected static final String FOREIGN_SAVE_MESSAGE = "Foreign entity should be updated";

	protected static final String LOCAL_SAVE_MESSAGE = "Local entity should be updated";

	protected static final String SAVE_BOTH_MESSAGES = "Entities were merged and should be updated";

	protected URI foreignAddress;

	protected T merged;

	protected boolean updateLocal;

	protected boolean updateForeign;

	public MergeSuccess(Class<?> clazz, T orgLocal, T orgForeign,
			URI foreignAddress, T merged, boolean updateLocal, boolean updateForeign) {
		super(clazz, orgLocal, orgForeign, null);
		this.foreignAddress = foreignAddress;
		this.merged = merged;
		this.updateLocal = updateLocal;
		this.updateForeign = updateForeign;
		resolveMessage();
	}

	public MergeSuccess(Class<?> clazz, T orgLocal, T orgForeign, URI foreignAddress) {
		super(clazz, orgLocal, orgForeign, NO_SAVE_MESSAGE);
		this.foreignAddress = foreignAddress;
		this.merged = null;
		this.updateLocal = false;
		this.updateForeign = false;
	}

	public MergeSuccess(MergeConflict<T> conflict, URI foreignAddress) {
		super(conflict.getClazz(), conflict.getOrgLocal(), conflict.getOrgForeign(), NO_SAVE_MESSAGE);
		this.foreignAddress = foreignAddress;
		this.merged = null;
		this.updateLocal = false;
		this.updateForeign = false;
	}

	public boolean isCompleted() {
		return true;
	}

	public boolean shouldUpdateForeign() {
		return updateForeign;
	}

	public boolean shouldUpdateLocal() {
		return updateLocal;
	}

	public URI getForeignAddress() {
		return foreignAddress;
	}

	public T getMerged() {
		return merged;
	}

	public boolean shouldSave() {
		return (updateLocal || updateForeign) && merged != null;
	}

	private void resolveMessage() {
		if (updateLocal && updateForeign) {
			this.message = SAVE_BOTH_MESSAGES;
		} else if (updateLocal) {
			this.message = LOCAL_SAVE_MESSAGE;
		} else if (updateForeign) {
			this.message = FOREIGN_SAVE_MESSAGE;
		} else {
			this.message = NO_SAVE_MESSAGE;
		}
	}
}
