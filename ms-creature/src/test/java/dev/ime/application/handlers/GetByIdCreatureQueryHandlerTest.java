package dev.ime.application.handlers;


import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.ime.application.usecases.GetByIdCreatureQuery;
import dev.ime.domain.model.Creature;
import dev.ime.domain.ports.outbound.ReadRepositoryPort;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class GetByIdCreatureQueryHandlerTest {

	@Mock
	private ReadRepositoryPort<Creature> readRepositoryPort;

	@InjectMocks
	private GetByIdCreatureQueryHandler getByIdCreatureQueryHandler;

	private Creature creature01;

	private final UUID creatureId01 = UUID.randomUUID();
	private final UUID areaId01 = UUID.randomUUID();
	private final String creatureName01 = "";
	private final String creatureDescription01 = "";
	private final GetByIdCreatureQuery getByIdQuery = new GetByIdCreatureQuery(creatureId01);

	@BeforeEach
	private void setUp() {	
		
		creature01 = new Creature();
		creature01.setCreatureId(creatureId01);
		creature01.setCreatureName(creatureName01);
		creature01.setCreatureDescription(creatureDescription01);
		creature01.setAreaId(areaId01);		
		
	}
	
	@Test
	void handle_shouldReturnMonoCreature() {		

		Mockito.when(readRepositoryPort.findById(Mockito.any(UUID.class))).thenReturn(Mono.just(creature01));

		StepVerifier
		.create(getByIdCreatureQueryHandler.handle(getByIdQuery))
		.expectNext(creature01)
		.verifyComplete();

		Mockito.verify(readRepositoryPort).findById(Mockito.any(UUID.class));

	}

}
