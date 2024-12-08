package dev.ime.application.dto;

import java.util.UUID;

import dev.ime.config.GlobalConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AreaDto(
		UUID areaId,
		@NotBlank @Pattern( regexp = GlobalConstants.PATTERN_NAME_FULL ) String areaName
		) {
	
}
