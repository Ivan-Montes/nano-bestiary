package dev.ime.domain.ports.inbound;


import reactor.core.publisher.Mono;

public interface RSocketControllerPort {

	Mono<Boolean> existsById(String id);
	
}
