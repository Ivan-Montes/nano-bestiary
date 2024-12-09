package dev.ime.application.exception;

import java.util.Map;
import java.util.UUID;

import dev.ime.config.GlobalConstants;

public class CreateRedisEntityException extends BasicException {

	private static final long serialVersionUID = 225785824958021918L;

	public CreateRedisEntityException(Map<String, String> errors) {
		super(
				UUID.randomUUID(), 
				GlobalConstants.EX_CREATEREDISENTITY, 
				GlobalConstants.EX_CREATEREDISENTITY_DESC, 
				errors);
	}
}
