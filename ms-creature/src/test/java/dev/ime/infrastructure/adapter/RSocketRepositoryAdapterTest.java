package dev.ime.infrastructure.adapter;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.ime.infrastructure.entity.CreatureJpaEntity;
import dev.ime.infrastructure.repository.CreatureReadRepository;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class RSocketRepositoryAdapterTest {

	@Mock
	private CreatureReadRepository creatureReadRepository;

	@InjectMocks
	private RSocketRepositoryAdapter rSocketRepositoryAdapter;
	
	private final UUID areaId01 = UUID.randomUUID();	

	@Test
	void existsAnyByAreaId_shouldReturnTrue() {

		Mockito.when(creatureReadRepository.findByAreaId(Mockito.any(UUID.class))).thenReturn(Flux.just(new CreatureJpaEntity()));
	
		StepVerifier
		.create(rSocketRepositoryAdapter.existsAnyByAreaId(areaId01))
		.expectNext(true)
		.verifyComplete();
		
		Mockito.verify(creatureReadRepository).findByAreaId(Mockito.any(UUID.class));

	}

}
