package dev.ime.application.exception;

import java.util.Map;
import java.util.UUID;

import dev.ime.config.GlobalConstants;

public class ValidationException extends BasicException{

	private static final long serialVersionUID = -1202282051409422055L;

	public ValidationException(Map<String, String> errors) {
		super(
				UUID.randomUUID(),
				GlobalConstants.EX_VALIDATION,
				GlobalConstants.EX_VALIDATION_DESC,
				errors);
		
	}

}
