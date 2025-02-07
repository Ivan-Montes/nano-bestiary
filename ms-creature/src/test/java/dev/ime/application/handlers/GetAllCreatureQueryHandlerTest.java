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

import dev.ime.application.usecases.GetAllCreatureQuery;
import dev.ime.domain.model.Creature;
import dev.ime.domain.ports.outbound.ReadRepositoryPort;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class GetAllCreatureQueryHandlerTest {

	@Mock
	private ReadRepositoryPort<Creature> readRepositoryPort;

	@InjectMocks
	private GetAllCreatureQueryHandler getAllCreatureQueryHandler;

	private Creature creature01;
	private Creature creature02;

	private final UUID creatureId01 = UUID.randomUUID();
	private final UUID creatureId02 = UUID.randomUUID();
	private final UUID areaId01 = UUID.randomUUID();
	private final String creatureName01 = "";
	private final String creatureName02 = "";
	private final String creatureDescription01 = "";
	private final String creatureDescription02 = "";
	private final PageRequest pageRequest = PageRequest.of(0, 100);
	private final GetAllCreatureQuery getAllQuery = new GetAllCreatureQuery(pageRequest);

	@BeforeEach
	private void setUp() {	
		
		creature01 = new Creature();
		creature01.setCreatureId(creatureId01);
		creature01.setCreatureName(creatureName01);
		creature01.setCreatureDescription(creatureDescription01);
		creature01.setAreaId(areaId01);
		
		creature02 = new Creature(creatureId02, creatureName02, creatureDescription02, areaId01);
		
	}

	@Test
	void handle_shouldReturnFluxCreature() {
		
		Mockito.when(readRepositoryPort.findAll(Mockito.any(Pageable.class))).thenReturn(Flux.just(creature01, creature02));
		
		StepVerifier
		.create(getAllCreatureQueryHandler.handle(getAllQuery))
		.expectNext(creature01)
		.expectNext(creature02)
		.verifyComplete();

		Mockito.verify(readRepositoryPort).findAll(Mockito.any(Pageable.class));
		
	}

}
