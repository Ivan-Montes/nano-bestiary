package dev.ime.application.exception;

import java.util.Map;
import java.util.UUID;

import dev.ime.config.GlobalConstants;

public class InvalidUUIDException extends BasicException {

	private static final long serialVersionUID = 5595246934566788020L;

	public InvalidUUIDException(Map<String, String> errors) {
		super(
				UUID.randomUUID(),
				GlobalConstants.EX_INVALIDUUID,
				GlobalConstants.EX_INVALIDUUID_DESC,
				errors
				);
	}

}
