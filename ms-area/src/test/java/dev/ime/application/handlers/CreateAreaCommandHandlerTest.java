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

import dev.ime.application.exception.UniqueValueException;
import dev.ime.application.usecases.CreateAreaCommand;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.command.Command;
import dev.ime.domain.event.Event;
import dev.ime.domain.model.Area;
import dev.ime.domain.ports.outbound.EventWriteRepositoryPort;
import dev.ime.domain.ports.outbound.ReadRepositoryPort;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class CreateAreaCommandHandlerTest {

	@Mock
	private EventWriteRepositoryPort eventWriteRepositoryPort;
	@Mock
	private ObjectMapper objectMapper;
	@Mock
	private ReadRepositoryPort<Area> readRepositoryPort;

	@InjectMocks
	private CreateAreaCommandHandler createAreaCommandHandler;

	private Event event;
	private CreateAreaCommand createAreaCommand;

	private final UUID areaId01 = UUID.randomUUID();
	private final String areaName01 = "";

	private UUID eventId = UUID.randomUUID();
	private final String eventCategory = GlobalConstants.AREA_CAT;
	private final String eventType = GlobalConstants.AREA_CREATED;
	private final Instant eventTimestamp = Instant.now();
	private Map<String, Object> eventData = new HashMap<>();	
	
	@BeforeEach
	private void setUp() {

		eventData = new HashMap<>();
		eventData.put(GlobalConstants.AREA_ID, areaId01.toString());
		eventData.put(GlobalConstants.AREA_NAME, areaName01);
		
		event = new Event(
				eventId,
				eventCategory,
				eventType,
				eventTimestamp,
				eventData);
		
		createAreaCommand = new CreateAreaCommand(areaId01, areaName01);
		
	}

	@SuppressWarnings("unchecked")
	@Test
	void handle_shouldReturnEvent() {
		
		Mockito.when(objectMapper.convertValue(Mockito.any(Command.class), Mockito.any(TypeReference.class))).thenReturn(eventData);
		Mockito.when(readRepositoryPort.findByName(Mockito.anyString())).thenReturn(Mono.empty());
		Mockito.when(eventWriteRepositoryPort.save(Mockito.any(Event.class))).thenReturn(Mono.just(event));
		
		StepVerifier
		.create(createAreaCommandHandler.handle(createAreaCommand))
		.expectNext(event)
		.verifyComplete();

		Mockito.verify(objectMapper).convertValue(Mockito.any(Command.class), Mockito.any(TypeReference.class));
		Mockito.verify(readRepositoryPort).findByName(Mockito.anyString());
		Mockito.verify(eventWriteRepositoryPort).save(Mockito.any(Event.class));
		
	}

	@Test
	void handle_WithRepeatedName_PropagateError() {
		
		Mockito.when(readRepositoryPort.findByName(Mockito.anyString())).thenReturn(Mono.just(new Area()));

		Mono<Event> result = createAreaCommandHandler.handle(createAreaCommand);
		
		StepVerifier.create(result)
		.expectError(UniqueValueException.class)
		.verify();
		Mockito.verify(readRepositoryPort).findByName(Mockito.anyString());

	}

}
