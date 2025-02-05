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
class RSocketCreatureControllerTest {

	@Mock
	private ReactiveLoggerUtils reactiveLoggerUtils;
	@Mock
	private RSocketServicePort rSocketServicePort;
	
	@InjectMocks
	private RSocketCreatureController rSocketCreatureController;
	
	private final UUID areaId01 = UUID.randomUUID();

	@Test
	void existsAnyByAreaId_shouldReturnTrue() {

		Mockito.when(reactiveLoggerUtils.logInfoAction(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.empty());
		Mockito.when(rSocketServicePort.existsAnyByAreaId(Mockito.any(UUID.class))).thenReturn(Mono.just(true));

		StepVerifier
		.create(rSocketCreatureController.existsAnyByAreaId(areaId01.toString()))
		.expectNext(true)
		.verifyComplete();
		
		Mockito.verify(rSocketServicePort).existsAnyByAreaId(Mockito.any(UUID.class));

	}

	@Test
	void existsAnyByAreaId_WithBadUUID_shouldReturnError() {
		
		Mockito.when(reactiveLoggerUtils.logInfoAction(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.empty());

		StepVerifier
		.create(rSocketCreatureController.existsAnyByAreaId(GlobalConstants.MSG_EVENT_ILLEGAL))
		.expectError(InvalidUUIDException.class)
		.verify();
		
	}
	
}
