package dev.ime.dto;

import java.util.Map;
import java.util.UUID;

public record ErrorResponse(
		UUID identifier, 
		String name, 
		String description, 
		Map<String,String>error
		) {

}
