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

import dev.ime.application.dto.AreaDto;
import dev.ime.domain.event.Event;
import dev.ime.domain.model.Area;
import dev.ime.infrastructure.entity.AreaJpaEntity;
import dev.ime.infrastructure.entity.EventMongoEntity;


@ExtendWith(MockitoExtension.class)
class AreaMapperTest {

	@InjectMocks
	private AreaMapper mapper;
	
	private UUID eventId = UUID.randomUUID();
	private final String eventCategory = GlobalConstants.AREA_CAT;
	private final String eventType = GlobalConstants.AREA_DELETED;
	private final Instant eventTimestamp = Instant.now();
	private final Map<String, Object> eventData = new HashMap<>();
	private UUID areaId = UUID.randomUUID();
	private String areaName = "";	
	
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
		
		AreaJpaEntity areaJpaEntity = new AreaJpaEntity(
				areaId,
				areaName);
		
		Area entity = mapper.fromJpaToDomain(areaJpaEntity);

		org.junit.jupiter.api.Assertions.assertAll( 
				()-> Assertions.assertThat(entity).isNotNull(),
				()-> Assertions.assertThat(entity.getAreaId()).isEqualTo(areaJpaEntity.getAreaId()),
				()-> Assertions.assertThat(entity.getAreaName()).isEqualTo(areaJpaEntity.getAreaName())
				);
	}

	@Test
	void fromDomainToDto_WithDomainEntity_ReturnDto() {
		
		Area area = new Area(
				areaId,
				areaName);
		
		AreaDto entity = mapper.fromDomainToDto(area);
		
		org.junit.jupiter.api.Assertions.assertAll( 
				()-> Assertions.assertThat(entity).isNotNull(),
				()-> Assertions.assertThat(entity.areaId()).isEqualTo(area.getAreaId()),
				()-> Assertions.assertThat(entity.areaName()).isEqualTo(area.getAreaName())
				);
		
	}	

}
