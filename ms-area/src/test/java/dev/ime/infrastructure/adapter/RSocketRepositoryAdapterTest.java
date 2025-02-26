package dev.ime.infrastructure.adapter;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.ime.infrastructure.entity.AreaJpaEntity;
import dev.ime.infrastructure.repository.AreaReadRepository;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class RSocketRepositoryAdapterTest {

	@Mock
	private AreaReadRepository areaReadRepository;

	@InjectMocks
	private RSocketRepositoryAdapter rSocketRepositoryAdapter;
	
	private final UUID areaId01 = UUID.randomUUID();	

	@Test
	void existsById_shouldReturnTrue() {
		
		Mockito.when(areaReadRepository.findById(Mockito.any(UUID.class))).thenReturn(Mono.just(new AreaJpaEntity()));
		
		StepVerifier
		.create(rSocketRepositoryAdapter.existsById(areaId01))
		.expectNext(true)
        .verifyComplete();
		
		Mockito.verify(areaReadRepository).findById(Mockito.any(UUID.class));

	}

}
