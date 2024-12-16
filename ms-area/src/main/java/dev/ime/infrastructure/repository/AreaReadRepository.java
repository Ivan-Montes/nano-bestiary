package dev.ime.infrastructure.repository;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;

import dev.ime.infrastructure.entity.AreaJpaEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AreaReadRepository extends ReactiveCrudRepository<AreaJpaEntity, UUID>, ReactiveSortingRepository<AreaJpaEntity, UUID>{

	Mono<AreaJpaEntity> findByAreaName(String name);
    Flux<AreaJpaEntity> findAllBy(Pageable pageable);

}
