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
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.query.Update;

import dev.ime.application.exception.CreateJpaEntityException;
import dev.ime.application.utils.ReactiveLoggerUtils;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.event.Event;
import dev.ime.infrastructure.entity.CreatureJpaEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class CreatureProjectorAdapterTest {

	@Mock
	private ReactiveLoggerUtils reactiveLoggerUtils;
	@Mock
	private R2dbcEntityTemplate r2dbcTemplate;
	
	@InjectMocks
	private CreatureProjectorAdapter creatureProjectorAdapter;

	private Event event;

	private final UUID creatureId01 = UUID.randomUUID();
	private final UUID areaId01 = UUID.randomUUID();
	private final String creatureName01 = "Kraken";
	private final String creatureDescription01 = "a legendary sea monster of enormous size";

	private UUID eventId = UUID.randomUUID();
	private final String eventCategory = GlobalConstants.CREATURE_CAT;
	private final String eventType = GlobalConstants.CREATURE_DELETED;
	private final Instant eventTimestamp = Instant.now();
	private Map<String, Object> eventData = new HashMap<>();

	@BeforeEach
	private void setUp() {	

		eventData = new HashMap<>();
		eventData.put(GlobalConstants.CREATURE_ID, creatureId01.toString());
		eventData.put(GlobalConstants.CREATURE_NAME, creatureName01);
		eventData.put(GlobalConstants.CREATURE_DESC, creatureDescription01);
		eventData.put(GlobalConstants.AREA_ID, areaId01.toString());

		event = new Event(
				eventId,
				eventCategory,
				eventType,
				eventTimestamp,
				eventData);
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	void create_shouldReturnMonoVoid() {
		
		Mockito.when(reactiveLoggerUtils.logInfoAction(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.empty());
		Mockito.when(r2dbcTemplate.selectOne(Mockito.any(Query.class), Mockito.any(Class.class))).thenReturn(Mono.empty());
		Mockito.when(r2dbcTemplate.insert(Mockito.any(CreatureJpaEntity.class))).thenReturn(Mono.just(new CreatureJpaEntity()));

		StepVerifier
		.create(creatureProjectorAdapter.create(event))
		.verifyComplete();

		Mockito.verify(r2dbcTemplate).selectOne(Mockito.any(Query.class), Mockito.any(Class.class));
		Mockito.verify(r2dbcTemplate).insert(Mockito.any(CreatureJpaEntity.class));		
		
	}

	@Test
	void create_WithBadName_shouldThrowError() {
		
		Mockito.when(reactiveLoggerUtils.logInfoAction(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.empty());

		eventData.put(GlobalConstants.CREATURE_NAME, "-_-'");
		event = new Event(
				eventId,
				eventCategory,
				eventType,
				eventTimestamp,
				eventData);
		
		StepVerifier
		.create(creatureProjectorAdapter.create(event))
		.expectError(CreateJpaEntityException.class)
		.verify();
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	void update_shouldReturnMonoVoid() {
		
		Mockito.when(reactiveLoggerUtils.logInfoAction(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.empty());
		Mockito.when(r2dbcTemplate.selectOne(Mockito.any(Query.class), Mockito.any(Class.class))).thenReturn(Mono.empty());
		Mockito.when(r2dbcTemplate.update(Mockito.any(Query.class), Mockito.any(Update.class), Mockito.any(Class.class))).thenReturn(Mono.just(2L));
		
		StepVerifier
		.create(creatureProjectorAdapter.update(event))
		.verifyComplete();

		Mockito.verify(r2dbcTemplate).selectOne(Mockito.any(Query.class), Mockito.any(Class.class));
		Mockito.verify(r2dbcTemplate).update(Mockito.any(Query.class), Mockito.any(Update.class), Mockito.any(Class.class));
		
	}

	@Test
	void deleteById_shouldReturnMonoVoid() {
		
		Mockito.when(reactiveLoggerUtils.logInfoAction(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.empty());
		Mockito.when(r2dbcTemplate.delete(Mockito.any(Query.class), Mockito.any(Class.class))).thenReturn(Mono.just(1L));

		StepVerifier
		.create(creatureProjectorAdapter.deleteById(event))
		.verifyComplete();

		Mockito.verify(r2dbcTemplate).delete(Mockito.any(Query.class), Mockito.any(Class.class));
		
	}
	
}
