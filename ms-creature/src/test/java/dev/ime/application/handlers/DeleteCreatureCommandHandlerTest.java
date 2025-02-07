package dev.ime.application.handlers;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.ime.application.usecases.DeleteCreatureCommand;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.command.Command;
import dev.ime.domain.event.Event;
import dev.ime.domain.model.Creature;
import dev.ime.domain.ports.outbound.EventWriteRepositoryPort;
import dev.ime.domain.ports.outbound.ReadRepositoryPort;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class DeleteCreatureCommandHandlerTest {

	@Mock
	private EventWriteRepositoryPort eventWriteRepositoryPort;
	@Mock
	private ObjectMapper objectMapper;
	@Mock
	private ReadRepositoryPort<Creature> readRepositoryPort;

	@InjectMocks
	private DeleteCreatureCommandHandler deleteCreatureCommandHandler;

	private Event event;
	private DeleteCreatureCommand deleteCreatureCommand;
	
	private final UUID creatureId01 = UUID.randomUUID();
	private final UUID areaId01 = UUID.randomUUID();
	private final String creatureName01 = "";
	private final String creatureDescription01 = "";

	private UUID eventId = UUID.randomUUID();
	private final String eventCategory = GlobalConstants.CREATURE_CAT;
	private final String eventType = GlobalConstants.CREATURE_DELETED;
	private final Instant eventTimestamp = Instant.now();
	private Map<String, Object> eventData = new HashMap<>();

	@BeforeEach
	private void setUp() {

		eventData = new HashMap<>();
		eventData.put(GlobalConstants.CREATURE_ID, creatureId01.toString());
		eventData.put(GlobalConstants.CREATURE_NAME, creatureName01);
		eventData.put(GlobalConstants.CREATURE_DESC, creatureDescription01);
		eventData.put(GlobalConstants.AREA_ID, areaId01.toString());
		
		event = new Event(
				eventId,
				eventCategory,
				eventType,
				eventTimestamp,
				eventData);
		
		deleteCreatureCommand = new DeleteCreatureCommand(creatureId01);		
		
	}

	
	@SuppressWarnings("unchecked")
	@Test
	void handle_shouldReturnEvent() {
		
		Mockito.when(readRepositoryPort.findById(Mockito.any(UUID.class))).thenReturn(Mono.just(new Creature()));
		Mockito.when(objectMapper.convertValue(Mockito.any(Command.class), Mockito.any(TypeReference.class))).thenReturn(eventData);		
		Mockito.when(eventWriteRepositoryPort.save(Mockito.any(Event.class))).thenReturn(Mono.just(event));

		StepVerifier
		.create(deleteCreatureCommandHandler.handle(deleteCreatureCommand))
		.expectNext(event)
		.verifyComplete();
		
		Mockito.verify(readRepositoryPort).findById(Mockito.any(UUID.class));
		Mockito.verify(objectMapper).convertValue(Mockito.any(Command.class), Mockito.any(TypeReference.class));
		Mockito.verify(eventWriteRepositoryPort).save(Mockito.any(Event.class));		

	}

}
