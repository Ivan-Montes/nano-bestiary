package dev.ime.infrastructure.repository;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import dev.ime.config.GlobalConstants;
import dev.ime.infrastructure.entity.EventMongoEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Testcontainers
@DataMongoTest
class EventMongoWriteRepositoryTest {

    @Autowired
	private EventMongoWriteRepository eventMongoWriteRepository;

    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo"));
	
	static {
        mongoDBContainer.start();
    }
	
	private EventMongoEntity eventMongoEntity;
	private final UUID eventId = UUID.randomUUID();
	private final String eventCategory = GlobalConstants.CREATURE_CAT;
	private final String eventType = GlobalConstants.CREATURE_DELETED;
	private final Instant eventTimestamp = Instant.now();
	private final Map<String, Object> eventData = new HashMap<>();
	
	@BeforeEach
	void initializeDataContainer() {

		eventMongoEntity = new EventMongoEntity();
		eventMongoEntity.setEventId(eventId);
		eventMongoEntity.setEventCategory(eventCategory);
		eventMongoEntity.setEventType(eventType);
		eventMongoEntity.setEventTimestamp(eventTimestamp);
		eventMongoEntity.setEventData(eventData);
		
		StepVerifier.create(eventMongoWriteRepository.save(eventMongoEntity))
        .expectNextCount(1)
        .verifyComplete();
		
	}

    @AfterEach
    void cleanUp() {
        StepVerifier
        .create(eventMongoWriteRepository.deleteAll())
        .verifyComplete();
    }
    
    @Test
    void connectionEstablished() {
      
        org.junit.jupiter.api.Assertions.assertAll(
    			()->Assertions.assertThat(mongoDBContainer.isCreated()).isTrue(),
    			()->Assertions.assertThat(mongoDBContainer.isRunning()).isTrue()
    			);
        
    }

    @Test
    void findById_WithRandomId_NoFoundElements() {
    	
    	Mono<EventMongoEntity> result = eventMongoWriteRepository.findById(UUID.randomUUID());
    	
    	StepVerifier.create(result)
    	.expectNextCount(0)
    	.verifyComplete();
    	
    }

    @Test
    void findById_WithRightId_FoundOneElement() {
    	
    	Mono<EventMongoEntity> result = eventMongoWriteRepository.findById(eventId);
    	
    	  StepVerifier.create(result)
          .assertNext(savedEvent -> {
        	org.junit.jupiter.api.Assertions.assertAll(
        			()->Assertions.assertThat(savedEvent.getEventId()).isEqualTo(eventId),
        			()->Assertions.assertThat(savedEvent.getEventCategory()).isEqualTo(eventCategory),
        			()->Assertions.assertThat(savedEvent.getEventType()).isEqualTo(eventType)
        			);
          })
          .verifyComplete();
    	
    }

}
