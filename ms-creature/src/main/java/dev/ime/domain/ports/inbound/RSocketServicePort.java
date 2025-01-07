package dev.ime.domain.ports.inbound;

import java.util.UUID;

import reactor.core.publisher.Mono;

public interface RSocketServicePort {

	Mono<Boolean> existsAnyByAreaId(UUID areaId);
	
}
