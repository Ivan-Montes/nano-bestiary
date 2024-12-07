package dev.ime.domain.ports.outbound;

import java.util.UUID;

import reactor.core.publisher.Mono;

public interface EntityCheckerPort {

	Mono<Boolean> existsById(UUID id);
	
}
