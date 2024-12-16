package dev.ime.infrastructure.entity;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import dev.ime.config.GlobalConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Document(GlobalConstants.EVENT_DB)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class EventMongoEntity {

	@Id
	@Field( name = GlobalConstants.EVENT_ID_DB)
	private UUID eventId;
	
	@Field( name = GlobalConstants.EVENT_CATEGORY_DB)
	private String eventCategory;
	
	@Field( name = GlobalConstants.EVENT_TYPE_DB)
	private String eventType;
	
	@Field( name = GlobalConstants.EVENT_TIMESTAMP_DB)
	private Instant eventTimestamp;
	
	@Field( name = GlobalConstants.EVENT_DATA_DB)
	private Map<String, Object> eventData;	
	
}
