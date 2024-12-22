package dev.ime.infrastructure.adapter;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import dev.ime.domain.ports.outbound.RSocketRepositoryPort;
import dev.ime.infrastructure.repository.AreaReadRepository;
import reactor.core.publisher.Mono;

@Repository
public class RSocketRepositoryAdapter implements RSocketRepositoryPort{

	private final AreaReadRepository areaReadRepository;

	public RSocketRepositoryAdapter(AreaReadRepository areaReadRepository) {
		super();
		this.areaReadRepository = areaReadRepository;
	}

	@Override
	public Mono<Boolean> existsById(UUID id) {

		return areaReadRepository
		.findById(id)
		.hasElement();

	}
	
}
