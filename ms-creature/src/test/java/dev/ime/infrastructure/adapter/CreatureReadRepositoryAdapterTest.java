package dev.ime.infrastructure.adapter;

import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import dev.ime.config.CreatureMapper;
import dev.ime.domain.model.Creature;
import dev.ime.infrastructure.entity.CreatureJpaEntity;
import dev.ime.infrastructure.repository.CreatureReadRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class CreatureReadRepositoryAdapterTest {

	@Mock
	private CreatureReadRepository creatureReadRepository;
	@Mock
	private CreatureMapper mapper;

	@InjectMocks
	private CreatureReadRepositoryAdapter creatureReadRepositoryAdapter;

	private Creature creature01;
	private Creature creature02;
	private CreatureJpaEntity creatureJpaEntity01;
	private CreatureJpaEntity creatureJpaEntity02;
	
	private final UUID creatureId01 = UUID.randomUUID();
	private final UUID creatureId02 = UUID.randomUUID();
	private final UUID areaId01 = UUID.randomUUID();
	private final String creatureName01 = "";
	private final String creatureName02 = "";
	private final String creatureDescription01 = "";
	private final String creatureDescription02 = "";
	private final PageRequest pageRequest = PageRequest.of(0, 100);
	
	@BeforeEach
	private void setUp() {	
		
		creature01 = new Creature(creatureId01, creatureName01, creatureDescription01, areaId01);
		creature02 = new Creature(creatureId02, creatureName02, creatureDescription02, areaId01);
		creatureJpaEntity01 = new CreatureJpaEntity(creatureId01, creatureName01, creatureDescription01, areaId01);
		creatureJpaEntity02 = new CreatureJpaEntity();
		creatureJpaEntity02.setCreatureId(creatureId01);
		creatureJpaEntity02.setCreatureName(creatureName01);
		creatureJpaEntity02.setCreatureDescription(creatureDescription01);
		creatureJpaEntity02.setAreaId(areaId01);
		
	}
	
	@Test
	void findAll_shouldReturnFluxArea() {

		Mockito.when(creatureReadRepository.findAllBy(Mockito.any(Pageable.class))).thenReturn(Flux.just(creatureJpaEntity01, creatureJpaEntity02));
		Mockito.when(mapper.fromJpaToDomain(Mockito.any(CreatureJpaEntity.class))).thenReturn(creature01,creature02);
	
		StepVerifier
		.create(creatureReadRepositoryAdapter.findAll(pageRequest))
		.expectNext(creature01, creature02)
		.verifyComplete();
		
		Mockito.verify(creatureReadRepository).findAllBy(Mockito.any(Pageable.class));
		Mockito.verify(mapper, Mockito.times(2)).fromJpaToDomain(Mockito.any(CreatureJpaEntity.class));
		
	}

	@Test
	void findById_shouldReturnMonoArea() {
		
		Mockito.when(creatureReadRepository.findById(Mockito.any(UUID.class))).thenReturn(Mono.just(creatureJpaEntity01));
		Mockito.when(mapper.fromJpaToDomain(Mockito.any(CreatureJpaEntity.class))).thenReturn(creature01);
		
		StepVerifier
		.create(creatureReadRepositoryAdapter.findById(creatureId01))
		.assertNext( areaFound -> {
			org.junit.jupiter.api.Assertions.assertAll(
					()->Assertions.assertThat(areaFound).isEqualTo(creature01),
					()->Assertions.assertThat(areaFound).isNotEqualTo(creature02),
        			()->Assertions.assertThat(areaFound).hasSameHashCodeAs(creature01)
        			);
		})
		.verifyComplete();

		Mockito.verify(creatureReadRepository).findById(Mockito.any(UUID.class));
		Mockito.verify(mapper).fromJpaToDomain(Mockito.any(CreatureJpaEntity.class));
		
	}

	@Test
	void findByName_shouldReturnMonoArea() {
		
		Mockito.when(creatureReadRepository.findByCreatureName(Mockito.anyString())).thenReturn(Mono.just(creatureJpaEntity01));
		Mockito.when(mapper.fromJpaToDomain(Mockito.any(CreatureJpaEntity.class))).thenReturn(creature01);
		
		StepVerifier
		.create(creatureReadRepositoryAdapter.findByName(creatureName01))
		.expectNext(creature01)
		.verifyComplete();

		Mockito.verify(creatureReadRepository).findByCreatureName(Mockito.anyString());
		Mockito.verify(mapper).fromJpaToDomain(Mockito.any(CreatureJpaEntity.class));
		
	}
	
}
