package org.openmrs.module.fhir.api.merge;

public enum MergeMessageEnum {
	NO_SAVE_MESSAGE("Entities are equal"),
	FOREIGN_SAVE_MESSAGE("Foreign entity should be updated"),
	LOCAL_SAVE_MESSAGE("Local entity should be updated"),
	SAVE_BOTH_MESSAGES("Entities were merged and should be updated"),
	CONFLICT("Entities cannot be merged automatically!");

	private final String message;

	MergeMessageEnum(String msg) {
		message = msg;
	}

	public String getValue() {
		return message;
	}
}
