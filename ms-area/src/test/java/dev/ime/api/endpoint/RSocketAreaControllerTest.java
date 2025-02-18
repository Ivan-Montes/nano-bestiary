package dev.ime.api.endpoint;


import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.ime.application.exception.InvalidUUIDException;
import dev.ime.application.utils.ReactiveLoggerUtils;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.ports.inbound.RSocketServicePort;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class RSocketAreaControllerTest {

	@Mock
	private ReactiveLoggerUtils reactiveLoggerUtils;
	@Mock
	private RSocketServicePort rSocketServicePort;
	
	@InjectMocks
	private RSocketAreaController rSocketAreaController;

	private final UUID areaId01 = UUID.randomUUID();
	
	
	@Test
	void existsById_shouldReturnTrue() {
		
		Mockito.when(rSocketServicePort.existsById(Mockito.any(UUID.class))).thenReturn(Mono.just(true));
		Mockito.when(reactiveLoggerUtils.logInfoAction(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.empty());

		StepVerifier
		.create(rSocketAreaController.existsById(areaId01.toString()))
		.expectNext(true)
		.verifyComplete();
		
		Mockito.verify(rSocketServicePort).existsById(Mockito.any(UUID.class));

	}

	@Test
	void existsById_WithBadUUID_shouldReturnError() {
		
		Mockito.when(reactiveLoggerUtils.logInfoAction(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.empty());

		StepVerifier
		.create(rSocketAreaController.existsById(GlobalConstants.MSG_EVENT_ILLEGAL))
		.expectError(InvalidUUIDException.class)
		.verify();
		
	}
	

}
