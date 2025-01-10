package dev.ime.application.dto;

import dev.ime.config.GlobalConstants;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PaginationDto(
	    @Min(0) Integer page,
	    @Min(1)@Max(100)Integer size,
	    @NotBlank String sortBy,
	    @Pattern(regexp = GlobalConstants.PS_A +"|"+ GlobalConstants.PS_D)String sortDir
	    ) {

}
