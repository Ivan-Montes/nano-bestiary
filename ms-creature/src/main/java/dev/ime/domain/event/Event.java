package dev.ime.domain.event;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class Event {

	private final UUID eventId;
	private final String eventCategory;
	private final String eventType;
	private final Instant eventTimestamp;
	private final Map<String, Object> eventData;
	
	public Event(String eventCategory, String eventType, Map<String, Object> eventData) {
		super();
		this.eventId = UUID.randomUUID();
		this.eventCategory = eventCategory;
		this.eventType = eventType;
		this.eventTimestamp = Instant.now();
		this.eventData = Collections.unmodifiableMap(new HashMap<>(eventData));
	}

    @JsonCreator
	public Event(@JsonProperty("eventId")UUID eventId, @JsonProperty("eventCategory")String eventCategory, @JsonProperty("eventType")String eventType, @JsonProperty("eventTimestamp")Instant eventTimestamp,
			@JsonProperty("eventData")Map<String, Object> eventData) {
		super();
		this.eventId = eventId;
		this.eventCategory = eventCategory;
		this.eventType = eventType;
		this.eventTimestamp = eventTimestamp;
		this.eventData = Collections.unmodifiableMap(new HashMap<>(eventData));
	}

	public UUID getEventId() {
		return eventId;
	}

	public String getEventCategory() {
		return eventCategory;
	}

	public String getEventType() {
		return eventType;
	}

	public Instant getEventTimestamp() {
		return eventTimestamp;
	}

	public Map<String, Object> getEventData() {
		return eventData;
	}

	@Override
	public int hashCode() {
		return Objects.hash(eventCategory, eventData, eventId, eventType, eventTimestamp);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Event other = (Event) obj;
		return Objects.equals(eventCategory, other.eventCategory) && Objects.equals(eventData, other.eventData)
				&& Objects.equals(eventId, other.eventId) && Objects.equals(eventType, other.eventType)
				&& Objects.equals(eventTimestamp, other.eventTimestamp);
	}

	@Override
	public String toString() {
		return "Event [eventId=" + eventId + ", eventCategory=" + eventCategory + ", eventType=" + eventType
				+ ", timeInstant=" + eventTimestamp + ", eventData=" + eventData + "]";
	}	
	
}
