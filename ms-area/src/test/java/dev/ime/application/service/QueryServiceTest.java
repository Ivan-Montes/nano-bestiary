package dev.ime.application.service;


import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import dev.ime.application.dispatcher.QueryDispatcher;
import dev.ime.application.dto.AreaDto;
import dev.ime.config.AreaMapper;
import dev.ime.domain.model.Area;
import dev.ime.domain.query.Query;
import dev.ime.domain.query.QueryHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class QueryServiceTest {

	@Mock
	private QueryDispatcher queryDispatcher;

	@Mock
	private AreaMapper mapper;
	
	@InjectMocks
	private QueryService queryService;	

	private Area area01;
	private Area area02;
	private AreaDto areaDto01;
	private AreaDto areaDto02;

	private final UUID areaId01 = UUID.randomUUID();
	private final UUID areaId02 = UUID.randomUUID();
	private final String areaName01 = "";
	private final String areaName02 = "";
	private final PageRequest pageRequest = PageRequest.of(0, 100);
	
	@BeforeEach
	private void setUp() {	
		
		area01 = new Area();
		area01.setAreaId(areaId01);
		area01.setAreaName(areaName01);
		area02 = new Area(areaId02, areaName02);
		areaDto01 = new AreaDto(areaId01, areaName01);
		areaDto02 = new AreaDto(areaId02, areaName02);
		
	}

	@SuppressWarnings("unchecked")
	@Test
	void getAll_shouldReturnFluxMultiple() {
		
		QueryHandler<Object> handler = Mockito.mock(QueryHandler.class);
		Mockito.when(queryDispatcher.getQueryHandler(Mockito.any(Query.class))).thenReturn(handler);
		Mockito.when(handler.handle(Mockito.any(Query.class))).thenReturn(Flux.just(area01, area02));
		Mockito.when(mapper.fromDomainToDto(Mockito.any(Area.class))).thenReturn(areaDto01).thenReturn(areaDto02);
		
		StepVerifier
		.create(queryService.getAll(pageRequest))
		.expectNext(areaDto01)
		.expectNext(areaDto02)
		.verifyComplete();
		
		Mockito.verify(queryDispatcher).getQueryHandler(Mockito.any(Query.class));
		Mockito.verify(handler).handle(Mockito.any(Query.class));
		Mockito.verify(mapper, Mockito.times(2)).fromDomainToDto(Mockito.any(Area.class));
		
	}

	@SuppressWarnings("unchecked")
	@Test
	void getById_shouldReturnMonoAreaDto() {
		
		QueryHandler<Object> handler = Mockito.mock(QueryHandler.class);
		Mockito.when(queryDispatcher.getQueryHandler(Mockito.any(Query.class))).thenReturn(handler);
		Mockito.when(handler.handle(Mockito.any(Query.class))).thenReturn(Mono.just(area01));
		Mockito.when(mapper.fromDomainToDto(Mockito.any(Area.class))).thenReturn(areaDto01);
		
		StepVerifier
		.create(queryService.getById(areaId01))
		.expectNext(areaDto01)
		.verifyComplete();
		
		Mockito.verify(queryDispatcher).getQueryHandler(Mockito.any(Query.class));
		Mockito.verify(handler).handle(Mockito.any(Query.class));
		Mockito.verify(mapper).fromDomainToDto(Mockito.any(Area.class));
		
	}
	
}
