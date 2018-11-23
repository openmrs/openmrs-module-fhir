package org.openmrs.module.fhir.api.util;

import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

public final class ErrorUtil {

	private ErrorUtil() {
	}

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

	public static void checkErrors(List<String> errors) {
		if (CollectionUtils.isNotEmpty(errors)) {
			String errorMessage = ErrorUtil.generateErrorMessage(errors,
					"The request cannot be processed due to the following issues\n");
			throw new UnprocessableEntityException(errorMessage);
		}
	}
}
