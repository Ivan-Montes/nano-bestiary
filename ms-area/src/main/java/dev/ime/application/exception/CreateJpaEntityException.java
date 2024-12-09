package dev.ime.application.exception;

import java.util.Map;
import java.util.UUID;

import dev.ime.config.GlobalConstants;

public class CreateJpaEntityException extends BasicException{

	private static final long serialVersionUID = 4676056739042095243L;

	public CreateJpaEntityException(Map<String, String> errors) {
		super(
				UUID.randomUUID(), 
				GlobalConstants.EX_CREATEJPAENTITY, 
				GlobalConstants.EX_CREATEJPAENTITY_DESC, 
				errors);
	}

}
