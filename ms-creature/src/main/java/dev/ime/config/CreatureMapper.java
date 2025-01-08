package dev.ime.config;

import org.springframework.stereotype.Component;

import dev.ime.application.dto.CreatureDto;
import dev.ime.domain.event.Event;
import dev.ime.domain.model.Creature;
import dev.ime.infrastructure.entity.EventMongoEntity;
import dev.ime.infrastructure.entity.CreatureJpaEntity;

@Component
public class CreatureMapper {

	public EventMongoEntity fromEventDomainToEventMongo(Event event) {
	
		return EventMongoEntity.builder()
				.eventId(event.getEventId())
				.eventCategory(event.getEventCategory())
				.eventType(event.getEventType())
				.eventTimestamp(event.getEventTimestamp())
				.eventData(event.getEventData())
				.build();
		
	}
	
	public Event fromEventMongoToEventDomain(EventMongoEntity eventMongoEntity) {
		
		return new Event(
				eventMongoEntity.getEventId(),
				eventMongoEntity.getEventCategory(),
				eventMongoEntity.getEventType(),
				eventMongoEntity.getEventTimestamp(),
				eventMongoEntity.getEventData()				
				);
				
	}
	
	public Creature fromJpaToDomain(CreatureJpaEntity creatureJpaEntity) {
		
		return new Creature(
				creatureJpaEntity.getCreatureId(),
				creatureJpaEntity.getCreatureName(),
				creatureJpaEntity.getCreatureDescription(),
				creatureJpaEntity.getAreaId()
				);
		
	}
	
	public CreatureDto fromDomainToDto(Creature creature) {
		
		return new CreatureDto(
				creature.getCreatureId(),
				creature.getCreatureName(),
				creature.getCreatureDescription(),
				creature.getAreaId()
				);
		
	}
	
}
