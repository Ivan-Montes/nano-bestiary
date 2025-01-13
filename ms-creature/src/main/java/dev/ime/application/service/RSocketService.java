package dev.ime.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import dev.ime.domain.ports.inbound.RSocketServicePort;
import dev.ime.domain.ports.outbound.RSocketRepositoryPort;
import reactor.core.publisher.Mono;

@Service
public class RSocketService implements RSocketServicePort{

	private final RSocketRepositoryPort rSocketRepositoryPort;

	public RSocketService(RSocketRepositoryPort rSocketRepositoryPort) {
		super();
		this.rSocketRepositoryPort = rSocketRepositoryPort;
	}

	@Override
	public Mono<Boolean> existsAnyByAreaId(UUID areaId) {
		
		return rSocketRepositoryPort
				.existsAnyByAreaId(areaId);

	}

}
