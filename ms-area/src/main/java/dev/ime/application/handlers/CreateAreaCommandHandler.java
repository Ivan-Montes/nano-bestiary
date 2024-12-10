package dev.ime.application.handlers;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.ime.application.exception.UniqueValueException;
import dev.ime.application.usecases.CreateAreaCommand;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.command.Command;
import dev.ime.domain.command.CommandHandler;
import dev.ime.domain.event.Event;
import dev.ime.domain.model.Area;
import dev.ime.domain.ports.outbound.EventWriteRepositoryPort;
import dev.ime.domain.ports.outbound.ReadRepositoryPort;
import reactor.core.publisher.Mono;

@Component
public class CreateAreaCommandHandler implements CommandHandler {

	private final EventWriteRepositoryPort eventWriteRepositoryPort;
	private final ObjectMapper objectMapper;
	private final ReadRepositoryPort<Area> readRepositoryPort;
	
	public CreateAreaCommandHandler(EventWriteRepositoryPort eventWriteRepositoryPort, ObjectMapper objectMapper,
			ReadRepositoryPort<Area> readRepositoryPort) {
		super();
		this.eventWriteRepositoryPort = eventWriteRepositoryPort;
		this.objectMapper = objectMapper;
		this.readRepositoryPort = readRepositoryPort;
	}

	@Override
	public Mono<Event> handle(Command command) {

	    return Mono.justOrEmpty(command)
	        .cast(CreateAreaCommand.class)
	        .flatMap(this::validateNameAlreadyUsed)
	        .map(this::createEvent)
			.flatMap(eventWriteRepositoryPort::save);
	    
	}
	
	private Mono<CreateAreaCommand> validateNameAlreadyUsed(CreateAreaCommand createCommand){
		
		return readRepositoryPort.findByName(createCommand.areaName())
                .flatMap(item -> Mono.error(new UniqueValueException(Map.of(GlobalConstants.AREA_NAME, createCommand.areaName()))))
                .thenReturn(createCommand);
		
	}	

	private Event createEvent(Command command) {		
		
		return new Event(
				GlobalConstants.AREA_CAT,
				GlobalConstants.AREA_CREATED,
				createEventData(command)
				);
		
	}
	
	private Map<String, Object> createEventData(Command command) {
		
		CreateAreaCommand createCommand = (CreateAreaCommand) command;
		return objectMapper.convertValue(createCommand, new TypeReference<Map<String, Object>>() {});

	}
	
}
