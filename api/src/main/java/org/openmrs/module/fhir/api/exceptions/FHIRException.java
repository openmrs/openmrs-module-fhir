package org.openmrs.module.fhir.api.exceptions;

public class FHIRException extends RuntimeException {

	public FHIRException(String message) {
		super(message);
	}

	public FHIRException(Throwable cause) {
		super(cause);
	}

	public FHIRException(String message, Throwable cause) {
		super(message, cause);
	}
}
