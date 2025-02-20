package dev.ime.application.handlers;


import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import dev.ime.application.usecases.GetAllAreaQuery;
import dev.ime.domain.model.Area;
import dev.ime.domain.ports.outbound.ReadRepositoryPort;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class GetAllAreaQueryHandlerTest {

	@Mock
	private ReadRepositoryPort<Area> readRepositoryPort;

	@InjectMocks
	private GetAllAreaQueryHandler getAllAreaQueryHandler;	

	private Area area01;
	private Area area02;
	private final UUID areaId01 = UUID.randomUUID();
	private final UUID areaId02 = UUID.randomUUID();
	private final String areaName01 = "";
	private final String areaName02 = "";
	private final PageRequest pageRequest = PageRequest.of(0, 100);
	private final GetAllAreaQuery getAllQuery = new GetAllAreaQuery(pageRequest);
	
	@BeforeEach
	private void setUp() {	
		
		area01 = new Area();
		area01.setAreaId(areaId01);
		area01.setAreaName(areaName01);

		area02 = new Area(areaId02, areaName02);
		
	}

	@Test
	void handle_shouldReturnFluxArea() {		
		
		Mockito.when(readRepositoryPort.findAll(Mockito.any(Pageable.class))).thenReturn(Flux.just(area01, area02));
		
		StepVerifier
		.create(getAllAreaQueryHandler.handle(getAllQuery))
		.expectNext(area01)
		.expectNext(area02)
		.verifyComplete();
		
		Mockito.verify(readRepositoryPort).findAll(Mockito.any(Pageable.class));
	}

}
