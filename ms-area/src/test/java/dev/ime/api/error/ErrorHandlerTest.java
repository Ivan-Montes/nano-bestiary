package dev.ime.api.error;


import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import dev.ime.application.exception.BasicException;
import dev.ime.application.exception.InvalidUUIDException;
import dev.ime.application.utils.ReactiveLoggerUtils;
import dev.ime.config.GlobalConstants;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class ErrorHandlerTest {

	@Mock
	private ReactiveLoggerUtils reactiveLoggerUtils;

	@InjectMocks
	private ErrorHandler errorHandler;
	
	@Test
	void handleException_WithGenericException_shouldReturnResponse() {
		
		Exception ex = new Exception(GlobalConstants.EX_PLAIN);
		Mockito.when(reactiveLoggerUtils.logInfoAction(Mockito.anyString(),Mockito.anyString(),Mockito.anyString())).thenReturn(Mono.empty());
		
		StepVerifier
		.create(errorHandler.handleException(ex))
		.assertNext( response -> {
			org.junit.jupiter.api.Assertions.assertAll(
					() -> Assertions.assertThat(response).isNotNull(),
					() -> Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST)
					);
		})
		.verifyComplete();
		Mockito.verify(reactiveLoggerUtils).logInfoAction(Mockito.anyString(),Mockito.anyString(),Mockito.anyString());
		
	}

	@Test
	void handleException_WithBasicExceptionFamily_ReturnMonoServerResponseWithError() {

		BasicException ex = new InvalidUUIDException(Map.of(GlobalConstants.MSG_REQUIRED, GlobalConstants.MSG_UNKNOWDATA));
		Mockito.when(reactiveLoggerUtils.logInfoAction(Mockito.anyString(),Mockito.anyString(),Mockito.anyString())).thenReturn(Mono.empty());

		StepVerifier
		.create(errorHandler.handleException(ex))
		.assertNext( response -> {
			org.junit.jupiter.api.Assertions.assertAll(
					()-> Assertions.assertThat(response).isNotNull(),
					()-> Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.I_AM_A_TEAPOT)
					);
		})
		.verifyComplete();	
		Mockito.verify(reactiveLoggerUtils).logInfoAction(Mockito.anyString(),Mockito.anyString(),Mockito.anyString());

	}
	
	@Test
	void handleException_WithIllegalArgumentException_ReturnMonoServerResponseWithError() {
		
		IllegalArgumentException ex = new IllegalArgumentException(GlobalConstants.EX_PLAIN);
		Mockito.when(reactiveLoggerUtils.logInfoAction(Mockito.anyString(),Mockito.anyString(),Mockito.anyString())).thenReturn(Mono.empty());

		StepVerifier.create(errorHandler.handleException(ex))
        .assertNext(response -> {        	
        	org.junit.jupiter.api.Assertions.assertAll(
					()-> Assertions.assertThat(response).isNotNull(),
					()-> Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
					);
        })
        .verifyComplete();				
		Mockito.verify(reactiveLoggerUtils).logInfoAction(Mockito.anyString(),Mockito.anyString(),Mockito.anyString());

	}	

}
