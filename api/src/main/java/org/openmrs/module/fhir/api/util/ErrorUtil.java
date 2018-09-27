package org.openmrs.module.fhir.api.util;

import java.util.List;

public class ErrorUtil {

	public static String generateErrorMessage(List<String> errors) {
		return generateErrorMessage(errors, "");
	}

	public static String generateErrorMessage(List<String> errors, String baseMessage) {
		StringBuilder errorMessage = new StringBuilder(baseMessage);
		for (int i = 0; i < errors.size(); i++) {
			errorMessage.append(i + 1).append(" : ").append(errors.get(i)).append("\n");
		}
		return errorMessage.toString();
	}
}
