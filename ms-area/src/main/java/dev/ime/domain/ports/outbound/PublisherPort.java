package dev.ime.domain.ports.outbound;

import dev.ime.domain.event.Event;
import reactor.core.publisher.Mono;

public interface PublisherPort {

	Mono<Void> publishEvent(Event event);
	
}
