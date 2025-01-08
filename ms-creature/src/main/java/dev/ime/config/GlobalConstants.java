package dev.ime.config;

import java.time.Duration;

public class GlobalConstants {

	private GlobalConstants() {
		super();
	}
	
	// Patterns
	public static final String PATTERN_NAME_FULL = "^[a-zA-Z0-9ñÑáéíóúÁÉÍÓÚ][a-zA-Z0-9ñÑáéíóúÁÉÍÓÚ\\s\\-\\.&,:]{1,49}$";
	public static final String PATTERN_DESC_FULL = "^[a-zA-Z0-9ñÑáéíóúÁÉÍÓÚ][a-zA-Z0-9ñÑáéíóúÁÉÍÓÚ\\s\\-\\.&,:]{1,127}$";			
	public static final String PATTERN_UUID_ZERO = "00000000-0000-0000-0000-000000000000";
	// Messages
	public static final String MSG_REQUIRED = " *REQUIRED* ";
	public static final String MSG_NODATA = "No data available";
	public static final String MSG_UNKNOWDATA = "Unknow data";
	public static final String MSG_MODLINES = "Modificated lines: ";
	public static final String MSG_PATTERN_SEVERE = "### [** eXception **] -> [%s] ###";
	public static final String MSG_PATTERN_INFO = "### [%s] -> [%s] -> [ %s ]";
	public static final String MSG_COMMAND_ILLEGAL = "Command not supported";
	public static final String MSG_QUERY_ILLEGAL = "Query not supported";
	public static final String MSG_EVENT_ILLEGAL = "Event not supported";
	public static final String MSG_HANDLER_NONE = "No handler found for type";
	public static final String MSG_HANDLER_OK = "CommandHandler processed succesfully";
	public static final String MSG_FLOW_CANCEL = "Cancellation occurred during processing";
	public static final String MSG_EVENT_ERROR = "Error processing events";
	public static final String MSG_FLOW_SUBS = "Subscribed to flow request";
	public static final String MSG_FLOW_OK = "Reactive flow processed succesfully";
	public static final String MSG_FLOW_ERROR = "Error processing reactive flow";
	public static final String MSG_FLOW_RESULT = "Reactive flow result";
	public static final String MSG_FLOW_PROCESS = "Processing reactive flow";
	public static final String MSG_PUBLISH_EVENT = "Publishing Event";	
	public static final String MSG_PUBLISH_OK = "Publish Event Succesfully";	
	public static final String MSG_PUBLISH_FAIL = "Publish Event Failed";		
	public static final String MSG_PUBLISH_END = "End Publish Event";			
	public static final String MSG_PAGED_FAIL = "Invalid page or size parameter";			
	public static final String MSG_REQUEST_FAIL = "Error processing request";	
	// Models
	public static final String AREA_CAT = "Area";
	public static final String AREA_CAT_INDEX = "index:area:";
	public static final String AREA_DB = "areas";
	public static final String AREA_ID = "areaId";
	public static final String AREA_ID_DB = "area_id";
	public static final String AREA_NAME = "areaName";
	public static final String AREA_NAME_DB = "area_name";
	public static final String CREATURE_CAT = "Creature";
	public static final String CREATURE_CAT_INDEX = "index:creature:";
	public static final String CREATURE_DB = "creatures";
	public static final String CREATURE_ID = "creatureId";
	public static final String CREATURE_ID_DB = "creature_id";
	public static final String CREATURE_NAME = "creatureName";
	public static final String CREATURE_NAME_DB = "creature_name";
	public static final String CREATURE_DESC = "creatureDescription";
	public static final String CREATURE_DESC_DB = "creature_description";
	public static final String EVENT_CAT = "Event";
	public static final String EVENT_DB = "events";
	public static final String EVENT_ID = "eventId";
	public static final String EVENT_ID_DB = "event_id";
	public static final String EVENT_CATEGORY_DB = "event_category";
	public static final String EVENT_TYPE_DB = "event_type";
	public static final String EVENT_TIMESTAMP_DB = "event_timestamp";
	public static final String EVENT_DATA_DB = "event_data";	
	// Topics
	public static final String AREA_CREATED = "area.created";
	public static final String AREA_UPDATED = "area.updated";
	public static final String AREA_DELETED = "area.deleted";
	public static final String CREATURE_CREATED = "creature.created";
	public static final String CREATURE_UPDATED = "creature.updated";
	public static final String CREATURE_DELETED = "creature.deleted";
	//Exceptions
	public static final String EX_RESOURCENOTFOUND = "ResourceNotFoundException";	
	public static final String EX_RESOURCENOTFOUND_DESC = "Exception is coming, the resource has not been found.";	
	public static final String EX_ENTITYASSOCIATED = "EntityAssociatedException";	
	public static final String EX_ENTITYASSOCIATED_DESC = "Hear me roar, some entity is still associated in the element";	
	public static final String EX_ILLEGALARGUMENT = "IllegalArgumentException";
	public static final String EX_ILLEGALARGUMENT_DESC = "Some argument is not supported";
	public static final String EX_EVENT_UNEXPEC = "Event Unexpected Exception";
	public static final String EX_EVENT_UNEXPEC_DESC = "Event Unexpected Exception";
	public static final String EX_VALIDATION = "ValidationException";
	public static final String EX_VALIDATION_DESC = "Kernel Panic in validation process";
	public static final String EX_UNIQUEVALUE = "UniqueValueException";
	public static final String EX_UNIQUEVALUE_DESC = "Unique Value constraint infringed";
	public static final String EX_INVALIDUUID = "InvalidUUIDException";
	public static final String EX_INVALIDUUID_DESC = "Fail to parse UUID";
	public static final String EX_PLAIN = "Exception";
	public static final String EX_PLAIN_DESC = "Exception because the night is dark and full of terrors";
	public static final String EX_EMPTYRESPONSE = "EmptyResponseException";
	public static final String EX_EMPTYRESPONSE_DESC = "No freak out, just an Empty Response";
	public static final String EX_CREATEJPAENTITY = "CreateJpaEntityException";
	public static final String EX_CREATEJPAENTITY_DESC = "Exception while creation a JPA entity for saving to sql db";
	public static final String EX_CREATEREDISENTITY = "CreateRedisEntityException";
	public static final String EX_CREATEREDISENTITY_DESC = "Exception while creation a REDIS entity for saving to db";
	public static final String EX_PUBLISHEVENT = "PublishEventException";
	public static final String EX_PUBLISHEVENT_DESC = "Failed to publish event";
	// Paging and Sorting
	public static final String PS_PAGE = "page";
	public static final String PS_SIZE = "size";
	public static final String PS_BY = "sortBy";
	public static final String PS_DIR = "sortDir";
	public static final String PS_A = "ASC";
	public static final String PS_D = "DESC";
	// Microservices
	public static final String MS_AREA = "ms-area";
	public static final String MS_CREATURE = "ms-creature";
	// Other
	public static final String RSOCKET_PORT = "rsocketPort";
	public static final String HOST = "Hostname";
	public static final int RSOCKET_MAX_RETRIES = 2;
	public static final Duration RSOCKET_TIMEOUT = Duration.ofSeconds(5);
	public static final Duration RSOCKET_RETRIES_DELAY = Duration.ofMillis(100);
	
}
