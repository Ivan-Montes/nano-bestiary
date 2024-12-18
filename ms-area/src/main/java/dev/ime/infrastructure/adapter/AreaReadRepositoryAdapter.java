package dev.ime.infrastructure.adapter;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import dev.ime.config.AreaMapper;
import dev.ime.domain.model.Area;
import dev.ime.domain.ports.outbound.ReadRepositoryPort;
import dev.ime.infrastructure.repository.AreaReadRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class AreaReadRepositoryAdapter implements ReadRepositoryPort<Area>{

	private final AreaReadRepository areaReadRepository;
	private final AreaMapper mapper;
	
	public AreaReadRepositoryAdapter(AreaReadRepository areaReadRepository, AreaMapper mapper) {
		super();
		this.areaReadRepository = areaReadRepository;
		this.mapper = mapper;
	}

	@Override
	public Flux<Area> findAll(Pageable pageable) {
		
		return areaReadRepository
				.findAllBy(pageable)
				.map(mapper::fromJpaToDomain);
		
	}

	@Override
	public Mono<Area> findById(UUID id) {

		return areaReadRepository
				.findById(id)
				.map(mapper::fromJpaToDomain);				
				
	}

	@Override
	public Mono<Area> findByName(String name) {

		return areaReadRepository
				.findByAreaName(name)
				.map(mapper::fromJpaToDomain);
		
	}

}
