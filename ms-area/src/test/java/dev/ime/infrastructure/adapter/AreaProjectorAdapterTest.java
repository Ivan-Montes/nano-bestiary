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
import dev.ime.infrastructure.entity.AreaJpaEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class AreaProjectorAdapterTest {

	@Mock
	private ReactiveLoggerUtils reactiveLoggerUtils;
	@Mock
	private R2dbcEntityTemplate r2dbcTemplate;
	
	@InjectMocks
	private AreaProjectorAdapter areaProjectorAdapter;

	private Event event;
	
	private UUID eventId = UUID.randomUUID();
	private final String eventCategory = GlobalConstants.AREA_CAT;
	private final String eventType = GlobalConstants.AREA_DELETED;
	private final Instant eventTimestamp = Instant.now();
	private Map<String, Object> eventData = new HashMap<>();

	private final UUID areaId01 = UUID.randomUUID();
	private final String areaName01 = "Atlantis";
	
	@BeforeEach
	private void setUp() {	

		eventData = new HashMap<>();
		eventData.put(GlobalConstants.AREA_ID, areaId01.toString());
		eventData.put(GlobalConstants.AREA_NAME, areaName01);

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
		Mockito.when(r2dbcTemplate.insert(Mockito.any(AreaJpaEntity.class))).thenReturn(Mono.just(new AreaJpaEntity()));
		
		StepVerifier
		.create(areaProjectorAdapter.create(event))
		.verifyComplete();

		Mockito.verify(r2dbcTemplate).selectOne(Mockito.any(Query.class), Mockito.any(Class.class));
		Mockito.verify(r2dbcTemplate).insert(Mockito.any(AreaJpaEntity.class));
		
	}
	
	@Test
	void create_WithBadName_shouldThrowError() {
		
		Mockito.when(reactiveLoggerUtils.logInfoAction(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.empty());

		eventData.put(GlobalConstants.AREA_NAME, "-_-'");
		event = new Event(
				eventId,
				eventCategory,
				eventType,
				eventTimestamp,
				eventData);
		
		StepVerifier
		.create(areaProjectorAdapter.create(event))
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
		.create(areaProjectorAdapter.update(event))
		.verifyComplete();

		Mockito.verify(r2dbcTemplate).selectOne(Mockito.any(Query.class), Mockito.any(Class.class));
		Mockito.verify(r2dbcTemplate).update(Mockito.any(Query.class), Mockito.any(Update.class), Mockito.any(Class.class));
		
	}
	
	@Test
	void deleteById_shouldReturnMonoVoid() {
		
		Mockito.when(reactiveLoggerUtils.logInfoAction(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.empty());
		Mockito.when(r2dbcTemplate.delete(Mockito.any(Query.class), Mockito.any(Class.class))).thenReturn(Mono.just(1L));

		StepVerifier
		.create(areaProjectorAdapter.deleteById(event))
		.verifyComplete();

		Mockito.verify(r2dbcTemplate).delete(Mockito.any(Query.class), Mockito.any(Class.class));
		
	}
	
}
