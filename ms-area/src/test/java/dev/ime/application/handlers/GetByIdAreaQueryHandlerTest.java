package dev.ime.application.handlers;


import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.ime.application.usecases.GetByIdAreaQuery;
import dev.ime.domain.model.Area;
import dev.ime.domain.ports.outbound.ReadRepositoryPort;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class GetByIdAreaQueryHandlerTest {

	@Mock
	private ReadRepositoryPort<Area> readRepositoryPort;

	@InjectMocks
	private GetByIdAreaQueryHandler getByIdAreaQueryHandler;
	
	private Area area01;
	
	private final UUID areaId01 = UUID.randomUUID();
	private final String areaName01 = "";

	private final GetByIdAreaQuery getByIdQuery = new GetByIdAreaQuery(areaId01);
	
	@BeforeEach
	private void setUp() {
		
		area01 = new Area(areaId01, areaName01);
		
	}
	
	@Test
	void handle_shouldReturnArea() {
		
		Mockito.when(readRepositoryPort.findById(Mockito.any(UUID.class))).thenReturn(Mono.just(area01));
		
		StepVerifier
		.create(getByIdAreaQueryHandler.handle(getByIdQuery))
		.expectNext(area01)
		.verifyComplete();

		Mockito.verify(readRepositoryPort).findById(Mockito.any(UUID.class));
		
	}

}
