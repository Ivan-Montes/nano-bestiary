package dev.ime.infrastructure.adapter;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveSetOperations;
import org.springframework.data.redis.core.ReactiveValueOperations;

import dev.ime.application.exception.CreateRedisEntityException;
import dev.ime.application.utils.ReactiveLoggerUtils;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.event.Event;
import dev.ime.infrastructure.entity.CreatureRedisEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class CreatureRedisProjectorAdapterTest {

	@Mock
	private ReactiveLoggerUtils reactiveLoggerUtils;
	@Mock
    private ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;

	@InjectMocks
	private CreatureRedisProjectorAdapter creatureRedisProjectorAdapter;
	
	private Event event;
	private CreatureRedisEntity creatureRedisEntity;
	
	private UUID eventId = UUID.randomUUID();
	private final String eventCategory = GlobalConstants.CREATURE_CAT;
	private final String eventType = GlobalConstants.CREATURE_DELETED;
	private final Instant eventTimestamp = Instant.now();
	private Map<String, Object> eventData = new HashMap<>();

	private final UUID areaId01 = UUID.randomUUID();
	private final UUID creatureId01 = UUID.randomUUID();
	
	@BeforeEach
	private void setUp() {	

		eventData = new HashMap<>();
		eventData.put(GlobalConstants.CREATURE_ID, creatureId01.toString());
		eventData.put(GlobalConstants.AREA_ID, areaId01.toString());

		event = new Event(
				eventId,
				eventCategory,
				eventType,
				eventTimestamp,
				eventData);
		
		creatureRedisEntity = new CreatureRedisEntity(creatureId01, areaId01);
		
	}

	@SuppressWarnings("unchecked")
	@Test
	void create_shouldInsertRedisEntity() {
		
		Mockito.when(reactiveLoggerUtils.logInfoAction(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.empty());
		Mockito.when(reactiveRedisTemplate.hasKey(Mockito.anyString())).thenReturn(Mono.just(true));
		ReactiveValueOperations<String, Object> reactiveValueOperations = Mockito.mock(ReactiveValueOperations.class);
		Mockito.when(reactiveRedisTemplate.opsForValue()).thenReturn(reactiveValueOperations);
		Mockito.when(reactiveValueOperations.get(Mockito.anyString())).thenReturn(Mono.just(creatureRedisEntity));
		ReactiveSetOperations<String, Object> reactiveSetOperations = Mockito.mock(ReactiveSetOperations.class);
		Mockito.when(reactiveRedisTemplate.opsForSet()).thenReturn(reactiveSetOperations);
		Mockito.when(reactiveSetOperations.remove(Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.just(1L));
		Mockito.when(reactiveValueOperations.set(Mockito.anyString(), Mockito.any(CreatureRedisEntity.class))).thenReturn(Mono.just(true));
		Mockito.when(reactiveSetOperations.add(Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.just(1L));
		
		StepVerifier
		.create(creatureRedisProjectorAdapter.create(event))
		.verifyComplete();
		
		Mockito.verify(reactiveRedisTemplate).hasKey(Mockito.anyString());
		Mockito.verify(reactiveRedisTemplate, Mockito.times(2)).opsForValue();
		Mockito.verify(reactiveValueOperations).get(Mockito.anyString());
		Mockito.verify(reactiveRedisTemplate, Mockito.times(2)).opsForSet();
		Mockito.verify(reactiveSetOperations).remove(Mockito.anyString(), Mockito.anyString());
		Mockito.verify(reactiveValueOperations).set(Mockito.anyString(), Mockito.any(CreatureRedisEntity.class));
		Mockito.verify(reactiveSetOperations).add(Mockito.anyString(), Mockito.anyString());
		
	}	

	@Test
	void create_WithEmptyEventData_ReturnMonoErrorOfCreateRedisException() {
		
		eventData.clear();	
		event = new Event(
				eventId,
				eventCategory,
				eventType,
				eventTimestamp,
				eventData);
		Mockito.when(reactiveLoggerUtils.logInfoAction(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.empty());

		StepVerifier
		.create(creatureRedisProjectorAdapter.create(event))
		.expectError(CreateRedisEntityException.class)
		.verify();
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	void deleteById_shouldDeleteEntity() {
		
		Mockito.when(reactiveLoggerUtils.logInfoAction(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.empty());
		Mockito.when(reactiveRedisTemplate.hasKey(Mockito.anyString())).thenReturn(Mono.just(true));
		ReactiveValueOperations<String, Object> reactiveValueOperations = Mockito.mock(ReactiveValueOperations.class);
		Mockito.when(reactiveRedisTemplate.opsForValue()).thenReturn(reactiveValueOperations);
		Mockito.when(reactiveValueOperations.get(Mockito.anyString())).thenReturn(Mono.just(creatureRedisEntity));
		ReactiveSetOperations<String, Object> reactiveSetOperations = Mockito.mock(ReactiveSetOperations.class);
		Mockito.when(reactiveRedisTemplate.opsForSet()).thenReturn(reactiveSetOperations);
		Mockito.when(reactiveSetOperations.remove(Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.just(1L));
		Mockito.when(reactiveValueOperations.delete(Mockito.anyString())).thenReturn(Mono.just(true));

		StepVerifier
		.create(creatureRedisProjectorAdapter.deleteById(event))
		.verifyComplete();

		Mockito.verify(reactiveRedisTemplate).hasKey(Mockito.anyString());
		Mockito.verify(reactiveRedisTemplate, Mockito.times(2)).opsForValue();
		Mockito.verify(reactiveValueOperations).get(Mockito.anyString());
		Mockito.verify(reactiveRedisTemplate).opsForSet();
		Mockito.verify(reactiveSetOperations).remove(Mockito.anyString(), Mockito.anyString());
		Mockito.verify(reactiveValueOperations).delete(Mockito.anyString());
		
	}
	
}
