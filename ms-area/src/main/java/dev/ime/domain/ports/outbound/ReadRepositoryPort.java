package dev.ime.domain.ports.outbound;

import java.util.UUID;

import org.springframework.data.domain.Pageable;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReadRepositoryPort<T> {

	Flux<T> findAll(Pageable pageable);
	Mono<T> findById(UUID id);
	Mono<T> findByName(String name);
	
}
