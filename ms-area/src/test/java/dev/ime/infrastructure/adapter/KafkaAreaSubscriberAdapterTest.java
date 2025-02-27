package dev.ime.infrastructure.adapter;


import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.ime.application.utils.ReactiveLoggerUtils;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.event.Event;
import dev.ime.domain.ports.outbound.BaseProjectorPort;
import dev.ime.domain.ports.outbound.ExtendedProjectorPort;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class KafkaAreaSubscriberAdapterTest {

	@Mock
	private ReactiveLoggerUtils reactiveLoggerUtils;
	@Mock
    private BaseProjectorPort baseProjectorPort;
	@Mock
    private ExtendedProjectorPort extendedProjectorPort;

	@InjectMocks
	private KafkaAreaSubscriberAdapter kafkaAreaSubscriberAdapter;
	
	private Event event;
	private ConsumerRecord<String, Event> consumerRecord;

	private final UUID eventId = UUID.randomUUID();
	private final String eventCategory = GlobalConstants.AREA_CAT;
	private final String eventType = GlobalConstants.AREA_DELETED;
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

	@Test
	void onMessage_shouldProcessMessage() {
		
		consumerRecord = createConsumerRecord(eventType);
		Mockito.when(reactiveLoggerUtils.logInfoAction(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.empty());
		Mockito.when(baseProjectorPort.deleteById(Mockito.any(Event.class))).thenReturn(Mono.empty());


		StepVerifier
		.create(kafkaAreaSubscriberAdapter.onMessage(consumerRecord))
		.verifyComplete();
		
		Mockito.verify(baseProjectorPort).deleteById(Mockito.any(Event.class));

	}

	@Test
	void onMessage_WithEmptyTopic_shouldManageError() {
		
		consumerRecord = createConsumerRecord("");
		Mockito.when(reactiveLoggerUtils.logInfoAction(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.empty());
		Mockito.when(reactiveLoggerUtils.logSevereAction(Mockito.anyString())).thenReturn(Mono.empty());


		StepVerifier
		.create(kafkaAreaSubscriberAdapter.onMessage(consumerRecord))
		.verifyComplete();		

	}
	
	private ConsumerRecord<String, Event> createConsumerRecord(String eventType) {
		
		return new ConsumerRecord<>(
				eventType,
				1,
				1L,
				"",
				event
				);
		
	}
	
}
