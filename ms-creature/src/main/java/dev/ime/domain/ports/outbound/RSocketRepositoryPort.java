package dev.ime.domain.ports.outbound;

import java.util.UUID;

import reactor.core.publisher.Mono;

public interface RSocketRepositoryPort {

	Mono<Boolean> existsAnyByAreaId(UUID areaId);

}
