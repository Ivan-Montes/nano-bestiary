package dev.ime.infrastructure.adapter;

import org.springframework.stereotype.Repository;

import dev.ime.config.CreatureMapper;
import dev.ime.domain.event.Event;
import dev.ime.domain.ports.outbound.EventWriteRepositoryPort;
import dev.ime.infrastructure.repository.EventMongoWriteRepository;
import reactor.core.publisher.Mono;

@Repository
public class EventMongoWriteRepositoryAdapter implements EventWriteRepositoryPort{

	private final EventMongoWriteRepository eventMongoWriteRepository;
	private final CreatureMapper mapper;
	
	public EventMongoWriteRepositoryAdapter(EventMongoWriteRepository eventMongoWriteRepository,
			CreatureMapper mapper) {
		super();
		this.eventMongoWriteRepository = eventMongoWriteRepository;
		this.mapper = mapper;
	}

	@Override
	public Mono<Event> save(Event event) {
		
		return Mono.fromSupplier( () -> mapper.fromEventDomainToEventMongo(event))
				.flatMap(eventMongoWriteRepository::save)
				.map(mapper::fromEventMongoToEventDomain);	
		
	}

}
