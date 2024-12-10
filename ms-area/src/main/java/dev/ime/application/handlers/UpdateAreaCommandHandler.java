package dev.ime.application.handlers;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.ime.application.exception.ResourceNotFoundException;
import dev.ime.application.exception.UniqueValueException;
import dev.ime.application.usecases.UpdateAreaCommand;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.command.Command;
import dev.ime.domain.command.CommandHandler;
import dev.ime.domain.event.Event;
import dev.ime.domain.model.Area;
import dev.ime.domain.ports.outbound.EventWriteRepositoryPort;
import dev.ime.domain.ports.outbound.ReadRepositoryPort;
import reactor.core.publisher.Mono;

@Component
public class UpdateAreaCommandHandler implements CommandHandler{

	private final EventWriteRepositoryPort eventWriteRepositoryPort;
	private final ObjectMapper objectMapper;
	private final ReadRepositoryPort<Area> readRepositoryPort;
	
	public UpdateAreaCommandHandler(EventWriteRepositoryPort eventWriteRepositoryPort, ObjectMapper objectMapper,
			ReadRepositoryPort<Area> readRepositoryPort) {
		super();
		this.eventWriteRepositoryPort = eventWriteRepositoryPort;
		this.objectMapper = objectMapper;
		this.readRepositoryPort = readRepositoryPort;
	}

	@Override
	public Mono<Event> handle(Command command) {

		return Mono.justOrEmpty(command)
		.ofType(UpdateAreaCommand.class)
		.flatMap(this::validateIdExists)
		.flatMap(this::validateNameAlreadyUsed)
		.map(this::createEvent)
		.flatMap(eventWriteRepositoryPort::save);
		
	}

	private Mono<UpdateAreaCommand> validateIdExists(UpdateAreaCommand updateCommand){
		
		return readRepositoryPort.findById(updateCommand.areaId())
				.switchIfEmpty(Mono.error( new ResourceNotFoundException(Map.of(GlobalConstants.AREA_ID, updateCommand.areaId().toString() )) ))						
				.thenReturn(updateCommand);	
		
	}

	private Mono<UpdateAreaCommand> validateNameAlreadyUsed(UpdateAreaCommand updateCommand){
		
		return readRepositoryPort.findByName(updateCommand.areaName())
		.map(Area::getAreaId)
		.filter( idFound -> !idFound.equals(updateCommand.areaId()))
		.flatMap( idFound -> Mono.error(new UniqueValueException(Map.of(GlobalConstants.AREA_NAME, idFound.toString()))))
        .thenReturn(updateCommand);		
		
	}	

	private Event createEvent(Command command) {		
		
		return new Event(
				GlobalConstants.AREA_CAT,
				GlobalConstants.AREA_UPDATED,
				createEventData(command)
				);
		
	}
	
	private Map<String, Object> createEventData(Command command) {
		
		UpdateAreaCommand createCommand = (UpdateAreaCommand) command;
		return objectMapper.convertValue(createCommand, new TypeReference<Map<String, Object>>() {});

	}
 
}
