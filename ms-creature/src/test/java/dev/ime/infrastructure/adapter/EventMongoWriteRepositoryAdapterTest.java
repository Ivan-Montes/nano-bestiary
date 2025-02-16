package dev.ime.infrastructure.adapter;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.ime.config.CreatureMapper;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.event.Event;
import dev.ime.infrastructure.entity.EventMongoEntity;
import dev.ime.infrastructure.repository.EventMongoWriteRepository;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class EventMongoWriteRepositoryAdapterTest {

	@Mock
	private EventMongoWriteRepository eventMongoWriteRepository;	

	@Mock
	private CreatureMapper mapper;	

	@InjectMocks
	private EventMongoWriteRepositoryAdapter eventMongoWriteRepositoryAdapter;

	private Event event;
	private Event eventClon;
	private EventMongoEntity eventMongoEntity;
	
	private final UUID eventId = UUID.randomUUID();
	private final String eventCategory = GlobalConstants.CREATURE_CAT;
	private final String eventType = GlobalConstants.CREATURE_DELETED;
	private final Instant eventTimestamp = Instant.now();
	private final Map<String, Object> eventData = new HashMap<>();
	
	@BeforeEach
	private void setUp() {

		event = new Event(
				eventId,
				eventCategory,
				eventType,
				eventTimestamp,
				eventData);
		
		eventClon = new Event(
				eventId,
				eventCategory,
				eventType,
				Instant.now(),
				eventData);
		
		eventMongoEntity = new EventMongoEntity(
				eventId,
				eventCategory,
				eventType,
				eventTimestamp,
				eventData);
	}	
	
	@Test
	void save_WithEvent_ReturnMonoEvent() {
		
		Mockito.when(mapper.fromEventDomainToEventMongo(Mockito.any(Event.class))).thenReturn(eventMongoEntity);
		Mockito.when(eventMongoWriteRepository.save(Mockito.any(EventMongoEntity.class))).thenReturn(Mono.just(eventMongoEntity));
		Mockito.when(mapper.fromEventMongoToEventDomain(Mockito.any(EventMongoEntity.class))).thenReturn(event);
		
		Mono<Event> result = eventMongoWriteRepositoryAdapter.save(event);
		        
		StepVerifier.create(result)
        .assertNext(savedEvent -> {
        	org.junit.jupiter.api.Assertions.assertAll(
        			()->Assertions.assertThat(savedEvent).isEqualTo(event),
        			()->Assertions.assertThat(savedEvent).isNotEqualTo(eventClon),
        			()->Assertions.assertThat(savedEvent).hasSameHashCodeAs(event),
        			()->Assertions.assertThat(savedEvent.getEventId()).isEqualTo(eventId),
        			()->Assertions.assertThat(savedEvent.getEventCategory()).isEqualTo(eventCategory),
        			()->Assertions.assertThat(savedEvent.getEventType()).isEqualTo(eventType),
                	()->Assertions.assertThat(savedEvent.getEventTimestamp()).isEqualTo(eventTimestamp),
                	()->Assertions.assertThat(savedEvent.getEventData()).isEqualTo(eventData)
        			);       
        })
        .verifyComplete();		
		Mockito.verify(mapper).fromEventDomainToEventMongo(event);
        Mockito.verify(eventMongoWriteRepository).save(eventMongoEntity);
        Mockito.verify(mapper).fromEventMongoToEventDomain(eventMongoEntity);

	}

}
