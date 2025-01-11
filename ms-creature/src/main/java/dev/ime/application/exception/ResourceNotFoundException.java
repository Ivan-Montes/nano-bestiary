package dev.ime.application.exception;

import java.util.Map;
import java.util.UUID;

import dev.ime.config.GlobalConstants;

public class ResourceNotFoundException extends BasicException{	

	private static final long serialVersionUID = 5436549421040910165L;

	public ResourceNotFoundException(Map<String, String> errors) {
		super(
				UUID.randomUUID(),
				GlobalConstants.EX_RESOURCENOTFOUND,
				GlobalConstants.EX_RESOURCENOTFOUND_DESC,
				errors
				);
	}

}
