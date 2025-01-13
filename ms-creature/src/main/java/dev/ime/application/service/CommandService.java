package dev.ime.application.service;

import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import dev.ime.application.dispatcher.CommandDispatcher;
import dev.ime.application.dto.CreatureDto;
import dev.ime.application.exception.InvalidUUIDException;
import dev.ime.application.exception.ResourceNotFoundException;
import dev.ime.application.usecases.CreateCreatureCommand;
import dev.ime.application.usecases.DeleteCreatureCommand;
import dev.ime.application.usecases.UpdateCreatureCommand;
import dev.ime.application.utils.ReactiveLoggerUtils;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.command.Command;
import dev.ime.domain.event.Event;
import dev.ime.domain.ports.inbound.CommandServicePort;
import dev.ime.domain.ports.outbound.PublisherPort;
import reactor.core.publisher.Mono;

@Service
public class CommandService implements CommandServicePort<CreatureDto>{

	private final ReactiveLoggerUtils reactiveLoggerUtils;
	private final CommandDispatcher commandDispatcher;
	private final PublisherPort publisherPort;
	
	public CommandService(ReactiveLoggerUtils reactiveLoggerUtils, CommandDispatcher commandDispatcher, PublisherPort publisherPort) {
		super();
		this.reactiveLoggerUtils = reactiveLoggerUtils;
		this.commandDispatcher = commandDispatcher;
		this.publisherPort = publisherPort;
	}
	
	@Override
	public Mono<Event> create(CreatureDto dto) {
			
		return Mono.justOrEmpty(dto)
				.switchIfEmpty(Mono.error( new ResourceNotFoundException(Map.of(GlobalConstants.CREATURE_CAT, GlobalConstants.MSG_REQUIRED )) ))						
				.map( item -> new CreateCreatureCommand(
						UUID.randomUUID(),
						dto.creatureName(),
						dto.creatureDescription(),
						dto.areaId()
						))
				.flatMap(this::runHandler)
				.flatMap(this::processEvents);		
        
	}
	
	@Override
	public Mono<Event> update(UUID id, CreatureDto dto) {		
		
		return Mono.justOrEmpty(dto)
				.switchIfEmpty(Mono.error( new ResourceNotFoundException(Map.of(GlobalConstants.CREATURE_CAT, GlobalConstants.MSG_REQUIRED )) ))										
				.map( item -> new UpdateCreatureCommand(
						id,
						dto.creatureName(),
						dto.creatureDescription(),
						dto.areaId()))
				.flatMap(this::runHandler)
				.flatMap(this::processEvents);		
        
	}
	
	@Override
	public Mono<Event> deleteById(UUID id) {
		
		return Mono.justOrEmpty(id)
				.switchIfEmpty(Mono.error( new InvalidUUIDException(Map.of(GlobalConstants.CREATURE_ID, GlobalConstants.MSG_REQUIRED )) ))										
				.map( item -> new DeleteCreatureCommand(id))
				.flatMap(this::runHandler)
				.flatMap(this::processEvents);		
        
	}
	
	private Mono<Event> runHandler(Command command){
		
		return Mono.just(command)
				.map(commandDispatcher::getCommandHandler)
				.flatMap( handler -> handler.handle(command));
		
	}

	private Mono<Event> processEvents(Event event) {
		
	    return Mono.just(event)	    		
	        .flatMap(eventItem -> reactiveLoggerUtils.logInfoAction(this.getClass().getSimpleName(), GlobalConstants.MSG_HANDLER_OK, event.toString()).thenReturn(event))
	        .flatMap(eventItem -> publisherPort.publishEvent(event).thenReturn(event))
	        .thenReturn(event);
	
	}
	
}
