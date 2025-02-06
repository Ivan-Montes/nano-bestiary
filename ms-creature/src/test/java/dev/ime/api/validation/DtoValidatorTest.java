package dev.ime.api.validation;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import dev.ime.application.dto.CreatureDto;
import dev.ime.application.exception.ValidationException;
import dev.ime.config.GlobalConstants;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class DtoValidatorTest {

	@Mock
	private Validator validator;

	@InjectMocks
	private DtoValidator dtoValidator;

	private CreatureDto creatureDto01;

	private final UUID creatureId01 = UUID.randomUUID();
	private final UUID areaId01 = UUID.randomUUID();
	private final String creatureName01 = "";
	private final String creatureDescription01 = "";
	
	@BeforeEach
	private void setUp() {	
		
		creatureDto01 = new CreatureDto(creatureId01, creatureName01, creatureDescription01, areaId01);
		
	}
	
	@Test
	void validateDto_shouldReturnDto() {
		
		Mockito.doNothing().when(validator).validate(Mockito.any(Object.class), Mockito.any(Errors.class));

		StepVerifier
		.create(dtoValidator.validateDto(creatureDto01))
		.expectNext(creatureDto01)
		.verifyComplete();
		
		Mockito.verify(validator).validate(Mockito.any(Object.class), Mockito.any(Errors.class));

	}
	
	@Test
	void validateDto_WithError_shouldReturnMonoValidationException() {

		Mockito.doAnswer( exec -> {
			Errors errors = exec.getArgument(1);
			errors.rejectValue(GlobalConstants.CREATURE_ID, GlobalConstants.MSG_UNKNOWDATA, GlobalConstants.EX_PLAIN_DESC);
			return null;
		}).when(validator).validate(Mockito.any(Object.class), Mockito.any(Errors.class));		

		StepVerifier.create(dtoValidator.validateDto(creatureDto01))
		.expectError(ValidationException.class)
		.verify();
		
		Mockito.verify(validator).validate(Mockito.any(Object.class), Mockito.any(Errors.class));
		
	}
	
}
