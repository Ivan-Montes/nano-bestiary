package dev.ime.application.exception;

import java.util.Map;
import java.util.UUID;

import dev.ime.config.GlobalConstants;

public class UniqueValueException extends BasicException{

	private static final long serialVersionUID = 4486775871090794064L;

	public UniqueValueException(Map<String, String> errors) {
		super(
				UUID.randomUUID(),
				GlobalConstants.EX_UNIQUEVALUE,
				GlobalConstants.EX_UNIQUEVALUE_DESC,
				errors);
		
	}

}
