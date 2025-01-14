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
import dev.ime.infrastructure.entity.AreaRedisEntity;
import reactor.core.publisher.Mono;

@Repository
@Qualifier("areaRedisProjectorAdapter")
public class AreaRedisProjectorAdapter implements BaseProjectorPort{

	private final ReactiveLoggerUtils reactiveLoggerUtils;
    private final ReactiveRedisTemplate<String, AreaRedisEntity> reactiveRedisTemplate;
	
    public AreaRedisProjectorAdapter(ReactiveLoggerUtils reactiveLoggerUtils,
			ReactiveRedisTemplate<String, AreaRedisEntity> reactiveRedisTemplate) {
		super();
		this.reactiveLoggerUtils = reactiveLoggerUtils;
		this.reactiveRedisTemplate = reactiveRedisTemplate;
	}

	@Override
	public Mono<Void> create(Event event) {

		return Mono.justOrEmpty(event.getEventData())
				.transform(this::logFlowStep)
				.flatMap(this::createEntity)		        
				.transform(this::logFlowStep)
		        .flatMap( entity -> reactiveRedisTemplate.opsForValue().set( generateKey(entity.getAreaId() ), entity))
				.switchIfEmpty(Mono.error(new EmptyResponseException(Map.of(GlobalConstants.AREA_CAT, GlobalConstants.EX_EMPTYRESPONSE_DESC))))
				.transform(this::addLogginOptions)
				.then();
	
	}

	@Override
	public Mono<Void> deleteById(Event event) {

		return Mono.justOrEmpty(event.getEventData())
				.transform(this::logFlowStep)
				.map( evenData -> evenData.get(GlobalConstants.AREA_ID))
				.cast(String.class)
				.map(UUID::fromString)			        
				.transform(this::logFlowStep)
				.flatMap( id -> reactiveRedisTemplate.opsForValue().delete( generateKey(id) ))
				.switchIfEmpty(Mono.error(new EmptyResponseException(Map.of(GlobalConstants.AREA_CAT, GlobalConstants.EX_EMPTYRESPONSE_DESC))))
				.transform(this::addLogginOptions)
				.then();
		
	}

	private Mono<AreaRedisEntity> createEntity(Map<String, Object> eventData) {
		
		return Mono.fromCallable( () -> {
			
	        UUID areaId = extractUuid(eventData, GlobalConstants.AREA_ID);
	        
			return new AreaRedisEntity(areaId);
			
		}).onErrorMap(e -> new CreateRedisEntityException(Map.of( GlobalConstants.AREA_CAT, e.getMessage() )));		
		
	}

	private UUID extractUuid(Map<String, Object> eventData, String key) {
		
	    return Optional.ofNullable(eventData.get(key))
	                   .map(Object::toString)
	                   .map(UUID::fromString)
	                   .orElseThrow(() -> new IllegalArgumentException(GlobalConstants.EX_ILLEGALARGUMENT_DESC + ": " + key));
	    
	}
	
	private String generateKey(UUID id) {
		
	    return GlobalConstants.AREA_CAT  + ":" + id.toString();
	    
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
