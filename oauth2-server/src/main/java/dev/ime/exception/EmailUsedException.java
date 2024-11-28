package dev.ime.exception;

import java.util.Map;
import java.util.UUID;

import dev.ime.config.GlobalConstants;

public class EmailUsedException extends BasicException{

	private static final long serialVersionUID = 4098406639497976742L;

	public EmailUsedException(Map<String, String> errors) {
		super(
				UUID.randomUUID(), 
				GlobalConstants.EX_EMAILUSED, 
				GlobalConstants.EX_EX_EMAILUSED_DESC, 
				errors);
	}

}
