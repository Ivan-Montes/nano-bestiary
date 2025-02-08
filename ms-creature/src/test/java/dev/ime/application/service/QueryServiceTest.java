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
import dev.ime.application.dto.CreatureDto;
import dev.ime.config.CreatureMapper;
import dev.ime.domain.model.Creature;
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
	private CreatureMapper mapper;

	@InjectMocks
	private QueryService queryService;
	
	private Creature creature01;
	private Creature creature02;
	private CreatureDto creatureDto01;
	private CreatureDto creatureDto02;

	private final UUID creatureId01 = UUID.randomUUID();
	private final UUID creatureId02 = UUID.randomUUID();
	private final UUID areaId01 = UUID.randomUUID();
	private final String creatureName01 = "";
	private final String creatureName02 = "";
	private final String creatureDescription01 = "";
	private final String creatureDescription02 = "";
	private final PageRequest pageRequest = PageRequest.of(0, 100);
	
	@BeforeEach
	private void setUp() {			
		
		creature01 = new Creature();
		creature01.setCreatureId(creatureId01);
		creature01.setCreatureName(creatureName01);
		creature01.setCreatureDescription(creatureDescription01);
		creature01.setAreaId(areaId01);
		creature02 = new Creature(creatureId02, creatureName02, creatureDescription02, areaId01);
		creatureDto01 = new CreatureDto(creatureId01, creatureName01, creatureDescription01, areaId01);
		creatureDto02 = new CreatureDto(creatureId02, creatureName02, creatureDescription02, areaId01);
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	void getAll_shouldReturnFluxMultiple() {

		QueryHandler<Object> queryHandler = Mockito.mock(QueryHandler.class);
		Mockito.when(queryDispatcher.getQueryHandler(Mockito.any(Query.class))).thenReturn(queryHandler);
		Mockito.when(queryHandler.handle(Mockito.any(Query.class))).thenReturn(Flux.just(creature01,creature02));
		Mockito.when(mapper.fromDomainToDto(Mockito.any(Creature.class))).thenReturn(creatureDto01, creatureDto02);
		
		StepVerifier
		.create(queryService.getAll(pageRequest))
		.expectNext(creatureDto01, creatureDto02)
		.verifyComplete();

		Mockito.verify(queryDispatcher).getQueryHandler(Mockito.any(Query.class));
		Mockito.verify(queryHandler).handle(Mockito.any(Query.class));
		Mockito.verify(mapper, Mockito.times(2)).fromDomainToDto(Mockito.any(Creature.class));
		
	}

	@SuppressWarnings("unchecked")
	@Test
	void getById_shouldReturnMonoAreaDto() {
		
		QueryHandler<Object> queryHandler = Mockito.mock(QueryHandler.class);
		Mockito.when(queryDispatcher.getQueryHandler(Mockito.any(Query.class))).thenReturn(queryHandler);
		Mockito.when(queryHandler.handle(Mockito.any(Query.class))).thenReturn(Mono.just(creature01));
		Mockito.when(mapper.fromDomainToDto(Mockito.any(Creature.class))).thenReturn(creatureDto01);
		
		StepVerifier
		.create(queryService.getById(areaId01))
		.expectNext(creatureDto01)
		.verifyComplete();
		
		Mockito.verify(queryDispatcher).getQueryHandler(Mockito.any(Query.class));
		Mockito.verify(queryHandler).handle(Mockito.any(Query.class));
		Mockito.verify(mapper).fromDomainToDto(Mockito.any(Creature.class));
		
	}
	
}
