package dev.ime.application.handlers;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.ime.application.exception.EntityAssociatedException;
import dev.ime.application.exception.ResourceNotFoundException;
import dev.ime.application.usecases.DeleteAreaCommand;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.command.Command;
import dev.ime.domain.command.CommandHandler;
import dev.ime.domain.event.Event;
import dev.ime.domain.model.Area;
import dev.ime.domain.ports.outbound.EventWriteRepositoryPort;
import dev.ime.domain.ports.outbound.ReadRepositoryPort;
import dev.ime.domain.ports.outbound.CreatureEntityCheckerPort;
import reactor.core.publisher.Mono;

@Component
public class DeleteAreaCommandHandler implements CommandHandler{

	private final EventWriteRepositoryPort eventWriteRepositoryPort;
	private final ObjectMapper objectMapper;
	private final ReadRepositoryPort<Area> readRepositoryPort;
	private final CreatureEntityCheckerPort redisCreatureCheckerAdapter;	
	private final CreatureEntityCheckerPort rSocketCreatureCheckerAdapter;

	public DeleteAreaCommandHandler(EventWriteRepositoryPort eventWriteRepositoryPort, ObjectMapper objectMapper,
			ReadRepositoryPort<Area> readRepositoryPort, @Qualifier("redisCreatureCheckerAdapter")CreatureEntityCheckerPort redisCreatureCheckerAdapter,
			@Qualifier("rSocketCreatureCheckerAdapter")CreatureEntityCheckerPort rSocketCreatureCheckerAdapter) {
		super();
		this.eventWriteRepositoryPort = eventWriteRepositoryPort;
		this.objectMapper = objectMapper;
		this.readRepositoryPort = readRepositoryPort;
		this.redisCreatureCheckerAdapter = redisCreatureCheckerAdapter;
		this.rSocketCreatureCheckerAdapter = rSocketCreatureCheckerAdapter;
	}

	@Override
	public Mono<Event> handle(Command command) {

		return Mono.justOrEmpty(command)
		.cast(DeleteAreaCommand.class)
		.flatMap(this::validateIdExists)
		.flatMap(this::validateExistsAnyByAreaId)
		.map(this::createEvent)
		.flatMap(eventWriteRepositoryPort::save);		
		
	}

	private Mono<DeleteAreaCommand> validateIdExists(DeleteAreaCommand deleteCommand){
	
		return readRepositoryPort.findById(deleteCommand.areaId())
				.switchIfEmpty(
						Mono.error(new ResourceNotFoundException(
												Map.of(
													GlobalConstants.AREA_ID, 
													deleteCommand.areaId().toString()
													))))
				.thenReturn(deleteCommand);	
		
	}
	
	private Mono<DeleteAreaCommand> validateExistsAnyByAreaId(DeleteAreaCommand deleteCommand){
		
		return this.consultRSocket(deleteCommand.areaId())
				.onErrorResume(throwable -> this.consultRedisCache(deleteCommand.areaId()))
				.onErrorReturn(true)
				.ofType(Boolean.class)
		        .filter( bool -> !bool )
				.switchIfEmpty( Mono.error(new EntityAssociatedException(Map.of(GlobalConstants.AREA_ID, deleteCommand.areaId().toString()))))
				.thenReturn(deleteCommand);
		
	}

	private Mono<Boolean> consultRSocket(UUID areaId){
		
		return rSocketCreatureCheckerAdapter
				.existsAnyByAreaId(areaId)				
    			.ofType(Boolean.class);
		
	}
		
	private Mono<Boolean> consultRedisCache(UUID areaId){
		
		return redisCreatureCheckerAdapter
    			.existsAnyByAreaId(areaId)
    			.ofType(Boolean.class);
		
	}
	
	private Event createEvent(Command command) {
		
		return new Event(
				GlobalConstants.AREA_CAT,
				GlobalConstants.AREA_DELETED,
				createEventData(command)
				);
		
	}

	private Map<String, Object> createEventData(Command command) {
		
		DeleteAreaCommand deletePositionCommand = (DeleteAreaCommand) command;
		return objectMapper.convertValue(deletePositionCommand, new TypeReference<Map<String, Object>>() {});
	
	}
	
}
