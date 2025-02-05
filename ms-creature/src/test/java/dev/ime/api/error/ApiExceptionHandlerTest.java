package dev.ime.api.error;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.resource.NoResourceFoundException;

import dev.ime.application.utils.ReactiveLoggerUtils;
import dev.ime.config.GlobalConstants;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class ApiExceptionHandlerTest {

	@Mock
	private ReactiveLoggerUtils reactiveLoggerUtils;

	@InjectMocks
	private ApiExceptionHandler apiExceptionHandler;
	
	
	@Test
	void handleNoResourceFoundException_shouldReturnErrorInfo() {
		
		NoResourceFoundException ex = new NoResourceFoundException(GlobalConstants.EX_PLAIN);
		Mockito.when(reactiveLoggerUtils.logSevereAction(Mockito.anyString())).thenReturn(Mono.empty());

		StepVerifier
		.create(apiExceptionHandler.handleNoResourceFoundException(ex))
		.assertNext( response -> {
			org.junit.jupiter.api.Assertions.assertAll(
					() -> Assertions.assertThat(response).isNotNull(),
					() -> Assertions.assertThat(response.getBody().name()).isEqualTo(GlobalConstants.EX_RESOURCENOTFOUND)
					);
		})
		.verifyComplete();
		Mockito.verify(reactiveLoggerUtils).logSevereAction(Mockito.anyString());
		
	}
	
	@Test
	void createHandleGenericExceptionErrorResponse_shouldReturnErrorInfo() {
		
		Exception ex = new Exception(GlobalConstants.MSG_EVENT_ILLEGAL);
		Mockito.when(reactiveLoggerUtils.logSevereAction(Mockito.anyString())).thenReturn(Mono.empty());

		StepVerifier
		.create(apiExceptionHandler.handleGenericException(ex))
		.assertNext( response -> {
			org.junit.jupiter.api.Assertions.assertAll(
					() -> Assertions.assertThat(response).isNotNull(),
					() -> Assertions.assertThat(response.getBody().name()).isEqualTo(GlobalConstants.EX_PLAIN)
					);
		})
		.verifyComplete();
		Mockito.verify(reactiveLoggerUtils).logSevereAction(Mockito.anyString());		
		
	}
	
}
