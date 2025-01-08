package dev.ime.domain.ports.outbound;


import dev.ime.domain.event.Event;
import reactor.core.publisher.Mono;

public interface BaseProjectorPort {

	Mono<Void> create(Event event);
	Mono<Void> deleteById(Event event);
	
}
