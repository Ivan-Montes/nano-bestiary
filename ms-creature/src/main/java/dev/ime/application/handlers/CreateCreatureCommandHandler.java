package dev.ime.application.handlers;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.ime.application.exception.ResourceNotFoundException;
import dev.ime.application.exception.UniqueValueException;
import dev.ime.application.usecases.CreateCreatureCommand;
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
public class CreateCreatureCommandHandler implements CommandHandler {

	private final EventWriteRepositoryPort eventWriteRepositoryPort;
	private final ObjectMapper objectMapper;
	private final ReadRepositoryPort<Creature> readRepositoryPort;
	private final EntityCheckerPort redisAreaCheckerAdapter;
	private final EntityCheckerPort rSocketAreaCheckerAdapter;

	public CreateCreatureCommandHandler(EventWriteRepositoryPort eventWriteRepositoryPort, ObjectMapper objectMapper,
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
	        .cast(CreateCreatureCommand.class)
	        .flatMap(this::validateNameAlreadyUsed)
	        .flatMap(this::validateAreaIdExists)
	        .map(this::createEvent)
			.flatMap(eventWriteRepositoryPort::save);
	    
	}

	private Mono<CreateCreatureCommand> validateNameAlreadyUsed(CreateCreatureCommand createCommand){
		
		return readRepositoryPort.findByName(createCommand.creatureName())
                .flatMap(item -> Mono.error(new UniqueValueException(Map.of(GlobalConstants.CREATURE_NAME, createCommand.creatureName()))))
                .thenReturn(createCommand);
		
	}
	
	private Mono<CreateCreatureCommand> validateAreaIdExists(CreateCreatureCommand createCommand){
		
		return this.consultRSocket(createCommand.areaId())
				.onErrorResume(throwable -> this.consultRedisCache(createCommand.areaId()))
				.ofType(Boolean.class)
		        .filter(Boolean::booleanValue)
				.switchIfEmpty( Mono.error(new ResourceNotFoundException(Map.of(GlobalConstants.AREA_ID, createCommand.areaId().toString()))))
				.thenReturn(createCommand);				
		
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
				GlobalConstants.CREATURE_CREATED,
				createEventData(command)
				);
		
	}
	
	private Map<String, Object> createEventData(Command command) {
		
		CreateCreatureCommand createCommand = (CreateCreatureCommand) command;
		return objectMapper.convertValue(createCommand, new TypeReference<Map<String, Object>>() {});

	}
	
}
