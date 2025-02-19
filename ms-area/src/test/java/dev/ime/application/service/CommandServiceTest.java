package dev.ime.application.service;


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

import dev.ime.application.dispatcher.CommandDispatcher;
import dev.ime.application.dto.AreaDto;
import dev.ime.application.utils.ReactiveLoggerUtils;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.command.Command;
import dev.ime.domain.command.CommandHandler;
import dev.ime.domain.event.Event;
import dev.ime.domain.ports.outbound.PublisherPort;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class CommandServiceTest {

	@Mock
	private ReactiveLoggerUtils reactiveLoggerUtils;
	@Mock
	private CommandDispatcher commandDispatcher;
	@Mock
	private PublisherPort publisherPort;

	@InjectMocks
	private CommandService commandService;
	
	private Event event;
	private AreaDto areaDto01;
	
	private final UUID areaId01 = UUID.randomUUID();
	private final String areaName01 = "";

	private final UUID eventId = UUID.randomUUID();
	private final String eventCategory = GlobalConstants.AREA_CAT;
	private final String eventType = GlobalConstants.AREA_DELETED;
	private final Instant eventTimestamp = Instant.now();
	private final Map<String, Object> eventData = new HashMap<>();
	
	@BeforeEach
	private void setUp() {	
		
		areaDto01 = new AreaDto(areaId01, areaName01);

		event = new Event(
				eventId,
				eventCategory,
				eventType,
				eventTimestamp,
				eventData);
	}

	@Test
	void create_shouldReturnEvent() {
		
		CommandHandler handler = Mockito.mock(CommandHandler.class);
		Mockito.when(commandDispatcher.getCommandHandler(Mockito.any(Command.class))).thenReturn(handler);
		Mockito.when(handler.handle(Mockito.any(Command.class))).thenReturn(Mono.just(event));
		Mockito.when(reactiveLoggerUtils.logInfoAction(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.empty());
		Mockito.when(publisherPort.publishEvent(Mockito.any(Event.class))).thenReturn(Mono.empty());
		
		StepVerifier
		.create(commandService.create(areaDto01))
		.expectNext(event)
		.verifyComplete();

		Mockito.verify(commandDispatcher).getCommandHandler(Mockito.any(Command.class));
		Mockito.verify(handler).handle(Mockito.any(Command.class));
		Mockito.verify(reactiveLoggerUtils).logInfoAction(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		Mockito.verify(publisherPort).publishEvent(Mockito.any(Event.class));
		
	}

	@Test
	void update_shouldReturnEvent() {
		
		CommandHandler handler = Mockito.mock(CommandHandler.class);
		Mockito.when(commandDispatcher.getCommandHandler(Mockito.any(Command.class))).thenReturn(handler);
		Mockito.when(handler.handle(Mockito.any(Command.class))).thenReturn(Mono.just(event));
		Mockito.when(reactiveLoggerUtils.logInfoAction(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.empty());
		Mockito.when(publisherPort.publishEvent(Mockito.any(Event.class))).thenReturn(Mono.empty());
		
		StepVerifier
		.create(commandService.update(areaId01,areaDto01))
		.expectNext(event)
		.verifyComplete();

		Mockito.verify(commandDispatcher).getCommandHandler(Mockito.any(Command.class));
		Mockito.verify(handler).handle(Mockito.any(Command.class));
		Mockito.verify(reactiveLoggerUtils).logInfoAction(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		Mockito.verify(publisherPort).publishEvent(Mockito.any(Event.class));
		
	}

	@Test
	void deleteById_shouldReturnEvent() {
		
		CommandHandler handler = Mockito.mock(CommandHandler.class);
		Mockito.when(commandDispatcher.getCommandHandler(Mockito.any(Command.class))).thenReturn(handler);
		Mockito.when(handler.handle(Mockito.any(Command.class))).thenReturn(Mono.just(event));
		Mockito.when(reactiveLoggerUtils.logInfoAction(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.empty());
		Mockito.when(publisherPort.publishEvent(Mockito.any(Event.class))).thenReturn(Mono.empty());
		
		StepVerifier
		.create(commandService.deleteById(areaId01))
		.expectNext(event)
		.verifyComplete();

		Mockito.verify(commandDispatcher).getCommandHandler(Mockito.any(Command.class));
		Mockito.verify(handler).handle(Mockito.any(Command.class));
		Mockito.verify(reactiveLoggerUtils).logInfoAction(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		Mockito.verify(publisherPort).publishEvent(Mockito.any(Event.class));
		
	}
	
}
