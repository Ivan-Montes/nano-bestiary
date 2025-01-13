package dev.ime.infrastructure.repository;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;

import dev.ime.infrastructure.entity.CreatureJpaEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CreatureReadRepository  extends ReactiveCrudRepository<CreatureJpaEntity, UUID>, ReactiveSortingRepository<CreatureJpaEntity, UUID>{

	Mono<CreatureJpaEntity> findByCreatureName(String name);
    Flux<CreatureJpaEntity> findAllBy(Pageable pageable);
    Flux<CreatureJpaEntity> findByAreaId(UUID areaId);
    
}
