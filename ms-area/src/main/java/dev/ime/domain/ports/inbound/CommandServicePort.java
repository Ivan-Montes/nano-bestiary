package dev.ime.domain.ports.inbound;

import java.util.UUID;

import dev.ime.domain.event.Event;
import reactor.core.publisher.Mono;

public interface CommandServicePort<T> {

	Mono<Event> create(T dto);
	Mono<Event> update(UUID id, T dto);
	Mono<Event> deleteById(UUID id);
	
}
