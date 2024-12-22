package dev.ime.infrastructure.adapter;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;

import dev.ime.application.exception.CreateRedisEntityException;
import dev.ime.application.exception.EmptyResponseException;
import dev.ime.application.utils.ReactiveLoggerUtils;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.event.Event;
import dev.ime.domain.ports.outbound.BaseProjectorPort;
import dev.ime.infrastructure.entity.CreatureRedisEntity;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Repository
@Qualifier("creatureRedisProjectorAdapter")
public class CreatureRedisProjectorAdapter implements BaseProjectorPort{

	private final ReactiveLoggerUtils reactiveLoggerUtils;
    private final ReactiveRedisTemplate<String, CreatureRedisEntity> reactiveRedisTemplate;
    private final ReactiveRedisTemplate<String, String> stringReactiveRedisTemplate;    

	public CreatureRedisProjectorAdapter(ReactiveLoggerUtils reactiveLoggerUtils,
			ReactiveRedisTemplate<String, CreatureRedisEntity> reactiveRedisTemplate,
			ReactiveRedisTemplate<String, String> stringReactiveRedisTemplate) {
		super();
		this.reactiveLoggerUtils = reactiveLoggerUtils;
		this.reactiveRedisTemplate = reactiveRedisTemplate;
		this.stringReactiveRedisTemplate = stringReactiveRedisTemplate;
	}

	@Override
	public Mono<Void> create(Event event) {

		return Mono.justOrEmpty(event.getEventData())
				.transform(this::logFlowStep)
				.flatMap(this::createEntity)		        
				.transform(this::logFlowStep)
                .flatMap(this::deleteFromIndexIfOperationIsUpdate)
                .flatMap(this::insertIntoRedis)	
                .switchIfEmpty(Mono.error(new EmptyResponseException(Map.of(GlobalConstants.CREATURE_CAT, GlobalConstants.EX_EMPTYRESPONSE_DESC))))
				.transform(this::addLogginOptions)
				.then();	
	
	}

	@Override
	public Mono<Void> deleteById(Event event) {

		return Mono.justOrEmpty(event.getEventData())
				.transform(this::logFlowStep)
				.map( evenData -> evenData.get(GlobalConstants.CREATURE_ID))
				.cast(String.class)
				.map(UUID::fromString)			        
				.transform(this::logFlowStep)
				.flatMap(this::deleteFromIndex)
				.flatMap( id -> reactiveRedisTemplate.opsForValue().delete( generateCreatureKey(id) ))
				.switchIfEmpty(Mono.error(new EmptyResponseException(Map.of(GlobalConstants.CREATURE_CAT, GlobalConstants.EX_EMPTYRESPONSE_DESC))))
				.transform(this::addLogginOptions)
				.then();
		
	}

	private Mono<CreatureRedisEntity> createEntity(Map<String, Object> eventData) {
		
		return Mono.fromCallable( () -> {
			
			UUID creatureId = extractUuid(eventData, GlobalConstants.CREATURE_ID);
			UUID areaId = extractUuid(eventData, GlobalConstants.AREA_ID);

			return new CreatureRedisEntity(creatureId, areaId);
			
		}).onErrorMap(e -> new CreateRedisEntityException(Map.of( GlobalConstants.CREATURE_CAT, e.getMessage() )));	
		
	}

	private UUID extractUuid(Map<String, Object> eventData, String key) {
		
	    return Optional.ofNullable(eventData.get(key))
	                   .map(Object::toString)
	                   .map(UUID::fromString)
	                   .orElseThrow(() -> new IllegalArgumentException(GlobalConstants.EX_ILLEGALARGUMENT_DESC + ": " + key));
	    
	}
	
	private String generateCreatureKey(UUID id) {
		
	    return GlobalConstants.CREATURE_CAT  + ":" + id.toString();
	    
	}
	
	private String generateAreaIndexKey(UUID id) {
		
	    return GlobalConstants.AREA_CAT_INDEX + id.toString();
	    
	}

	private Mono<CreatureRedisEntity> deleteFromIndexIfOperationIsUpdate(CreatureRedisEntity entity) {
		
	    String key = generateCreatureKey(entity.getCreatureId());
	    
		return reactiveRedisTemplate
			.hasKey(key)
	        .filter(Boolean::booleanValue)
	        .flatMap(exists -> reactiveRedisTemplate.opsForValue().get(key))
			.ofType(CreatureRedisEntity.class)
			.flatMap(entityFound -> {
	            String oldIndexKey = generateAreaIndexKey(entityFound.getAreaId());
	            return stringReactiveRedisTemplate.opsForSet().remove(oldIndexKey, entity.getCreatureId().toString());
	        })
			.then(Mono.just(entity))
	        .defaultIfEmpty(entity);
		
	}

	private Mono<Tuple2<Boolean, Long>> insertIntoRedis(CreatureRedisEntity entity) {
		
	    String key = generateCreatureKey(entity.getCreatureId());
	    String indexKey = generateAreaIndexKey(entity.getAreaId());
	    
	    return Mono.zip(
	    		reactiveRedisTemplate.opsForValue().set(key, entity),
	    		stringReactiveRedisTemplate.opsForSet().add(indexKey, entity.getCreatureId().toString())
	    );
	    
	}

	private Mono<UUID> deleteFromIndex(UUID id) {
		
	    String key = generateCreatureKey(id);
	    
		return reactiveRedisTemplate
			.hasKey(key)
	        .filter(Boolean::booleanValue)
	        .flatMap(exists -> reactiveRedisTemplate.opsForValue().get(key))
			.ofType(CreatureRedisEntity.class)
			.flatMap(entityFound -> {
	            String oldIndexKey = generateAreaIndexKey(entityFound.getAreaId());
	            return stringReactiveRedisTemplate.opsForSet().remove(oldIndexKey, id.toString());
	        })
			.then(Mono.just(id))
	        .defaultIfEmpty(id);
		
	}

private <T> Mono<T> addLogginOptions(Mono<T> reactiveFlow){
		
		return reactiveFlow
				.doOnSubscribe( subscribed -> this.logInfo( GlobalConstants.MSG_FLOW_SUBS, subscribed.toString()) )
				.doOnSuccess( success -> this.logInfo( GlobalConstants.MSG_FLOW_OK, createExtraInfo(success) ))
	            .doOnCancel( () -> this.logInfo( GlobalConstants.MSG_FLOW_CANCEL, GlobalConstants.MSG_NODATA) )
	            .doOnError( error -> this.logInfo( GlobalConstants.MSG_FLOW_ERROR, error.toString()) )
		        .doFinally( signal -> this.logInfo( GlobalConstants.MSG_FLOW_RESULT, signal.toString()) );		
			
	}
	
	private void logInfo(String action, String extraInfo) {
		
    	reactiveLoggerUtils.logInfoAction(this.getClass().getSimpleName(), action, extraInfo).subscribe();

	}
	
	private <T> String createExtraInfo(T response) {
		
		return response instanceof Number? GlobalConstants.MSG_MODLINES + response.toString():response.toString();
				
	}

	private <T> Mono<T> logFlowStep(Mono<T> reactiveFlow){
		
		return reactiveFlow		
				.flatMap( data -> reactiveLoggerUtils.logInfoAction( this.getClass().getSimpleName(), GlobalConstants.MSG_FLOW_PROCESS, data.toString() ).thenReturn(data) );	
			
	}
	
}
