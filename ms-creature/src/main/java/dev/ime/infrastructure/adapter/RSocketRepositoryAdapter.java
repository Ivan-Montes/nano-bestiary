package dev.ime.infrastructure.adapter;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import dev.ime.domain.ports.outbound.RSocketRepositoryPort;
import dev.ime.infrastructure.repository.CreatureReadRepository;
import reactor.core.publisher.Mono;

@Repository
public class RSocketRepositoryAdapter implements RSocketRepositoryPort{

	private final CreatureReadRepository creatureReadRepository;

	public RSocketRepositoryAdapter(CreatureReadRepository creatureReadRepository) {
		super();
		this.creatureReadRepository = creatureReadRepository;
	}

	@Override
	public Mono<Boolean> existsAnyByAreaId(UUID areaId) {
		
		return creatureReadRepository
				.findByAreaId(areaId)
				.hasElements();
		
	}
	
}
