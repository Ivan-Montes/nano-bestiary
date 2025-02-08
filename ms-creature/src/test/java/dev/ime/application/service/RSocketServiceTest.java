package dev.ime.application.service;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.ime.domain.ports.outbound.RSocketRepositoryPort;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class RSocketServiceTest {

	@Mock
	private RSocketRepositoryPort rSocketRepositoryPort;

	@InjectMocks
	private RSocketService rSocketService;	
	
	@Test
	void existsById_shouldReturnTrue() {
		
		Mockito.when(rSocketRepositoryPort.existsAnyByAreaId(Mockito.any(UUID.class))).thenReturn(Mono.just(true));
		
		StepVerifier
		.create(rSocketService.existsAnyByAreaId(UUID.randomUUID()))
		.expectNext(true)
		.verifyComplete();
		
		Mockito.verify(rSocketRepositoryPort).existsAnyByAreaId(Mockito.any(UUID.class));

	}

}
