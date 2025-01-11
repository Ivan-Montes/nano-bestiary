package dev.ime.application.exception;

import java.util.Map;
import java.util.UUID;

import dev.ime.config.GlobalConstants;

public class PublishEventException extends BasicException{

	private static final long serialVersionUID = -4015480582558372054L;

	public PublishEventException(Map<String, String> errors) {
		super(
				UUID.randomUUID(), 
				GlobalConstants.EX_PUBLISHEVENT, 
				GlobalConstants.EX_PUBLISHEVENT_DESC, 
				errors);
	}

}
