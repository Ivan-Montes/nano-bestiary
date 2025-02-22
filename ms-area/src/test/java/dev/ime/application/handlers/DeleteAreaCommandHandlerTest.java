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

import dev.ime.application.exception.EntityAssociatedException;
import dev.ime.application.usecases.DeleteAreaCommand;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.command.Command;
import dev.ime.domain.event.Event;
import dev.ime.domain.model.Area;
import dev.ime.domain.ports.outbound.CreatureEntityCheckerPort;
import dev.ime.domain.ports.outbound.EventWriteRepositoryPort;
import dev.ime.domain.ports.outbound.ReadRepositoryPort;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class DeleteAreaCommandHandlerTest {

	@Mock
	private EventWriteRepositoryPort eventWriteRepositoryPort;

	@Mock
	private ObjectMapper objectMapper;

	@Mock
	private ReadRepositoryPort<Area> readRepositoryPort;

	@Mock
	private CreatureEntityCheckerPort entityCreatureCheckerAdapter;	

	@InjectMocks
	private DeleteAreaCommandHandler deleteAreaCommandHandler;	
	
	private Event event;
	private DeleteAreaCommand deleteAreaCommand;
	
	private final UUID areaId01 = UUID.randomUUID();

	private UUID eventId = UUID.randomUUID();
	private final String eventCategory = GlobalConstants.AREA_CAT;
	private final String eventType = GlobalConstants.AREA_DELETED;
	private final Instant eventTimestamp = Instant.now();
	private Map<String, Object> eventData = new HashMap<>();	
	
	@BeforeEach
	private void setUp() {

		eventData = new HashMap<>();
		eventData.put(GlobalConstants.AREA_ID, areaId01.toString());
		
		event = new Event(
				eventId,
				eventCategory,
				eventType,
				eventTimestamp,
				eventData);
		
		deleteAreaCommand = new DeleteAreaCommand(areaId01);
			
				
	}

	@SuppressWarnings("unchecked")
	@Test
	void handle_shouldReturnEvent() {

		Mockito.when(readRepositoryPort.findById(Mockito.any(UUID.class))).thenReturn(Mono.just(new Area()));
		Mockito.when(entityCreatureCheckerAdapter.existsAnyByAreaId(Mockito.any(UUID.class))).thenReturn(Mono.just(false));
		Mockito.when(objectMapper.convertValue(Mockito.any(Command.class), Mockito.any(TypeReference.class))).thenReturn(eventData);
		Mockito.when(eventWriteRepositoryPort.save(Mockito.any(Event.class))).thenReturn(Mono.just(event));
		
		StepVerifier
		.create(deleteAreaCommandHandler.handle(deleteAreaCommand))
		.expectNext(event)
		.verifyComplete();
		
		Mockito.verify(readRepositoryPort).findById(Mockito.any(UUID.class));
		Mockito.verify(entityCreatureCheckerAdapter).existsAnyByAreaId(Mockito.any(UUID.class));
		Mockito.verify(objectMapper).convertValue(Mockito.any(Command.class), Mockito.any(TypeReference.class));
		Mockito.verify(eventWriteRepositoryPort).save(Mockito.any(Event.class));
		
	}

	@SuppressWarnings("unchecked")
	@Test
	void handle_WithErrorOnRSocket_shouldReturnEvent() {

		Mockito.when(readRepositoryPort.findById(Mockito.any(UUID.class))).thenReturn(Mono.just(new Area()));
		Mockito.when(entityCreatureCheckerAdapter.existsAnyByAreaId(Mockito.any(UUID.class))).thenReturn(Mono.error(new Exception())).thenReturn(Mono.just(false));
		Mockito.when(objectMapper.convertValue(Mockito.any(Command.class), Mockito.any(TypeReference.class))).thenReturn(eventData);
		Mockito.when(eventWriteRepositoryPort.save(Mockito.any(Event.class))).thenReturn(Mono.just(event));
		
		StepVerifier
		.create(deleteAreaCommandHandler.handle(deleteAreaCommand))
		.expectNext(event)
		.verifyComplete();
		
		Mockito.verify(readRepositoryPort).findById(Mockito.any(UUID.class));
		Mockito.verify(entityCreatureCheckerAdapter, Mockito.times(2)).existsAnyByAreaId(Mockito.any(UUID.class));
		Mockito.verify(objectMapper).convertValue(Mockito.any(Command.class), Mockito.any(TypeReference.class));
		Mockito.verify(eventWriteRepositoryPort).save(Mockito.any(Event.class));
		
	}
	
	@Test
	void handle_WithEntitiesAssociated_shouldPropagateError() {

		Mockito.when(readRepositoryPort.findById(Mockito.any(UUID.class))).thenReturn(Mono.just(new Area()));
		Mockito.when(entityCreatureCheckerAdapter.existsAnyByAreaId(Mockito.any(UUID.class))).thenReturn(Mono.just(true));
		
		StepVerifier
		.create(deleteAreaCommandHandler.handle(deleteAreaCommand))
		.expectError(EntityAssociatedException.class)
		.verify();
		
		Mockito.verify(readRepositoryPort).findById(Mockito.any(UUID.class));
		Mockito.verify(entityCreatureCheckerAdapter).existsAnyByAreaId(Mockito.any(UUID.class));
		
	}
	
}
