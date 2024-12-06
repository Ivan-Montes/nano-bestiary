package dev.ime.config;

import org.springframework.stereotype.Component;

import dev.ime.application.dto.AreaDto;
import dev.ime.domain.event.Event;
import dev.ime.domain.model.Area;
import dev.ime.infrastructure.entity.EventMongoEntity;
import dev.ime.infrastructure.entity.AreaJpaEntity;

@Component
public class AreaMapper {

	public EventMongoEntity fromEventDomainToEventMongo(Event event) {
	
		return EventMongoEntity.builder()
				.eventId(event.getEventId())
				.eventCategory(event.getEventCategory())
				.eventType(event.getEventType())
				.eventTimestamp(event.getEventTimestamp())
				.eventData(event.getEventData())
				.build();
		
	}
	
	public Event fromEventMongoToEventDomain(EventMongoEntity entity) {
		
		return new Event(
				entity.getEventId(),
				entity.getEventCategory(),
				entity.getEventType(),
				entity.getEventTimestamp(),
				entity.getEventData()				
				);
				
	}
	
	public Area fromJpaToDomain(AreaJpaEntity entity) {
		
		return new Area(
				entity.getAreaId(),
				entity.getAreaName()
				);
		
	}
	
	public AreaDto fromDomainToDto(Area domain) {
		
		return new AreaDto(
				domain.getAreaId(),
				domain.getAreaName()
				);
		
	}
	
}
