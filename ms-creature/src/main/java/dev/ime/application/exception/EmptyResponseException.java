package dev.ime.application.exception;

import java.util.Map;
import java.util.UUID;

import dev.ime.config.GlobalConstants;

public class EmptyResponseException extends BasicException{
	
	private static final long serialVersionUID = -2526712375865934370L;

	public EmptyResponseException(Map<String, String> errors) {
		super(
				UUID.randomUUID(),
				GlobalConstants.EX_EMPTYRESPONSE,
				GlobalConstants.EX_EMPTYRESPONSE_DESC,
				errors);
		
	}

}
