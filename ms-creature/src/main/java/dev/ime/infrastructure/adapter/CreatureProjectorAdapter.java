package dev.ime.infrastructure.adapter;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.query.Update;
import org.springframework.stereotype.Repository;

import dev.ime.application.exception.CreateJpaEntityException;
import dev.ime.application.exception.EmptyResponseException;
import dev.ime.application.exception.UniqueValueException;
import dev.ime.application.utils.ReactiveLoggerUtils;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.event.Event;
import dev.ime.domain.ports.outbound.BaseProjectorPort;
import dev.ime.domain.ports.outbound.ExtendedProjectorPort;
import dev.ime.infrastructure.entity.CreatureJpaEntity;
import reactor.core.publisher.Mono;

@Repository
@Qualifier("creatureProjectorAdapter")
public class CreatureProjectorAdapter implements BaseProjectorPort, ExtendedProjectorPort{

	private final ReactiveLoggerUtils reactiveLoggerUtils;
	private final R2dbcEntityTemplate r2dbcTemplate;
	
	public CreatureProjectorAdapter(ReactiveLoggerUtils reactiveLoggerUtils, R2dbcEntityTemplate r2dbcTemplate) {
		super();
		this.reactiveLoggerUtils = reactiveLoggerUtils;
		this.r2dbcTemplate = r2dbcTemplate;
	}

	@Override
	public Mono<Void> create(Event event) {

		return Mono.justOrEmpty(event.getEventData())		        
			.transform(this::logFlowStep)
			.flatMap(this::createJpaEntity)		        
			.transform(this::logFlowStep)
			.flatMap(this::validateNameAlreadyUsed)
			.flatMap(this::insertQuery)
			.switchIfEmpty(Mono.error(new EmptyResponseException(Map.of(GlobalConstants.CREATURE_CAT, GlobalConstants.EX_EMPTYRESPONSE_DESC))))
			.transform(this::addLogginOptions)
	        .then();
		
	}

	private Mono<CreatureJpaEntity> createJpaEntity(Map<String, Object> eventData) {
		
		return Mono.fromCallable( () -> {
			
			UUID creatureId = extractUuid(eventData, GlobalConstants.CREATURE_ID);
	        String creatureName = extractString(eventData, GlobalConstants.CREATURE_NAME, GlobalConstants.PATTERN_NAME_FULL);
	        String creatureDescription = extractString(eventData, GlobalConstants.CREATURE_DESC, GlobalConstants.PATTERN_DESC_FULL);
			UUID areaId = extractUuid(eventData, GlobalConstants.AREA_ID);

			return CreatureJpaEntity
		    		.builder()
		    		.creatureId(creatureId)
		    		.creatureName(creatureName)
		    		.creatureDescription(creatureDescription)
		    		.areaId(areaId)
		    		.build();
			
		}).onErrorMap(e -> new CreateJpaEntityException(Map.of( GlobalConstants.CREATURE_CAT, e.getMessage() )));
		
	}

	private UUID extractUuid(Map<String, Object> eventData, String key) {
		
	    return Optional.ofNullable(eventData.get(key))
	                   .map(Object::toString)
	                   .map(UUID::fromString)
	                   .orElseThrow(() -> new IllegalArgumentException(GlobalConstants.EX_ILLEGALARGUMENT_DESC + ": " + key));
	    
	}

	private String extractString(Map<String, Object> eventData, String key, String patternConstraint) {
		
		String value = Optional.ofNullable(eventData.get(key))
	                   .map(Object::toString)
	                   .orElse("");
	    
	    Pattern compiledPattern = Pattern.compile(patternConstraint);
	    Matcher matcher = compiledPattern.matcher(value);
	    if (!matcher.matches()) {
	        throw new IllegalArgumentException(GlobalConstants.EX_ILLEGALARGUMENT_DESC + " OwO " +  key );
	    }

	    return value;
	    
	}

	private Mono<CreatureJpaEntity> validateNameAlreadyUsed(CreatureJpaEntity entity) {
	    
		return r2dbcTemplate.selectOne(
				Query.query(Criteria.where(GlobalConstants.CREATURE_NAME_DB).is(entity.getCreatureName())
						.and(GlobalConstants.CREATURE_ID_DB).not(entity.getAreaId())),
				CreatureJpaEntity.class)				
				.flatMap( entityFound -> Mono.error(new UniqueValueException(Map.of(GlobalConstants.CREATURE_NAME, entityFound.getCreatureName()))))
				.then(Mono.just(entity));		
	}
	
    private Mono<CreatureJpaEntity> insertQuery(CreatureJpaEntity entity) {
    	
        return r2dbcTemplate.insert(entity);
        
    }

	@Override
	public Mono<Void> update(Event event) {

		return Mono.justOrEmpty(event.getEventData())		        
		.transform(this::logFlowStep)
		.flatMap(this::createJpaEntity)		        
		.transform(this::logFlowStep)
		.flatMap(this::validateNameAlreadyUsed)	
		.flatMap(this::updateQuery)	
		.switchIfEmpty(Mono.error(new EmptyResponseException(Map.of(GlobalConstants.CREATURE_CAT, GlobalConstants.EX_EMPTYRESPONSE_DESC))))
		.transform(this::addLogginOptions)
        .then();
		
	}

	private Mono<Long> updateQuery(CreatureJpaEntity entity) {
		 
		return r2dbcTemplate.update(
				Query.query(Criteria.where(GlobalConstants.CREATURE_ID_DB).is(entity.getCreatureId())),
				Update.update(GlobalConstants.CREATURE_NAME_DB, entity.getCreatureName())
					  .set(GlobalConstants.CREATURE_DESC_DB, entity.getCreatureDescription())
					  .set(GlobalConstants.AREA_ID_DB, entity.getAreaId()),
				CreatureJpaEntity.class);
		
	}
	
	@Override
	public Mono<Void> deleteById(Event event) {

		return Mono.justOrEmpty(event.getEventData())		        
		.transform(this::logFlowStep)
		.map( evenData -> evenData.get(GlobalConstants.CREATURE_ID))
		.cast(String.class)
		.map(UUID::fromString)			        
		.transform(this::logFlowStep)
		.flatMap( this::deleteByIdQuery )		
		.switchIfEmpty(Mono.error(new EmptyResponseException(Map.of(GlobalConstants.CREATURE_CAT, GlobalConstants.EX_EMPTYRESPONSE_DESC))))		
		.transform(this::addLogginOptions)
		.then();
		
	}

	private Mono<Long> deleteByIdQuery(UUID entityId) {
		
	    return r2dbcTemplate.delete(
	    		Query.query(Criteria.where(GlobalConstants.CREATURE_ID_DB).is(entityId)),
	    		CreatureJpaEntity.class
	    		);
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
