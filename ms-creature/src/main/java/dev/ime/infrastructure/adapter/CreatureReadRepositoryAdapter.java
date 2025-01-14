package dev.ime.infrastructure.adapter;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import dev.ime.config.CreatureMapper;
import dev.ime.domain.model.Creature;
import dev.ime.domain.ports.outbound.ReadRepositoryPort;
import dev.ime.infrastructure.repository.CreatureReadRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class CreatureReadRepositoryAdapter implements ReadRepositoryPort<Creature>{

	private final CreatureReadRepository creatureReadRepository;
	private final CreatureMapper mapper;
	
	public CreatureReadRepositoryAdapter(CreatureReadRepository creatureReadRepository, CreatureMapper mapper) {
		super();
		this.creatureReadRepository = creatureReadRepository;
		this.mapper = mapper;
	}

	@Override
	public Flux<Creature> findAll(Pageable pageable) {

		return creatureReadRepository
				.findAllBy(pageable)
				.map(mapper::fromJpaToDomain);
		
	}

	@Override
	public Mono<Creature> findById(UUID id) {

		return creatureReadRepository
				.findById(id)
				.map(mapper::fromJpaToDomain);				
				
	}

	@Override
	public Mono<Creature> findByName(String name) {

		return creatureReadRepository
				.findByCreatureName(name)
				.map(mapper::fromJpaToDomain);
		
	}
	
}
