package dev.ime.application.exception;

import java.util.Map;
import java.util.UUID;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class BasicException extends RuntimeException{

	private static final long serialVersionUID = 8951117738944807004L;
	
	private final UUID identifier;
	private final String name;
	private final String description;	
	private final Map<String, String> errors;
	
	public BasicException(UUID identifier, String name, String description, Map<String, String> errors) {
		super(name);
		this.identifier = identifier;
		this.name = name;
		this.description = description;
		this.errors = errors;
	}
	
	
}
