package dev.ime.application.handlers;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.ime.application.exception.ResourceNotFoundException;
import dev.ime.application.usecases.DeleteCreatureCommand;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.command.Command;
import dev.ime.domain.command.CommandHandler;
import dev.ime.domain.event.Event;
import dev.ime.domain.model.Creature;
import dev.ime.domain.ports.outbound.EventWriteRepositoryPort;
import dev.ime.domain.ports.outbound.ReadRepositoryPort;
import reactor.core.publisher.Mono;

@Component
public class DeleteCreatureCommandHandler implements CommandHandler {

	private final EventWriteRepositoryPort eventWriteRepositoryPort;
	private final ObjectMapper objectMapper;
	private final ReadRepositoryPort<Creature> readRepositoryPort;
	
	public DeleteCreatureCommandHandler(EventWriteRepositoryPort eventWriteRepositoryPort, ObjectMapper objectMapper,
			ReadRepositoryPort<Creature> readRepositoryPort) {
		super();
		this.eventWriteRepositoryPort = eventWriteRepositoryPort;
		this.objectMapper = objectMapper;
		this.readRepositoryPort = readRepositoryPort;
	}

	@Override
	public Mono<Event> handle(Command command) {

		return Mono.justOrEmpty(command)
		.cast(DeleteCreatureCommand.class)
		.flatMap(this::validateIdExists)
		.map(this::createEvent)
		.flatMap(eventWriteRepositoryPort::save);		
		
	}

	private Mono<DeleteCreatureCommand> validateIdExists(DeleteCreatureCommand deleteCommand){
		
		return readRepositoryPort.findById(deleteCommand.creatureId())
				.switchIfEmpty(Mono.error(new ResourceNotFoundException(Map.of(GlobalConstants.CREATURE_ID,deleteCommand.creatureId().toString()))))
				.thenReturn(deleteCommand);	
		
	}
	
	private Event createEvent(Command command) {
		
		return new Event(
				GlobalConstants.CREATURE_CAT,
				GlobalConstants.CREATURE_DELETED,
				createEventData(command)
				);
	}

	private Map<String, Object> createEventData(Command command) {
		
		DeleteCreatureCommand deletePositionCommand = (DeleteCreatureCommand) command;
		return objectMapper.convertValue(deletePositionCommand, new TypeReference<Map<String, Object>>() {});
	
	}
	
}
