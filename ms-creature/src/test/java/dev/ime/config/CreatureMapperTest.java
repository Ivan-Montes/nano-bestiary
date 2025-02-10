package dev.ime.config;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.ime.application.dto.CreatureDto;
import dev.ime.domain.event.Event;
import dev.ime.domain.model.Creature;
import dev.ime.infrastructure.entity.CreatureJpaEntity;
import dev.ime.infrastructure.entity.EventMongoEntity;

@ExtendWith(MockitoExtension.class)
class CreatureMapperTest {

	@InjectMocks
	private CreatureMapper mapper;	

	private final UUID creatureId01 = UUID.randomUUID();
	private final UUID areaId01 = UUID.randomUUID();
	private final String creatureName01 = "";
	private final String creatureDescription01 = "";

	private UUID eventId = UUID.randomUUID();
	private final String eventCategory = GlobalConstants.CREATURE_CAT;
	private final String eventType = GlobalConstants.CREATURE_DELETED;
	private final Instant eventTimestamp = Instant.now();
	private final Map<String, Object> eventData = new HashMap<>();

	@Test
	void fromEventDomainToEventMongo_WithEvent_ReturnEventMongoEntity() {
		
		Event event = new Event(
				eventId,
				eventCategory,
				eventType,
				eventTimestamp,
				eventData);
		
		EventMongoEntity entity = mapper.fromEventDomainToEventMongo(event);
		
		org.junit.jupiter.api.Assertions.assertAll( 
				()-> Assertions.assertThat(entity).isNotNull(),
				()-> Assertions.assertThat(entity.getEventId()).isEqualTo(event.getEventId()),
				()-> Assertions.assertThat(entity.getEventCategory()).isEqualTo(event.getEventCategory()),
				()-> Assertions.assertThat(entity.getEventType()).isEqualTo(event.getEventType()),
				()-> Assertions.assertThat(entity.getEventTimestamp()).isEqualTo(event.getEventTimestamp()),
				()-> Assertions.assertThat(entity.getEventData()).isEqualTo(event.getEventData())
				);
	}
	
	@Test
	void fromEventMongoToEventDomain_WithEventMongoEntity_ReturnEvent() {		

		EventMongoEntity eventMongoEntity = new EventMongoEntity(
				eventId,
				eventCategory,
				eventType,
				eventTimestamp,
				eventData);
		
		Event entity = mapper.fromEventMongoToEventDomain(eventMongoEntity);
		
		org.junit.jupiter.api.Assertions.assertAll( 
				()-> Assertions.assertThat(entity).isNotNull(),
				()-> Assertions.assertThat(entity.getEventId()).isEqualTo(eventMongoEntity.getEventId()),
				()-> Assertions.assertThat(entity.getEventCategory()).isEqualTo(eventMongoEntity.getEventCategory()),
				()-> Assertions.assertThat(entity.getEventType()).isEqualTo(eventMongoEntity.getEventType()),
				()-> Assertions.assertThat(entity.getEventTimestamp()).isEqualTo(eventMongoEntity.getEventTimestamp()),
				()-> Assertions.assertThat(entity.getEventData()).isEqualTo(eventMongoEntity.getEventData())
				);
		
	}
	
	@Test
	void fromJpaToDomain_WithJpaEntity_ReturnDomainEntity() {
		
		CreatureJpaEntity jpaEntity = new CreatureJpaEntity(
				creatureId01,
				creatureName01,
				creatureDescription01,
				areaId01);
		
		Creature creature = mapper.fromJpaToDomain(jpaEntity);

		org.junit.jupiter.api.Assertions.assertAll( 
				()-> Assertions.assertThat(creature).isNotNull(),
				()-> Assertions.assertThat(creature.getCreatureId()).isEqualTo(jpaEntity.getCreatureId()),
				()-> Assertions.assertThat(creature.getCreatureName()).isEqualTo(jpaEntity.getCreatureName()),
				()-> Assertions.assertThat(creature.getCreatureDescription()).isEqualTo(jpaEntity.getCreatureDescription()),
				()-> Assertions.assertThat(creature.getAreaId()).isEqualTo(jpaEntity.getAreaId())
				);
	}

	@Test
	void fromDomainToDto_WithDomainEntity_ReturnDto() {
		
		Creature creature = new Creature(
				creatureId01,
				creatureName01,
				creatureDescription01,
				areaId01);
		
		CreatureDto creatureDto = mapper.fromDomainToDto(creature);
		
		org.junit.jupiter.api.Assertions.assertAll( 
				()-> Assertions.assertThat(creatureDto).isNotNull(),
				()-> Assertions.assertThat(creatureDto.creatureId()).isEqualTo(creature.getCreatureId()),
				()-> Assertions.assertThat(creatureDto.creatureName()).isEqualTo(creature.getCreatureName()),
				()-> Assertions.assertThat(creatureDto.creatureDescription()).isEqualTo(creature.getCreatureDescription()),
				()-> Assertions.assertThat(creatureDto.areaId()).isEqualTo(creature.getAreaId())
				);
		
	}
	
}
