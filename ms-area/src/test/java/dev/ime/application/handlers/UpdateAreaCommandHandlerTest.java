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
import dev.ime.application.usecases.UpdateAreaCommand;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.command.Command;
import dev.ime.domain.event.Event;
import dev.ime.domain.model.Area;
import dev.ime.domain.ports.outbound.EventWriteRepositoryPort;
import dev.ime.domain.ports.outbound.ReadRepositoryPort;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class UpdateAreaCommandHandlerTest {

	@Mock
	private EventWriteRepositoryPort eventWriteRepositoryPort;
	@Mock
	private ObjectMapper objectMapper;
	@Mock
	private ReadRepositoryPort<Area> readRepositoryPort;

	@InjectMocks
	private UpdateAreaCommandHandler updateAreaCommandHandler;
	
	private Event event;
	private UpdateAreaCommand updateAreaCommand;
	private Area area01;

	private final UUID areaId01 = UUID.randomUUID();
	private final String areaName01 = "";

	private UUID eventId = UUID.randomUUID();
	private final String eventCategory = GlobalConstants.AREA_CAT;
	private final String eventType = GlobalConstants.AREA_UPDATED;
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
		
		updateAreaCommand = new UpdateAreaCommand(areaId01, areaName01);
		
		area01 = new Area(areaId01, areaName01);

	}

	@SuppressWarnings("unchecked")
	@Test
	void handle_shouldReturnEvent() {
		
		Mockito.when(readRepositoryPort.findById(Mockito.any(UUID.class))).thenReturn(Mono.just(new Area()));
		Mockito.when(readRepositoryPort.findByName(Mockito.anyString())).thenReturn(Mono.just(area01));
		Mockito.when(objectMapper.convertValue(Mockito.any(Command.class), Mockito.any(TypeReference.class))).thenReturn(eventData);
		Mockito.when(eventWriteRepositoryPort.save(Mockito.any(Event.class))).thenReturn(Mono.just(event));

		StepVerifier
		.create(updateAreaCommandHandler.handle(updateAreaCommand))
		.expectNext(event)
		.verifyComplete();
		
		Mockito.verify(readRepositoryPort).findById(Mockito.any(UUID.class));		
		Mockito.verify(objectMapper).convertValue(Mockito.any(Command.class), Mockito.any(TypeReference.class));
		Mockito.verify(readRepositoryPort).findByName(Mockito.anyString());
		Mockito.verify(eventWriteRepositoryPort).save(Mockito.any(Event.class));
		
	}
	
	@Test
	void handle_WithNameAlreadyUsed_shouldPropagateError() {

		area01.setAreaId(UUID.randomUUID());
		Mockito.when(readRepositoryPort.findById(Mockito.any(UUID.class))).thenReturn(Mono.just(new Area()));
		Mockito.when(readRepositoryPort.findByName(Mockito.anyString())).thenReturn(Mono.just(area01));

		StepVerifier
		.create(updateAreaCommandHandler.handle(updateAreaCommand))
		.expectError(UniqueValueException.class)
		.verify();
		
		Mockito.verify(readRepositoryPort).findById(Mockito.any(UUID.class));
		Mockito.verify(readRepositoryPort).findByName(Mockito.anyString());
		
	}

}
