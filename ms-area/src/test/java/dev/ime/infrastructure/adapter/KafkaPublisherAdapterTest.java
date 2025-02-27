package dev.ime.infrastructure.adapter;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;

import dev.ime.application.exception.PublishEventException;
import dev.ime.application.utils.ReactiveLoggerUtils;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.event.Event;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.SenderRecord;
import reactor.kafka.sender.SenderResult;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class KafkaPublisherAdapterTest {

	@Mock
	private ReactiveKafkaProducerTemplate<String, Object> reactiveKafkaTemplate;
	@Mock
	private ReactiveLoggerUtils reactiveLoggerUtils;

	@InjectMocks
	private KafkaPublisherAdapter kafkaPublisherAdapter;

	private Event event;

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
				
	}

	@SuppressWarnings("unchecked")
	@Test
	void publishEvent_shouldDoIt() {
		
		Mockito.when(reactiveLoggerUtils.logInfoAction(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.empty());
		SenderResult<Object> senderResult = Mockito.mock(SenderResult.class);
		Mockito.when(reactiveKafkaTemplate.send(Mockito.any(SenderRecord.class))).thenReturn(Mono.just(senderResult));
		RecordMetadata recordMetadata = Mockito.mock(RecordMetadata.class);
		Mockito.when(senderResult.recordMetadata()).thenReturn(recordMetadata);
		Mockito.when(recordMetadata.topic()).thenReturn(eventType);
		
		StepVerifier
        .create(kafkaPublisherAdapter.publishEvent(event))        
        .verifyComplete();

		Mockito.verify(reactiveKafkaTemplate).send(Mockito.any(SenderRecord.class));
		Mockito.verify(senderResult).recordMetadata();
		Mockito.verify(recordMetadata).topic();
		
	}

	@SuppressWarnings("unchecked")
	@Test
	void publishEvent_withForcedError_shouldManage() {		

		Mockito.when(reactiveLoggerUtils.logInfoAction(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.empty());
		Mockito.when(reactiveKafkaTemplate.send(Mockito.any(SenderRecord.class))).thenReturn(Mono.error(new Exception(GlobalConstants.EX_PLAIN)));
		
		StepVerifier
        .create(kafkaPublisherAdapter.publishEvent(event))
        .expectError(PublishEventException.class)
        .verify();
		
		Mockito.verify(reactiveKafkaTemplate).send(Mockito.any(SenderRecord.class));

	}
	
}
