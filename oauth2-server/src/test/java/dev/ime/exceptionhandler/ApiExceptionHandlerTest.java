package dev.ime.exceptionhandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import dev.ime.dto.ErrorResponse;
import dev.ime.exception.BasicException;
import dev.ime.utils.LoggerUtils;

@ExtendWith(MockitoExtension.class)
class ApiExceptionHandlerTest {

	@Mock
    private LoggerUtils loggerUtil;

	@InjectMocks
	private ApiExceptionHandler apiExceptionHandler;

	private final String name = "Our Exception";
	private final String description = "Our Exception, born and raised here";
	private final UUID uuid = UUID.randomUUID();
	private Map<String, String> errors;
	
	@BeforeEach
	private void setUp() {
		
		
	}		
	
	@Test
	void methodArgumentNotValidException_WithException_ReturnResponse() {
		
		MethodArgumentNotValidException ex = Mockito.mock(MethodArgumentNotValidException.class);
		BindingResult bindingResult = Mockito.mock(BindingResult.class);
		Mockito.when(ex.getBindingResult()).thenReturn(bindingResult);
		List<ObjectError>listErrors = new ArrayList<>();
		listErrors.add(new FieldError(name, description, description));
		Mockito.when(bindingResult.getAllErrors()).thenReturn(listErrors);

		ResponseEntity<ErrorResponse> response = apiExceptionHandler.methodArgumentNotValidException(ex);
		
		org.junit.jupiter.api.Assertions.assertAll(
				()-> Assertions.assertThat(response).isNotNull(),
				()-> Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE)
				);
	}
	
	@Test
	void noResourceFoundException_WithException_ReturnResponse() {

		NoResourceFoundException ex = new NoResourceFoundException(null, description);
		
		ResponseEntity<ErrorResponse> response = apiExceptionHandler.noResourceFoundException(ex);
		
		org.junit.jupiter.api.Assertions.assertAll(
				()-> Assertions.assertThat(response).isNotNull(),
				()-> Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE)
				);
	}

	@Test
	void lastExceptionStands_WithException_ReturnResponse() {

		Exception ex = new Exception(description);
		
		ResponseEntity<ErrorResponse> response = apiExceptionHandler.lastExceptionStands(ex);
		
		org.junit.jupiter.api.Assertions.assertAll(
				()-> Assertions.assertThat(response).isNotNull(),
				()-> Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST)
				);
	}
	
	@Test
	void handleBasicExceptionExtendedClasses_basicException_ReturnResponseEntity() {
		
		ResponseEntity<ErrorResponse> response = apiExceptionHandler.handleBasicExceptionExtendedClasses(new BasicException(uuid, name, description, errors));
		
		org.junit.jupiter.api.Assertions.assertAll(
				()-> Assertions.assertThat(response).isNotNull(),
				()-> Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST)
				);	
	}
}
