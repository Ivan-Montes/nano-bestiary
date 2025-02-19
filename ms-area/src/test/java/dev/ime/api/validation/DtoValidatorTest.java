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

import dev.ime.application.dto.AreaDto;
import dev.ime.application.exception.ValidationException;
import dev.ime.config.GlobalConstants;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class DtoValidatorTest {

	@Mock
	private Validator validator;

	@InjectMocks
	private DtoValidator dtoValidator;

	private AreaDto areaDto01;

	private final UUID areaId01 = UUID.randomUUID();
	private final String areaName01 = "";
	
	@BeforeEach
	private void setUp() {

		areaDto01 = new AreaDto(areaId01, areaName01);
		
	}
		
	@Test
	void validateDto_shouldReturnDto() {
		
		Mockito.doNothing().when(validator).validate(Mockito.any(Object.class), Mockito.any(Errors.class));

		StepVerifier
		.create(dtoValidator.validateDto(areaDto01))
		.expectNext(areaDto01)
		.verifyComplete();
		
		Mockito.verify(validator).validate(Mockito.any(Object.class), Mockito.any(Errors.class));

	}
	
	@Test
	void validateDto_WithError_shouldReturnMonoValidationException() {

		Mockito.doAnswer( exec -> {
			Errors errors = exec.getArgument(1);
			errors.rejectValue(GlobalConstants.AREA_ID, GlobalConstants.MSG_UNKNOWDATA, GlobalConstants.EX_PLAIN_DESC);
			return null;
		}).when(validator).validate(Mockito.any(Object.class), Mockito.any(Errors.class));		

		StepVerifier.create(dtoValidator.validateDto(areaDto01))
		.expectError(ValidationException.class)
		.verify();
		
		Mockito.verify(validator).validate(Mockito.any(Object.class), Mockito.any(Errors.class));
		
	}
	
}
