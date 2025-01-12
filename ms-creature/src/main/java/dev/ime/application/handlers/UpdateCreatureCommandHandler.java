package dev.ime.application.handlers;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.ime.application.exception.ResourceNotFoundException;
import dev.ime.application.exception.UniqueValueException;
import dev.ime.application.usecases.UpdateCreatureCommand;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.command.Command;
import dev.ime.domain.command.CommandHandler;
import dev.ime.domain.event.Event;
import dev.ime.domain.model.Creature;
import dev.ime.domain.ports.outbound.EventWriteRepositoryPort;
import dev.ime.domain.ports.outbound.ReadRepositoryPort;
import dev.ime.domain.ports.outbound.EntityCheckerPort;
import reactor.core.publisher.Mono;

@Component
public class UpdateCreatureCommandHandler implements CommandHandler {

	private final EventWriteRepositoryPort eventWriteRepositoryPort;
	private final ObjectMapper objectMapper;
	private final ReadRepositoryPort<Creature> readRepositoryPort;
	private final EntityCheckerPort redisAreaCheckerAdapter;
	private final EntityCheckerPort rSocketAreaCheckerAdapter;

	public UpdateCreatureCommandHandler(EventWriteRepositoryPort eventWriteRepositoryPort, ObjectMapper objectMapper,
			ReadRepositoryPort<Creature> readRepositoryPort, @Qualifier("redisAreaCheckerAdapter")EntityCheckerPort redisAreaCheckerAdapter,
			@Qualifier("rSocketAreaCheckerAdapter")EntityCheckerPort rSocketAreaCheckerAdapter) {
		super();
		this.eventWriteRepositoryPort = eventWriteRepositoryPort;
		this.objectMapper = objectMapper;
		this.readRepositoryPort = readRepositoryPort;
		this.redisAreaCheckerAdapter = redisAreaCheckerAdapter;
		this.rSocketAreaCheckerAdapter = rSocketAreaCheckerAdapter;
	}

	@Override
	public Mono<Event> handle(Command command) {

		return Mono.justOrEmpty(command)
		.ofType(UpdateCreatureCommand.class)
		.flatMap(this::validateIdExists)
		.flatMap(this::validateNameAlreadyUsed)
        .flatMap(this::validateAreaIdExists)
		.map(this::createEvent)
		.flatMap(eventWriteRepositoryPort::save);
		
	}

	private Mono<UpdateCreatureCommand> validateIdExists(UpdateCreatureCommand updateCommand){
		
		return readRepositoryPort.findById(updateCommand.creatureId())
				.switchIfEmpty(Mono.error( new ResourceNotFoundException(Map.of(GlobalConstants.CREATURE_ID, updateCommand.creatureId().toString() )) ))						
				.thenReturn(updateCommand);	
		
	}

	private Mono<UpdateCreatureCommand> validateNameAlreadyUsed(UpdateCreatureCommand updateCommand){
		
		return readRepositoryPort.findByName(updateCommand.creatureName())
		.map(Creature::getCreatureId)
		.filter( idFound -> !idFound.equals(updateCommand.creatureId()))
		.flatMap( idFound -> Mono.error(new UniqueValueException(Map.of(GlobalConstants.CREATURE_NAME, idFound.toString()))))
        .thenReturn(updateCommand);		
		
	}	

	private Mono<UpdateCreatureCommand> validateAreaIdExists(UpdateCreatureCommand updateCommand){
		
		return this.consultRSocket(updateCommand.areaId())
        .onErrorResume(throwable -> this.consultRedisCache(updateCommand.areaId()))
		.ofType(Boolean.class)
        .filter(Boolean::booleanValue)
		.switchIfEmpty( Mono.error(new ResourceNotFoundException(Map.of(GlobalConstants.AREA_ID, updateCommand.areaId().toString()))))
		.thenReturn(updateCommand);					
		
	}

	private Mono<Boolean> consultRSocket(UUID areaId){
		
		return rSocketAreaCheckerAdapter
				.existsById(areaId)				
    			.ofType(Boolean.class);
		
	}
	
	private Mono<Boolean> consultRedisCache(UUID id){
		
		return redisAreaCheckerAdapter
    			.existsById(id)
    			.ofType(Boolean.class)
    			.filter(Boolean::booleanValue)
    			.switchIfEmpty(Mono.just(false))
    			.onErrorReturn(false);
		
	}
	
	private Event createEvent(Command command) {		
		
		return new Event(
				GlobalConstants.CREATURE_CAT,
				GlobalConstants.CREATURE_UPDATED,
				createEventData(command)
				);
		
	}

	private Map<String, Object> createEventData(Command command) {
		
		UpdateCreatureCommand createCommand = (UpdateCreatureCommand) command;
		return objectMapper.convertValue(createCommand, new TypeReference<Map<String, Object>>() {});

	}
 
}
