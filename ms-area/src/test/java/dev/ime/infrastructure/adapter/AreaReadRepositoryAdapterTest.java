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

import dev.ime.config.AreaMapper;
import dev.ime.domain.model.Area;
import dev.ime.infrastructure.entity.AreaJpaEntity;
import dev.ime.infrastructure.repository.AreaReadRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class AreaReadRepositoryAdapterTest {

	@Mock
	private AreaReadRepository areaReadRepository;
	@Mock
	private AreaMapper mapper;

	@InjectMocks
	private AreaReadRepositoryAdapter areaReadRepositoryAdapter;	

	private Area area01;
	private Area area02;
	private AreaJpaEntity areaJpaEntity01;
	private AreaJpaEntity areaJpaEntity02;
	private final UUID areaId01 = UUID.randomUUID();
	private final UUID areaId02 = UUID.randomUUID();
	private final String areaName01 = "";
	private final String areaName02 = "";
	private final PageRequest pageRequest = PageRequest.of(0, 100);

	@BeforeEach
	private void setUp() {	
		
		area01 = new Area(areaId01, areaName01);
		area02 = new Area(areaId02, areaName02);
		areaJpaEntity01 = new AreaJpaEntity(areaId01, areaName01);
		areaJpaEntity02 = new AreaJpaEntity();
		areaJpaEntity02.setAreaId(areaId02);
		areaJpaEntity02.setAreaName(areaName02);
		
	}

	@Test
	void findAll_shouldReturnFluxArea() {
		
		Mockito.when(areaReadRepository.findAllBy(Mockito.any(Pageable.class))).thenReturn(Flux.just(areaJpaEntity01,areaJpaEntity02));
		Mockito.when(mapper.fromJpaToDomain(Mockito.any(AreaJpaEntity.class))).thenReturn(area01, area02);
		
		StepVerifier
		.create(areaReadRepositoryAdapter.findAll(pageRequest))
		.expectNext(area01)
		.expectNext(area02)
		.verifyComplete();

		Mockito.verify(areaReadRepository).findAllBy(Mockito.any(Pageable.class));
		Mockito.verify(mapper,Mockito.times(2)).fromJpaToDomain(Mockito.any(AreaJpaEntity.class));
		
	}

	@Test
	void findById_shouldReturnMonoArea() {
		
		Mockito.when(areaReadRepository.findById(Mockito.any(UUID.class))).thenReturn(Mono.just(areaJpaEntity01));
		Mockito.when(mapper.fromJpaToDomain(Mockito.any(AreaJpaEntity.class))).thenReturn(area01);
		
		StepVerifier
		.create(areaReadRepositoryAdapter.findById(areaId01))
		.assertNext( areaFound -> {
			org.junit.jupiter.api.Assertions.assertAll(
					()->Assertions.assertThat(areaFound).isEqualTo(area01),
					()->Assertions.assertThat(areaFound).isNotEqualTo(area02),
        			()->Assertions.assertThat(areaFound).hasSameHashCodeAs(area01)
        			);
		})
		.verifyComplete();

		Mockito.verify(areaReadRepository).findById(Mockito.any(UUID.class));
		Mockito.verify(mapper).fromJpaToDomain(Mockito.any(AreaJpaEntity.class));
		
	}

	@Test
	void findByName_shouldReturnMonoArea() {
		
		Mockito.when(areaReadRepository.findByAreaName(Mockito.anyString())).thenReturn(Mono.just(areaJpaEntity01));
		Mockito.when(mapper.fromJpaToDomain(Mockito.any(AreaJpaEntity.class))).thenReturn(area01);
		
		StepVerifier
		.create(areaReadRepositoryAdapter.findByName(areaName01))
		.expectNext(area01)
		.verifyComplete();

		Mockito.verify(areaReadRepository).findByAreaName(Mockito.anyString());
		Mockito.verify(mapper).fromJpaToDomain(Mockito.any(AreaJpaEntity.class));
		
	}

}
